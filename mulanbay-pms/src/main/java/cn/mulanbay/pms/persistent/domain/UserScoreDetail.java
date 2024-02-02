package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户评分详情
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "user_score_detail")
public class UserScoreDetail implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_score_id")
    private Long userScoreId;

    @ManyToOne
    @JoinColumn(name = "config_id")
    private ScoreConfig scoreConfig;
    @Column(name = "score")
    private Integer score;
    @Column(name = "stat_value")
    private Double statValue;
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

    public Long getUserScoreId() {
        return userScoreId;
    }

    public void setUserScoreId(Long userScoreId) {
        this.userScoreId = userScoreId;
    }

    public ScoreConfig getScoreConfig() {
        return scoreConfig;
    }

    public void setScoreConfig(ScoreConfig scoreConfig) {
        this.scoreConfig = scoreConfig;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Double getStatValue() {
        return statValue;
    }

    public void setStatValue(Double statValue) {
        this.statValue = statValue;
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
        if (other instanceof UserScoreDetail bean) {
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
