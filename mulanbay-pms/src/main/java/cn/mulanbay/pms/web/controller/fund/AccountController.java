package cn.mulanbay.pms.web.controller.fund;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.BudgetHandler;
import cn.mulanbay.pms.persistent.domain.Account;
import cn.mulanbay.pms.persistent.domain.AccountSnapshot;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.dto.fund.AccountStat;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.AccountService;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.fund.account.*;
import cn.mulanbay.pms.web.bean.req.fund.budget.BudgetSH;
import cn.mulanbay.pms.web.bean.req.main.UserCommonForm;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieData;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieSerieData;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieSerieDetailData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 账户
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private static Class<Account> beanClass = Account.class;

    @Autowired
    AccountService accountService;

    @Autowired
    BudgetHandler budgetHandler;

    @Autowired
    BudgetService budgetService;

    /**
     * 获取账户树
     *
     * @param sf
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(AccountSH sf) {
        try {
            sf.setPage(PageRequest.NO_PAGE);
            PageRequest pr = sf.buildQuery();
            pr.setBeanClass(beanClass);
            Sort s = new Sort("type", Sort.ASC);
            pr.addSort(s);
            List<Account> gtList = baseService.getBeanList(pr);
            List<TreeBean> result = new ArrayList<>();
            String current = gtList.get(0).getTypeName();
            TreeBean typeTreeBean = new TreeBean();
            typeTreeBean.setId("P_" + current);
            typeTreeBean.setText(current);
            int n = gtList.size();
            for (int i = 0; i < n; i++) {
                Account pc = gtList.get(i);
                TreeBean tb = new TreeBean();
                tb.setId(pc.getAccountId());
                tb.setText(pc.getAccountName());
                if (pc.getTypeName().equals(current)) {
                    typeTreeBean.addChild(tb);
                }
                if (!pc.getTypeName().equals(current)) {
                    current = pc.getTypeName();
                    result.add(typeTreeBean);
                    typeTreeBean = new TreeBean();
                    typeTreeBean.setId("P_" + current);
                    typeTreeBean.setText(current);
                    typeTreeBean.addChild(tb);
                }
                if (i == n - 1) {
                    //最后一个
                    result.add(typeTreeBean);
                }
            }
            return callback(result);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取账户树异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(AccountSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("createdTime", Sort.DESC);
        pr.addSort(s);
        PageResult<Account> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid AccountForm bean) {
        Account account = new Account();
        BeanCopy.copy(bean, account);
        accountService.saveAccount(account);
        return callback(null);
    }

    /**
     * 改变
     *
     * @return
     */
    @RequestMapping(value = "/change", method = RequestMethod.POST)
    public ResultBean change(@RequestBody @Valid AccountChangeForm bean) {
        Account account = baseService.getObject(beanClass,bean.getAccountId());
        BigDecimal beforeAmount = account.getAmount();
        account.setAmount(bean.getAfterAmount());
        accountService.updateAccount(account, beforeAmount, bean.getRemark());
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "accountId") Long accountId) {
        Account account = baseService.getObject(beanClass,accountId);
        return callback(account);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid AccountForm bean) {
        Account account = baseService.getObject(beanClass,bean.getAccountId());
        BigDecimal beforeAmount = account.getAmount();
        BeanCopy.copy(bean, account);
        accountService.updateAccount(account, beforeAmount, "用户修改账户余额自动写入");
        return callback(null);
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
            accountService.deleteAccount(Long.valueOf(s),deleteRequest.getUserId());
        }
        return callback(null);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(AccountStatSH as) {
        List<AccountStat> list = this.accountService.statAccount(as.getUserId(), as.getGroupType(),
                as.getType(), as.getStatus(), as.getSnapshotId());
        String title;
        String subTitleSuffix = "";
        if (as.getSnapshotId() == null) {
            title = "实时账户分析";
        } else {
            AccountSnapshot asi = baseService.getObject(AccountSnapshot.class, as.getSnapshotId());
            title = "账户快照分析(" + asi.getSnapshotName() + ")";
            subTitleSuffix = "(" + DateUtil.getFormatDate(asi.getCreatedTime(), DateUtil.FormatDay1) + ")";
        }
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle(title);
        chartPieData.setUnit("元");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("账户");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        for (AccountStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getValue());
            serieData.getData().add(dataDetail);
            totalValue = totalValue.add(bean.getValue());
        }
        String subTitle = "总计：" + NumberUtil.getValue(totalValue,SCALE) + "元";
        subTitle += subTitleSuffix;
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return callback(chartPieData);
    }

    /**
     * 获取当前账号信息
     *
     * @return
     */
    @RequestMapping(value = "/currentInfo", method = RequestMethod.GET)
    public ResultBean currentInfo(UserCommonForm ucf) {
        Long userId = ucf.getUserId();
        BigDecimal currentAccountAmount = accountService.selectAccountAmount(userId);
        Map res = new HashMap<>();
        res.put("currentAccountAmount", currentAccountAmount);
        BudgetSH bs = new BudgetSH();
        bs.setStatus(CommonStatus.ENABLE);
        bs.setPeriod(PeriodType.MONTHLY);
        bs.setUserId(userId);
        PageRequest pr = bs.buildQuery();
        pr.setBeanClass(Budget.class);
        //预算默认以预算列表来统计实现
        List<Budget> budgetList = baseService.getBeanList(pr);
        BigDecimal monthBudget = new BigDecimal(0);
        for (Budget b : budgetList) {
            monthBudget =monthBudget.add(b.getAmount()) ;
        }
        //月度消费以预算来实现
        res.put("lastMonthConsume", monthBudget);
        res.put("lastMonthSalary", 0);
        return callback(res);
    }

    /**
     * 生成快照
     *
     * @return
     */
    @RequestMapping(value = "/createSnapshot", method = RequestMethod.POST)
    public ResultBean createSnapshot(@RequestBody @Valid CreateAccountSnapshotForm bean) {
        Date bussDay = new Date();
        String bussKey = null;
        if(bean.getPeriod()==null){
            //普通快照
            bussKey = DateUtil.getFormatDate(bussDay, DateUtil.Format24Datetime2);
        }else{
            //上个月或者去年
            Date date = null;
            if(bean.getPeriod()==PeriodType.MONTHLY){
                date = DateUtil.getDateMonth(-1,bussDay);
            }else if(bean.getPeriod()==PeriodType.YEARLY){
                date = DateUtil.getDateYear(-1,bussDay);
            }
            bussKey = BussUtil.getBussKey(bean.getPeriod(), date);
        }
        accountService.createSnapshot(bean.getSnapshotName(), bussKey,bean.getPeriod(), bean.getRemark(), bean.getUserId());
        return callback(null);
    }

}
