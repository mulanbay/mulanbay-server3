package cn.mulanbay.pms.web.controller.auth;

import cn.mulanbay.pms.persistent.domain.UserSet;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.auth.user.UserSetForm;
import cn.mulanbay.pms.web.bean.req.main.UserCommonForm;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户设置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/userSet")
public class UserSetController extends BaseController {

    private static Class<UserSet> beanClass = UserSet.class;

    @Autowired
    AuthService authService;

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean get(UserCommonForm ucf) {
        UserSet bean = baseService.getObject(beanClass,ucf.getUserId());
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid UserSetForm form) {
        UserSet bean = baseService.getObject(beanClass,form.getUserId());
        BeanCopy.copy(form, bean);
        baseService.updateObject(bean);
        return callback(null);
    }

}
