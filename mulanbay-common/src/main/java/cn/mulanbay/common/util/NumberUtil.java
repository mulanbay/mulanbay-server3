package cn.mulanbay.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * 数字工具类
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class NumberUtil {

	/**
	 * 产生随机数
	 * @param x 随机最大的数
	 * @return
	 */
	public static int getRandom(int x) {
		int num = (int) (Math.random() * x);
		return num;
	}

	/**
	 * 产生随机数
	 * @param n 位数
	 * @return
	 */
	public static String getRandNum(int n) {
		StringBuffer numStr = new StringBuffer();
		int num;
		for (int i = 0; i < n; i++) {
			// Math.random() 随机出0-1之间的实数，返回值是一个double 类型的
			num = (int) (Math.random() * 10);
			numStr.append(String.valueOf(num));
		}
		return numStr.toString();
	}

	/**
	 * 字符转换为Long数组
	 * @param ids
	 * @return
	 */
	public static Long[] stringToLongArray(String ids){
		String[] ss = ids.split(",");
		Long[] result = new Long[ss.length];
		for(int i=0; i< ss.length;i++){
			result[i]=Long.valueOf(ss[i]);
		}
		return result;
	}

	/**
	 * 获取平均值
	 * @param value
	 * @param counts
	 * @param scale
	 * @return
	 */
	public static double getAverageValue(BigDecimal value,BigInteger counts,int scale){
		if(value==null){
			return 0;
		}
		return getAverageValue(value.doubleValue(),counts.intValue(),scale);
	}

	/**
	 * 获取平均值
	 * @param value
	 * @param counts
	 * @param scale
	 * @return
	 */
	public static double getAverageValue(double value,int counts,int scale){
		if(counts==0){
			return 0;
		}
		double l =value/counts;
		BigDecimal b = new BigDecimal(l);
		double v  =  b.setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue();
		return v;
	}

	/**
	 * 获取值保留小数位数
	 * @param value
	 * @param scale
	 * @return
	 */
	public static double getValue(double value, int scale){
		BigDecimal b = new BigDecimal(value);
		return getValue(b,scale);
	}

	/**
	 * 获取值保留小数位数
	 * @param value
	 * @param scale
	 * @return
	 */
	public static double getValue(BigDecimal value,int scale){
		double v  =  value.setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue();
		return v;
	}

	/**
	 * 获取百分数（乘了100的数）
	 * @param value
	 * @param total 总数
	 * @param scale
	 * @return
	 */
	public static double getPercentValue(long value,long total,int scale){
		if(total==0){
			return 0;
		}
		double l =(value*100.0)/total;
		BigDecimal b = new BigDecimal(l);
		double v  =  b.setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue();
		return v;
	}

	/**
	 * 获取百分数（乘了100的数）
	 * @param value
	 * @param counts
	 * @param scale
	 * @return
	 */
	public static double getPercentValue(Double value,Double counts,int scale){
		if(value==null||counts==null||counts<=0){
			return 0;
		}
		double l =(value*100)/counts;
		BigDecimal b = new BigDecimal(l);
		double v  =  b.setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue();
		return v;
	}

	public static boolean priceEquals(BigDecimal a, BigDecimal b){
		return priceEquals(a.doubleValue(),b.doubleValue());
	}

	/**
	 * 价格是否相等
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean priceEquals(double a, double b){
		if(Math.abs(a-b)<=0.001){
			return true;
		}else{
			return false;
		}
	}


	/**
	 * 求和
	 * @param values
	 * @return
	 */
	public static BigDecimal sum(BigDecimal... values){
		BigDecimal total= new BigDecimal(0);
		for(BigDecimal v : values){
			if(v!=null){
				total= total.add(v);
			}
		}
		return total;
	}

	/**
	 * 获取百分数（乘了100的数）
	 * @param value
	 * @param counts
	 * @param scale
	 * @return
	 */
	public static BigDecimal getPercentValue(BigDecimal value,BigDecimal total,int scale){
		if(value==null||total==null||total.doubleValue()<=0){
			return new BigDecimal(0);
		}
		BigDecimal b = value.multiply(new BigDecimal(100)).divide(total,scale, RoundingMode.HALF_UP);
		return b;
	}

	/**
	 * 判断是否为数字格式或者小数
	 * @param str
	 *     待校验参数
	 * @return
	 *     如果全为数字，返回true；否则，返回false
	 */
	public static boolean isNumber(String str){
		String reg = "\\d+(\\.\\d+)?";
		return str.matches(reg);
	}
}
