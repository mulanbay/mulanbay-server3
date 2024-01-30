package cn.mulanbay.pms.web.controller.fund;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.BudgetHandler;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.BudgetLog;
import cn.mulanbay.pms.persistent.domain.BudgetSnapshot;
import cn.mulanbay.pms.persistent.domain.Consume;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeBudgetStat;
import cn.mulanbay.pms.persistent.enums.BudgetLogSource;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.consume.consume.ConsumeSH;
import cn.mulanbay.pms.web.bean.req.fund.budgetSnapshot.*;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.bean.res.fund.budget.BudgetChildrenVo;
import cn.mulanbay.pms.web.bean.res.fund.budget.BudgetDetailVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 预算快照
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/budgetSnapshot")
public class BudgetSnapShotController extends BaseController {

    private static Class<BudgetSnapshot> beanClass = BudgetSnapshot.class;

    @Autowired
    BudgetService budgetService;

    @Autowired
    BudgetHandler budgetHandler;

    /**
     * 获取列表，普通模式
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(BudgetSnapshotSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.ASC);
        pr.addSort(sort);
        PageResult<BudgetSnapshot> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 获取列表,查询所有相关联的
     *
     * @return
     */
    @RequestMapping(value = "/detailList", method = RequestMethod.GET)
    public ResultBean detailList(BudgetSnapshotDetailListSH sf) {
        BudgetLog budgetLog = baseService.getObject(BudgetLog.class, sf.getBudgetLogId());
        Date bussDay = budgetLog.getOccurDate();
        String bussKey = budgetLog.getBussKey();
        List<BudgetSnapshot> snapshotList = budgetService.getBudgetSnapshotList(sf.getUserId(),sf.getBudgetLogId());
        List<BudgetDetailVo> res = new ArrayList<>();
        for(BudgetSnapshot bg : snapshotList){
            BudgetDetailVo vo=null;
            switch (bg.getPeriod()){
                case ONCE:
                    if(bg.getGoodsTypeId()!=null){
                        //实时统计
                        vo = this.getDetail(bg,bussDay,bussKey);
                    }else{
                        vo = new BudgetDetailVo();
                        BeanCopy.copy(bg,vo);
                        //查手动日志
                        BudgetLog bl = budgetService.selectBudgetLog(budgetLog.getBussKey(), bg.getUserId(), null, bg.getFromId());
                        if(bl!=null){
                            //有记录
                            vo.setCpPaidTime(bl.getOccurDate());
                            BigDecimal total = bl.getTotalAmount();
                            vo.setCpPaidAmount(total);
                            vo.setSource(bl.getSource());
                        }
                    }
                    break;
                case MONTHLY:
                    if(budgetLog.getPeriod()==PeriodType.YEARLY){
                        //月报，直接空数据,前端以子列表显示
                        vo = this.createDefault(bg);
                    }else{
                        vo = this.getDetail(bg,bussDay,bussKey);
                        vo.setBussKey(budgetLog.getBussKey());
                    }
                    break;
                case YEARLY:
                    vo = this.getDetail(bg,bussDay,bussKey);
                    break;
                default:
                    break;
            }
            //ID号是原来复制过来的Budget编号
            vo.setBudgetId(bg.getFromId());
            //设置快照ID，查询子列表有用
            vo.setSnapshotId(bg.getSnapshotId());
            res.add(vo);
        }
        return callback(res);
    }

    /**
     * 生成默认
     * @param bg
     * @return
     */
    private BudgetDetailVo createDefault(BudgetSnapshot bg){
        BudgetDetailVo vo = new BudgetDetailVo();
        BeanCopy.copyProperties(bg,vo);
        vo.setBudgetId(bg.getFromId());
        vo.setHasChild(true);
        return vo;
    }
    /**
     * 获取子列表
     *
     * @return
     */
    @RequestMapping(value = "/getChildren", method = RequestMethod.GET)
    public ResultBean getChildren(BudgetSnapshotChildrenSH sf) {
        BudgetSnapshot snapshot = baseService.getObject(beanClass,sf.getSnapshotId());
        BudgetLog bl = baseService.getObject(BudgetLog.class,snapshot.getBudgetLogId());
        /**
         * 1.父类是年度快照，子类是每月预算，那么查询当期年度所有该月度预算的列表（正常情况下有12条）
         * 2.父类是年度快照，子类是每天预算，那么查询当期年度所有该天预算的列表（正常情况下有365-366条）
         * 3.父类是月度快照，子类是每天预算，那么查询当期月份所有该天预算的列表（正常情况下有28-31条）
         * 目前只实现第一种
         */
        PeriodType parentPeriod = bl.getPeriod();
        if(parentPeriod==PeriodType.YEARLY){
            List<BudgetSnapshot> snapshotList = budgetService.getMonthBudgetSnapshotList(sf.getUserId(),snapshot.getFromId(),bl.getBussKey());
            BudgetChildrenVo res = new BudgetChildrenVo();
            BigDecimal budgetAmount = new BigDecimal(0);
            BigDecimal paidAmount = new BigDecimal(0);
            for(BudgetSnapshot ss : snapshotList){
                Date bussDay = budgetHandler.getBussDay(ss.getBussKey());
                BudgetDetailVo child = this.getDetail(ss,bussDay,ss.getBussKey());
                BigDecimal b = child.getCpPaidAmount()==null ? new BigDecimal(0):child.getCpPaidAmount();
                budgetAmount=budgetAmount.add(child.getAmount());
                paidAmount=paidAmount.add(b);
                res.addChild(child);
            }
            res.setBudgetAmount(budgetAmount);
            res.setCpPaidAmount(paidAmount);
            res.setBussKey(bl.getBussKey());
            res.setBudgetName(snapshot.getBudgetName());
            if(sf.isNeedChart()){
                String title = "["+res.getBudgetName()+"]"+res.getBussKey()+"执行统计";
                ChartData chartData = this.createChartData(title,res.getChildren());
                res.setChartData(chartData);
            }
            return callback(res);
        }
        return callback(null);
    }

