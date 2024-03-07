package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.pms.persistent.domain.CalendarTemplate;
import cn.mulanbay.pms.persistent.domain.StatBindConfig;
import cn.mulanbay.pms.persistent.domain.UserCalendar;
import cn.mulanbay.pms.persistent.dto.calendar.CalendarLogDTO;
import cn.mulanbay.pms.persistent.dto.report.StatSQLDTO;
import cn.mulanbay.pms.persistent.enums.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
     * 保存日历配置模板
     *
     * @param bean
     * @param configList
     */
    public void saveCalendarTemplate(CalendarTemplate bean, List<StatBindConfig> configList) {
        try {
            this.saveEntity(bean);
            if (StringUtil.isNotEmpty(configList)) {
                for (StatBindConfig c : configList) {
                    c.setFid(bean.getTemplateId());
                    c.setType(StatBussType.PLAN);
                }
                this.saveEntities(configList.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存日历配置模板异常", e);
        }
    }

    /**
     * 删除日历配置模板
     *
     * @param templateId
     * @return
     */
    public void deleteCalendarTemplate(Long templateId) {
        try {
            //删除配置绑定
            String sql = "delete from stat_bind_config where type=?1 and fid=?2";
            this.execSqlUpdate(sql, StatBussType.CALENDAR,templateId);

            //删除用户计划
            String sql2 = "delete from user_calendar where template_id=?1 ";
            this.execSqlUpdate(sql2, templateId);

            //删除模版
            String sql4 = "delete from calendar_template where template_id=?1 ";
            this.execSqlUpdate(sql4, templateId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除日历配置模板异常", e);
        }
    }

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
    public UserCalendar getUserCalendar(Long userId, BussType sourceType,Long sourceId) {
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

    /**
     * 获取用户日历配置列表
     *
     * @return
     */
    public List<CalendarTemplate> getCalendarTemplateList(Integer minLevel) {
        try {
            String hql = "from CalendarTemplate where status=?0 and level<=?1 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,CalendarTemplate.class, CommonStatus.ENABLE, minLevel);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户日历配置列表异常", e);
        }
    }

    /**
     * 获取用户日历的执行流水日志,不分页
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @param templateId
     * @param bindValues
     * @return
     */
    public List<CalendarLogDTO> getCalendarLogResultList(Long userId, Date startDate, Date endDate, Long templateId, String bindValues){
        return this.getCalendarLogResultList(userId,startDate,endDate,templateId,bindValues,NO_PAGE,NO_PAGE_SIZE);
    }

    /**
     * 获取用户日历的执行流水日志,分页
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @param templateId
     * @param bindValues
     * @param page
     * @param pageSize
     * @return
     */
    public List<CalendarLogDTO> getCalendarLogResultList(Long userId, Date startDate, Date endDate, Long templateId, String bindValues,int page,int pageSize) {
        try {
            CalendarTemplate template = this.getEntityById(CalendarTemplate.class, templateId);
            StatSQLDTO sqlDTO = this.assembleSQL(template,userId,bindValues,startDate,endDate);
            String sqlContent = sqlDTO.getSqlContent();
            List<Object[]> rr = null;
            if (template.getSqlType() == SqlType.HQL) {
                rr = this.getEntityListHI(sqlContent,page,pageSize,Object[].class, sqlDTO.getArgArray());
            } else {
                rr = this.getEntityListSI(sqlContent,page,pageSize,Object[].class, sqlDTO.getArgArray());
            }
            List<CalendarLogDTO> res = new ArrayList<>();
            for (Object[] oo : rr) {
                CalendarLogDTO clr = new CalendarLogDTO();
                clr.setDate((Date) oo[0]);
                clr.setValue(oo[1].toString());
                if (oo.length > 2) {
                    clr.setUnit(oo[2] == null ? null : oo[2].toString());
                }
                if (oo.length > 3) {
                    clr.setName(oo[3] == null ? null : oo[3].toString());
                }
                res.add(clr);
            }
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户日历的执行流水日志异常", e);
        }
    }

    /**
     * 计算封装SQL
     * @param template
     * @param userId
     * @param bindValues
     * @return
     */
    protected StatSQLDTO assembleSQL(CalendarTemplate template,Long userId,String bindValues, Date startTime,Date endTime) {
        StatSQLDTO dto = new StatSQLDTO();
        dto.setSqlContent(template.getSqlContent());
        if(StringUtil.isNotEmpty(bindValues)){
            List<StatValueClass> vcs = this.getBindValueClassList(template.getTemplateId(), StatBussType.CALENDAR);
            String[] bs = bindValues.split(",");
            int n = bs.length;
            for(int i=0;i<n;i++){
                dto.addArg(this.formatBindValue(vcs.get(i),bs[i]));
            }
        }
        //肯定绑定userId
        dto.addArg(userId);
        //最后绑定时间
        dto.addArg(startTime);
        dto.addArg(endTime);
        return dto;
    }
}
