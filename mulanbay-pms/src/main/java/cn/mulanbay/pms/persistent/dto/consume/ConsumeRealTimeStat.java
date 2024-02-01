package cn.mulanbay.pms.persistent.dto.consume;

import java.math.BigDecimal;

public class ConsumeRealTimeStat {

    private Long id;
    private String name;
    private BigDecimal value;

    public void add(BigDecimal v){
        if(v==null){
            return;
        }
        if(value ==null){
            value = new BigDecimal(0);
        }
        value = value.add(v);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
