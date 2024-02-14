package cn.mulanbay.pms.persistent.dto.sleep;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SleepAnalyseStat {

    private Object group;

    //睡眠时间（或者睡眠时长）
    private Object value;

    public SleepAnalyseStat(Object group, Object value) {
        this.group = group;
        this.value = value;
    }

    public Object getGroup() {
        return group;
    }

    public void setGroup(Object group) {
        this.group = group;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public double getGroupValue() {
        if (group instanceof BigDecimal) {
            return ((BigDecimal) group).doubleValue();
        }
        if (group instanceof BigInteger) {
            return ((BigInteger) group).doubleValue();
        }
        return Double.parseDouble(group.toString());
    }

    public double getVE() {
        if (value == null) {
            return 0;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }
        if (value instanceof BigInteger) {
            return ((BigInteger) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

}
