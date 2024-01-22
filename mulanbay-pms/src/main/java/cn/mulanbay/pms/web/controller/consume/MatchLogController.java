package cn.mulanbay.pms.web.controller.consume;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Consume;
import cn.mulanbay.pms.persistent.domain.MatchLog;
import cn.mulanbay.pms.persistent.domain.GoodsType;
import cn.mulanbay.pms.persistent.enums.GoodsMatchType;
import cn.mulanbay.pms.web.bean.req.consume.matchLog.MatchLogSH;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.bean.res.consume.matchLog.MatchLogVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消费记录匹配日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/matchLog")
public class MatchLogController extends BaseController {

    private static Class<MatchLog> beanClass = MatchLog.class;

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(MatchLogSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<MatchLog> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 获取比较数据
     *
     * @return
     */
    @RequestMapping(value = "/getCompareData", method = RequestMethod.GET)
    public ResultBean getCompareData(@RequestParam(name = "id") Long id) {
        MatchLogVo vo = new MatchLogVo();
        MatchLog matchLog = baseService.getObject(beanClass,id);
        vo.setMatchLog(matchLog);
        Consume consume = baseService.getObject(Consume.class,matchLog.getConsumeId());
        vo.setConsumeData(consume);
        GoodsMatchType matchType = matchLog.getMatchType();
        if(matchType==GoodsMatchType.CONSUME){
            Consume compareBean = baseService.getObject(Consume.class,matchLog.getCompareId());
            vo.setCompareData(compareBean);
        }else{
            GoodsType compareBean = baseService.getObject(GoodsType.class,matchLog.getCompareId());
            vo.setCompareData(compareBean);
        }
        return callback(vo);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(MatchLogSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        pr.setNeedTotal(false);
        pr.setPage(PageRequest.NO_PAGE);
        Sort sort = new Sort("createdTime", Sort.ASC);
        pr.addSort(sort);
        List<MatchLog> list = baseService.getBeanList(pr);
        ChartData chartData = new ChartData();
        chartData.setTitle("商品自动匹配分析");
        chartData.setLegendData(new String[]{"AI匹配度", "实际匹配度"});
        ChartYData yData = new ChartYData("AI匹配度","");
        ChartYData y2Data = new ChartYData("实际匹配度","");
        for (MatchLog bean : list) {
            chartData.getXdata().add(DateUtil.getFormatDate(bean.getCreatedTime(),DateUtil.Format24Datetime));
            yData.getData().add(NumberUtil.getValue(bean.getAiMatch(),2));
            y2Data.getData().add(NumberUtil.getValue(bean.getAcMatch(),2));
        }
        chartData.getYdata().add(yData);
        chartData.getYdata().add(y2Data);
        return callback(chartData);
    }

}
