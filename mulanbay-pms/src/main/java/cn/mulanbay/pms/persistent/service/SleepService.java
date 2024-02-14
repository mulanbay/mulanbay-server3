package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.Sleep;
import cn.mulanbay.pms.persistent.dto.sleep.SleepAnalyseStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.SleepStatType;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.health.sleep.SleepAnalyseStatSH;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 睡眠
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class SleepService extends BaseHibernateDao {


    /**
     * 获取睡眠
     *
     * @param sleepDate
     * @return
     */
    public Sleep getSleep(Date sleepDate) {
        try {
            String hql = "from Sleep where sleepDate=?1 ";
            return this.getEntity(hql,Sleep.class, sleepDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取睡眠异常", e);
        }
    }

    /**
     * 获取最近的没有起床信息的睡眠
     *
     * @param fromTime
     * @return
     */
    public Sleep getNearUnGetUp(Date fromTime,Long userId) {
        try {
            String hql = "from Sleep where userId=?1 and getUpTime is null and sleepTime>=?2 ";
            return this.getEntity(hql,Sleep.class, userId,fromTime);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最近的没有起床信息的睡眠异常", e);
        }
    }

    /**
     * 睡眠分析统计
     *
     * @param sf
     * @return
     */
    public List<SleepAnalyseStat> getAnalyseSta(SleepAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            StringBuffer sb = new StringBuffer();
            if (sf.getValueType() == SleepStatType.DURATION) {
                sb.append("select " + MysqlUtil.dateTypeMethod("sleep_date", sf.getGroupType()) + " as xValue,");
                sb.append(" duration as yValue ");
            } else if (sf.getValueType() == SleepStatType.SLEEP_TIME) {
                sb.append("select " + MysqlUtil.dateTypeMethod("sleep_time", sf.getGroupType()) + " as xValue,");
                sb.append(MysqlUtil.dateTypeMethod("sleep_time", DateGroupType.HOURMINUTE) + " as yValue ");
            } else if (sf.getValueType() == SleepStatType.GETUP_TIME) {
                sb.append("select " + MysqlUtil.dateTypeMethod("get_up_time", sf.getGroupType()) + " as xValue,");
                sb.append(MysqlUtil.dateTypeMethod("get_up_time", DateGroupType.HOURMINUTE) + " as yValue ");
            }else if (sf.getValueType() == SleepStatType.WAKEUP_COUNT) {
                sb.append("select " + MysqlUtil.dateTypeMethod("sleep_time", sf.getGroupType()) + " as xValue,");
                sb.append("wake_up_count as yValue ");
            } else {
                sb.append("select " + MysqlUtil.dateTypeMethod("sleep_date", sf.getGroupType()) + " as xValue,");
                sb.append("quality as yValue ");
            }
            sb.append(" from sleep ");
            sb.append(pr.getParameterString());
            //sb.append(" and sleep_time is not null and get_up_time is not null ");
            List args = pr.getParameterValueList();
            List<SleepAnalyseStat> list = this.getEntityListSI(sb.toString(), NO_PAGE,NO_PAGE_SIZE, SleepAnalyseStat.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "睡眠分析统计异常", e);
        }
    }
}
