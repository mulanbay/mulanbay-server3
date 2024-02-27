package cn.mulanbay.pms.web.bean.req.report;

import cn.mulanbay.common.aop.BindUserLevel;

public class ReportUserTreeSH extends ReportTreeSH implements BindUserLevel {

    private Integer level;

    public Integer getLevel() {
        return level;
    }

    @Override
    public void setLevel(Integer level) {
        this.level = level;
    }
}
