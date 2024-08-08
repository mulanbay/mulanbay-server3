package cn.mulanbay.pms.web.controller.data;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.DataHandler;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.UserCalendarHandler;
import cn.mulanbay.pms.handler.bean.calendar.UserCalendarBean;
import cn.mulanbay.pms.handler.bean.calendar.UserCalendarIdBean;
import cn.mulanbay.pms.handler.bean.data.CommonDataBean;
import cn.mulanbay.pms.persistent.domain.BehaviorTemplate;
import cn.mulanbay.pms.persistent.domain.Message;
import cn.mulanbay.pms.persistent.domain.UserCalendar;
import cn.mulanbay.pms.persistent.dto.calendar.CalendarLogDTO;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import cn.mulanbay.pms.persistent.service.BehaviorService;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.data.calendar.*;
import cn.mulanbay.pms.web.bean.res.data.calendar.UserCalendarSourceVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户日历
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/userCalendar")
public class UserCalendarController extends BaseController {

    private static Class<UserCalendar> beanClass = UserCalendar.class;

    @Autowired
    UserCalendarService userCalendarService;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    UserCalendarHandler userCalendarHandler;

    @Autowired
    NotifyHandler notifyHandler;

    @Autowired
    DataHandler dataHandler;

    @Autowired
    BehaviorService behaviorService;

    /**
     * 获取列表数据
     * @param sf
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(UserCalendarSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("finishType", Sort.ASC);
        pr.addSort(s);
        Sort s2 = new Sort("expireTime", Sort.DESC);
        pr.addSort(s2);
        PageResult<UserCalendar> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 我的日历页面使用
     *
     * @param sf
     * @return
     */
    @RequestMapping(value = "/calendarList", method = RequestMethod.GET)
    public ResultBean calendarList(UserCalendarListSH sf) {
        List<UserCalendarBean> res = userCalendarHandler.getUserCalendarList(sf);
        return callback(res);
    }

    /**
     * 获取绑定的日志列表数据
     * @param sf
     * @return
     */
    @RequestMapping(value = "/flowLogList", method = RequestMethod.GET)
    public ResultBean flowLogList(UserCalendarFlowLogSH sf) {
        UserCalendar bean = baseService.getObject(beanClass,sf.getCalendarId());
        if(bean.getTemplateId()==null){
            return callback(new ArrayList<>());
        }
        BehaviorTemplate template = baseService.getObject(BehaviorTemplate.class,bean.getTemplateId());
        List<CalendarLogDTO> list = behaviorService.getCalendarLogList(sf.getUserId(),sf.getStartDate(),sf.getEndDate(),template,bean.getBindValues(),sf.getPage(),sf.getPageSize());
        return callback(list);
    }


