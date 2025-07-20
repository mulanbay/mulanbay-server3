package cn.mulanbay.pms.web.controller.life;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.service.ExperienceService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.life.detail.ExperienceDetailForm;
import cn.mulanbay.pms.web.bean.req.life.experience.ExperienceDetailSH;
import cn.mulanbay.pms.web.bean.req.life.experience.ExperienceDetailTreeSH;
import cn.mulanbay.pms.web.bean.req.life.experience.ExperienceSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static cn.mulanbay.persistent.query.PageRequest.NO_PAGE;

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
     * 获取类型树列表
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(ExperienceDetailTreeSH ts) {
        try {
            Experience exp = null;
            if(ts.getExpId()!=null){
                exp = baseService.getObject(Experience.class, ts.getExpId());
            }else{
                ExperienceDetail bean = baseService.getObject(beanClass,ts.getDetailId());
                exp = bean.getExperience();
            }
            ExperienceDetailSH sf = new ExperienceDetailSH();
            sf.setExpId(exp.getExpId());
            sf.setUserId(ts.getUserId());
            sf.setNeedTotal(false);
            sf.setPage(NO_PAGE);
            PageResult<ExperienceDetail> qr = this.getResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (ExperienceDetail le : qr.getBeanList()) {
                TreeBean tb = new TreeBean();
                tb.setId(le.getDetailId());
                String cityInfo = ""+le.getStartCity().getCityName()+"-->"+le.getArriveCity().getCityName();
                String text = DateUtil.getFormatDate(le.getOccurDate(),DateUtil.FormatDay1)+"("+cityInfo+")";
                tb.setText(text);
                list.add(tb);
            }
            Boolean needRoot = ts.getNeedRoot();
            if(needRoot!=null && needRoot){
                //添加根目录
                TreeBean root = new TreeBean();
                root.setId(null);
                root.setText(exp.getExpName());
                root.setChildren(list);
                List<TreeBean> newList = new ArrayList<TreeBean>();
                newList.add(root);
                return callback(newList);
            }else{
                return callback(list);
            }

        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取类型树列表异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public ResultBean list(ExperienceDetailSH sf) {
        PageResult<ExperienceDetail> qr = this.getResult(sf);
        return callbackDataGrid(qr);
    }

    private PageResult<ExperienceDetail> getResult(ExperienceDetailSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("occurDate", Sort.ASC);
        pr.addSort(s);
        Sort s2 = new Sort("createdTime", Sort.ASC);
        pr.addSort(s2);
        PageResult<ExperienceDetail> qr = baseService.getBeanResult(pr);
        return qr;
    }
    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ExperienceDetailForm form) {
        ExperienceDetail bean = new ExperienceDetail();
        this.formToBean(form,bean);
        experienceService.saveOrUpdateDetail(bean, true);
        return callback(bean);
    }

    private void formToBean(ExperienceDetailForm form, ExperienceDetail bean){
        BeanCopy.copy(form, bean);
        Experience experience = baseService.getObject(Experience.class,form.getExpId());
        bean.setExperience(experience);
        if(bean.getCost()==null){
            bean.setCost(new BigDecimal(0));
        }
        //出发
        Country startCountry = baseService.getObject(Country.class,form.getStartCountryId());
        bean.setStartCountry(startCountry);
        if(form.getStartProvinceId()!=null){
            Province startProvince = baseService.getObject(Province.class,form.getStartProvinceId());
            bean.setStartProvince(startProvince);
        }
        if(form.getStartCityId()!=null){
            City startCity = baseService.getObject(City.class,form.getStartCityId());
            bean.setStartCity(startCity);
        }
        if(form.getStartDistrictId()!=null){
            District startDistrict = baseService.getObject(District.class,form.getStartDistrictId());
            bean.setStartDistrict(startDistrict);
        }
        //抵达
        Country arriveCountry = baseService.getObject(Country.class,form.getArriveCountryId());
        bean.setArriveCountry(arriveCountry);
        if(form.getArriveProvinceId()!=null){
            Province arriveProvince = baseService.getObject(Province.class,form.getArriveProvinceId());
            bean.setArriveProvince(arriveProvince);
        }
        if(form.getArriveCityId()!=null){
            City arriveCity = baseService.getObject(City.class,form.getArriveCityId());
            bean.setArriveCity(arriveCity);
        }
        if(form.getArriveDistrictId()!=null){
            District arriveDistrict = baseService.getObject(District.class,form.getArriveDistrictId());
            bean.setArriveDistrict(arriveDistrict);
        }
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
     * 最近的明细
     *
     * @return
     */
    @RequestMapping(value = "/lastDetail", method = RequestMethod.GET)
    public ResultBean lastDetail(@RequestParam(name = "expId") Long expId) {
        ExperienceDetail bean = experienceService.getLastDetail(expId);
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
        this.formToBean(form,bean);
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

}
