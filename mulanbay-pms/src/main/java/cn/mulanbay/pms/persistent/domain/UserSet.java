package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;


/**
 * 用户配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "user_set")
public class UserSet implements java.io.Serializable {

    private static final long serialVersionUID = -785812508054385206L;

    @Id
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "send_email")
    private Boolean sendEmail;

    @Column(name = "send_wx")
    private Boolean sendWx;

    /**
     * 评分的配置组，对应的是ScoreConfig中的key
     */
    @Column(name = "score_group")
    private String scoreGroup;

    /**
     * 常住城市
     */
    @Column(name = "resident_city")
    private String residentCity;

    /**
     * 看病记录商品类型
     */
    @Column(name = "treat_goods_type_id")
    private Long treatGoodsTypeId;

    /**
     * 看病记录商品子类型
     */
    @Column(name = "treat_sub_goods_type_id")
    private Long treatSubGoodsTypeId;

    /**
     * 看病记录购买来源
     */
    @Column(name = "treat_buy_type_id")
    private Long treatBuyTypeId;
    /**
     * 默认支付方式
     */
    @Column(name = "payment")
    private Payment payment;

    /**
     * 默认购买来源
     */
    @Column(name = "buy_type_id")
    private Long buyTypeId;
    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    public Long getUserId() {
        return userId;
    }

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

    public String getScoreGroup() {
        return scoreGroup;
    }

    public void setScoreGroup(String scoreGroup) {
        this.scoreGroup = scoreGroup;
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

    public Long getTreatSubGoodsTypeId() {
        return treatSubGoodsTypeId;
    }

    public void setTreatSubGoodsTypeId(Long treatSubGoodsTypeId) {
        this.treatSubGoodsTypeId = treatSubGoodsTypeId;
    }

    public Long getTreatBuyTypeId() {
        return treatBuyTypeId;
    }

    public void setTreatBuyTypeId(Long treatBuyTypeId) {
        this.treatBuyTypeId = treatBuyTypeId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Long getBuyTypeId() {
        return buyTypeId;
    }

    public void setBuyTypeId(Long buyTypeId) {
        this.buyTypeId = buyTypeId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }


    @Override
    public boolean equals(Object other) {
        if (other instanceof UserSet bean) {
            return bean.getUserId().equals(this.getUserId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
