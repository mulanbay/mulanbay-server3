package cn.mulanbay.pms.web.bean.req.system.dbClean;


import cn.mulanbay.pms.persistent.enums.CleanType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DBManualCleanForm {

    @NotNull(message = "ID不能为空")
    private Long id;

    @Min(value = 1, message = "天数最小值为1")
    private Integer days;

    @NotNull(message = "清理类型不能为空")
    private CleanType cleanType;

    /**
     * 是否加上附加条件
     */
    @NotNull(message = "是否加上附加条件不能为空")
    private Boolean useEc;

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

    public CleanType getCleanType() {
        return cleanType;
    }

    public void setCleanType(CleanType cleanType) {
        this.cleanType = cleanType;
    }

    public Boolean getUseEc() {
        return useEc;
    }

    public void setUseEc(Boolean useEc) {
        this.useEc = useEc;
    }
}
