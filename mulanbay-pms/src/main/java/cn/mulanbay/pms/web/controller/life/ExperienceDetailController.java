package cn.mulanbay.pms.web.controller.life;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Experience;
import cn.mulanbay.pms.persistent.domain.ExperienceDetail;
import cn.mulanbay.pms.persistent.service.ExperienceService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.life.experience.ExperienceDetailSH;
import cn.mulanbay.pms.web.bean.req.life.detail.ExperienceDetailForm;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 人生经历明细
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/experienceDetail")
public class ExperienceDetailController extends BaseController {

    private static Class<ExperienceDetail> beanClass = ExperienceDetail.class;

    @Autowired
    ExperienceService experienceService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public ResultBean list(ExperienceDetailSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("occurDate", Sort.ASC);
        pr.addSort(s);
        PageResult<ExperienceDetail> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ExperienceDetailForm form) {
        ExperienceDetail bean = new ExperienceDetail();
        BeanCopy.copy(form, bean);
        Experience experience = baseService.getObject(Experience.class,form.getExpId());
        bean.setExperience(experience);
        bean.setCost(new BigDecimal(0));
        experienceService.saveOrUpdateDetail(bean, true);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "detailId") Long detailId) {
        ExperienceDetail bean = baseService.getObject(beanClass,detailId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ExperienceDetailForm form) {
        ExperienceDetail bean = baseService.getObject(beanClass,form.getDetailId());
        BeanCopy.copy(form, bean);
        Experience experience = baseService.getObject(Experience.class,form.getExpId());
        bean.setExperience(experience);
        experienceService.saveOrUpdateDetail(bean, true);
        return callback(bean);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        Long[] ll = NumberUtil.stringToLongArray(deleteRequest.getIds());
        for (Long id : ll) {
            experienceService.deleteExperienceDetail(id, deleteRequest.getUserId(), false);
        }
        return callback(null);
    }

    /**
     * 获取国际位置
     *
     * @param countryId
     * @return
     */
    @RequestMapping(value = "/getCountryLocation", method = RequestMethod.GET)
    public ResultBean getCountryLocation(@RequestParam(name = "countryId") Long countryId) {
        String s = experienceService.getCountryLocation(countryId);
        return callback(s);
    }

    /**
     * 根据城市位置
     *
     * @param city
     * @return
     */
    @RequestMapping(value = "/getCityLocation", method = RequestMethod.GET)
    public ResultBean getCityLocation(@RequestParam(name = "city") String city) {
        String s = experienceService.getCityLocation(city);
        return callback(s);
    }

}
