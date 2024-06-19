package cn.mulanbay.pms.web.bean.req.config.levelConfig;

import cn.mulanbay.pms.persistent.enums.CommonStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class LevelConfigForm {

    private Long id;

    @NotNull(message = "级别不能为空")
    private Integer level;

    @NotEmpty(message = "级别名称不能为空")
    private String levelName;

    //达到该等级的最低积分数要求
    @NotNull(message = "最低积分数要求不能为空")
    private Integer points;

    //达到该等级的最低积分数的至少连续天数
    @NotNull(message = "最低积分数的至少连续天数不能为空")
    private Integer pointsDays;

    //达到该等级的最低评分要求
    @NotNull(message = "最低评分要求不能为空")
    private Integer score;

    //达到该等级的最低评分的至少连续天数
    @NotNull(message = "最低评分的至少连续天数不能为空")
    private Integer scoreDays;

    //该等级对应的角色
    private Long roleId;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    private String remark;

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

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
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
}
