package cn.mulanbay.pms.web.bean.req.sport.milestone;

import cn.mulanbay.web.bean.request.PageSearch;
import jakarta.validation.constraints.NotNull;

/**
 * 运动里程碑刷新
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class SportMilestoneRefreshForm extends PageSearch {

    @NotNull(message = "运动类型不能为空")
    private Long sportId;

    private boolean reInit;

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public boolean isReInit() {
        return reInit;
    }

    public void setReInit(boolean reInit) {
        this.reInit = reInit;
    }
}
