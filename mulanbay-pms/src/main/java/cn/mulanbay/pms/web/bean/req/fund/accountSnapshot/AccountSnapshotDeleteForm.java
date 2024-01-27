package cn.mulanbay.pms.web.bean.req.fund.accountSnapshot;

import cn.mulanbay.common.aop.BindUser;

public class AccountSnapshotDeleteForm implements BindUser {

    private Long userId;

    private Long snapshotId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Long snapshotId) {
        this.snapshotId = snapshotId;
    }
}
