package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 人生经历中的消费记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "experience_consume")
public class ExperienceConsume implements java.io.Serializable {
    private static final long serialVersionUID = -9148930052842204849L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "consume_id", unique = true, nullable = false)
    private Long consumeId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "consume_name")
    private String consumeName;

    /**
     * 购买时间
     */
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "buy_time")
    private Date buyTime;

    @ManyToOne
    @JoinColumn(name = "detail_id", nullable = true)
    private ExperienceDetail detail;

    @ManyToOne
    @JoinColumn(name = "goods_type_id", nullable = true)
    private GoodsType goodsType;

    @Column(name = "cost",precision = 9,scale = 2)
    private BigDecimal cost;
    //原始的消费ID
    @Column(name = "sc_id")
    private Long scId;
    // 是否加入统计
    @Column(name = "stat")
    private Boolean stat;
    @Column(name = "remark")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time",updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time",insertable = false)
    private Date modifyTime;

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getConsumeName() {
        return consumeName;
    }

    public void setConsumeName(String consumeName) {
        this.consumeName = consumeName;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public ExperienceDetail getDetail() {
        return detail;
    }

    public void setDetail(ExperienceDetail detail) {
        this.detail = detail;
    }

    public GoodsType getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(GoodsType goodsType) {
        this.goodsType = goodsType;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Long getScId() {
        return scId;
    }

    public void setScId(Long scId) {
        this.scId = scId;
    }

    public Boolean getStat() {
        return stat;
    }

    public void setStat(Boolean stat) {
        this.stat = stat;
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
}
