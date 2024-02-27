package cn.mulanbay.pms.persistent.dto.stat;

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

    private long compareValue = 0L;

    /**
     * 超出警告值
     */
    private long owv = 0L;

    /**
     * 超出报警值
     */
    private long oav = 0L;

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

    public void setCompareValue(long compareValue) {
        this.compareValue = compareValue;
    }

    public long getOwv() {
        return owv;
    }

    public void setOwv(long owv) {
        this.owv = owv;
    }

    public long getOav() {
        return oav;
    }

    public void setOav(long oav) {
        this.oav = oav;
    }

    /**
     * 计算
     */
    public void calculte() {
        StatTemplate template = userStat.getTemplate();
        //计算比较值
        if (template.getResultType() == ResultType.DATE || template.getResultType() == ResultType.DATE_NAME) {
            if (value == null) {
                //没有数据
                return;
            }
            //计算秒数
            long v = (System.currentTimeMillis() - this.getValue()) / 1000;
            if (template.getValueType() == ValueType.DAY) {
                this.compareValue = v / (3600 * 24);
            } else if (template.getValueType() == ValueType.HOUR) {
                this.compareValue = v / 3600;
            } else if (template.getValueType() == ValueType.MINUTE) {
                this.compareValue = v / 60;
            }
        } else if (template.getResultType() == ResultType.NUMBER || template.getResultType() == ResultType.NUMBER_NAME) {
            this.compareValue = this.getValue();
        } else {
            //
        }
        //超过warning的值，告警类与统计类的逻辑正好相反
        if (template.getCompareType() == CompareType.LESS) {
            this.owv = getCompareValue() - userStat.getWarningValue();
        } else {
            this.owv = userStat.getWarningValue() - getCompareValue();
        }
        // 超过alert的值
        if (template.getCompareType() == CompareType.LESS) {
            this.oav = getCompareValue() - userStat.getAlertValue();
        } else {
            this.oav = userStat.getAlertValue() - getCompareValue();
        }
    }

    /**
     * 比较值获取
     *
     * @return
     */
    public long getCompareValue() {
        return this.compareValue;
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
