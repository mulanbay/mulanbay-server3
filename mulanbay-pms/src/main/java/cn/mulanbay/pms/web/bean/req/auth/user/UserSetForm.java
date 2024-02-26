package cn.mulanbay.pms.web.bean.req.auth.user;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.persistent.enums.Payment;
import jakarta.validation.constraints.NotNull;

public class UserSetForm implements BindUser {

    private Long userId;

    @NotNull(message = "是否发送EMail不能为空")
    private Boolean sendEmail;

    @NotNull(message = "是否发送微信消息不能为空")
    private Boolean sendWx;
    //评分的配置组，对应的是ScoreConfig中的key
    @NotNull(message = "评分的配置组不能为空")
    private Long scoreGroupId;
    //常住城市
    private String residentCity;
    //看病记录商品类型
    @NotNull(message = "看病记录商品类型不能为空")
    private Long treatGoodsTypeId;
    //看病记录商品来源
    @NotNull(message = "看病记录商品来源不能为空")
    private Long treatSourceId;
    //默认支付方式
    @NotNull(message = "默认支付方式不能为空")
    private Payment payment;

    private String remark;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public Boolean getSendWx() {
        return sendWx;
    }

    public void setSendWx(Boolean sendWx) {
        this.sendWx = sendWx;
    }

    public Long getScoreGroupId() {
        return scoreGroupId;
    }

    public void setScoreGroupId(Long scoreGroupId) {
        this.scoreGroupId = scoreGroupId;
    }

    public String getResidentCity() {
        return residentCity;
    }

    public void setResidentCity(String residentCity) {
        this.residentCity = residentCity;
    }

    public Long getTreatGoodsTypeId() {
        return treatGoodsTypeId;
    }

    public void setTreatGoodsTypeId(Long treatGoodsTypeId) {
        this.treatGoodsTypeId = treatGoodsTypeId;
    }

    public Long getTreatSourceId() {
        return treatSourceId;
    }

    public void setTreatSourceId(Long treatSourceId) {
        this.treatSourceId = treatSourceId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
