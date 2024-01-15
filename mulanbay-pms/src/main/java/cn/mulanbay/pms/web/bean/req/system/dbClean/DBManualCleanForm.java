package cn.mulanbay.pms.web.bean.req.system.dbClean;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DBManualCleanForm {

    @NotNull(message = "ID不能为空")
    private Long id;

    @Min(value = 1, message = "天数最小值为1")
    private Integer days;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
