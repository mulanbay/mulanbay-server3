package cn.mulanbay.pms.web.bean.req.life.archive;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.BussType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ArchiveForm implements BindUser {

    private Long archiveId;
    private Long userId;

    @NotEmpty(message = "标题不能为空")
    private String title;

    @NotEmpty(message = "内容不能为空")
    private String content;

    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @NotNull(message = "发生时间不能为空")
    private Date date;

    @NotNull(message = "业务类型不能为空")
    private BussType bussType;

    private Long sourceId;

    private String remark;

    public Long getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(Long archiveId) {
        this.archiveId = archiveId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BussType getBussType() {
        return bussType;
    }

    public void setBussType(BussType bussType) {
        this.bussType = bussType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
