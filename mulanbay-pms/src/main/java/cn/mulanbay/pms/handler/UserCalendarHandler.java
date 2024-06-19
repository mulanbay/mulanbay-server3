package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.NullType;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.handler.bean.calendar.UserCalendarBean;
import cn.mulanbay.pms.handler.bean.calendar.UserCalendarIdBean;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.calendar.CalendarLogDTO;
import cn.mulanbay.pms.persistent.dto.consume.ConsumeBudgetStat;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import cn.mulanbay.pms.persistent.service.*;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.data.calendar.UserCalendarListSH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Autowired
    BaseService baseService;

    @Autowired
    BehaviorService behaviorService;

    public UserCalendarHandler() {
        super("用户日历处理");
    }

    /**
     * 产生新的ID，因为页面中需要唯一的ID，对于周期类型的日历在视图中同一个日历有多个记录
     * @param source
     * @param id
     * @return
     */
    private String generateNewId(BussSource source, Long id){
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
        BussType source = BussType.valueOf(ss[0]);
        bean.setSource(source);
        return bean;
    }

    /**
     * 获取用户日历列表
     *
     * @param sf
     * @return
     */
    public List<UserCalendarBean> getUserCalendarList(UserCalendarListSH sf) {//不需要完成的，则完成类型非空
        if(!sf.getNeedFinished()){
            sf.setNotFinish(NullType.NOT_NULL);
        }
        if(!sf.getNeedPeriod()){
            sf.setPeriod(PeriodType.ONCE);
        }
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(UserCalendar.class);
        List<UserCalendarBean> culist = baseService.getBeanList(pr);
        List<UserCalendarBean> res = new ArrayList<>();
        for (UserCalendar uc : culist) {
            if(!sf.getNeedExpired()&&uc.getFinishType()==UserCalendarFinishType.EXPIRED){
                continue;
            }
            PeriodType period = uc.getPeriod();
            Long templateId = uc.getTemplateId();
            Map<String,CalendarLogDTO> flowMap = new HashMap<>();
            if (templateId != null && sf.getNeedBandLog()) {
                //流水日志(和行为绑定)
                BehaviorTemplate template = baseService.getObject(BehaviorTemplate.class,templateId);
                List<CalendarLogDTO> flowLogs = behaviorService.getCalendarLogList(sf.getUserId(),sf.getStartDate(),sf.getEndDate(),template,uc.getBindValues(), BaseHibernateDao.NO_PAGE,BaseHibernateDao.NO_PAGE_SIZE);
                for(CalendarLogDTO dto : flowLogs){
                    flowMap.put(DateUtil.getFormatDate(dto.getDate(),DateUtil.FormatDay1),dto);
                }
            }
            if (period == PeriodType.ONCE) {
                //单次的不需要循环
                UserCalendarBean copy = new UserCalendarBean();
                BeanCopy.copy(uc, copy);
                copy.setId(uc.getBussIdentityKey());
                copy.setExpireTime(uc.getBussDay());
                res.add(copy);
                this.setCalendarFinishLog(copy,flowMap);
                continue;
            }
            Date dd = sf.getStartDate();
            while (dd.before(sf.getEndDate())) {
                boolean need = this.checkNeedAdd(uc,dd,period);
                if (need) {
                    UserCalendarBean copy = new UserCalendarBean();
                    BeanCopy.copy(uc, copy);
                    copy.setId(this.generateNewId(BussSource.MANUAL,uc.getCalendarId()));
                    //当前时间加上配置的时分秒时间
                    String bd = DateUtil.getFormatDate(dd, DateUtil.FormatDay1);
                    bd += " " + DateUtil.getFormatDate(uc.getBussDay(), "HH:mm:ss");
                    Date calDate = DateUtil.getDate(bd, DateUtil.Format24Datetime);
                    copy.setBussDay(calDate);
                    copy.setExpireTime(calDate);
                    copy.setReadOnly(true);
                    //从日历配置模板中加载日历完成情况
                    this.setCalendarFinishLog(copy,flowMap);
                    res.add(copy);
                }
                //加1天
                dd = DateUtil.getDate(1, dd);
            }
            //时间线对不上的
            for(CalendarLogDTO dto : flowMap.values()){
                UserCalendarBean copy = new UserCalendarBean();
                BeanCopy.copy(uc, copy);
                copy.setId(this.generateNewId(uc.getSourceType(),uc.getCalendarId()));
                copy.setBussDay(dto.getDate());
                copy.setExpireTime(dto.getDate());
                copy.setReadOnly(true);
                copy.setFinishType(UserCalendarFinishType.MANUAL);
                copy.setFinishTime(dto.getDate());
                copy.setMatch(false);
                copy.setValue(dto.getValue());
                copy.setUnit(dto.getUnit());
                copy.setAllDay(false);
                //有些日历的实际内容是具体操作的详情
                if (StringUtil.isNotEmpty(dto.getName())) {
                    copy.setContent(dto.getName());
                }
                res.add(copy);
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
     * 判断是否需要该日历
     * @param uc
     * @param compareDate
     * @param period
     * @return
     */
    private boolean checkNeedAdd(UserCalendar uc,Date compareDate,PeriodType period){
        //是否需要该日历
        boolean need = false;
        if (period == PeriodType.DAILY) {
            need = true;
        } else if (period == PeriodType.WEEKLY) {
            String[] pvs = uc.getPeriodValues().split(",");
            int v = DateUtil.getDayIndexInWeek(compareDate);
            if (isInPeriodValues(pvs, v)) {
                need = true;
            }
        } else if (period == PeriodType.MONTHLY) {
            String[] pvs = uc.getPeriodValues().split(",");
            int v = DateUtil.getDayOfMonth(compareDate);
            if (isInPeriodValues(pvs, v)) {
                need = true;
            }
        } else if (period == PeriodType.YEARLY) {
            String md = DateUtil.getFormatDate(compareDate, "MM-dd");
            String cd = DateUtil.getFormatDate(uc.getBussDay(), "MM-dd");
            if (md.equals(cd)) {
                need = true;
            }
        }
        return need;
    }

    /**
     * 设置日历完成日志
     *
     * @param copy
     * @param flowMap
     */
    private void setCalendarFinishLog(UserCalendarBean copy, Map<String,CalendarLogDTO> flowMap) {
        String key = DateUtil.getFormatDate(copy.getBussDay(),DateUtil.FormatDay1);
        CalendarLogDTO dto = flowMap.get(key);
        if(dto==null){
            return;
        }
        copy.setFinishType(UserCalendarFinishType.MANUAL);
        copy.setFinishTime(dto.getDate());
        copy.setValue(dto.getValue());
        copy.setUnit(dto.getUnit());
        copy.setMatch(true);
        if (StringUtil.isNotEmpty(dto.getName())) {
            copy.setContent(dto.getName());
        }
        //移除该key
        flowMap.remove(key);
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
            res.add(generateTreatDrugUserCalendar(b));
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
            UserCalendarBean vo = this.generateConsumeUserCalendar(br);
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
                    res.add(generateBudgetUserCalendar(b, bcDate));
                }
            } else if (b.getPeriod() == PeriodType.MONTHLY) {
                res.add(generateBudgetUserCalendar(b, bcDate));
            } else if (b.getPeriod() == PeriodType.YEARLY) {
                //月度预算
                String m1 = DateUtil.getFormatDate(bcDate, "MM");
                String m2 = DateUtil.getFormatDate(ept, "MM");
                if (m1.equals(m2)) {
                    res.add(generateBudgetUserCalendar(b, bcDate));
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
    private UserCalendarBean generateConsumeUserCalendar(Consume br) {
        UserCalendarBean uc = new UserCalendarBean();
        uc.setId(this.generateNewId(BussSource.CONSUME,br.getConsumeId()));
        uc.setReadOnly(true);
        uc.setAllDay(false);
        uc.setTitle("商品过期");
        uc.setDelays(0);
        uc.setSourceType(BussSource.CONSUME);
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
    private UserCalendarBean generateTreatDrugUserCalendar(TreatDrug b) {
        UserCalendarBean uc = new UserCalendarBean();
        uc.setId(this.generateNewId(BussSource.TREAT_DRUG,b.getDrugId()));
        uc.setReadOnly(true);
        uc.setAllDay(true);
        uc.setTitle(b.getDrugName());
        uc.setDelays(0);
        uc.setSourceType(BussSource.TREAT_DRUG);
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
    private UserCalendarBean generateBudgetUserCalendar(Budget b, Date date) {
        UserCalendarBean uc = new UserCalendarBean();
        uc.setId(this.generateNewId(BussSource.BUDGET,b.getBudgetId()));
        uc.setReadOnly(true);
        if(b.getPeriod()==PeriodType.ONCE){
            uc.setAllDay(false);
        }else{
            uc.setAllDay(true);
        }
        uc.setTitle(b.getBudgetName());
        uc.setPeriod(b.getPeriod());
        String content = b.getBudgetName() + "预算金额:" + NumberUtil.getValue(b.getAmount(),SCALE) + "元。";
        uc.setDelays(0);
        uc.setSourceType(BussSource.BUDGET);
        uc.setSourceId(b.getBudgetId());
        ConsumeBudgetStat bs= budgetHandler.getActualAmount(b,date);
        if (bs.getTotalPrice() != null) {
            uc.setFinishTime(bs.getMaxConsumeDate());
            uc.setFinishType(UserCalendarFinishType.MANUAL);
            content += "实际支出金额:" + NumberUtil.getValue(bs.getTotalPrice(),SCALE ) + "元,";
            content += "支出时间:" + DateUtil.getFormatDate(bs.getMaxConsumeDate(), DateUtil.Format24Datetime);
        }
        Date nextPayTime = budgetHandler.getNextPayTime(b, date);
        uc.setBussDay(nextPayTime);
        uc.setExpireTime(nextPayTime);
        uc.setCreatedTime(b.getCreatedTime());
        uc.setContent(content);
        return uc;
    }
}
