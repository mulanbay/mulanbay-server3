package cn.mulanbay.pms.web.controller.work;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Company;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.main.UserCommonFrom;
import cn.mulanbay.pms.web.bean.req.work.company.CompanyForm;
import cn.mulanbay.pms.web.bean.req.work.company.CompanySH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 公司
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/company")
public class CompanyController extends BaseController {

    private static Class<Company> beanClass = Company.class;

    /**
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(UserCommonFrom cts) {
        try {
            CompanySH sf = new CompanySH();
            sf.setPage(PageRequest.NO_PAGE);
            PageResult<Company> pr = this.getResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<Company> gtList = pr.getBeanList();
            for (Company gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getCompanyId());
                tb.setText(gt.getCompanyName());
                list.add(tb);
            }
            return callback(list);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取公司树异常",
                    e);
        }
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(CompanySH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<Company> getResult(CompanySH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("entryDate", Sort.DESC);
        pr.addSort(sort);
        PageResult<Company> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid CompanyForm form) {
        Company bean = new Company();
        BeanCopy.copy(form, bean);
        bean.setDays(caleDays(form));
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "companyId") Long companyId) {
        Company bean = baseService.getObject(beanClass,companyId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid CompanyForm form) {
        Company bean = baseService.getObject(beanClass,form.getCompanyId());
        BeanCopy.copy(form, bean);
        bean.setDays(caleDays(form));
        baseService.updateObject(bean);
        return callback(bean);
    }

    /**
     * 计算天数
     *
     * @param formRequest
     * @return
     */
    private int caleDays(CompanyForm formRequest) {
        if (formRequest.getQuitDate() == null) {
            return 0;
        }
        int days = DateUtil.getIntervalDays(formRequest.getEntryDate(), formRequest.getQuitDate());
        return days;
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
