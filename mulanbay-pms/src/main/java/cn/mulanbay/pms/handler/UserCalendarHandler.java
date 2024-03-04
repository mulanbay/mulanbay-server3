package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.handler.bean.calendar.UserCalendarBean;
import cn.mulanbay.pms.handler.bean.calendar.UserCalendarIdBean;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.calendar.CalendarLogDTO;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import cn.mulanbay.pms.persistent.enums.UserCalendarSource;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import cn.mulanbay.pms.persistent.service.TreatService;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.web.bean.req.data.calendar.UserCalendarListSH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 用户日历处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class UserCalendarHandler extends BaseHandler {

    @Autowired
    UserCalendarService userCalendarService;

    @Autowired
    BudgetService budgetService;

    @Autowired
    BudgetHandler budgetHandler;

    @Autowired
    TreatService treatService;

    @Autowired
    ConsumeService consumeService;

    public UserCalendarHandler() {
        super("用户日历处理");
    }

    /**
     * 产生新的ID，因为页面中需要唯一的ID，对于周期类型的日历在视图中同一个日历有多个记录
     * @param source
     * @param id
     * @return
     */
    private String generateNewId(UserCalendarSource source,Long id){
        return source.name()+"--"+id;
    }

    /**
     * 解析Bean
     * @param id
     * @return
     */
    public UserCalendarIdBean parseId(String id){
        String[] ss = id.split("--");
        UserCalendarIdBean bean = new UserCalendarIdBean();
        bean.setId(Long.valueOf(ss[1]));
        UserCalendarSource source = UserCalendarSource.valueOf(ss[0]);
        bean.setSource(source);
        return bean;
    }

    /**
     * 获取用户日历列表
     *
     * @param sf
     * @return
     */
    public List<UserCalendarBean> getUserCalendarList(UserCalendarListSH sf) {
        List<UserCalendar> culist = userCalendarService.getCurrentUserCalendarList(sf.getUserId(), sf.getName(), sf.getSourceType(), sf.getNeedFinished(), sf.getNeedPeriod(), sf.getStartDate(), sf.getEndDate());
        List<UserCalendarBean> res = new ArrayList<>();
        for (UserCalendar b : culist) {
            PeriodType period = b.getPeriod();
            if (period == PeriodType.ONCE) {
                UserCalendarBean copy = new UserCalendarBean();
                //ID类型不一样，不能copy
                BeanCopy.copy(b, copy);
                copy.setId(this.generateNewId(UserCalendarSource.MANUAL,b.getCalendarId()));
                copy.setExpireTime(b.getBussDay());
                res.add(copy);
            } else {
                Long templateId = b.getTemplateId();
                List<CalendarLogDTO> flowLogs = null;
                if (templateId != null && true == sf.getNeedBandLog()) {
                    //流水日志
                    flowLogs = userCalendarService.getCalendarLogResultList(sf.getUserId(), sf.getStartDate(), sf.getEndDate(), templateId, b.getBindValues());
                }
                Date dd = sf.getStartDate();
                while (dd.before(sf.getEndDate())) {
                    boolean need = false;
                    if (period == PeriodType.DAILY) {
                        need = true;
                    } else if (period == PeriodType.WEEKLY) {
                        String[] pvs = b.getPeriodValues().split(",");
                        int v = DateUtil.getDayIndexInWeek(dd);
                        if (isInPeriodValues(pvs, v)) {
                            need = true;
                        }
                    } else if (period == PeriodType.MONTHLY) {
                        String[] pvs = b.getPeriodValues().split(",");
                        int v = DateUtil.getDayOfMonth(dd);
                        if (isInPeriodValues(pvs, v)) {
                            need = true;
                        }
                    } else if (period == PeriodType.YEARLY) {
                        String md = DateUtil.getFormatDate(dd, "MM-dd");
                        String cd = DateUtil.getFormatDate(b.getBussDay(), "MM-dd");
                        if (md.equals(cd)) {
                            need = true;
                        }
                    }
                    if (need) {
                        UserCalendarBean copy = new UserCalendarBean();
                        BeanCopy.copy(b, copy);
                        copy.setId(this.generateNewId(UserCalendarSource.MANUAL,b.getCalendarId()));
                        //当前时间加上配置的时分秒时间
                        String bd = DateUtil.getFormatDate(dd, DateUtil.FormatDay1);
                        bd += " " + DateUtil.getFormatDate(b.getBussDay(), "HH:mm:ss");
                        Date calDate = DateUtil.getDate(bd, DateUtil.Format24Datetime);
                        copy.setBussDay(calDate);
                        copy.setExpireTime(calDate);
                        copy.setReadOnly(true);
                        //从日历配置模板中加载日历完成情况
                        setCalendarFinishLog(copy, flowLogs);
                        res.add(copy);
                    } else {
                        //时间线对不上的
                        UserCalendarBean copy = getCalendarLogNotMatch(b, dd, flowLogs);
                        if (copy != null) {
                            res.add(copy);
                        }
                    }
                    //加1天
                    dd = DateUtil.getDate(1, dd);
                }
            }
        }
        // 用药日历
        if (sf.getNeedTreatDrug()) {
            res.addAll(getTreatDrugCalendar(sf));
        }
        if (sf.getNeedBudget()) {
            res.addAll(getBudgetCalendar(sf));
        }
        if (sf.getNeedConsume()) {
            res.addAll(getConsumeCalendar(sf));
        }
        return res;
    }

    /**
     * 对于时间对不上的，比如跑步设置为周三，但是实际在周四跑的
     *
     * @param uc
     * @param date
     * @param flowLogs
     * @return
     */
    private UserCalendarBean getCalendarLogNotMatch(UserCalendar uc, Date date, List<CalendarLogDTO> flowLogs) {
        if (StringUtil.isEmpty(flowLogs)) {
            return null;
        } else {
            for (CalendarLogDTO cls : flowLogs) {
                boolean b = DateUtil.isTheSameDay(date, cls.getDate());
                if (b) {
                    UserCalendarBean copy = new UserCalendarBean();
                    BeanCopy.copy(b, copy);
                    copy.setId(this.generateNewId(uc.getSourceType(),uc.getCalendarId()));
                    copy.setBussDay(cls.getDate());
                    copy.setExpireTime(cls.getDate());
                    copy.setReadOnly(true);
                    copy.setFinishType(UserCalendarFinishType.MANUAL);
                    copy.setFinishTime(cls.getDate());
                    copy.setMatch(false);
                    copy.setValue(cls.getValue());
                    copy.setUnit(cls.getUnit());
                    //有些日历的实际内容是具体操作的详情
                    if (StringUtil.isNotEmpty(cls.getName())) {
                        copy.setContent(cls.getName());
                    }
                    return copy;
                }
            }
        }
        return null;
    }

    /**
     * 设置日历完成日志
     * todo 对于时间对不上的，比如跑步设置为周三，但是实际在周四跑的
     *
     * @param copy
     * @param flowLogs
     */
    private void setCalendarFinishLog(UserCalendarBean copy, List<CalendarLogDTO> flowLogs) {
        if (StringUtil.isEmpty(flowLogs)) {
            return;
        } else {
            for (CalendarLogDTO cls : flowLogs) {
                boolean b = DateUtil.isTheSameDay(copy.getBussDay(), cls.getDate());
                if (b) {
                    copy.setFinishType(UserCalendarFinishType.MANUAL);
                    copy.setFinishTime(cls.getDate());
                    copy.setValue(cls.getValue());
                    copy.setUnit(cls.getUnit());
                    copy.setMatch(true);
                    if (StringUtil.isNotEmpty(cls.getName())) {
                        copy.setContent(cls.getName());
                    }
                    return;
                }
            }
        }
    }

    /**
     * 获取用药记录日历
     *
     * @param sf
     * @return
     */
    private List<UserCalendarBean> getTreatDrugCalendar(UserCalendarListSH sf) {
        List<UserCalendarBean> res = new ArrayList<>();
        List<TreatDrug> drugList = treatService.getDrugForCalendar(sf.getUserId(), sf.getName(), sf.getStartDate(), sf.getEndDate());
        for (TreatDrug b : drugList) {
            res.add(generateUserCalendar(b));
        }
        return res;
    }

    /**
     * 获取消费日历(商品)
     *
     * @param sf
     * @return
     */
    private List<UserCalendarBean> getConsumeCalendar(UserCalendarListSH sf) {
        List<UserCalendarBean> res = new ArrayList<>();
        List<Consume> brList = consumeService.getExpectInvalidList(sf.getStartDate(),sf.getEndDate(),sf.getUserId());
        for(Consume br : brList){
            UserCalendarBean vo = this.generateUserCalendar(br);
            res.add(vo);
        }
        return res;
    }

    /**
     * 获取预算日历
     *
     * @param sf
     * @return
     */
    private List<UserCalendarBean> getBudgetCalendar(UserCalendarListSH sf) {
        List<UserCalendarBean> res = new ArrayList<>();
        //todo 预算日历(目前月视图有效)
        List<Budget> budgetList = budgetService.getActiveUserBudget(sf.getUserId(), sf.getName(), true, true);
        long times = sf.getEndDate().getTime() - sf.getStartDate().getTime();
        //与预算判断为中间的时间
        Date bcDate = new Date(sf.getStartDate().getTime() + times / 2);
        for (Budget b : budgetList) {
            Date ept = b.getExpectPaidTime();
            if (b.getPeriod() == PeriodType.ONCE) {
                if (ept.after(sf.getStartDate()) && ept.before(sf.getEndDate())) {
                    res.add(generateUserCalendar(b, bcDate));
                }
            } else if (b.getPeriod() == PeriodType.MONTHLY) {
                res.add(generateUserCalendar(b, bcDate));
            } else if (b.getPeriod() == PeriodType.YEARLY) {
                //月度预算
                String m1 = DateUtil.getFormatDate(bcDate, "MM");
                String m2 = DateUtil.getFormatDate(ept, "MM");
                if (m1.equals(m2)) {
                    res.add(generateUserCalendar(b, bcDate));
                }
            }
        }
        return res;
    }

    private boolean isInPeriodValues(String[] pvs, int v) {
        for (String s : pvs) {
            if (s.equals(String.valueOf(v))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 构建用户日历
     * @param br
     * @return
     */
    private UserCalendarBean generateUserCalendar(Consume br) {
        UserCalendarBean uc = new UserCalendarBean();
        uc.setId(this.generateNewId(UserCalendarSource.CONSUME,br.getConsumeId()));
        uc.setReadOnly(true);
        uc.setAllDay(true);
        uc.setTitle("商品过期");
        uc.setDelays(0);
        uc.setSourceType(UserCalendarSource.CONSUME);
        uc.setSourceId(br.getConsumeId());
        uc.setBussDay(br.getExpectInvalidTime());
        uc.setExpireTime(br.getExpectInvalidTime());
        uc.setContent("商品达到预期报废时间，"+br.getGoodsName());
        return uc;
    }

    /**
     * 构建用户日历
     * @param b
     * @return
     */
    private UserCalendarBean generateUserCalendar(TreatDrug b) {
        UserCalendarBean uc = new UserCalendarBean();
        uc.setId(this.generateNewId(UserCalendarSource.TREAT_DRUG,b.getDrugId()));
        uc.setReadOnly(true);
        uc.setAllDay(true);
        uc.setTitle(b.getDrugName());
        uc.setDelays(0);
        uc.setSourceType(UserCalendarSource.TREAT_DRUG);
        uc.setSourceId(b.getDrugId());
        uc.setBussDay(b.getBeginDate());
        uc.setExpireTime(b.getEndDate());
        uc.setContent("用药方式:每" + b.getPerDay() + "天" + b.getPerTimes() + "次" + ",每次" + b.getEc() + b.getEu() + "," + b.getUseWay());
        return uc;
    }

    /**
     * 构建用户日历
     * @param b
     * @param date
     * @return
     */
    private UserCalendarBean generateUserCalendar(Budget b, Date date) {
        UserCalendarBean uc = new UserCalendarBean();
        uc.setId(this.generateNewId(UserCalendarSource.BUDGET,b.getBudgetId()));
        uc.setReadOnly(true);
        uc.setAllDay(true);
        uc.setTitle(b.getBudgetName());
        uc.setPeriod(b.getPeriod());
        String content = b.getBudgetName() + "预算金额:" + NumberUtil.getValue(b.getAmount(),SCALE) + "元。";
        uc.setDelays(0);
        uc.setSourceType(UserCalendarSource.BUDGET);
        uc.setSourceId(b.getBudgetId());
        String bussKey = BussUtil.getBussKey(b.getPeriod(), date);
        BudgetLog bl = budgetService.selectBudgetLog(bussKey, b.getUserId().longValue(), null, b.getBudgetId());
        Date nextPayTime = budgetHandler.getNextPayTime(b, date);
        if (bl != null) {
            uc.setFinishTime(bl.getBussDay());
            uc.setFinishType(UserCalendarFinishType.MANUAL);
            BigDecimal total = bl.getTrAmount().add(bl.getNcAmount()).add(bl.getBcAmount());
            content += "实际支出金额:" + NumberUtil.getValue(total,SCALE ) + "元,";
            content += "支出时间:" + DateUtil.getFormatDate(bl.getBussDay(), DateUtil.Format24Datetime);
        }
        uc.setBussDay(nextPayTime);
        uc.setExpireTime(nextPayTime);
        uc.setCreatedTime(b.getCreatedTime());
        uc.setContent(content);
        return uc;
    }
}