    /**
     * 发送消息
     *
     * @return
     */
    @RequestMapping(value = "/sendCalendarMessage", method = RequestMethod.POST)
    public ResultBean sendCalendarMessage(@RequestBody @Valid SendCalendarMessageForm form) {
        UserCalendarListSH sf = new UserCalendarListSH();
        Date start = DateUtil.getDate(form.getDate(), DateUtil.FormatDay1);
        sf.setStartDate(start);
        sf.setEndDate(DateUtil.tillMiddleNight(form.getCalendarDate()));
        sf.setUserId(form.getUserId());
        sf.setNeedFinished(false);
        sf.setNeedPeriod(true);
        sf.setNeedBudget(true);
        sf.setNeedTreatDrug(true);
        sf.setNeedBandLog(false);
        List<UserCalendarBean> ucList = userCalendarHandler.getUserCalendarList(sf);
        //过滤掉不适合的
        List<UserCalendar> res = new ArrayList<>();
        int sn = DateUtil.getDayOfYear(start);
        for (UserCalendarBean vo : ucList) {
            int cn = DateUtil.getDayOfYear(vo.getBussDay());
            if (sn == cn) {
                UserCalendar uc = new UserCalendar();
                BeanCopy.copy(vo,uc);
                UserCalendarIdBean idBean = userCalendarHandler.parseId(vo.getId());
                uc.setCalendarId(idBean.getId());
                res.add(uc);
            }
        }
        Date now = new Date();
        int n = res.size();
        //int tp = (int)Math.ceil(((n+0.0)/5));
        String title = form.getDate();
        int bd = DateUtil.getIntervalDays(start, now);
        if (bd == 0) {
            title += "(今日)";
        }
        title += "日历行程[共" + n + "条]";
        //每条消息的日历数
        int p = 5;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            UserCalendar uc = res.get(i);
            sb.append((i + 1) + ".");
            sb.append(uc.getTitle());
            if (false == uc.getAllDay()) {
                sb.append("[" + DateUtil.getFormatDate(uc.getBussDay(), "HH:mm") + "]");
            }
            sb.append("\n");
            if ((i + 1) % p == 0 || i == n - 1) {
                //发送消息
                notifyHandler.addMessage(PmsCode.CALENDAR_MESSAGE_NOTIFY, title, sb.toString(), form.getUserId(), now);
                sb = new StringBuffer();
            }
        }
        return callback(null);
    }


    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid UserCalendarForm form) {
        UserCalendar bean = new UserCalendar();
        BeanCopy.copy(form, bean);
        checkUserCalendarData(bean);
        bean.setDelays(0);
        bean.setReadOnly(false);
        //bean.setSourceType(BussSource.MANUAL);
        bean.setBussIdentityKey(BussSource.MANUAL.name()+"_"+StringUtil.genUUID());
        baseService.saveObject(bean);
        return callback(null);
    }

    private void checkUserCalendarData(UserCalendar bean) {
        PeriodType period = bean.getPeriod();
        //默认第一天
        if (period == PeriodType.WEEKLY || period == PeriodType.MONTHLY) {
            if (StringUtil.isEmpty(bean.getPeriodValues())) {
                bean.setPeriodValues("1");
            }
        }
        if (bean.getExpireTime() == null) {
            if (period == PeriodType.ONCE) {
                bean.setExpireTime(DateUtil.tillMiddleNight(new Date()));
            } else {
                //设置最大值
                bean.setExpireTime(DateUtil.getDate("2099-12-31 23:59:59", DateUtil.Format24Datetime));
            }

        }
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "calendarId") Long calendarId) {
        UserCalendar bean = baseService.getObject(beanClass,calendarId);
        return callback(bean);
    }


    /**
     * 查找源
     *
     * @return
     */
    @RequestMapping(value = "/sourceDetail", method = RequestMethod.GET)
    public ResultBean sourceDetail(@RequestParam(name = "calendarId") Long calendarId) {
        UserCalendar bean = baseService.getObject(beanClass,calendarId);
        UserCalendarSourceVo vo = new UserCalendarSourceVo();
        vo.setCalendarData(bean);
        if(bean.getSourceId()!=null){
            CommonDataBean sourceData = dataHandler.getSourceData(bean.getSourceType(),bean.getSourceId());
            vo.setSourceData(sourceData);
        }
        if(bean.getMessageId()!=null){
            Message sourceMessage = baseService.getObject(Message.class,bean.getMessageId());
            vo.setSourceMessage(sourceMessage);
        }
        if(bean.getFinishSourceId()!=null){
            CommonDataBean finishSourceData = dataHandler.getSourceData(bean.getFinishSource(),bean.getFinishSourceId());
            vo.setFinishSourceData(finishSourceData);
        }
        if(bean.getFinishMessageId()!=null){
            Message finishSourceMessage = baseService.getObject(Message.class,bean.getFinishMessageId());
            vo.setFinishSourceMessage(finishSourceMessage);
        }
        return callback(vo);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid UserCalendarForm form) {
        UserCalendar bean = baseService.getObject(beanClass,form.getCalendarId());
        BeanCopy.copy(form, bean);
        checkUserCalendarData(bean);
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 今日行程
     *
     * @return
     */
    @RequestMapping(value = "/todayCalendarList")
    public ResultBean todayCalendarList() {
        List<UserCalendar> list = userCalendarService.getCurrentUserCalendarList(this.getCurrentUserId());
        return callback(list);
    }

    /**
     * 完成今日行程
     *
     * @return
     */
    @RequestMapping(value = "/finish", method = RequestMethod.POST)
    public ResultBean finish(@RequestBody @Valid UserCalendarFinishForm form) {
        UserCalendar bean = baseService.getObject(beanClass,form.getCalendarId());
        if (bean.getFinishTime() != null) {
            return callbackErrorInfo("日程无法被重复完成。");
        }
        bean.setFinishTime(form.getFinishTime());
        bean.setFinishType(UserCalendarFinishType.MANUAL);
        baseService.updateObject(bean);
        //删除缓存ß
        String key = MessageFormat.format(CacheKey.USER_TODAY_CALENDAR_COUNTS, form.getCalendarId());
        cacheHandler.delete(key);
        return callback(null);
    }

    /**
     * 更新时间(页面拖动使用)
     *
     * @return
     */
    @RequestMapping(value = "/updateDate", method = RequestMethod.POST)
    public ResultBean updateDate(@RequestBody @Valid UserCalendarUpdateDateForm form) {
        UserCalendar bean = baseService.getObject(beanClass,form.getCalendarId());
        bean.setBussDay(form.getBussDay());
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 重开
     *
     * @return
     */
    @RequestMapping(value = "/reOpen", method = RequestMethod.POST)
    public ResultBean reOpen(@RequestBody @Valid UserCalendarFinishForm form) {
        UserCalendar bean = baseService.getObject(beanClass,form.getCalendarId());
        Date date = new Date();
        bean.setFinishTime(null);
        bean.setExpireTime(date);
        bean.setBussDay(date);
        bean.setFinishType(null);
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        baseService.deleteObjects(beanClass, NumberUtil.stringToLongArray(deleteRequest.getIds()));
        return callback(null);
    }

}