    private ChartData createChartData(String title,List<BudgetDetailVo> list){
        ChartData chartData = new ChartData();
        chartData.setTitle(title);
        chartData.setLegendData(new String[]{"预算(元)","花费(元)","比例(%)"});
        //混合图形下使用
        chartData.addYAxis("金额","元");
        chartData.addYAxis("比例","%");
        ChartYData yData1 = new ChartYData();
        yData1.setName("预算(元)");
        ChartYData yData2 = new ChartYData();
        yData2.setName("花费(元)");
        ChartYData yData3 = new ChartYData();
        yData3.setName("比例(%)");
        for (BudgetDetailVo bean : list) {
            chartData.addXData(bean.getBussKey());
            yData1.getData().add(NumberUtil.getValue(bean.getAmount(),SCALE));
            if(bean.getCpPaidTime()==null){
                yData2.getData().add("0");
            }else{
                yData2.getData().add(NumberUtil.getValue(bean.getCpPaidAmount(),SCALE));
            }
            yData3.getData().add(Math.round(bean.getRate().doubleValue()));
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData3);
        return chartData;
    }

    /**
     * 获取子列表
     *
     * @return
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public ResultBean history(BudgetSnapshotHistorySH sf) {
        Budget budget = baseService.getObject(Budget.class,sf.getBudgetId());
        sf.setStartBussKey(budgetHandler.createBussKey(budget.getPeriod(),sf.getStartDate()));
        sf.setEndBussKey(budgetHandler.createBussKey(budget.getPeriod(),sf.getEndDate()));
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("bussKey", Sort.DESC);
        pr.addSort(sort);
        PageResult<BudgetSnapshot> qr = baseService.getBeanResult(pr);
        Map res = new HashMap<>();
        List<BudgetDetailVo> list = new ArrayList<>();
        for (BudgetSnapshot bg : qr.getBeanList()) {
            if(bg.getBussKey().length()==4){
                //在年度里面不需要统计
                BudgetDetailVo bdb = this.createDefault(bg);
                list.add(bdb);
            }else{
                Date bussDay = budgetHandler.getBussDay(bg.getBussKey());
                BudgetDetailVo bdb = this.getDetail(bg,bussDay,bg.getBussKey());
                list.add(bdb);
            }
        }
        res.put("beanList",list);
        res.put("maxRow",qr.getMaxRow());
        res.put("page",qr.getPage());
        res.put("pageSize",qr.getPageSize());
        if(sf.isNeedChart()){
            String title = "["+budget.getBudgetName()+"]历史执行统计";
            ChartData chartData = this.createChartData(title,list);
            res.put("chartData",chartData);
        }
        return callback(res);
    }

    /**
     * 获取详情
     * @param bg
     * @param bussDay
     * @return
     */
    private BudgetDetailVo getDetail(BudgetSnapshot bg,Date bussDay,String bussKey ){
        BudgetDetailVo bdb = new BudgetDetailVo();
        BeanCopy.copyProperties(bg, bdb);
        if (bg.getGoodsTypeId()!=null) {
            //查询预算实际支付
            Budget budget = new Budget();
            BeanCopy.copyProperties(bg,budget);
            ConsumeBudgetStat bs= budgetHandler.getActualAmount(budget,bussDay);
            if (bs.getTotalPrice() != null) {
                bdb.setCpPaidTime(bs.getMaxConsumeDate());
                bdb.setCpPaidAmount(bs.getTotalPrice());
            }
            bdb.setSource(BudgetLogSource.REAL_TIME);
        }else{
            //查询日志
            BudgetLog bl = budgetService.selectBudgetLog(bussKey,bg.getUserId(),null,bg.getFromId());
            if (bl != null) {
                bdb.setCpPaidTime(bl.getOccurDate());
                bdb.setCpPaidAmount(bl.getTotalAmount());
                bdb.setSource(bl.getSource());
            }
        }
        bdb.setBudgetId(bg.getFromId());
        return bdb;
    }

