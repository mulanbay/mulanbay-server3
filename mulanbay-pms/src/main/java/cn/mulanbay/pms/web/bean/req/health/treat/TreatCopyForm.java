package cn.mulanbay.pms.web.bean.req.health.treat;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.TreatStage;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class TreatCopyForm implements BindUser {

    @NotNull(message = "看病记录编号不能为空")
    private Long treatId;
    private Long userId;

    // 看病日期
    @NotNull(message = "看病日期不能为空")
    private Date treatTime;

    @NotNull(message = "门诊阶段不能为空")
    private TreatStage stage;

    //同步到消费记录
    private Boolean syncToConsume=true;

    /**
     * 复制药品
     */
    @NotNull(message = "复制药品不能为空")
    private Boolean copyDrug;

    /**
     * 复制手术
     */
    @NotNull(message = "复制手术不能为空")
    private Boolean copyOperation;

    public Long getTreatId() {
        return treatId;
    }

    public void setTreatId(Long treatId) {
        this.treatId = treatId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getTreatTime() {
        return treatTime;
    }

    public void setTreatTime(Date treatTime) {
        this.treatTime = treatTime;
    }

    public TreatStage getStage() {
        return stage;
    }

    public void setStage(TreatStage stage) {
        this.stage = stage;
    }

    public Boolean getSyncToConsume() {
        return syncToConsume;
    }

    public void setSyncToConsume(Boolean syncToConsume) {
        this.syncToConsume = syncToConsume;
    }

    public Boolean getCopyDrug() {
        return copyDrug;
    }

    public void setCopyDrug(Boolean copyDrug) {
        this.copyDrug = copyDrug;
    }

    public Boolean getCopyOperation() {
        return copyOperation;
    }

    public void setCopyOperation(Boolean copyOperation) {
        this.copyOperation = copyOperation;
    }
}
