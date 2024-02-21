package cn.mulanbay.pms.web.controller.dream;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.persistent.domain.Dream;
import cn.mulanbay.pms.persistent.domain.DreamRemind;
import cn.mulanbay.pms.persistent.service.DreamService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.dream.DreamRemindForm;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 梦想提醒
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/dreamRemind")
public class DreamRemindController extends BaseController {

    @Autowired
    DreamService dreamService;

    private static Class<DreamRemind> beanClass = DreamRemind.class;


    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid DreamRemindForm form) {
        DreamRemind bean = new DreamRemind();
        BeanCopy.copy(form, bean);
        Dream dream = baseService.getObject(Dream.class,form.getDreamId());
        bean.setDream(dream);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/getByDream", method = RequestMethod.GET)
    public ResultBean getByDream(@RequestParam(name = "dreamId") Long dreamId) {
        DreamRemind bean = dreamService.getRemind(dreamId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid DreamRemindForm form) {
        DreamRemind bean = baseService.getObject(beanClass,form.getRemindId());
        BeanCopy.copy(form, bean);
        Dream dream = baseService.getObject(Dream.class,form.getDreamId());
        bean.setDream(dream);
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