    /**
     * 获取详情列表
     * @param sf
     * @return
     */
    private PageResult<BudgetDetailVo> getBudgetDetail(BudgetSnapshotSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.ASC);
        pr.addSort(sort);
        PageResult<BudgetSnapshot> qr = baseService.getBeanResult(pr);
        BudgetLog budgetLog = baseService.getObject(BudgetLog.class, sf.getBudgetLogId());
        PageResult<BudgetDetailVo> res = new PageResult<>(qr);
        List<BudgetDetailVo> list = new ArrayList<>();
        Date bussDay = budgetLog.getOccurDate();
        String bussKey = budgetLog.getBussKey();
        for (BudgetSnapshot bg : qr.getBeanList()) {
            BudgetDetailVo bdb = this.getDetail(bg,bussDay,bussKey);
            list.add(bdb);
        }
        res.setBeanList(list);
        return res;
    }

    /**
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(BudgetSnapshotSH sf) {
        PageResult<BudgetDetailVo> res = this.getBudgetDetail(sf);
        BudgetLog budgetLog = baseService.getObject(BudgetLog.class, sf.getBudgetLogId());
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle(budgetLog.getBussKey() + "预算实际消费分析");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("花费");
        //总的值
        BigDecimal total = budgetLog.getTotalAmount();
        BigDecimal other = total.subtract(new BigDecimal(0));
        for (BudgetDetailVo bg : res.getBeanList()) {
            BigDecimal cpPaidAmount = bg.getCpPaidAmount();
            if (cpPaidAmount != null) {
                chartPieData.getXdata().add(bg.getBudgetName());
                ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
                dataDetail.setName(bg.getBudgetName());
                dataDetail.setValue(NumberUtil.getValue(cpPaidAmount,SCALE));
                serieData.getData().add(dataDetail);
                other = other.subtract(cpPaidAmount);
            }
        }
        //看病
        chartPieData.getXdata().add("看病花费");
        ChartPieSerieDetailData trDetail = new ChartPieSerieDetailData();
        trDetail.setName("看病花费");
        trDetail.setValue(NumberUtil.getValue( budgetLog.getTrAmount(),SCALE));
        serieData.getData().add(trDetail);
        other = other.subtract(budgetLog.getTrAmount());

        //饮食成本
        Date[] ds = budgetHandler.getDateRange(budgetLog.getPeriod(), budgetLog.getOccurDate(), true);
        //BigDecimal dietPrice = dietService.getTotalPrice(sf.getUserId(), ds[0], ds[1]);
        //todo
        BigDecimal dietPrice = new BigDecimal(0d);
        chartPieData.getXdata().add("饮食花费");
        ChartPieSerieDetailData dietDetail = new ChartPieSerieDetailData();
        dietDetail.setName("饮食花费");
        dietDetail.setValue(NumberUtil.getValue(dietPrice,SCALE));
        serieData.getData().add(dietDetail);
        other = other.subtract(dietPrice);

        //其他
        chartPieData.getXdata().add("其他花费");
        ChartPieSerieDetailData otherDetail = new ChartPieSerieDetailData();
        otherDetail.setName("其他花费");
        otherDetail.setValue(NumberUtil.getValue(other,SCALE));
        serieData.getData().add(otherDetail);

        String subTitle = "预算金额:" + NumberUtil.getValue(budgetLog.getBudgetAmount(),SCALE) + "元,实际总消费:" + NumberUtil.getValue(total,SCALE) + "元";
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);

        return callback(chartPieData);
    }

    /**
     * 获取消费记录
     *
     * @return
     */
    @RequestMapping(value = "/consume", method = RequestMethod.GET)
    public ResultBean consume(BudgetSnapshotConsumeSH sf) {
        BudgetSnapshot snapshot = baseService.getObject(beanClass,sf.getSnapshotId());
        Date[] ds = this.getDateRange(snapshot);
        ConsumeSH brs = new ConsumeSH();
        brs.setUserId(sf.getUserId());
        brs.setPage(sf.getPage());
        brs.setPageSize(sf.getPageSize());
        brs.setStartDate(ds[0]);
        brs.setEndDate(ds[1]);
        brs.setGoodsTypeId(snapshot.getGoodsTypeId());
        brs.setTags(snapshot.getTags());
        PageRequest pr = brs.buildQuery();
        Sort s = new Sort("buyTime", Sort.DESC);
        pr.addSort(s);
        pr.setBeanClass(Consume.class);
        PageResult<Consume> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    private Date[] getDateRange(BudgetSnapshot snapshot ){
        Date bussDay = budgetHandler.getBussDay(snapshot.getBussKey());
        BudgetLog bl = baseService.getObject(BudgetLog.class,snapshot.getBudgetLogId());
        /**
         * 周期需要使用父类的周期
         * 假如snapshot是月度类型预算，但是该记录是在年度预算统计里面，需要查询的是整个年度的数据
         */
        Date[] ds = budgetHandler.getDateRange(bl.getPeriod(), bussDay, true);
        return ds;
    }

}
