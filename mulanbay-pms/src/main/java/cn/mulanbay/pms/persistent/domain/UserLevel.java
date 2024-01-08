package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 用户级别配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "user_level")
public class UserLevel implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "level")
    private Integer level;

    @Column(name = "name")
    private String name;

    /**
     * 达到该等级的最低积分数要求
     */
    @Column(name = "points")
    private Integer points;

    /**
     * 达到该等级的最低积分数的至少连续天数
     */
    @Column(name = "points_days")
    private Integer pointsDays;

    /**
     * 达到该等级的最低评分要求
     */
    @Column(name = "score")
    private Integer score;

    /**
     * 达到该等级的最低评分的至少连续天数
     */
    @Column(name = "score_days")
    private Integer scoreDays;

    /**
     * 该等级对应的角色
     */
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "status")
    private CommonStatus status;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getPointsDays() {
        return pointsDays;
    }

    public void setPointsDays(Integer pointsDays) {
        this.pointsDays = pointsDays;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScoreDays() {
        return scoreDays;
    }

    public void setScoreDays(Integer scoreDays) {
        this.scoreDays = scoreDays;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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
        if (other instanceof UserLevel) {
            UserLevel bean = (UserLevel) other;
            return bean.getId().equals(this.getId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
