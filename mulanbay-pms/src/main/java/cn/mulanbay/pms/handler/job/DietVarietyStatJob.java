package cn.mulanbay.pms.handler.job;

import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.DietHandler;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.persistent.domain.DietVarietyLog;
import cn.mulanbay.pms.persistent.enums.DietType;
import cn.mulanbay.pms.persistent.service.DietService;
import cn.mulanbay.pms.web.bean.req.food.diet.DietVarietySH;
import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.enums.JobResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 饮食多样性统计
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class DietVarietyStatJob extends AbstractBaseJob {

    private static final Logger logger = LoggerFactory.getLogger(DietVarietyStatJob.class);

    private DietVarietyStatJobPara para;

    private DietService dietService;

    private BaseService baseService;

    private DietHandler dietHandler;

    private NotifyHandler notifyHandler;

    @Override
    public TaskResult doTask() {
        TaskResult tr = new TaskResult();
        dietService = BeanFactoryUtil.getBean(DietService.class);
        baseService = BeanFactoryUtil.getBean(BaseService.class);
        dietHandler = BeanFactoryUtil.getBean(DietHandler.class);
        notifyHandler = BeanFactoryUtil.getBean(NotifyHandler.class);
        Date bussDay = this.getBussDay();
        Date endDate = DateUtil.tillMiddleNight(bussDay);
        Date startDate = DateUtil.getDate(0 - para.getDays(), bussDay);
        List<Long> userIds = dietService.getUserIdList(startDate, endDate);
        if (StringUtil.isEmpty(userIds)) {
            tr.setResult(JobResult.SKIP);
            tr.setComment("没有需要统计的用户");
        } else {
            for (Long userId : userIds) {
                stat(startDate, endDate, userId, DietType.BREAKFAST);
                stat(startDate, endDate, userId, DietType.LUNCH);
                stat(startDate, endDate, userId, DietType.DINNER);
                stat(startDate, endDate, userId, null);
            }
            tr.setResult(JobResult.SUCCESS);
            tr.setComment("统计了" + userIds.size() + "个用户");
        }
        return tr;
    }

    private void stat(Date startDate, Date endDate, Long userId, DietType dietType) {
        try {
            DietVarietySH sf = new DietVarietySH();
            sf.setUserId(userId);
            sf.setStartDate(startDate);
            sf.setEndDate(endDate);
            if (dietType != null) {
                sf.setDietType((short) dietType.ordinal());
            }
            sf.setOrderByField(para.getOrderByField());
            float v = dietHandler.getFoodsAvgSim(sf);
            //发送消息
            String sv = String.valueOf(NumberUtil.getValue(v * 100,0));
            String dietTypeName = dietType == null ? "" : dietType.getName();
            String title = dietTypeName + "多样性统计";
            String ds = DateUtil.getFormatDate(startDate, DateUtil.FormatDay1) + "~" + DateUtil.getFormatDate(endDate, DateUtil.FormatDay1);
            String content = ds + dietTypeName + "的重复度为:" + sv + "%";
            notifyHandler.addNotifyMessage(PmsCode.DIET_VARIETY_STAT, title, content,
                    userId, null);
            //加入统计历史
            addStatLog(startDate, endDate, userId, dietType, v);
        } catch (Exception e) {
            logger.error("饮食多样性统计异常", e);
        }
    }

    private void addStatLog(Date startDate, Date endDate, Long userId, DietType dietType, float v) {
        try {
            DietVarietyLog log = new DietVarietyLog();
            log.setUserId(userId);
            log.setDietType(dietType);
            log.setStartDate(startDate);
            log.setEndDate(endDate);
            log.setVariety((double) v);
            log.setCreatedTime(new Date());
            log.setRemark("调度统计自动生成");
            baseService.saveObject(log);
        } catch (Exception e) {
            logger.error("增加饮食多样性统计日志异常", e);
        }
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        ParaCheckResult pcr = new ParaCheckResult();
        para = this.getTriggerParaBean();
        return pcr;
    }

    @Override
    public Class getParaDefine() {
        return DietVarietyStatJobPara.class;
    }
}
