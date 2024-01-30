package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.GoodsMatchType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 消费记录匹配日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "match_log")
public class MatchLog implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    //实际的消费记录
    @Column(name = "consume_id")
    private Long consumeId;

    @Column(name = "consume_data")
    private String consumeData;

    //参与比较的消费记录
    @Column(name = "compare_id")
    private Long compareId;

    @Column(name = "compare_data")
    private String compareData;

    @Column(name = "match_type")
    private GoodsMatchType matchType;

    @Column(name = "goods_name")
    private String goodsName;

    //ai算出的匹配度
    @Column(name = "ai_match")
    private float aiMatch;
    //实际的匹配度
    @Column(name = "ac_match")
    private float acMatch;
    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    public String getConsumeData() {
        return consumeData;
    }

    public void setConsumeData(String consumeData) {
        this.consumeData = consumeData;
    }

    public Long getCompareId() {
        return compareId;
    }

    public void setCompareId(Long compareId) {
        this.compareId = compareId;
    }

    public String getCompareData() {
        return compareData;
    }

    public void setCompareData(String compareData) {
        this.compareData = compareData;
    }

    public GoodsMatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(GoodsMatchType matchType) {
        this.matchType = matchType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public float getAiMatch() {
        return aiMatch;
    }

    public void setAiMatch(float aiMatch) {
        this.aiMatch = aiMatch;
    }

    public float getAcMatch() {
        return acMatch;
    }

    public void setAcMatch(float acMatch) {
        this.acMatch = acMatch;
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

    @Transient
    public String getMatchTypeName() {
        return matchType == null ? null : matchType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MatchLog bean) {
            return bean.getId().equals(this.getId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
