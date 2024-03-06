package cn.mulanbay.pms.web.bean.req.work.company;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.pms.common.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class CompanyForm implements BindUser {

    private Long companyId;

    private Long userId;

    @NotEmpty(message = "公司名不能为空")
    private String companyName;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    @NotNull(message = "入职时间不能为空")
    private Date entryDate;

    @JsonFormat(pattern = Constant.DATE_FORMAT)
    private Date quitDate;

    private String remark;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getQuitDate() {
        return quitDate;
    }

    public void setQuitDate(Date quitDate) {
        this.quitDate = quitDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
