package cn.mulanbay.pms.web.bean.req.system.resources;

import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.ResourcesType;
import cn.mulanbay.web.bean.request.PageSearch;
import jakarta.persistence.Column;

public class ResourcesSH extends PageSearch {

    @Query(fieldName = "path", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "type", op = Parameter.Operator.EQ)
    private ResourcesType type;

    @Query(fieldName = "referId", op = Parameter.Operator.EQ)
    private Long referId;

    @Query(fieldName = "bussSource", op = Parameter.Operator.EQ)
    private BussSource bussSource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourcesType getType() {
        return type;
    }

    public void setType(ResourcesType type) {
        this.type = type;
    }

    public Long getReferId() {
        return referId;
    }

    public void setReferId(Long referId) {
        this.referId = referId;
    }

    public BussSource getBussSource() {
        return bussSource;
    }

    public void setBussSource(BussSource bussSource) {
        this.bussSource = bussSource;
    }
}
