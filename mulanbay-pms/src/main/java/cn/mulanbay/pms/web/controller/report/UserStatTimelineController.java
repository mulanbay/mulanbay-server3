package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.UserStat;
import cn.mulanbay.pms.persistent.domain.UserStatTimeline;
import cn.mulanbay.pms.persistent.dto.report.StatResultDTO;
import cn.mulanbay.pms.persistent.dto.report.StatTimelineNameValueStat;
import cn.mulanbay.pms.persistent.service.StatService;
import cn.mulanbay.pms.web.bean.req.report.stat.UserStatTimelineStatSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.mulanbay.persistent.query.PageRequest.NO_PAGE;

/**
 * 用户统计的时间线日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("userStatTimeline")
public class UserStatTimelineController extends BaseController {

    @Autowired
    StatService statService;

    private static Class<UserStatTimeline> beanClass = UserStatTimeline.class;

    /**
     * 值统计
     *
     * @return
     */
    @RequestMapping(value = "/valueStat", method = RequestMethod.GET)
    public ResultBean valueStat(UserStatTimelineStatSH sf) {
       sf.setPage(NO_PAGE);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        List<UserStatTimeline> list = baseService.getBeanList(pr);
        UserStat userStat = baseService.getObject(UserStat.class,sf.getStatId());
        ChartData chartData = new ChartData();
        chartData.setTitle("["+userStat.getTitle()+"]统计值分析");
        chartData.setUnit(userStat.getUnit());
        chartData.setLegendData(new String[]{"统计值"});
        ChartYData yData1 = new ChartYData("统计值",userStat.getUnit());
        for (UserStatTimeline bean : list) {
            StatResultDTO dto = new StatResultDTO();
            dto.setValue(bean.getValue());
            dto.setNameValue(bean.getNameValue());
            dto.setUserStat(bean.getStat());
            dto.calculte();
            String name = DateUtil.getFormatDate(bean.getCreatedTime(),DateUtil.FormatDay1);
            chartData.getXdata().add(name);
            yData1.getData().add(dto.getStatValue());
        }
        chartData.getYdata().add(yData1);
        return callback(chartData);
    }

    /**
     * 名称值统计
     *
     * @return
     */
    @RequestMapping(value = "/nameValueStat", method = RequestMethod.GET)
    public ResultBean nameValueStat(UserStatTimelineStatSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        UserStat userStat = baseService.getObject(UserStat.class,sf.getStatId());
        chartPieData.setTitle("["+userStat.getTitle()+"]名称统计值分析");
        chartPieData.setUnit("个");
        ChartPieSerieData seriesData = new ChartPieSerieData();
        seriesData.setName("名称值");
        List<StatTimelineNameValueStat> list = statService.getTimelineNameValueStat(sf);
        for (StatTimelineNameValueStat bean : list) {
            String name = bean.getName()==null ? "空值":bean.getName();
            chartPieData.getXdata().add(name);
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(name);
            dataDetail.setValue(bean.getTotalCount());
            seriesData.getData().add(dataDetail);
        }
        chartPieData.getDetailData().add(seriesData);
        return callback(chartPieData);
    }
}
