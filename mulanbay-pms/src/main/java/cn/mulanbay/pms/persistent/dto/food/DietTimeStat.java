package cn.mulanbay.pms.persistent.dto.food;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DietTimeStat {

    private Object timeGroup;

    /**
     * 时间点
     */
    private Object value;

    public DietTimeStat(Object timeGroup, Object value) {
        this.timeGroup = timeGroup;
        this.value = value;
    }

    public Object getTimeGroup() {
        return timeGroup;
    }

    public void setTimeGroup(Object timeGroup) {
        this.timeGroup = timeGroup;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public double getTimeGroupValue() {
        if (timeGroup instanceof BigDecimal) {
            return ((BigDecimal) timeGroup).doubleValue();
        }
        if (timeGroup instanceof BigInteger) {
            return ((BigInteger) timeGroup).doubleValue();
        }
        return Double.parseDouble(timeGroup.toString());
    }

    public double getDoubleValue() {
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
