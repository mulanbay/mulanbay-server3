package cn.mulanbay.pms.web.bean.req.read.category;

import cn.mulanbay.common.aop.BindUser;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BookCategoryForm implements BindUser {

    private Long cateId;

    private Long userId;

    @NotEmpty(message = "名称不能为空")
    private String cateName;

    @NotNull(message = "排序号不能为空")
    private Short orderIndex;

    private String remark;

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
