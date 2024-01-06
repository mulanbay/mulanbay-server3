package cn.mulanbay.pms.web.bean.req.config.dictGroup;

import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.web.bean.request.PageSearch;

public class DictGroupSH extends PageSearch {

    @Query(fieldName = "groupName,code", op = Parameter.Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @Query(fieldName = "code", op = Parameter.Operator.EQ)
    private String code;

    @Query(fieldName = "status", op = Parameter.Operator.EQ)
    private CommonStatus status;

    private Boolean needRoot;

    private String idField;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public Boolean getNeedRoot() {
        return needRoot;
    }

    public void setNeedRoot(Boolean needRoot) {
        this.needRoot = needRoot;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }
}
