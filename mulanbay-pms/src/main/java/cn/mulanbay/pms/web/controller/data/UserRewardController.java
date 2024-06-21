package cn.mulanbay.pms.web.controller.data;

import cn.mulanbay.business.handler.lock.RedisDistributedLock;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.DataHandler;
import cn.mulanbay.pms.handler.bean.data.CommonDataBean;
import cn.mulanbay.pms.persistent.domain.UserReward;
import cn.mulanbay.pms.persistent.dto.reward.UserRewardDateStat;
import cn.mulanbay.pms.persistent.dto.reward.UserRewardSourceStat;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.service.UserRewardService;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.data.reward.UserRewardDateStatSH;
import cn.mulanbay.pms.web.bean.req.data.reward.UserRewardSH;
import cn.mulanbay.pms.web.bean.req.data.reward.UserRewardSourceStatSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/userReward")
public class UserRewardController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserRewardController.class);

    private static Class<UserReward> beanClass = UserReward.class;

    @Autowired
    UserRewardService userRewardService;

    @Autowired
    DataHandler dataHandler;

    @Autowired
    RedisDistributedLock redisDistributedLock;

    /**
     * 获取任列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(UserRewardSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s1 = new Sort("createdTime", Sort.DESC);
        pr.addSort(s1);
        PageResult<UserReward> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        String[] ss = deleteRequest.getIds().split(",");
        Long userId = deleteRequest.getUserId();
        for (String s : ss) {
            UserReward bean = baseService.getObject(beanClass,Long.parseLong(s));
            String key = CacheKey.getKey(CacheKey.REWARD_POINT_LOCK, userId.toString());
            try {
                boolean b = redisDistributedLock.lock(key, 5000L, 3, 20);
                if (!b) {
                    return callbackErrorCode(PmsCode.USER_REWARD_UPDATE_LOCK_FAIL);
                }
                userRewardService.revertUserPoint(bean, "还原原来的积分记录");
            } catch (Exception e) {
                logger.error("更新用户ID=" + userId + "积分异常", e);
            } finally {
                redisDistributedLock.releaseLock(key);
            }
        }
        return callback(null);
    }

    /**
     * 查找源
     *
     * @return
     */
    @RequestMapping(value = "/sourceDetail", method = RequestMethod.GET)
    public ResultBean sourceDetail(@RequestParam(name = "id") Long id) {
        UserReward bean = baseService.getObject(beanClass,id);
        CommonDataBean data = dataHandler.getSourceData(bean.getSource(),bean.getSourceId());
        Map<String,Object> res = new HashMap<>();
        res.put("rewardData",bean);
        res.put("beanData",data);
        return callback(res);
    }

    /**
     * 来源统计
     *
     * @return
     */
    @RequestMapping(value = "/sourceStat", method = RequestMethod.GET)
    public ResultBean sourceStat(@Valid UserRewardSourceStatSH ss) {
        if(ss.getSource()!=null&&ss.getSourceId()!=null){
            return callback(this.createScoreStat(ss));
        }else{
            return callback(this.createSourceStat(ss));
        }
    }

    private String getSeriesName(Short source,Number id){
        if(source==null){
            BussSource bs = BussSource.getSource(id.intValue());
            return bs==null? "未知":bs.getName();
        }else{
            BussSource bs = BussSource.getSource(source.intValue());
            CommonDataBean bean = dataHandler.getSourceData(bs,id.longValue());
            return bean.getTitle();
        }
    }

    /**
     * 基于来源的统计
     * @param ss
     * @return
     */
    private ChartPieData createSourceStat(UserRewardSourceStatSH ss){
        List<UserRewardSourceStat> list = userRewardService.getUserRewardSourceStat(ss);
        GroupType type = ss.getGroupType();
        ChartPieData chartPieData = new ChartPieData();
        if(type==GroupType.COUNT){
            chartPieData.setTitle("用户积分来源统计分析(次数)");
            chartPieData.setUnit("次");
        }else{
            chartPieData.setTitle("用户积分来源统计分析(分数)");
            chartPieData.setUnit("分");
        }
        ChartPieSerieData seriesData = new ChartPieSerieData();
        seriesData.setName("积分");
        for (UserRewardSourceStat bean : list) {
            String name = this.getSeriesName(ss.getSource(),bean.getId());
            chartPieData.getXdata().add(name);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(name);
            if(type==GroupType.COUNT){
                dataDetail.setValue(bean.getTotalCount());
            }else{
                dataDetail.setValue(bean.getTotalRewards());
            }
            seriesData.getData().add(dataDetail);
        }
        chartPieData.getDetailData().add(seriesData);
        return chartPieData;
    }

    /**
     * 基于分数得分的统计
     * @param ss
     * @return
     */
    private ChartPieData createScoreStat(UserRewardSourceStatSH ss){
        List<UserRewardSourceStat> list = userRewardService.getUserRewardScoreStat(ss);
        GroupType type = ss.getGroupType();
        ChartPieData chartPieData = new ChartPieData();
        if(type==GroupType.COUNT){
            chartPieData.setTitle("用户积分分数分析(次数)");
            chartPieData.setUnit("次");
        }else{
            chartPieData.setTitle("用户积分分数分析(分数)");
            chartPieData.setUnit("分");
        }
        ChartPieSerieData seriesData = new ChartPieSerieData();
        seriesData.setName("分数类型");
        for (UserRewardSourceStat bean : list) {
            String name = this.getScoreName(bean.getId());
            chartPieData.getXdata().add(name);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(name);
            if(type==GroupType.COUNT){
                dataDetail.setValue(bean.getTotalCount());
            }else{
                dataDetail.setValue(bean.getTotalRewards());
            }
            seriesData.getData().add(dataDetail);
        }
        chartPieData.getDetailData().add(seriesData);
        return chartPieData;
    }

    private String getScoreName(Number id){
        if(id.intValue()==1){
            return "正分";
        }else if(id.intValue()==-1){
            return "负分";
        }else{
            return "零分";
        }
    }


    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(UserRewardDateStatSH sf) {
        switch (sf.getDateGroupType()) {
            case DAYCALENDAR:
                //日历
                return callback(createChartCalendarData(sf));
            case HOURMINUTE:
                return callback(createScatterChartData(sf));
            default:
                return callback(createDateStatChartData(sf));
        }
    }

    private ScatterChartData createScatterChartData(UserRewardDateStatSH sf){
        //散点图
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        List<Date> dateList = userRewardService.getRewardDateList(sf);
        return ChartUtil.createHMChartData(dateList, "用户积分分析", "时间点");
    }
    /**
     * 按时间属性统计
     *
     * @param sf
     * @return
     */
    private ChartData createDateStatChartData(UserRewardDateStatSH sf) {
        List<UserRewardDateStat> list = userRewardService.getDateStat(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("用户积分统计");
        chartData.setLegendData(new String[]{"分数", "次数"});
        //混合图形下使用
        chartData.addYAxis("分数", "分");
        chartData.addYAxis("次数", "次");
        ChartYData yData1 = new ChartYData("次数", "次");
        ChartYData yData2 = new ChartYData("分数", "分");
        int year = DateUtil.getYear(sf.getEndDate() == null ? new Date() : sf.getEndDate());
        for (UserRewardDateStat bean : list) {
            chartData.getIntXData().add(bean.getDateIndexValue());
            if (sf.getDateGroupType() == DateGroupType.MONTH) {
                chartData.getXdata().add(bean.getDateIndexValue() + "月份");
            } else if (sf.getDateGroupType() == DateGroupType.YEAR) {
                chartData.getXdata().add(bean.getDateIndexValue() + "年");
            } else if (sf.getDateGroupType() == DateGroupType.WEEK) {
                chartData.getXdata().add("第" + bean.getDateIndexValue() + "周");
            } else {
                chartData.getXdata().add(bean.getDateIndexValue().toString());
            }
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalRewards());
        }
        chartData.getYdata().add(yData2);
        //次数放最后
        chartData.getYdata().add(yData1);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    private ChartCalendarData createChartCalendarData(UserRewardDateStatSH sf) {
        List<UserRewardDateStat> list = userRewardService.getDateStat(sf);
        ChartCalendarData calendarData = ChartUtil.createChartCalendarData("用户积分统计", "分数", "分", sf, list);
        calendarData.setTop(3);
        return calendarData;
    }
}
