package cn.mulanbay.pms.persistent.dto.fund;

import java.math.BigDecimal;

public class BudgetStat {

    private Object id;
    private String name;
    private BigDecimal value;

    public BudgetStat(Object id, String name, BigDecimal value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
