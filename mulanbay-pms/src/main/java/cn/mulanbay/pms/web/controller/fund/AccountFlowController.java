package cn.mulanbay.pms.web.controller.fund;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Account;
import cn.mulanbay.pms.persistent.domain.AccountFlow;
import cn.mulanbay.pms.persistent.dto.fund.AccountFlowSnapshotStat;
import cn.mulanbay.pms.persistent.service.AccountFlowService;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.fund.accountFlow.AccountFlowSH;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户流水
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/accountFlow")
public class AccountFlowController extends BaseController {

    private static Class<AccountFlow> beanClass = AccountFlow.class;

    @Autowired
    AccountFlowService accountFlowService;

    /**
     * 获取列表
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(AccountFlowSH sf) {
        return callbackDataGrid(this.getResult(sf));
    }

    private PageResult<AccountFlow> getResult(AccountFlowSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("createdTime", Sort.DESC);
        pr.addSort(s);
        PageResult<AccountFlow> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 分析
     *
     * @return
     */
    @RequestMapping(value = "/analyse", method = RequestMethod.GET)
    public ResultBean analyse(@Valid AccountFlowSH sf) {
        if (sf.getAccountId() == null) {
            return callback(this.createSnapshotAnalyseChart(sf));
        }
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("createdTime", Sort.ASC);
        pr.addSort(s);
        PageResult<AccountFlow> qr = baseService.getBeanResult(pr);
        ChartData chartData = new ChartData();
        Account account = baseService.getObject(Account.class, sf.getAccountId());
        chartData.setTitle("[" + account.getAccountName() + "]账户变化分析");
        chartData.setUnit("元");
        chartData.setLegendData(new String[]{"账户余额", "账户变化值"});
        //混合图形下使用
        chartData.addYAxis("余额","元");
        chartData.addYAxis("变化值","元");
        ChartYData yData = new ChartYData("账户余额","元");
        ChartYData y2Data = new ChartYData("账户变化值","元");
        for (AccountFlow bean : qr.getBeanList()) {
            chartData.getXdata().add(DateUtil.getFormatDate(bean.getCreatedTime(), DateUtil.FormatDay1));
            yData.getData().add(NumberUtil.getValue(bean.getAfterAmount(),0));
            y2Data.getData().add(NumberUtil.getValue( bean.getAfterAmount().subtract(bean.getBeforeAmount()),0));
        }
        String subTitle = this.getDateTitle(sf);
        chartData.setSubTitle(subTitle);
        chartData.getYdata().add(yData);
        chartData.getYdata().add(y2Data);
        return callback(chartData);
    }

    /**
     * 总账户变化
     * @param sf
     * @return
     */
    private ChartData createSnapshotAnalyseChart(AccountFlowSH sf) {
        List<AccountFlowSnapshotStat> list = accountFlowService.statSnapshot(sf.getUserId(), sf.getStartDate(), sf.getEndDate());
        ChartData chartData = new ChartData();
        chartData.setTitle("总账户变化分析(有效账户)");
        chartData.setUnit("元");
        chartData.setLegendData(new String[]{"账户总额", "账户变化值"});
        //混合图形下使用
        chartData.addYAxis("余额","元");
        chartData.addYAxis("变化值","元");
        ChartYData yData = new ChartYData("账户总额");
        ChartYData y2Data = new ChartYData("账户变化值");
        int n = list.size();
        if (n > 0) {
            //由于生成快照时很难获取上一次的金额，所以其实是没有保存到账户流水中
            BigDecimal before = list.get(0).getAfterAmount();
            for (int i = 0; i < n; i++) {
                AccountFlowSnapshotStat bean = list.get(i);
                String xs=null;
                int l = bean.getBussKey().length();
                if(l<=8){
                    xs =bean.getBussKey();
                }else{
                    xs = bean.getBussKey().substring(0, 8);
                }
                chartData.getXdata().add(xs);
                yData.getData().add(bean.getAfterAmount().doubleValue());
                double s = NumberUtil.getValue(bean.getAfterAmount().subtract(before),0);
                y2Data.getData().add(s);
                before = bean.getAfterAmount();
            }
        }

        String subTitle = this.getDateTitle(sf);
        chartData.setSubTitle(subTitle);
        chartData.getYdata().add(yData);
        chartData.getYdata().add(y2Data);
        return chartData;
    }


    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        baseService.deleteObjects(beanClass, NumberUtil.stringArrayToLongArray(deleteRequest.getIds().split(",")));
        return callback(null);
    }

}
