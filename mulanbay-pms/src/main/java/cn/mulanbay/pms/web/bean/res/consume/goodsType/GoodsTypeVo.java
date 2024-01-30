package cn.mulanbay.pms.web.bean.res.consume.goodsType;

import cn.mulanbay.pms.persistent.enums.CommonStatus;

import java.util.ArrayList;
import java.util.List;

public class GoodsTypeVo {

    private Long typeId;

    private String typeName;

    private Long pid;
    private String pname;
    private String behaviorName;
    private CommonStatus status;
    private Short orderIndex;
    // 是否加入统计
    private Boolean stat;
    private List<GoodsTypeVo> children = new ArrayList<>();

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getBehaviorName() {
        return behaviorName;
    }

    public void setBehaviorName(String behaviorName) {
        this.behaviorName = behaviorName;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public Short getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Short orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getStat() {
        return stat;
    }

    public void setStat(Boolean stat) {
        this.stat = stat;
    }

    public List<GoodsTypeVo> getChildren() {
        return children;
    }

    public void setChildren(List<GoodsTypeVo> children) {
        this.children = children;
    }

    public Boolean getHasChildren() {
        return true;
    }
}
