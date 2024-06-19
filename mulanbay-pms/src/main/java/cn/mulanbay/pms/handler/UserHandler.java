package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.pms.persistent.domain.MonitorUser;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.domain.UserSet;
import cn.mulanbay.pms.persistent.enums.MonitorBussType;
import cn.mulanbay.pms.persistent.service.MonitorUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户处理
 *
 * @author fenghong
 * @create 2024-01-13
 */
@Component
public class UserHandler extends BaseHandler {

    @Autowired
    CommonCacheHandler commonCacheHandler;

    @Autowired
    MonitorUserService systemMonitorUserService;

    public UserHandler() {
        super("用户处理");
    }

    /**
     * 用户配置
     *
     * @param userId
     * @return
     */
    public UserSet getUserSet(Long userId){
        return commonCacheHandler.getBean(UserSet.class,userId);
    }

    /**
     * 删除用户配置
     *
     * @param userId
     * @return
     */
    public void expireUserSet(Long userId){
        commonCacheHandler.removeBean(UserSet.class,userId);
    }

    /**
     * 用户
     *
     * @param userId
     * @return
     */
    public User getUser(Long userId){
        return commonCacheHandler.getBean(User.class,userId);
    }


    /**
     * 获取监控用户列表
     * todo 后期修改为缓存
     *
     * @param bussType
     * @return
     */
    public List<MonitorUser> getMonitorUserList(MonitorBussType bussType){
        return systemMonitorUserService.selectListByType(bussType);
    }

}
