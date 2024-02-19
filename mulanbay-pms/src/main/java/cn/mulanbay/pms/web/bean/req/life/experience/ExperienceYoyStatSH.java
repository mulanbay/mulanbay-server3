package cn.mulanbay.pms.web.bean.req.life.experience;

import cn.mulanbay.pms.persistent.enums.ExperienceType;
import cn.mulanbay.pms.web.bean.req.BaseYoyStatSH;

import java.util.List;


public class ExperienceYoyStatSH extends BaseYoyStatSH {

    private Long userId;

    private List<ExperienceType> types;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<ExperienceType> getTypes() {
        return types;
    }

    public void setTypes(List<ExperienceType> types) {
        this.types = types;
    }
}
