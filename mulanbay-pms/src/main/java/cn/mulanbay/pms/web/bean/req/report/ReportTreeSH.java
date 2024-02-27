package cn.mulanbay.pms.web.bean.req.report;

import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.web.bean.req.CommonTreeSH;

public class ReportTreeSH extends CommonTreeSH {

    private BussType bussType;

    public BussType getBussType() {
        return bussType;
    }

    public void setBussType(BussType bussType) {
        this.bussType = bussType;
    }
}
