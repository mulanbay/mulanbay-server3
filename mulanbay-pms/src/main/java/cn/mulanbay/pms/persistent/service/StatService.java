package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.StatBindConfig;
import cn.mulanbay.pms.persistent.domain.StatTemplate;
import cn.mulanbay.pms.persistent.domain.UserStat;
import cn.mulanbay.pms.persistent.domain.UserStatRemind;
import cn.mulanbay.pms.persistent.dto.report.StatResultDTO;
import cn.mulanbay.pms.persistent.dto.report.StatSQLDTO;
import cn.mulanbay.pms.persistent.dto.report.StatTimelineNameValueStat;
import cn.mulanbay.pms.persistent.enums.*;
import cn.mulanbay.pms.web.bean.req.report.stat.UserStatTimelineStatSH;
import cn.mulanbay.schedule.enums.TriggerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.mulanbay.pms.common.Constant.EXTRA_SQL_RPC;

@Service
@Transactional
public class StatService extends BaseReportService {

    private static final Logger logger = LoggerFactory.getLogger(StatService.class);

    /**
     * 统计列表
     *
     * @param userId
     * @return
     */
    public List<StatResultDTO> getStatResultList(Long userId) {
        try {
            List<StatResultDTO> results = new ArrayList<>();
            String hql = "from UserStat where status=?1 and userId=?2 order by orderIndex ";
            //获取用户的统计列表
            List<UserStat> userStatList = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,UserStat.class, CommonStatus.ENABLE,userId);
            if (userStatList.isEmpty()) {
                return results;
            } else {
                for (UserStat nc : userStatList) {
                    StatResultDTO result = this.getStatResult(nc);
                    if (result != null) {
                        results.add(result);
                    }
                }
            }
            return results;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计列表异常", e);
        }
    }

    /**
     * 获取提醒的统计结果
     *
     * @param userStat
     * @return
     */
    public StatResultDTO getStatResult(UserStat userStat) {
        try {
            StatTemplate template = userStat.getTemplate();
            ResultType resultType = template.getResultType();
            StatSQLDTO sqlDTO = this.assembleSQL(userStat);
            List<Object[]> rr = null;
            if (template.getSqlType() == SqlType.HQL) {
                rr = this.getEntityListHI(sqlDTO.getSqlContent(),NO_PAGE,NO_PAGE_SIZE,Object[].class,sqlDTO.getArgArray());
            } else {
                rr = this.getEntityListSI(sqlDTO.getSqlContent(),NO_PAGE,NO_PAGE_SIZE,Object[].class,sqlDTO.getArgArray());
            }
            if (!rr.isEmpty()) {
                Object[] rs = rr.get(0);
                Object value = rs[0];
                StatResultDTO dto = new StatResultDTO();
                if (value != null) {
                    switch (resultType){
                        case DATE -> {
                            dto.setValue(((Date) value).getTime());
                        }
                        case NUMBER -> {
                            dto.setValue(Long.valueOf(value.toString()));
                        }
                        case DATE_NAME -> {
                            dto.setValue(((Date) value).getTime());
                            Object nameValue = rs[1];
                            dto.setNameValue(nameValue == null ? "" : nameValue.toString());
                        }
                        case NUMBER_NAME -> {
                            dto.setValue(Long.valueOf(value.toString()));
                            Object nameValue = rs[1];
                            dto.setNameValue(nameValue == null ? "" : nameValue.toString());
                        }
                    }
                }else{
                    logger.warn("统计出" + template.getTemplateName() + "的结果为空");
                }
                dto.setUserStat(userStat);
                //计算
                dto.calculte();
                return dto;
            } else {
                StatResultDTO dto = new StatResultDTO();
                dto.setNameValue("--");
                dto.setUserStat(userStat);
                return dto;
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计提醒[" + userStat.getTitle() + "]异常", e);
        }
    }

    /**
     * 计算封装SQL
     *
     * @param un
     * @return
     */
    protected StatSQLDTO assembleSQL(UserStat un) {
        StatSQLDTO dto = new StatSQLDTO();
        StatTemplate template = un.getTemplate();
        dto.setSqlContent(template.getSqlContent());
        String bindValues = un.getBindValues();
        //肯定绑定userId
        dto.addArg(un.getUserId());
        return this.appendExtraBindSQL(template.getTemplateId(), StatBussType.STAT,bindValues,template.getParas(),dto);
    }

    /**
     * @param statId
     * @param userId
     * @return
     */
    public UserStatRemind getUserStatRemind(Long statId, Long userId) {
        try {
            String hql = "from UserStatRemind where stat.statId=?1 and userId=?2";
            return this.getEntity(hql,UserStatRemind.class, statId, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "查找用户提醒异常", e);
        }
    }

    /**
     * 更新最后提醒时间
     *
     * @param remindId
     */
    public void updateLastRemindTime(Long remindId, Date date) {
        try {
            String hql = "update UserStatRemind set lastRemindTime=?1 where remindId=?2";
            this.updateEntities(hql, date, remindId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新最后提醒时间异常", e);
        }
    }


    /**
     * 获取需要提醒的用户提醒列表
     *
     * @return
     */
    public List<UserStat> getNeedRemindUserStat() {
        try {
            String hql = "from UserStat where status=?1 and remind=?2 and userId>0 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,UserStat.class, CommonStatus.ENABLE, true);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要提醒的用户提醒列表异常", e);
        }
    }

    /**
     * 获取提醒配置列表
     *
     * @return
     */
    public List<StatTemplate> getStatTemplateList(Integer minLevel) {
        try {
            String hql = "from StatTemplate where status=?1 and level<=?2 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,StatTemplate.class, CommonStatus.ENABLE, minLevel);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取提醒配置列表异常", e);
        }
    }

    /**
     * 获取提醒配置
     * 需要根据用户级别判断
     *
     * @return
     */
    public StatTemplate getStatTemplate(Long templateId, Integer userLevel) {
        try {
            String hql = "from StatTemplate where templateId=?1 and level<=?2 ";
            return this.getEntity(hql,StatTemplate.class, templateId, userLevel);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取提醒配置列表异常", e);
        }
    }

    /**
     * 获取提醒配置
     * 需要根据用户级别判断
     *
     * @return
     */
    public void saveOrUpdateUserStat(UserStat bean) {
        try {
            if(bean.getStatId()==null){
                this.saveEntity(bean);
            }else{
                this.updateEntity(bean);
            }
            //检查提醒配置
            UserStatRemind remind = this.getUserStatRemind(bean.getStatId(), bean.getUserId());
            if (remind == null) {
                //生成默认
                remind = new UserStatRemind();
                remind.setOverRate(80);
                remind.setRemark("由表单页面自动生成");
                remind.setRemindTime("08:30");
                remind.setTriggerInterval(1);
                remind.setTriggerType(TriggerType.DAY);
                remind.setUserId(bean.getUserId());
                remind.setStat(bean);
                this.saveEntity(remind);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取提醒配置列表异常", e);
        }
    }

    /**
     * 保存统计模板
     * @param bean
     * @param bindList
     */
    public void saveStatTemplate(StatTemplate bean,List<StatBindConfig> bindList) {
        try {
            this.saveEntity(bean);
            if(StringUtil.isNotEmpty(bindList)){
                for(StatBindConfig c : bindList) {
                    c.setFid(bean.getTemplateId());
                    c.setType(StatBussType.STAT);
                }
                this.saveEntities(bindList.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存提醒配置模板异常", e);
        }
    }

    /**
     * 删除模版
     *
     * @param templateId
     */
    public void deleteStatTemplate(Long templateId) {
        try {
            //删除配置绑定
            String sql = "delete from stat_bind_config where type=?1 and fid=?2";
            this.execSqlUpdate(sql, StatBussType.STAT,templateId);

            //删除用户统计时间线
            String sql3 = "delete from user_stat_timeline where stat_id in (select stat_id from user_stat where template_id=?1) ";
            this.execSqlUpdate(sql3, templateId);

            //删除用户统计
            String sql2 = "delete from user_stat where template_id=?1 ";
            this.execSqlUpdate(sql2, templateId);

            //删除模版
            String sql4 = "delete from stat_template where template_id=?1 ";
            this.execSqlUpdate(sql4, templateId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除阅读记录异常", e);
        }
    }

    /**
     * 删除用户统计
     *
     * @param statId
     */
    public void deleteUserStat(Long statId) {
        try {
            //删除用户统计时间线
            String sql1 = "delete from user_stat_timeline where stat_id=?1 ";
            this.execSqlUpdate(sql1, statId);

            //删除用户统计时间线
            String sql2 = "delete from user_stat_remind where stat_id=?1 ";
            this.execSqlUpdate(sql2, statId);

            //删除用户统计
            String sql3 = "delete from user_stat where stat_id=?1 ";
            this.execSqlUpdate(sql3, statId);

        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除阅读记录异常", e);
        }
    }


    /**
     * 查找用户统计的提醒
     *
     * @param statId
     */
    public UserStatRemind getUserStatRemind(Long statId) {
        try {
            String hql = "from UserStatRemind where stat.statId=?1 ";
            return this.getEntity(hql,UserStatRemind.class, statId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "查找用户统计的提醒异常", e);
        }
    }

    /**
     * 获取最大排序号
     *
     * @param bussType
     */
    public Short getTemplateMaxOrderIndex(BussType bussType) {
        try {
            String hql = "select max(orderIndex) from StatTemplate where bussType=?1 ";
            Object o =  this.getEntity(hql,Object.class, bussType);
            return o==null? null : Short.parseShort(o.toString());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最大排序号异常", e);
        }
    }

    /**
     * 时间线名称值统计
     *
     * @param sf
     * @return
     */
    public List<StatTimelineNameValueStat> getTimelineNameValueStat(UserStatTimelineStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select nameValue as name,count(0) as totalCount from UserStatTimeline
                    {query_para}
                    group by nameValue
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<StatTimelineNameValueStat> list = this.getEntityListHI(statSql, NO_PAGE,NO_PAGE_SIZE, StatTimelineNameValueStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "时间线名称值统计异常", e);
        }
    }


}
