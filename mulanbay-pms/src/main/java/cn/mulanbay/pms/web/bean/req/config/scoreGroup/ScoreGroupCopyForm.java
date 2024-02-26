package cn.mulanbay.pms.web.bean.req.config.scoreGroup;

public class ScoreGroupCopyForm {

    //模板的ID
    private Long templateId;

    //新的分组的名称
    private String groupName;

    private String code;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
