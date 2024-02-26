package cn.mulanbay.pms.web.bean.req.config.scoreConfig;

import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.CompareType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ScoreConfigForm {

    private Long configId;

    @NotEmpty(message = "名称不能为空")
    private String configName;

    @NotNull(message = "组号不能为空")
    private Long groupId;

    //sql语句
    @NotEmpty(message = "SQL语句不能为空")
    private String sqlContent;

    @NotNull(message = "极限值不能为空")
    private Double limitValue;

    @NotNull(message = "比较类型不能为空")
    private CompareType compareType;

    @NotNull(message = "最大值不能为空")
    private Integer maxScore;

    @NotNull(message = "状态不能为空")
    private CommonStatus status;

    private String remark;

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
}
