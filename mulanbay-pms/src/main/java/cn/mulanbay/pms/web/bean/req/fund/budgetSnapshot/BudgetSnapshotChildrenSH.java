package cn.mulanbay.pms.web.bean.req.fund.budgetSnapshot;

import cn.mulanbay.common.aop.BindUser;

public class BudgetSnapshotChildrenSH implements BindUser {

    private Long snapshotId;

    private Long userId;

    private boolean needChart;

    public Long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Long snapshotId) {
        this.snapshotId = snapshotId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isNeedChart() {
        return needChart;
    }

    public void setNeedChart(boolean needChart) {
        this.needChart = needChart;
    }
}
