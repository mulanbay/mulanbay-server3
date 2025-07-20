package cn.mulanbay.pms.web.controller.life;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.ExperienceConsume;
import cn.mulanbay.pms.persistent.domain.ExperienceDetail;
import cn.mulanbay.pms.persistent.domain.GoodsType;
import cn.mulanbay.pms.persistent.service.ExperienceService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.life.consume.ExperienceConsumeForm;
import cn.mulanbay.pms.web.bean.req.life.consume.ExperienceConsumeSH;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 人生经历明细
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/experienceConsume")
public class ExperienceConsumeController extends BaseController {

    private static Class<ExperienceConsume> beanClass = ExperienceConsume.class;

    @Autowired
    ExperienceService experienceService;

    /**
     * 获取任务列表
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public ResultBean list(ExperienceConsumeSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("buyTime", Sort.DESC);
        pr.addSort(s);
        PageResult<ExperienceConsume> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ExperienceConsumeForm form) {
        ExperienceConsume bean = new ExperienceConsume();
        this.copyBean(form,bean);
        experienceService.saveOrUpdateConsume(bean, true);
        return callback(null);
    }

    private void copyBean(ExperienceConsumeForm form, ExperienceConsume bean) {
        BeanCopy.copy(form, bean);
        ExperienceDetail detail = baseService.getObject(ExperienceDetail.class,form.getDetailId());
        if(detail.getExperience().getExpId().longValue()!=form.getExpId().longValue()){
            throw new ApplicationException(PmsCode.EXP_ID_NOT_EQUALS);
        }
        bean.setDetail(detail);
        GoodsType goodsType = baseService.getObject(GoodsType.class,form.getGoodsTypeId());
        bean.setGoodsType(goodsType);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "consumeId") Long consumeId) {
        ExperienceConsume bean = baseService.getObject(beanClass,consumeId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ExperienceConsumeForm form) {
        ExperienceConsume bean = baseService.getObject(beanClass,form.getConsumeId());
        this.copyBean(form,bean);
        experienceService.saveOrUpdateConsume(bean, true);
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
