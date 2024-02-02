package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.CompareType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 评分配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "score_config")
public class ScoreConfig implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "config_id", unique = true, nullable = false)
    private Long configId;
    //名称
    @Column(name = "config_name")
    private String configName;
    @Column(name = "group_id")
    private Long groupId;
    //sql语句
    @Column(name = "sql_content")
    private String sqlContent;
    /**
     * 极限值
     * （1）compareType=MORE时，比如音乐练习得分类型
     * limitValue=LV（例如：0.8），表示每天频率超过0.8时得到满分MS（假如为10分），0为0分
     * 那么sqlContent统计出来的值VV在0-0.8之间时按照比例乘以10取整，比如0.2时的得分为0.2/0.8*10=4
     * 公式为score = VV/LV*MS
     * （2）compareType=LESS时，比如消费得分类型
     * limitValue=LV（例如：0.8），表示每天频率超过0.8时得到0分，0为满分MS（假如为10分）
     * 那么sqlContent统计出来的值VV在0-0.8之间时，分值为（0.8-统计值）按照比例乘以10取整，比如0.2时的得分为（0.8-0.2）/0.8*10=7.5约等于8
     * 公式为score = （LV-VV）/VV*MS
     */
    @Column(name = "limit_value")
    private Double limitValue;

    @Column(name = "compare_type")
    private CompareType compareType;

    @Column(name = "max_score")
    private Integer maxScore;
    //账户状态
    @Column(name = "status")
    private CommonStatus status;
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

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public Double getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(Double limitValue) {
        this.limitValue = limitValue;
    }

    public CompareType getCompareType() {
        return compareType;
    }

    public void setCompareType(CompareType compareType) {
        this.compareType = compareType;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
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
        if (other instanceof ScoreConfig bean) {
            return bean.getConfigId().equals(this.getConfigId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(configId);
    }
}
