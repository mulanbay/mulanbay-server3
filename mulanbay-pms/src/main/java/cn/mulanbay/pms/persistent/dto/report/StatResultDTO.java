package cn.mulanbay.pms.persistent.dto.report;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.persistent.domain.StatTemplate;
import cn.mulanbay.pms.persistent.domain.UserStat;
import cn.mulanbay.pms.persistent.enums.CompareType;
import cn.mulanbay.pms.persistent.enums.ResultType;
import cn.mulanbay.pms.persistent.enums.ValueType;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户统计的结果封装类
 */
public class StatResultDTO implements Serializable {

    private static final long serialVersionUID = 2561383920772533581L;

    /**
     * 统计值:
     * (1)数字类型
     * (2)时间类型：时间戳
     */
    private Long value;

    /**
     * 有名称的统计值
     */
    private String nameValue;

    private UserStat userStat;

    /**
     * 统计值，经过单位转换
     */
    private long statValue = 0L;

    /**
     * 超出期望的值
     */
    private long overValue = 0L;


    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getNameValue() {
        return nameValue;
    }

    public void setNameValue(String nameValue) {
        this.nameValue = nameValue;
    }

    public UserStat getUserStat() {
        return userStat;
    }

    public void setUserStat(UserStat userStat) {
        this.userStat = userStat;
    }

    public long getStatValue() {
        return statValue;
    }

    public void setStatValue(long statValue) {
        this.statValue = statValue;
    }

    public long getOverValue() {
        return overValue;
    }

    public void setOverValue(long overValue) {
        this.overValue = overValue;
    }

    /**
     * 计算
     */
    public void calculte() {
        StatTemplate template = userStat.getTemplate();
        if (value == null) {
            //没有数据
            return;
        }
        //计算比较值
        if (template.getResultType() == ResultType.DATE || template.getResultType() == ResultType.DATE_NAME) {
            //计算秒数
            long v = (System.currentTimeMillis() - this.getValue()) / 1000;
            if (template.getValueType() == ValueType.DAY) {
                this.statValue = v / (3600 * 24);
            } else if (template.getValueType() == ValueType.HOUR) {
                this.statValue = v / 3600;
            } else if (template.getValueType() == ValueType.MINUTE) {
                this.statValue = v / 60;
            }
        } else if (template.getResultType() == ResultType.NUMBER || template.getResultType() == ResultType.NUMBER_NAME) {
            this.statValue = this.getValue();
        } else {
            //
        }
        //超过warning的值，告警类与统计类的逻辑正好相反
        if (userStat.getCompareType() == CompareType.LESS) {
            this.overValue = this.statValue - userStat.getExpectValue();
        } else {
            this.overValue = userStat.getExpectValue() - this.statValue;
        }
    }

    /**
     * 获取结果值
     *
     * @return
     */
    public String getResultValue() {
        //第一个值没有数据则直接返回
        if(value==null){
            return null;
        }
        StatTemplate template = userStat.getTemplate();
        if (template.getResultType() == ResultType.DATE) {
            return DateUtil.getFormatDate(new Date(this.getValue()), DateUtil.FormatDay1);
        } else if (template.getResultType() == ResultType.NUMBER) {
            return this.getValue().toString();
        } else if (template.getResultType() == ResultType.DATE_NAME) {
            return "" + this.getNameValue() + DateUtil.getFormatDate(new Date(this.getValue()), DateUtil.FormatDay1);
        } else if (template.getResultType() == ResultType.NUMBER_NAME) {
            return "" + this.getNameValue() + this.getValue().toString();
        } else {
            return null;
        }
    }


}
