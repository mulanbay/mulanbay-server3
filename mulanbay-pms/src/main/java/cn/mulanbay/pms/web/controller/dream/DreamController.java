package cn.mulanbay.pms.web.controller.dream;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.Dream;
import cn.mulanbay.pms.persistent.domain.DreamDelay;
import cn.mulanbay.pms.persistent.dto.dream.DreamStat;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.enums.DreamStatus;
import cn.mulanbay.pms.persistent.service.DreamService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.dream.DreamDelaySH;
import cn.mulanbay.pms.web.bean.req.dream.DreamForm;
import cn.mulanbay.pms.web.bean.req.dream.DreamSH;
import cn.mulanbay.pms.web.bean.req.dream.DreamStatSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 梦想
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/dream")
public class DreamController extends BaseController {

    @Autowired
    DreamService dreamService;

    @Autowired
    CacheHandler cacheHandler;

    private static Class<Dream> beanClass = Dream.class;

    /**
     * 获取梦想列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(DreamSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort(sf.getSortField(), sf.getSortType());
        pr.addSort(s);
        PageResult<Dream> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid DreamForm form) {
        Dream bean = new Dream();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "dreamId") Long dreamId) {
        Dream bean = baseService.getObject(beanClass,dreamId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid DreamForm form) {
        if (form.getStatus() == DreamStatus.FINISHED && form.getFinishDate() == null) {
            return callbackErrorCode(PmsCode.DREAM_FININSH_DATE_NULL);
        }
        Dream dbData = baseService.getObject(beanClass,form.getDreamId());
        DreamDelay delay = null;
        long daysChange = form.getExpectDate().getTime() - dbData.getExpectDate().getTime();
        if (daysChange > 0) {
            //延期
            int delays = dbData.getDelays() == null ? 0 : dbData.getDelays();
            dbData.setDelays(delays + 1);
            delay = new DreamDelay();
            delay.setDream(dbData);
            delay.setUserId(form.getUserId());
            delay.setFromDate(dbData.getExpectDate());
            delay.setToDate(form.getExpectDate());

        }
        BeanCopy.copy(form, dbData);
        dreamService.updateDream(dbData,delay);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        Long[] ids = NumberUtil.stringToLongArray(deleteRequest.getIds());
        for (Long id : ids) {
            dreamService.deleteDream(id);
        }
        return callback(null);
    }

    /**
     * 统计页面进入
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(DreamStatSH sf) {
        return getChartData(sf);
    }

    private ResultBean getChartData(DreamStatSH sf) {
        List<DreamStat> list = dreamService.getDreamStat(sf);
        if (sf.getChartType() == ChartType.PIE) {
            return callback(createStatPieData(list, sf));
        } else {
            return callback(createStatBarData(list, sf));
        }
    }

    /**
     * 饼图数据
     *
     * @param list
     * @param sf
     * @return
     */
    private ChartPieData createStatPieData(List<DreamStat> list, DreamStatSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("梦想统计");
        chartPieData.setUnit("个");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("梦想");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        for (DreamStat bean : list) {
            String name = this.getStatName(sf.getGroupType(), bean);
            chartPieData.getXdata().add(name);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(name);
            dataDetail.setValue(bean.getTotalCount().intValue());
            serieData.getData().add(dataDetail);
            totalValue = totalValue.add(new BigDecimal(bean.getTotalCount().intValue()));
        }
        String subTitle = ChartUtil.getDateTitle(sf, String.valueOf(totalValue.longValue()) + "个");
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }

    /**
     * 柱状图数据
     *
     * @param list
     * @param sf
     * @return
     */
    private ChartData createStatBarData(List<DreamStat> list, DreamStatSH sf) {
        ChartData chartData = new ChartData();
        chartData.setTitle("梦想统计");
        chartData.setUnit("个");
        chartData.setLegendData(new String[]{"类别"});
        ChartYData yData = new ChartYData();
        yData.setName("类别");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        for (DreamStat bean : list) {
            chartData.getXdata().add(getStatName(sf.getGroupType(), bean));
            yData.getData().add(bean.getTotalCount().intValue());
            totalValue = totalValue.add(new BigDecimal(bean.getTotalCount().intValue()));
        }
        chartData.getYdata().add(yData);
        String subTitle = ChartUtil.getDateTitle(sf, String.valueOf(totalValue.longValue()) + "个");
        chartData.setSubTitle(subTitle);
        return chartData;

    }

    private String getStatName(DreamStatSH.GroupType groupType, DreamStat dd) {
        if (groupType == DreamStatSH.GroupType.DIFFICULTY) {
            return "困难等级(" + dd.getId() + ")";
        } else if (groupType == DreamStatSH.GroupType.IMPORTANT) {
            return "重要等级(" + dd.getId().doubleValue() + ")";
        } else if (groupType == DreamStatSH.GroupType.STATUS) {
            return "状态(" + DreamStatus.getDreamStatus(dd.getId().intValue()).getName() + ")";
        } else {
            return null;
        }
    }


    /**
     * 获取梦想延迟列表
     *
     * @return
     */
    @RequestMapping(value = "/delayList", method = RequestMethod.GET)
    public ResultBean delayList(DreamDelaySH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(DreamDelay.class);
        PageResult<DreamDelay> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

}
