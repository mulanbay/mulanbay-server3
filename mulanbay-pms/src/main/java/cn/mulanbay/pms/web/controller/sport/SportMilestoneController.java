package cn.mulanbay.pms.web.controller.sport;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.Sport;
import cn.mulanbay.pms.persistent.domain.SportMilestone;
import cn.mulanbay.pms.persistent.service.ExerciseService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.CommonTreeSH;
import cn.mulanbay.pms.web.bean.req.sport.milestone.SportMilestoneForm;
import cn.mulanbay.pms.web.bean.req.sport.milestone.SportMilestoneRefreshForm;
import cn.mulanbay.pms.web.bean.req.sport.milestone.SportMilestoneSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 运动里程碑
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/sportMilestone")
public class SportMilestoneController extends BaseController {

    private static Class<SportMilestone> beanClass = SportMilestone.class;

    @Autowired
    ExerciseService sportExerciseService;

    /**
     * 获取运动里程碑树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(CommonTreeSH cts) {
        try {
            SportMilestoneSH sf = new SportMilestoneSH();
            sf.setPage(PageRequest.NO_PAGE);
            sf.setUserId(cts.getUserId());
            PageResult<SportMilestone> pr = getResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<SportMilestone> gtList = pr.getBeanList();
            for (SportMilestone gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getMilestoneId());
                tb.setText(gt.getMilestoneName());
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, cts.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取运动里程碑树异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(SportMilestoneSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<SportMilestone> getResult(SportMilestoneSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        PageResult<SportMilestone> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid SportMilestoneForm form) {
        SportMilestone bean = new SportMilestone();
        BeanCopy.copy(form, bean);
        Sport sportType = baseService.getObject(Sport.class,form.getSportId());
        bean.setSport(sportType);
        checkSportMilestone(bean);
        //设置排序号
        Short orderIndex = sportExerciseService.getMaxIndexOfMilestone(form.getSportId(), bean.getMilestoneId());
        if(orderIndex==null){
            orderIndex =1;
        }else{
            orderIndex ++;
        }
        bean.setOrderIndex(orderIndex);
        baseService.saveObject(bean);
        return callback(null);
    }

    private void checkSportMilestone(SportMilestone bean) {
        //1： 判断里程和时间
        if (bean.getValue() == null && bean.getDuration() == null) {
            throw new ApplicationException(PmsCode.SPORT_MILESTONE_KM_MN_NULL);
        } else if (bean.getValue() == null && bean.getDuration() != null) {
            throw new ApplicationException(PmsCode.SPORT_MILESTONE_KM_NULL);
        }
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "milestoneId") Long milestoneId) {
        SportMilestone bean = baseService.getObject(beanClass,milestoneId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid SportMilestoneForm form) {
        SportMilestone bean = baseService.getObject(beanClass,form.getMilestoneId());
        BeanCopy.copy(form, bean);
        Sport sportType = baseService.getObject(Sport.class,form.getSportId());
        bean.setSport(sportType);
        checkSportMilestone(bean);
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


    /**
     * 刷新
     *
     * @return
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResultBean refresh(@RequestBody @Valid SportMilestoneRefreshForm sf) {
        sportExerciseService.updateAndRefreshSportMilestone(sf.isReInit(), sf.getSportId());
        return callback(null);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@Valid SportMilestoneSH sf) {
        PageResult<SportMilestone> pr = this.getResult(sf);
        ChartData chartData = new ChartData();
        if (sf.getSportId() == null) {
            chartData.setTitle("里程碑统计");
        } else {
            Sport sport = baseService.getObject(Sport.class,sf.getSportId());
            chartData.setTitle("[" + sport.getSportName() + "]里程碑统计");
        }
        chartData.setLegendData(new String[]{"花费时长"});
        ChartYData yData1 = new ChartYData("花费时长","天");
        for (SportMilestone bean : pr.getBeanList()) {
            String name = bean.getMilestoneName();
            if (bean.getAlais() != null) {
                name += "(" + bean.getAlais() + ")";
            }
            chartData.getXdata().add(name);
            yData1.getData().add(bean.getCostDays());
        }
        chartData.getYdata().add(yData1);
        return callback(chartData);
    }
}
