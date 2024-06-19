package cn.mulanbay.pms.web.bean.req.behavior;

public class UserBehaviorWordCloudSH extends UserBehaviorCalendarSH {

    /**
     * 是否忽略短语
     */
    private Boolean ignoreShort;

    public Boolean getIgnoreShort() {
        return ignoreShort;
    }

    public void setIgnoreShort(Boolean ignoreShort) {
        this.ignoreShort = ignoreShort;
    }
}
