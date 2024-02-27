package cn.mulanbay.pms.web.bean.req.report.bind;

import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.pms.persistent.enums.StatBussType;
import cn.mulanbay.web.bean.request.PageSearch;

public class StatBindConfigSH extends PageSearch {

    @Query(fieldName = "fid", op = Parameter.Operator.EQ)
    private Long fid;

    @Query(fieldName = "type", op = Parameter.Operator.EQ)
    private StatBussType type;

    public Long getFid() {
        return fid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public StatBussType getType() {
        return type;
    }

    public void setType(StatBussType type) {
        this.type = type;
    }
}
