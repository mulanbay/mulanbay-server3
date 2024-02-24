package cn.mulanbay.pms.web.controller.work;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.work.businessTrip.BusinessTripForm;
import cn.mulanbay.pms.web.bean.req.work.businessTrip.BusinessTripSH;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Date;

/**
 * 出差管理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/businessTrip")
public class BusinessTripController extends BaseController {

    private static Class<BusinessTrip> beanClass = BusinessTrip.class;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(BusinessTripSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<BusinessTrip> getResult(BusinessTripSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("tripDate", Sort.DESC);
        pr.addSort(sort);
        PageResult<BusinessTrip> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid BusinessTripForm form) {
        BusinessTrip bean = new BusinessTrip();
        this.formToBean(form,bean);
        baseService.saveObject(bean);
        return callback(null);
    }

    private void formToBean(BusinessTripForm form,BusinessTrip bean){
        BeanCopy.copy(form,bean);
        Company company = baseService.getObject(Company.class,form.getCompanyId());
        bean.setCompany(company);
        Country country = baseService.getObject(Country.class,form.getCountryId());
        bean.setCountry(country);
        Province province = baseService.getObject(Province.class,form.getProvinceId());
        bean.setProvince(province);
        City city = baseService.getObject(City.class,form.getCityId());
        bean.setCity(city);
        District district = baseService.getObject(District.class,form.getDistrictId());
        bean.setDistrict(district);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "tripId") Long tripId) {
        BusinessTrip bean = baseService.getObject(beanClass,tripId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid BusinessTripForm form) {
        BusinessTrip bean = baseService.getObject(beanClass,form.getTripId());
        this.formToBean(form,bean);
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
