package cn.mulanbay.persistent.query;

import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 通用查询的每个字段查询参数定义
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class Parameter {

	/**
	 * 空值，表示不加入到查询绑定值中
	 */
	public final static Object NULL_VALUE = new Object();

	/**
	 * SQL语句类型时sql中的变量序号占位符
	 */
	public final static String SQL_BI = "{bind_index}";

	/**
	 * 如果是REFER类型，表示运算符是另外一个域来决定，且其类型为Operator
	 * NULL_NOTNULL 由值来决定
	 * @see NullType
	 */
	public enum Operator {
		EQ("="),
		NE("!="),
		LIKE("like"),
		GT(">"),
		LT("<"),
		GTE(">="),
		LTE("<="),
		IN("in"),
		NOTIN("not in"),
		SQL(""),
		REFER(""),
		NULL_NOTNULL;

		private String symbol;

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		Operator() {
		}

		Operator(String symbol) {
			this.symbol = symbol;
		}
	}

	private String fieldName;

	private Operator condition;

	private Object value;

	private CrossType crossType;

	/**
	 * 请求参数
	 */
	private String paraStr;

	/**
	 * 主要针对like类型需要添加%符号，只有第一次的时候才需要添加
	 */
	private boolean valueChanged=false;

	/**
	 * 默认为一个
	 * cross类型的有多个参数
	 */
	private int paras=1;

	private boolean supportNullValue;

	public int getParas() {
		return paras;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Operator getCondition() {
		return condition;
	}

	public void setCondition(Operator condition) {
		this.condition = condition;
	}

	public Object getValue() {
		return value;
	}

	public CrossType getCrossType() {
		return crossType;
	}

	public void setCrossType(CrossType crossType) {
		this.crossType = crossType;
	}

	public boolean isSupportNullValue() {
		return supportNullValue;
	}

	public void setSupportNullValue(boolean supportNullValue) {
		this.supportNullValue = supportNullValue;
	}

	public Parameter(String fieldName, Operator condition) {
		super();
		this.fieldName = fieldName;
		this.condition = condition;
	}

	public Parameter(String fieldName, Operator condition, Object value) {
		super();
		this.fieldName = fieldName;
		this.condition = condition;
		this.value = value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * 获取查询参数，不过该方法做了很多逻辑操作，不能调用两次以上
	 * @param paraIndex 绑定参数索引
	 * @return
	 */
	public String getParameterString(int paraIndex) {
		//如果已经计算过则直接返回
		if(StringUtil.isNotEmpty(paraStr)){
			return paraStr;
		}
		if(condition== Operator.SQL){
			paraStr = fieldName.replace(SQL_BI,String.valueOf(paraIndex));
			return paraStr;
		}else if(crossType== CrossType.NULL){
			//普通类型，不需要多个域以前查询
			paraStr =  fieldName + " "+this.getOperateSymbol(paraIndex);
			return paraStr;
		}else{
			//跨多个域
			String[] ss = fieldName.split(",");
			int n =ss.length;
			if(n==1){
				//还是普通类型
				paraStr = fieldName + this.getOperateSymbol(paraIndex);
				return paraStr;
			}
			List newValues = new ArrayList();
			StringBuffer sb = new StringBuffer();
			sb.append(" (");
			for(int i=0;i<n;i++){
				//对于in () 这种类型，下标索引值需要在这里添加，根据值的个数添加
				String os = this.getOperateSymbol(paraIndex);
				sb.append(ss[i] +" "+ os);
				if(i<n-1){
					sb.append(" "+crossType.getSymbol()+" ");
				}
				//值的设置需要放在操作符的设置后面，涉及到like类型的重新设置问题
				if(value!=null){
					if(value instanceof List || value instanceof Set ){
						Collection vv = (Collection) value;
						newValues.addAll(vv);
						//如果是集合类型，下标索引值要右移个数 = 集合元素的个数
						paraIndex += vv.size();
					}else{
						newValues.add(value);
						paraIndex += 1;
					}
				}else{
					paraIndex += 1;
				}
			}
			sb.append(")");
			//这里需要重新设置，对于CROSS类型需要在这里设置
			paras=n;
			//重新设置绑定变量
			this.value =newValues;
			paraStr = sb.toString();
			return paraStr;
		}
	}

	/**
	 * sql的操作运算符
	 * @param paraIndex 绑定参数索引
	 * @return
	 */
	private String getOperateSymbol(int paraIndex){
		if(condition== Operator.IN||condition== Operator.NOTIN){
			if(value instanceof String){
				String s = condition.getSymbol()+" (" + value.toString() + ") ";
				value = NULL_VALUE;
				paras=0;
				return s;
			}else if(value instanceof List || value instanceof Set){
				// 集合类型，value不需要重新设置，由getParameterString()方法重新设置
				Collection newValues = (Collection) value;
				int n = newValues.size();
				paras=n;
				StringBuffer sb = new StringBuffer();
				sb.append(condition.getSymbol()+" (" );
				for(int i=0;i<n;i++){
					//此时index没有改变上层的firstIndex序号
					sb.append("?"+(paraIndex++));
					if(i<n-1){
						sb.append(",");
					}
					//newValues.add(value);
				}
				sb.append(") ");
				//this.value=newValues;
				return sb.toString();
			}else{
				throw new PersistentException("无法识别的IN 或者 NOTIN 绑定变量类型");
			}
		}else if(condition== Operator.LIKE){
			if(!valueChanged){
				value = "%"+value.toString()+"%";
				valueChanged=true;
			}
			paras=1;
			return condition.getSymbol()+" ?" +(paraIndex++)+" ";
		}else if(condition== Operator.NULL_NOTNULL){
			String s;
			if(value==null){
				s = null;
			}else if(value== NullType.NULL){
				s = " is null ";
			}else{
				s = " is not null ";
			}
			//不能加入变量绑定中
			value = NULL_VALUE;
			paras=0;
			return s;
		}else{
			paras=1;
			return condition.getSymbol()+" ?"+(paraIndex++)+" ";
		}
	}
}
