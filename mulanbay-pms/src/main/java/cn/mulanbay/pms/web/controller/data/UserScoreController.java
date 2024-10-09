package cn.mulanbay.pms.web.controller.data;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.UserScoreHandler;
import cn.mulanbay.pms.persistent.domain.UserScore;
import cn.mulanbay.pms.persistent.domain.UserScoreDetail;
import cn.mulanbay.pms.persistent.dto.score.UserScorePointsCompareDTO;
import cn.mulanbay.pms.persistent.enums.ChartType;
import cn.mulanbay.pms.persistent.service.UserScoreService;
import cn.mulanbay.pms.web.bean.req.data.score.SelfJudgeForm;
import cn.mulanbay.pms.web.bean.req.data.score.UserScorePointsCompareSH;
import cn.mulanbay.pms.web.bean.req.data.score.UserScoreReSaveForm;
import cn.mulanbay.pms.web.bean.req.score.UserScoreSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户评分
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/userScore")
public class UserScoreController extends BaseController {

    private static Class<UserScore> beanClass = UserScore.class;

    @Autowired
    UserScoreHandler userScoreHandler;

    @Autowired
    UserScoreService userScoreService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(UserScoreSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("endTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<UserScore> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "scoreId") Long scoreId) {
        UserScore bean = baseService.getObject(beanClass,scoreId);
        return callback(bean);
    }

    /**
     * 积分详情
     *
     * @return
     */
    @RequestMapping(value = "/scoreDetail", method = RequestMethod.GET)
    public ResultBean scoreDetail(@RequestParam(name = "scoreId") Long scoreId) {
        List<UserScoreDetail> list = userScoreService.selectDetailList(scoreId);
        return callback(list);
    }

    /**
     * 用户自评
     *
     * @return
     */
    @RequestMapping(value = "/selfJudge", method = RequestMethod.POST)
    public ResultBean selfJudge(@RequestBody @Valid SelfJudgeForm jlr) {
        List<UserScoreDetail> list = userScoreHandler.calcUseScore(jlr.getUserId(), jlr.getScoreGroupId(), new Date());
        return callback(list);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@Valid UserScoreSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("endTime", Sort.ASC);
        pr.addSort(sort);
        pr.setPage(-1);
        List<UserScore> list = baseService.getBeanList(pr);
        if (sf.getChartType() == ChartType.LINE) {
            return callback(this.createLineStatChartData(list,sf.getUserId(),sf.getPredict()));
        } else {
            return callback(this.createPieStatChartData(list));
        }
    }

    /**
     * 饼图模式
     *
     * @param list
     * @return
     */
    private ChartPieData createPieStatChartData(List<UserScore> list) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("用户评分统计分析");
        chartPieData.setUnit("次");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("次数");
        Map<String, Integer> map = new HashMap<>();
        int totalScore = 0;
        int total = 0;
        for (UserScore bean : list) {
            int x = bean.getScore() / 10;
            String key = x * 10 + "-" + (x + 1) * 10 + "分";
            Integer n = map.get(key);
            if (n == null) {
                map.put(key, 1);
            } else {
                map.put(key, n + 1);
            }
            totalScore += bean.getScore();
            total++;
        }
        for (String key : map.keySet()) {
            chartPieData.getXdata().add(key);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(key);
            dataDetail.setValue(map.get(key));
            serieData.getData().add(dataDetail);
        }
        String subTitle = "平均分：" + NumberUtil.getAvg(totalScore, total, 1);
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }

    /**
     * 折现图模式
     *
     * @param list
     * @return
     */
    private ChartData createLineStatChartData(List<UserScore> list,Long userId,Boolean predict) {
        ChartData chartData = new ChartData();
        chartData.setTitle("用户评分统计分析");
        chartData.setUnit("分");
        chartData.setLegendData(new String[]{"分数","预测"});
        ChartYData yData1 = new ChartYData("分数","分");
        ChartYData yData2 = new ChartYData("预测","分");
        int totalScore = 0;
        int total = 0;
        int lastScore =0;
        for (UserScore bean : list) {
            chartData.getXdata().add(DateUtil.getFormatDate(bean.getEndTime(), DateUtil.FormatDay1));
            yData1.getData().add(bean.getScore());
            if(predict){
                int predictScore = userScoreHandler.predict(userId,lastScore);
                yData2.getData().add(predictScore);
            }
            totalScore += bean.getScore();
            total++;
            lastScore = bean.getScore();
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        String subTitle = "平均分：" + NumberUtil.getAvg(totalScore, total, 1);
        chartData.setSubTitle(subTitle);
        return chartData;
    }

    /**
     * 评分分析
     * 雷达图
     *
     * @return
     */
    @RequestMapping(value = "/analyse", method = RequestMethod.GET)
    public ResultBean analyse(@RequestParam(name = "scoreId") Long scoreId) {
        UserScore userScore = baseService.getObject(beanClass,scoreId);
        List<UserScoreDetail> list = userScoreService.selectDetailList(scoreId);
        ChartRadarData chartRadarData = new ChartRadarData();
        String date = DateUtil.getFormatDate(userScore.getEndTime(),DateUtil.FormatDay1);
        chartRadarData.setTitle("["+date+"]评分分析");
        chartRadarData.addLegend("评分");
        ChartRadarSerieData serieData = new ChartRadarSerieData();
        serieData.setName("评分");
        long total = 0L;
        long scTotal = 0L;
        for(UserScoreDetail usd : list){
            chartRadarData.addIndicatorData(usd.getScoreConfig().getConfigName(),usd.getScoreConfig().getMaxScore().longValue());
            serieData.addData(usd.getScore().longValue());
            total+=usd.getScore().longValue();
            scTotal+=usd.getScoreConfig().getMaxScore().longValue();
        }
        chartRadarData.setSubTitle("总得分:"+total+" (满分:"+scTotal+")");
        chartRadarData.addSerie(serieData);
        return callback(chartRadarData);
    }

    /**
     * 重新保存
     *
     * @return
     */
    @RequestMapping(value = "/reSave", method = RequestMethod.POST)
    public ResultBean reSave(@RequestBody @Valid UserScoreReSaveForm sf) {
        Date d = sf.getStartDate();
        while (!d.after(sf.getEndDate())) {
            Date[] dates = userScoreHandler.getDays(d);
            userScoreHandler.saveUseScoreAsync(sf.getUserId(), dates[0], dates[1], true);
            d = DateUtil.getDate(1, d);
        }
        return callback(null);
    }

    /**
     * 积分和评分比对
     *
     * @return
     */
    @RequestMapping(value = "/compare", method = RequestMethod.GET)
    public ResultBean compare(@Valid UserScorePointsCompareSH sf) {
        List<UserScorePointsCompareDTO> list = userScoreService.scorePointsCompare(sf.getUserId(), sf.getStartDate(), sf.getEndDate());
        ChartData chartData = new ChartData();
        chartData.setTitle("用户评分与积分比较");
        chartData.setLegendData(new String[]{"评分", "积分"});
        chartData.addYAxis("评分", "分");
        chartData.addYAxis("积分", "分");
        ChartYData yData1 = new ChartYData("评分","分");
        ChartYData yData2 = new ChartYData("积分","分");
        for (UserScorePointsCompareDTO bean : list) {
            chartData.getXdata().add(bean.getDate().toString());
            yData1.getData().add(bean.getScore().intValue());
            yData2.getData().add(bean.getPoints().intValue());
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        return callback(chartData);
    }

}
