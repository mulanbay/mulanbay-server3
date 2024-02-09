package cn.mulanbay.pms.web.bean.req.health.drug;

import cn.mulanbay.common.aop.BindUser;

import jakarta.validation.constraints.NotNull;

public class TreatDrugLastSH implements BindUser {

    @NotNull(message = "名称不能为空")
    private String drugName;

    private Long userId;

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
