package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.persistent.domain.UserPlan;
import cn.mulanbay.pms.persistent.domain.UserPlanRemind;
import cn.mulanbay.pms.persistent.service.PlanService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.report.plan.UserPlanRemindForm;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户计划的提醒
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("userPlanRemind")
public class UserPlanRemindController extends BaseController {

    @Autowired
    PlanService planService;

    private static Class<UserPlanRemind> beanClass = UserPlanRemind.class;


    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid UserPlanRemindForm form) {
        UserPlanRemind bean = new UserPlanRemind();
        BeanCopy.copy(form, bean);
        UserPlan plan = baseService.getObject(UserPlan.class,form.getPlanId());
        bean.setPlan(plan);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/getByUserPlan", method = RequestMethod.GET)
    public ResultBean getByUserPlan(@RequestParam(name = "planId") Long planId) {
        UserPlanRemind bean = planService.getUserPlanRemind(planId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid UserPlanRemindForm form) {
        UserPlanRemind bean = baseService.getObject(beanClass,form.getRemindId());
        BeanCopy.copy(form, bean);
        UserPlan plan = baseService.getObject(UserPlan.class,form.getPlanId());
        bean.setPlan(plan);
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
