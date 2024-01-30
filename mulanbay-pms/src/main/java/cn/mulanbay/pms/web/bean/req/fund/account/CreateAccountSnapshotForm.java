package cn.mulanbay.pms.web.bean.req.fund.account;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import jakarta.validation.constraints.NotEmpty;

public class CreateAccountSnapshotForm implements BindUser {

    private Long userId;

    @NotEmpty(message = "快照名称不能为空")
    private String snapshotName;

    private PeriodType period;

    private String remark;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSnapshotName() {
        return snapshotName;
    }

    public void setSnapshotName(String snapshotName) {
        this.snapshotName = snapshotName;
    }

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
