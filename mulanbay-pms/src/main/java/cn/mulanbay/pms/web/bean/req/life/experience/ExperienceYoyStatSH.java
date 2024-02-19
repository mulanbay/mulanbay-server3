package cn.mulanbay.pms.web.bean.req.life.experience;

import cn.mulanbay.pms.web.bean.req.BaseYoyStatSH;

public class ExperienceYoyStatSH extends BaseYoyStatSH {

    private Long userId;

    private String types;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }
}
