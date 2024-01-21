package cn.mulanbay.pms.web.bean.res.consume.matchLog;

import cn.mulanbay.pms.persistent.domain.Consume;
import cn.mulanbay.pms.persistent.domain.MatchLog;

public class MatchLogVo {

    private MatchLog matchLog;

    private Consume consumeData;

    private Object compareData;

    public MatchLog getMatchLog() {
        return matchLog;
    }

    public void setMatchLog(MatchLog matchLog) {
        this.matchLog = matchLog;
    }

    public Consume getConsumeData() {
        return consumeData;
    }

    public void setConsumeData(Consume consumeData) {
        this.consumeData = consumeData;
    }

    public Object getCompareData() {
        return compareData;
    }

    public void setCompareData(Object compareData) {
        this.compareData = compareData;
    }
}
