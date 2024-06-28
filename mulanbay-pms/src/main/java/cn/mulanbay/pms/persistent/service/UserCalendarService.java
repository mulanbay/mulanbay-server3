package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.pms.persistent.domain.UserCalendar;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.enums.UserCalendarFinishType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author fenghong
 * @create 2018-03-12 21:55
 */
@Service
@Transactional
public class UserCalendarService extends BaseReportService {

    private static final Logger logger = LoggerFactory.getLogger(UserCalendarService.class);

    /**
     * 获取用户日历
     *
     * @param userId
     * @param bussIdentityKey
     * @param expireTime 失效时间
     * @return
     */
    public UserCalendar getUserCalendar(Long userId, String bussIdentityKey, Date expireTime) {
        try {
            String hql = "from UserCalendar where userId =?1 and bussIdentityKey=?2 and expireTime>=?3 ";
            return this.getEntity(hql,UserCalendar.class, userId, bussIdentityKey, expireTime);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR, "获取用户日历异常", e);

        }
    }

    /**
     * 获取用户日历
     *
     * @param userId
     * @param sourceType
     * @param sourceId
     * @return
     */
    public UserCalendar getUserCalendar(Long userId, BussSource sourceType,Long sourceId) {
        try {
            String hql = "from UserCalendar where userId =?1 and sourceType=?2 and sourceId=?3 ";
            return this.getEntity(hql,UserCalendar.class, userId, sourceType, sourceId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR, "获取用户日历异常", e);

        }
    }

    /**
     * 获取用户目前需要的日历
     *
     * @return
     */
    public List<UserCalendar> getCurrentUserCalendarList(Long userId) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("from UserCalendar where expireTime>=?1 and finishTime is null ");
            if (userId != null) {
                sb.append("and userId=" + userId);
            } else {
                sb.append("order by userId");
            }
            return this.getEntityListHI(sb.toString(),NO_PAGE,NO_PAGE_SIZE,UserCalendar.class, new Date());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR, "获取用户目前需要的日历异常", e);

        }
    }

    /**
     * 获取用户目前需要的日历数
     *
     * @return
     */
    public Long getTodayUserCalendarCount(Long userId) {
        try {
            String hql = "select count(0) from UserCalendar where expireTime>=?1 and finishedTime is null and userId=?2 ";

            return this.getCount(hql, new Date(), userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR, "获取用户目前需要的日历数异常", e);

        }
    }


    /**
     * 更新用户日历
     *
     * @param uc
     * @param newDate
     */
    public void updateUserCalendarToDate(UserCalendar uc, Date newDate, Long messageId) {
        try {
            uc.setBussDay(newDate);
            uc.setDelays(uc.getDelays() + 1);
            uc.setMessageId(messageId);
            this.updateEntity(uc);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR, "更新用户日历异常", e);
        }
    }

    /**
     * 添加用户日历
     *
     * @param uc
     */
    public void addUserCalendarToDate(UserCalendar uc) {
        try {
            uc.setDelays(0);
            if (uc.getAllDay() == null) {
                uc.setAllDay(true);
            }
            if (uc.getReadOnly() == null) {
                uc.setReadOnly(false);
            }
            if (uc.getPeriod() == null) {
                uc.setPeriod(PeriodType.ONCE);
            }
            uc.setCreatedTime(new Date());
            this.saveEntity(uc);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR, "添加用户日历异常", e);
        }
    }

    /**
     * 更新用户日历为已经完成
     *
     * @param userId
     * @param bussIdentityKey
     * @param finishTime
     * @param finishType
     */
    public void updateUserCalendarForFinish(Long userId, String bussIdentityKey, Date finishTime, UserCalendarFinishType finishType, Long finishSourceId, BussSource finishSource, Long finishMessageId) {
        try {
            //失效时间需要大于完成时间
            UserCalendar calendar = this.getUserCalendar(userId,bussIdentityKey,finishTime);
            if(calendar==null){
                logger.warn("未找到失效时间大于{},bussIdentityKey={}的日历",finishTime,bussIdentityKey);
                return;
            }
            calendar.setFinishTime(finishTime);
            calendar.setFinishType(finishType);
            calendar.setFinishSourceId(finishSourceId);
            calendar.setFinishSource(finishSource);
            calendar.setFinishMessageId(finishMessageId);
            this.updateEntity(calendar);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR, "更新用户日历为已经完成异常", e);

        }
    }

    /**
     * 更新用户日历为过期
     *
     * @param now
     */
    public int updateUserCalendarForExpired(Date now) {
        try {
            String hql = "update UserCalendar set finishType=?1,modifyTime=?2 " +
                    "where expireTime<=?3 and finishTime is null and finishType is null";
            return this.updateEntities(hql, UserCalendarFinishType.EXPIRED, now, now);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR, "更新用户日历为过期异常", e);

        }
    }

}
