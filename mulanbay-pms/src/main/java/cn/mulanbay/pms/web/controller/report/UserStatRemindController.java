package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.persistent.domain.UserStat;
import cn.mulanbay.pms.persistent.domain.UserStatRemind;
import cn.mulanbay.pms.persistent.service.StatService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.report.stat.UserStatRemindForm;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户统计的提醒
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("userStatRemind")
public class UserStatRemindController extends BaseController {

    @Autowired
    StatService statService;

    private static Class<UserStatRemind> beanClass = UserStatRemind.class;


    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid UserStatRemindForm form) {
        UserStatRemind bean = new UserStatRemind();
        BeanCopy.copy(form, bean);
        UserStat stat = baseService.getObject(UserStat.class,form.getStatId());
        bean.setStat(stat);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/getByUserStat", method = RequestMethod.GET)
    public ResultBean getByUserStat(@RequestParam(name = "statId") Long statId) {
        UserStatRemind bean = statService.getUserStatRemind(statId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid UserStatRemindForm form) {
        UserStatRemind bean = baseService.getObject(beanClass,form.getRemindId());
        BeanCopy.copy(form, bean);
        UserStat stat = baseService.getObject(UserStat.class,form.getStatId());
        bean.setStat(stat);
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        baseService.deleteObjects(beanClass, NumberUtil.stringToLongArray(deleteRequest.getIds()));
        return callback(null);
    }

}
