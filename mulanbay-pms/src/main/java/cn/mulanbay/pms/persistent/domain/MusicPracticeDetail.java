package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.TuneLevel;
import cn.mulanbay.pms.persistent.enums.TuneType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 音乐练习曲子
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "music_practice_detail")
public class MusicPracticeDetail implements java.io.Serializable {
    private static final long serialVersionUID = 7184640780429015652L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "detail_id", unique = true, nullable = false)
    private Long detailId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "practice_id")
    private MusicPractice practice;

    @Column(name = "tune")
    private String tune;

    /**
     * 次数
     */
    @Column(name = "times")
    private Integer times;

    @Column(name = "level")
    private TuneLevel level;

    @Column(name = "tune_type")
    private TuneType tuneType;
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

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public MusicPractice getPractice() {
        return practice;
    }

    public void setPractice(MusicPractice practice) {
        this.practice = practice;
    }

    public String getTune() {
        return tune;
    }

    public void setTune(String tune) {
        this.tune = tune;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public TuneLevel getLevel() {
        return level;
    }

    public void setLevel(TuneLevel level) {
        this.level = level;
    }

    public TuneType getTuneType() {
        return tuneType;
    }

    public void setTuneType(TuneType tuneType) {
        this.tuneType = tuneType;
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
    public String getLevelName() {
        return level==null? null:level.getName();
    }

    @Transient
    public String getTuneTypeName() {
        return tuneType==null? null:tuneType.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MusicPracticeDetail bean) {
            return bean.getDetailId().equals(this.getDetailId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(detailId);
    }
}
