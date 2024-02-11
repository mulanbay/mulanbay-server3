package cn.mulanbay.pms.web.controller.health;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Treat;
import cn.mulanbay.pms.persistent.domain.TreatDrug;
import cn.mulanbay.pms.persistent.domain.TreatDrugDetail;
import cn.mulanbay.pms.persistent.service.TreatService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.health.drug.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.health.TreatDrugCalendarVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 药品
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/treatDrug")
public class TreatDrugController extends BaseController {

    private static Class<TreatDrug> beanClass = TreatDrug.class;

    /**
     * 分类分组的时长
     * 例：365天则说明统计最近一年内的分组信息
     */
    @Value("${mulanbay.health.categoryDays}")
    int categoryDays;

    @Autowired
    TreatService treatService;

    /**
     * 获取看病用药的疾病分类列表
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(TreatDrugGroupSH sf) {
        try {
            Date endDate = new Date();
            Date startDate = DateUtil.getDate(-categoryDays,endDate);
            sf.setStartDate(startDate);
            sf.setEndDate(endDate);
            List<String> categoryList = treatService.getDrugCateList(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            int i = 0;
            for (String gt : categoryList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt);
                tb.setText(gt);
                list.add(tb);
                i++;
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取看病用药的疾病分类列表异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(TreatDrugSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("endDate", Sort.DESC);
        pr.addSort(s);
        PageResult<TreatDrug> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid TreatDrugForm form) {
        TreatDrug bean = new TreatDrug();
        BeanCopy.copy(form, bean);
        Treat treat = baseService.getObject(Treat.class,form.getTreatId());
        bean.setTreat(treat);
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "drugId") Long drugId) {
        TreatDrug bean = baseService.getObject(beanClass, drugId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid TreatDrugForm form) {
        TreatDrug bean = baseService.getObject(beanClass, form.getDrugId());
        BeanCopy.copy(form, bean);
        Treat treat = baseService.getObject(Treat.class,form.getTreatId());
        bean.setTreat(treat);
        baseService.updateObject(bean);
        return callback(bean);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
       String[] ss = deleteRequest.getIds().split(",");
       for(String s : ss){
           treatService.deleteDrug(Long.valueOf(s));
       }
        return callback(null);
    }

    /**
     * 最近一次的用药
     *
     * @return
     */
    @RequestMapping(value = "/lastDrug", method = RequestMethod.GET)
    public ResultBean lastDrug(@Valid TreatDrugLastSH tdr) {
        TreatDrug bean = treatService.getLastDrug(tdr.getDrugName(),tdr.getUserId());
        return callback(bean);
    }

    /**
     * 用药日历
     *
     * @return
     */
    @RequestMapping(value = "/calendar", method = RequestMethod.GET)
    public ResultBean calendar(@Valid TreatDrugCalendarSH sf) {
        List<TreatDrug> drugList = treatService.getActiveDrugList(sf.getBussDay(),sf.getUserId());
        List<TreatDrugCalendarVo> res = this.initCalendar();
        for(TreatDrug td :drugList){
            //初始化
            switch (td.getPerTimes()){
                case 1:
                    this.addDetail(8,res,td);
                    break;
                case 2:
                    this.addDetail(8,res,td);
                    this.addDetail(17,res,td);
                    break;
                case 3:
                    this.addDetail(8,res,td);
                    this.addDetail(12,res,td);
                    this.addDetail(17,res,td);
                    break;
                default:
                    break;
            }
            //查找用药列表
            List<TreatDrugDetail> detailList = treatService.getDrugDetailList(sf.getBussDay(),sf.getUserId(),td.getDrugId());
            for(TreatDrugDetail dd : detailList){
                int hour = Integer.parseInt(DateUtil.getFormatDate(dd.getOccurTime(),"HH"));
                TreatDrugCalendarVo vo = this.getCalendarVo(hour,res);
                boolean b = vo.appendDetail(dd.getDetailId(),dd.getOccurTime(),td.getDrugId());
                if(!b){
                    //未匹配上,直接添加
                    vo.addDetail(td,dd.getDrug().getDrugId(),dd.getOccurTime(),td.getDrugId());
                }
            }
        }
        return callback(res);
    }

    /**
     * 增加明细
     * @param hour
     * @param voList
     * @param td
     */
    private void addDetail(int hour,List<TreatDrugCalendarVo> voList,TreatDrug td){
        TreatDrugCalendarVo vo = this.getCalendarVo(hour,voList);
        vo.addTreatDrug(td);
    }

    /**
     * 查找TreatDrugCalendarVo
     * @param hour
     * @param voList
     * @return
     */
    private TreatDrugCalendarVo getCalendarVo(int hour,List<TreatDrugCalendarVo> voList){
        int n = voList.size();
        for(int i=0;i<n;i++){
            TreatDrugCalendarVo vo = voList.get(i);
            if(vo.getStartHour()<=hour&&hour<=vo.getEndHour()){
                return vo;
            }
        }
        //默认最后一个
        return voList.get(n-1);
    }

    /**
     * 初始化
     * @return
     */
    private List<TreatDrugCalendarVo> initCalendar(){
        List<TreatDrugCalendarVo> list = new ArrayList<>();
        TreatDrugCalendarVo mov = new TreatDrugCalendarVo("早",0,10);
        list.add(mov);
        TreatDrugCalendarVo miv = new TreatDrugCalendarVo("中",11,13);
        list.add(miv);
        TreatDrugCalendarVo afv = new TreatDrugCalendarVo("晚",14,23);
        list.add(afv);
        return list;
    }

}
