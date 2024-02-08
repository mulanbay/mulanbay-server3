package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.Exercise;
import cn.mulanbay.pms.persistent.domain.Sport;
import cn.mulanbay.pms.persistent.domain.SportMilestone;
import cn.mulanbay.pms.persistent.dto.sport.*;
import cn.mulanbay.pms.persistent.enums.BestType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.NextMilestoneType;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.sport.exercise.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 锻炼
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class ExerciseService extends BaseHibernateDao {

    /**
     * 获取按时间的统计
     *
     * @param sf
     * @return
     */
    public List<ExerciseDateStat> getDateStat(ExerciseDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,sum(value) as totalValue,count(0) as totalCount,sum(max_heart_rate) as totalMaxHeartRate,
                    sum(avg_heart_rate) as totalAvgHeartRate,sum(speed) as totalSpeed,sum(duration) as totalDuration,sum(pace) as totalPace
                    from ( select {date_group_field} as indexValue,value,duration,speed,pace,max_heart_rate,avg_heart_rate from exercise
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("exercise_time", dateGroupType))
                    .replace("{query_para}",pr.getParameterString());
            List<ExerciseDateStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, ExerciseDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "锻炼统计异常", e);
        }
    }

    /**
     * 获取锻炼的总的统计
     *
     * @param sf
     * @return
     */
    public ExerciseStat getStat(ExerciseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql = "select count(*) as totalCount,sum(duration) as totalDuration,sum(value) as totalValue from exercise";
            sql += pr.getParameterString();
            List<ExerciseStat> list = this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE, ExerciseStat.class, pr.getParameterValue());
            return list.get(0);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "锻炼统计异常", e);
        }
    }

    /**
     * 获取锻炼的多重统计：最大、最小、平均
     *
     * @param sf
     * @return
     */
    public ExerciseMultiStat getMultiStat(ExerciseMultiStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql = """
                    select sport_id as sportId,
                    max(value) as 'maxValue',
                    max(duration) as maxDuration,
                    max(speed) as maxSpeed,
                    max(max_speed) as maxMaxSpeed,
                    min(pace) as maxPace,
                    min(max_pace) as maxMaxPace,
                    max(max_heart_rate) as maxMaxHeartRate,
                    max(avg_heart_rate) as maxAvgHeartRate,
                    
                    min(value) as 'minValue',
                    min(duration) as minDuration,
                    min(speed) as minSpeed,
                    min(max_speed) as minMaxSpeed,
                    max(pace) as minPace,
                    max(max_pace) as minMaxPace,
                    min(max_heart_rate) as minMaxHeartRate,
                    min(avg_heart_rate) as minAvgHeartRate,
                    
                    avg(value) as 'avgValue',
                    avg(duration) as avgDuration,
                    avg(speed) as avgSpeed,
                    avg(max_speed) as avgMaxSpeed,
                    avg(pace) as avgPace,
                    avg(max_pace) as avgMaxPace,
                    avg(max_heart_rate) as avgMaxHeartRate,
                    avg(avg_heart_rate) as avgAvgHeartRate
                    from exercise
                    {query_para}
                    group by sport_id
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString());
            ExerciseMultiStat data = null;
            List<ExerciseMultiStat> list = this.getEntityListSI(sql, NO_PAGE, NO_PAGE_SIZE, ExerciseMultiStat.class, pr.getParameterValue());
            if (list.isEmpty()) {
                data = new ExerciseMultiStat();
                data.setSportId(sf.getSportId());
                return data;
            } else {
                data = list.get(0);
                if (sf.getSportId() != null) {
                    Sport sport = this.getEntityById(Sport.class, sf.getSportId());
                    data.setUnit(sport.getUnit());
                }
            }
            return data;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取锻炼的多重统计：最大、最小、平均异常", e);
        }
    }

    /**
     * 根据最大统计值获取锻炼信息
     *
     * @param sf
     * @return
     */
    public Long getExerciseIdByMultiStat(ExerciseByMultiStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String method = "";
            if (sf.getType() == ExerciseByMultiStatSH.Type.MAX) {
                method = "max";
                if (sf.getGroupType() == GroupType.MAX_PACE || sf.getGroupType() == GroupType.PACE) {
                    //pace正好相反
                    method = "min";
                }
            } else {
                method = "min";
                if (sf.getGroupType() == GroupType.MAX_PACE || sf.getGroupType() == GroupType.PACE) {
                    //pace正好相反
                    method = "max";
                }
            }
            String paraString = pr.getParameterString();
            StringBuffer sb = new StringBuffer();
            sb.append("select exerciseId from Exercise");
            sb.append(paraString);
            sb.append(" and " + sf.getGroupType().getField() + " =");
            sb.append("(select " + method + "(" + sf.getGroupType().getField() + ") from Exercise ");
            PageRequest pr2 = sf.buildQuery();
            //下标需要重新计算
            pr2.setFirstIndex(pr.getNextIndex());
            sb.append(pr2.getParameterString() + ") ");
            List newArgs = new ArrayList();
            //需要两遍，因为前面的条件在父查询、子查询中都有包含
            newArgs.addAll(pr.getParameterValueList());
            newArgs.addAll(pr2.getParameterValueList());
            List<Long> list = this.getEntityListHI(sb.toString(), sf.getIndex(), 1,Long.class, newArgs.toArray());
            if(list.isEmpty()){
                return null;
            }else{
                return list.get(0);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取锻炼的最大值的统计异常", e);
        }
    }

    /**
     * 获取最大的里程碑ID
     *
     * @param sportId
     * @param selfMilestoneId 除去自己的
     * @return
     */
    public Short getMaxIndexOfMilestone(Long sportId, Long selfMilestoneId) {
        try {
            String sql = "select max(orderIndex) from SportMilestone where sport.sportId = ?1 ";
            if (selfMilestoneId != null) {
                sql += "and milestoneId !=" + selfMilestoneId;
            }
            Short r = this.getEntity(sql,Short.class, sportId);
            return r;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "锻炼统计异常", e);
        }
    }

    /**
     * 保存锻炼，且更新里程碑(目前仅针对新增操作)
     *
     * @param list
     * @param updateMilestone
     */
    public void saveExerciseList(List<Exercise> list, boolean updateMilestone) {
        for (Exercise se : list) {
            this.saveExercise(se, updateMilestone);
        }
    }

    /**
     * 保存锻炼，且更新里程碑(目前仅针对新增操作)
     *
     * @param bean
     * @param updateMilestone
     */
    public void saveExercise(Exercise bean, boolean updateMilestone) {
        try {
            boolean isCreate = true;
            if (bean.getExerciseId() == null) {
                this.saveEntity(bean);
            } else {
                isCreate = false;
                this.updateEntity(bean);
            }
            this.updateBest(bean, isCreate);
            if (isCreate && updateMilestone) {
                updateAndRefreshSportMilestone(false, bean.getSport().getSportId());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存锻炼异常", e);
        }
    }

    /**
     * 达到的里程碑，不管以前的锻炼是否已经有实现过
     *
     * @param exerciseId
     */
    public List<SportMilestone> getAchieveMilestones(Long exerciseId) {
        try {
            Exercise se = this.getEntityById(Exercise.class, exerciseId);
            String hql = "from SportMilestone where ((value<=?1 and duration=0) or (value<=?2 and duration>=?3 and duration>0)) and sport.sportId=?4 order by orderIndex desc";
            List<SportMilestone> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,SportMilestone.class, se.getValue(), se.getValue(), se.getDuration(), se.getSport().getSportId());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "保存锻炼异常", e);
        }
    }

    /**
     * 锻炼类型列表
     *
     * @param userId
     */
    public List<Sport> getSportList(Long userId) {
        try {
            String hql = "from Sport where userId=?1 order by orderIndex desc";
            List<Sport> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Sport.class, userId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "锻炼类型列表异常", e);
        }
    }

    /**
     * 获取下一个待达到的里程碑
     *
     * @param exerciseId
     * @param type
     */
    public Long getNextAchieveMilestoneId(Long exerciseId, NextMilestoneType type) {
        try {
            Exercise se = this.getEntityById(Exercise.class, exerciseId);
            if (null == type || type == NextMilestoneType.CURRENT) {
                //当前该锻炼需要实现的下一个里程碑
                String hql = "select max(orderIndex) from SportMilestone where ((value<=?1 and duration=0) or (value<=?2 and duration>=?3 and duration>0)) and sport.sportId=?4 ";
                Short orderIndex = this.getEntity(hql,Short.class, se.getValue(), se.getValue(), se.getDuration(), se.getSport().getSportId());
                if (orderIndex == null) {
                    //没有，则取第一个
                    orderIndex = 1;
                } else {
                    orderIndex++;
                }
                String hql2 = "select milestoneId from SportMilestone where orderIndex=?1 and sport.sportId=?2 ";
                return this.getEntity(hql2,Long.class, orderIndex, se.getSport().getSportId());
            } else {
                //针对所有,未实现就可以了
                String hql = "select max(orderIndex) from SportMilestone where exercise.exerciseId !=null and sport.sportId=?1 ";
                Short orderIndex = this.getEntity(hql,Short.class, se.getSport().getSportId());
                if (orderIndex == null) {
                    //没有，则取第一个
                    orderIndex = 1;
                } else {
                    orderIndex++;
                }
                String hql2 = "select milestoneId from SportMilestone where orderIndex=?1 and sport.sportId=?2 ";
                return this.getEntity(hql2,Long.class, orderIndex, se.getSport().getSportId());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取下一个待达到的里程碑异常", e);
        }
    }

    /**
     * 更新最佳状态
     *
     * @param bean
     * @param isCreate
     */
    private void updateBest(Exercise bean, boolean isCreate) {
        try {
            boolean needUpdate = false;
            //获取最佳的（采用平均速度，最大的速度意义不大）,跟以前的比，除了个人录入原因外，实际上当前录入的可能都是最新的
            String sql = "select max(value) as 'maxValue',max(speed) as maxSpeed from exercise where sport_id=?1 and exercise_time<?2 ";
            String addtion = "";
            if (!isCreate) {
                addtion = " and exercise_id != " + bean.getExerciseId();
            }
            List<ExerciseMultiStat> list = this.getEntityListSI(sql + addtion, 1, 1, ExerciseMultiStat.class, bean.getSport().getSportId(), bean.getExerciseTime());
            ExerciseMultiStat data = list.get(0);
            BigDecimal mv = data.getMaxValue();
            if (mv == null || mv.doubleValue() < bean.getValue()) {
                //说明现在的是最佳,把以前的都更新为历史
                String sql2 = "update exercise set value_best=?1 where value_best=?2 and sport_id=?3 ";
                this.execSqlUpdate(sql2 + addtion, BestType.ONCE.ordinal(), BestType.CURRENT.ordinal(), bean.getSport().getSportId());
                needUpdate = true;
                bean.setValueBest(BestType.CURRENT);
            }
            BigDecimal speed = data.getMaxSpeed();
            if (speed == null || speed.doubleValue() < bean.getSpeed()) {
                //说明现在的是最佳,把以前的都更新为历史
                String hql = "update exercise set fast_best=?1 where fast_best=?2 and sport_id=?3 ";
                this.execSqlUpdate(hql + addtion, BestType.ONCE.ordinal(), BestType.CURRENT.ordinal(), bean.getSport().getSportId());
                needUpdate = true;
                bean.setFastBest(BestType.CURRENT);
            }

            if (needUpdate) {
                this.updateEntity(bean);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新最佳状态异常", e);
        }
    }

    /**
     * 初始化里程碑(由于配置文件中事务的规则以方法名做前缀，因此这里需要特别注意)
     *
     * @param reInit
     * @param sportId
     */
    public void updateAndRefreshSportMilestone(boolean reInit, Long sportId) {
        try {
            if (reInit) {
                //全部初始化
                String sql = "update sport_milestone set exercise_id = null,start_time = null,end_time=null,cost_days=null where sport_id= ?1 ";
                this.execSqlUpdate(sql, sportId);
            }
            //获取没有完成的里程碑
            String hql = "from SportMilestone where sport.sportId =?1 and exercise.exerciseId is null order by orderIndex";
            List<SportMilestone> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,SportMilestone.class, sportId);
            for (SportMilestone sm : list) {
                idpUpdateAndRefreshMilestone(sm);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "初始化里程碑异常", e);
        }
    }

    /**
     * 重新刷新锻炼的最佳统计数据
     *
     * @param sportId
     */
    public void updateAndRefreshExerciseMaxStat(Long sportId) {
        try {
            //获取没有完成的里程碑
            String hql = "from Exercise where sport.sportId =?1 order by exerciseTime";
            List<Exercise> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Exercise.class, sportId);
            for (Exercise sm : list) {
                updateBest(sm, false);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "重新刷新锻炼的最佳统计数据异常", e);
        }
    }

    /**
     * 独立出来希望能做单独事务
     *
     * @param sm
     */
    public void idpUpdateAndRefreshMilestone(SportMilestone sm) {
        try {
            String hh = "from Exercise where sport.sportId =?1 and value>=?2 ";
            if (sm.getDuration() > 0) {
                hh += " and duration<=" + sm.getDuration();
            }
            hh += " order by exerciseTime asc";
            Exercise se = this.getEntity(hh,Exercise.class, sm.getSport().getSportId(), sm.getValue());
            if (se != null) {
                // 再获取花费天数
                Date fromDate = this.getPreviousMilestoneExercise(sm, se.getExerciseId());
                Date toDate = se.getExerciseTime();
                int costDays = DateUtil.getIntervalDays(fromDate, toDate);
                sm.setExercise(se);
                sm.setStartTime(fromDate);
                sm.setEndTime(se.getExerciseTime());
                sm.setCostDays(costDays);
                this.updateEntity(sm);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新里程碑的锻炼异常", e);
        }
    }


    /**
     * 获取上一个实现的里程碑的锻炼(不能和当前的一样)
     *
     * @param milestone
     * @param exerciseId
     * @return
     */
    public Date getPreviousMilestoneExercise(SportMilestone milestone, Long exerciseId) {
        try {
            String hql = "from SportMilestone where sport.sportId =?1 and exercise.exerciseId !=null and exercise.exerciseId !=?2 order by orderIndex desc";
            SportMilestone sm = this.getEntity(hql,SportMilestone.class, milestone.getSport().getSportId(), exerciseId);
            if (sm == null) {
                //获取第一个
                String hh = "from Exercise where sport.sportId =?1 order by exerciseTime asc";
                Exercise se = this.getEntity(hh,Exercise.class, milestone.getSport().getSportId());
                return se.getExerciseTime();
            } else {
                return sm.getEndTime();
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "获取上一个实现的里程碑的锻炼异常", e);
        }
    }

    /**
     * 获取实现的里程数（如果以前已经有实现了，那就不计算在内）
     *
     * @param exerciseId
     * @return
     */
    public long getMilestoneCount(Long exerciseId) {
        try {
            String hql = "select count(*) from SportMilestone where exercise.exerciseId=?1 ";
            return this.getCount(hql, exerciseId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取实现的里程数异常", e);
        }
    }

    /**
     * 获取实现的里程数
     *
     * @param exerciseId
     * @return
     */
    public List<SportMilestone> getMilestoneList(Long exerciseId) {
        try {
            String hql = "from SportMilestone where exercise.exerciseId=?1 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,SportMilestone.class, exerciseId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取实现的里程数异常", e);
        }
    }

    /**
     * 删除锻炼
     *
     * @param exerciseId
     * @return
     */
    public void deleteExercise(Long exerciseId) {
        try {
            //删除绑定的里程碑
            String hql = "update SportMilestone set exercise.exerciseId=null,startTime=null,endTime=null,costDays=null where exercise.exerciseId=?1 ";
            this.updateEntities(hql, exerciseId);

            String hql2 = "delete from Exercise where exerciseId=?1 ";
            this.updateEntities(hql2, exerciseId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除锻炼异常", e);
        }
    }

    /**
     * 获取最佳的锻炼
     *
     * @param sf
     * @return
     */
    public List<ExerciseBestDTO> getBestMilestoneExerciseList(ExerciseDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select exercise_time as exerciseTime,value,max_speed as maxSpeed from exercise
                    {query_para}
                    and {bestField} is not null order by exercise_time
                    """;
            statSql = statSql
                    .replace("{query_para}",pr.getParameterString())
                    .replace("{bestField}",sf.getBestField());
            return this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE,ExerciseBestDTO.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最佳的锻炼异常", e);
        }
    }

    /**
     * 获取按锻炼类型是时间的统计
     *
     * @param sf
     * @return
     */
    public List<ExerciseOverallStat> statOverallSportExercise(ExerciseOverallStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(true);
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,sport_id as sportId,count(0) as totalCount,sum(value) as totalValue,sum(duration) as totalDuration
                    from ( select {date_group_field} as indexValue,sport_id,value,duration from exercise
                    {query_para}
                    ) as res group by sport_id,indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("exercise_time", dateGroupType))
                             .replace("{query_para}",pr.getParameterString());
            List<ExerciseOverallStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, ExerciseOverallStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取按锻炼类型是时间的统计异常", e);
        }
    }

    /**
     * 获取时间列表
     * @param sf
     * @return
     */
    public List<Date> getExerciseDateList(ExerciseDateStatSH sf) {
        try {
            String sql = """
                    select exercise_time from exercise
                    {query_para}
                     order by exercise_time
                    """;
            PageRequest pr = sf.buildQuery();
            sql = sql.replace("{query_para}",pr.getParameterString());
            List<Date> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,Date.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取时间列表异常", e);
        }
    }

}
