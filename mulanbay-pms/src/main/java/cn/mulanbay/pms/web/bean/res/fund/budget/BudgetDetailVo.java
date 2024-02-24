package cn.mulanbay.pms.web.bean.res.fund.budget;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.enums.BudgetLogSource;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public class BudgetDetailVo extends Budget {

    //快照ID
    private Long snapshotId;

    //下一次支付时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date nextPayTime;

    //跟实际需要实现的还剩余几天
    private Integer leftDays;

    //本期支付时间
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    private Date cpPaidTime;

    private BigDecimal cpPaidAmount;

    private String bussKey;

    //是否有子类
    private boolean hasChild;

    //数据来源
    private BudgetLogSource source;

    /**
     * 比例
     * @return
     */
    public BigDecimal getRate(){
        return NumberUtil.getPercent(this.cpPaidAmount,this.getAmount(),2);
    }

    public Long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Long snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Date getNextPayTime() {
        return nextPayTime;
    }

    public void setNextPayTime(Date nextPayTime) {
        this.nextPayTime = nextPayTime;
    }

    public Integer getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(Integer leftDays) {
        this.leftDays = leftDays;
    }

    public Date getCpPaidTime() {
        return cpPaidTime;
    }

    public void setCpPaidTime(Date cpPaidTime) {
        this.cpPaidTime = cpPaidTime;
    }

    public BigDecimal getCpPaidAmount() {
        return cpPaidAmount;
    }

    public void setCpPaidAmount(BigDecimal cpPaidAmount) {
        this.cpPaidAmount = cpPaidAmount;
    }

    public String getBussKey() {
        return bussKey;
    }

    public void setBussKey(String bussKey) {
        this.bussKey = bussKey;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public BudgetLogSource getSource() {
        return source;
    }

    public void setSource(BudgetLogSource source) {
        this.source = source;
    }
}
