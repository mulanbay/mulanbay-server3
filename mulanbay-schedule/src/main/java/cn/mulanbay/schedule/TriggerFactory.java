package cn.mulanbay.schedule;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.schedule.enums.TriggerType;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 调度工厂类
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class TriggerFactory {

    private static final Logger logger = LoggerFactory.getLogger(TriggerFactory.class);

    /**
     * 创建触发器
     *
     * @param ts
     * @return
     */
    public static Trigger createTrigger(TaskTrigger ts) {
        try {
            TriggerType triggerType = ts.getTriggerType();
			/**
			 * 调度的开始执行时间
			 * 该方法主要针对服务器被重启后还是能按照原来的调度模式继续运行，不需要重新设置
			 * 1. 如果没有被执行过为设置的首次执行时间
			 * 2. 如果已经被执行过为下一次执行时间
			 *
			 */
            Date startTime = ts.getNextExecuteTime() ==null ? ts.getFirstExecuteTime() : ts.getNextExecuteTime();
            // todo 每个调度可以定义最后的执行时间，目前TaskTrigger没有定义
            Date endTime = null;
            int interval = ts.getTriggerInterval();
            String tid = ts.getTriggerId().toString();
            switch (triggerType) {
                case SECOND -> {
                    return newTrigger()
                            .withIdentity(tid,ts.getGroupName())
                            .startAt(startTime)
                            .endAt(endTime)
                            .withSchedule( calendarIntervalSchedule().withIntervalInSeconds(interval))
                            .build();
                }
                case MINUTE -> {
                    return newTrigger()
                            .withIdentity(tid,ts.getGroupName())
                            .startAt(startTime)
                            .endAt(endTime)
                            .withSchedule(calendarIntervalSchedule().withIntervalInMinutes(interval))
                            .build();
                }
                case HOUR -> {
                    return newTrigger()
                            .withIdentity(tid,ts.getGroupName())
                            .startAt(startTime)
                            .endAt(endTime)
                            .withSchedule(calendarIntervalSchedule().withIntervalInHours(interval))
							.build();
                }
                case DAY -> {
                    return newTrigger()
                            .withIdentity(tid,ts.getGroupName())
                            .startAt(startTime)
                            .endAt(endTime)
                            .withSchedule(calendarIntervalSchedule().withIntervalInDays(interval))
							.build();
                }
                case WEEK -> {
                    String daysString = ts.getCronExpression();
                    if (daysString == null || "".equals(daysString)) {
                        // 没有设置，则默认星期一
                        daysString = "2";
                    }
                    String[] days = daysString.split(",");
                    Set<Integer> ds = new HashSet<Integer>();
                    for (String d : days) {
                        ds.add(Integer.valueOf(d));
                    }
                    return newTrigger()
                            .withIdentity(tid,ts.getGroupName())
                            .startAt(startTime)
                            .endAt(endTime)
                            .withSchedule(dailyTimeIntervalSchedule().onDaysOfTheWeek(ds))
                            .build();
                }
                case MONTH -> {
                    return newTrigger()
                            .withIdentity(tid,ts.getGroupName())
                            .startAt(startTime)
                            .endAt(endTime)
                            .withSchedule(calendarIntervalSchedule().withIntervalInMonths(interval))
                            .build();
                }
                case YEAR -> {
                    return newTrigger()
                            .withIdentity(tid,ts.getGroupName())
                            .startAt(startTime)
                            .endAt(endTime)
                            .withSchedule(
                                    calendarIntervalSchedule().withIntervalInYears(
                                            interval)).build();
                }
                case CRON -> {
                    return newTrigger()
                            .withIdentity(tid,ts.getGroupName())
							.startAt(startTime)
                            .withSchedule(cronSchedule(ts.getCronExpression()))
                            .build();
                }
                case NOW -> {
                    return newTrigger()
                            .withIdentity(tid,ts.getGroupName())
                            .withSchedule(dailyTimeIntervalSchedule().withRepeatCount(interval))
							.build();
                }
                default -> {
                    logger.warn("未支持的调度类型：" + triggerType);
					return null;
                }
            }
        } catch (Exception e) {
            logger.error(ts.getTriggerName() + "[" + ts.getTriggerId()
                    + "]生成触发器异常", e);
            throw new ApplicationException(ScheduleCode.TRIGGER_CREATE_ERROR, ts.getTriggerName() + "[" + ts.getTriggerId()
                    + "]生成触发器异常", e);
        }
    }

}
