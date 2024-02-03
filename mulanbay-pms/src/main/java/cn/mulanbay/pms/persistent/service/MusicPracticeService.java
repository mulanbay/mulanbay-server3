package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.Instrument;
import cn.mulanbay.pms.persistent.domain.MusicPractice;
import cn.mulanbay.pms.persistent.domain.MusicPracticeDetail;
import cn.mulanbay.pms.persistent.dto.music.*;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.ChartOrderType;
import cn.mulanbay.pms.web.bean.req.music.musicPractice.*;
import cn.mulanbay.pms.web.bean.req.music.musicPracticeDetail.MusicPracticeDetailSH;
import cn.mulanbay.pms.web.bean.req.music.musicPracticeDetail.MusicPracticeDetailTreeSH;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 音乐
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class MusicPracticeService extends BaseHibernateDao {

    /**
     * 练习的曲子统计
     * 如果是hql查询，那么返回结果绑定字段是映射的类型
     *
     * @param sf
     * @return
     */
    public List<MusicPracticeTuneStat> getTuneStat(MusicPracticeDetailSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select tune as name,sum(times) as totalTimes from MusicPracticeDetail
                    ({query_para})
                    group by tune order by totalTimes desc
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<MusicPracticeTuneStat> list = this.getEntityListSI(statSql, pr.getPage(), pr.getPageSize(), MusicPracticeTuneStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "练习的曲子统计", e);
        }
    }

    /**
     * 获取口琴练习到目前为止的统计
     *
     * @param maxPracticeDate
     * @param userId
     * @param instrumentId
     * @return
     */
    public MusicPracticeSummaryStat getTillNowStat(Date maxPracticeDate, Long userId, Long instrumentId) {
        try {
            String sql = "select count(*) as totalCount,sum(minutes) as totalMinutes from music_practice ";
            sql += "where practice_date<=?1 and user_id=?2 and instrument_id=?3";
            List<MusicPracticeSummaryStat> list = this.getEntityListSI(sql, NO_PAGE, NO_PAGE_SIZE, MusicPracticeSummaryStat.class, maxPracticeDate, userId, instrumentId);
            return list.get(0);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取口琴练习到目前为止的统计异常", e);
        }
    }


    /**
     * 获取口琴练习总的统计
     *
     * @param sf
     * @return
     */
    @SuppressWarnings("unchecked")
    public MusicPracticeSummaryStat getSummaryStat(MusicPracticeStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql = "select count(*) as totalCount,sum(minutes) as totalMinutes from music_practice";
            sql += pr.getParameterString();
            List<MusicPracticeSummaryStat> list = this.getEntityListSI(sql, NO_PAGE, NO_PAGE_SIZE, MusicPracticeSummaryStat.class, pr.getParameterValue());
            return list.get(0);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取口琴练习总的统计异常", e);
        }
    }

    /**
     * 获取口琴练习乐器的统计
     *
     * @param sf
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<InstrumentStat> getInstrumentStat(MusicPracticeStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select instrument_id as instrumentId,count(0) as totalCount,sum(minutes) as totalMinutes from music_practice
                    ({query_para})
                    group by instrument_id
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<InstrumentStat> list = this.getEntityListSI(statSql, NO_PAGE, NO_PAGE_SIZE, InstrumentStat.class, pr.getParameterValue());
            //获取乐器名
            for (InstrumentStat mp : list) {
                String mi = this.getInstrumentName(mp.getInstrumentId());
                mp.setInstrumentName(mi);
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取口琴练习总的统计异常", e);
        }
    }

    /**
     * 口琴练习统计
     *
     * @param sf
     * @return
     */
    public List<MusicPracticeDateStat> getDateStat(MusicPracticeDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,sum(minutes) as totalMinutes ,count(0) as totalCount
                    from (select {date_group_field} as indexValue,minutes from music_practice
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("practice_date", dateGroupType))
                             .replace("{query_para}",pr.getParameterString());
            List<MusicPracticeDateStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, MusicPracticeDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "口琴练习统计异常", e);
        }
    }

    /**
     * 口琴练习统计
     *
     * @param sf
     * @return
     */
    public List<MusicPracticeTimeStat> getTimeStat(MusicPracticeTimeStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            StringBuffer sb = new StringBuffer();
            sb.append("select indexValue,count(0) as totalCount ");
            sb.append("from (");
            if (dateGroupType == DateGroupType.MINUTE) {
                //统计花费的练习时间
                sb.append("select minutes as indexValue");
            } else {
                sb.append("select " + MysqlUtil.dateTypeMethod("start_time", dateGroupType) + " as indexValue");
            }
            sb.append(" from music_practice ");
            sb.append(pr.getParameterString());
            sb.append(") tt group by indexValue ");
            if (sf.getChartOrderType() == ChartOrderType.X) {
                sb.append(" order by indexValue");
            } else {
                sb.append(" order by totalCount desc");
            }
            List<MusicPracticeTimeStat> list = this.getEntityListSI(sb.toString(), NO_PAGE,NO_PAGE_SIZE, MusicPracticeTimeStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "口琴练习统计异常", e);
        }
    }

    /**
     * 音乐练习比对统计
     *
     * @param sf
     * @return
     */
    public List<MusicPracticeCompareStat> getCompareStat(MusicPracticeCompareStatSH sf, Long musicInstrumentId) {
        try {
            PageRequest pr = sf.buildQuery();
            StringBuffer sb = new StringBuffer();
            sb.append("select " + MysqlUtil.dateTypeMethod("start_time", sf.getXgroupType()) + " as cx,");
            sb.append(MysqlUtil.dateTypeMethod("start_time", sf.getYgroupType()) + " as cy ");
            sb.append(" from music_practice ");
            sb.append(pr.getParameterString());
            sb.append(" and instrument_id = ?" + pr.getNextIndex());
            List args = pr.getParameterValueList();
            args.add(musicInstrumentId);
            List<MusicPracticeCompareStat> list = this.getEntityListSI(sb.toString(),NO_PAGE,NO_PAGE_SIZE, MusicPracticeCompareStat.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "音乐练习比对统计异常", e);
        }
    }

    /**
     * 获取乐器名称
     *
     * @param instrumentId
     * @return
     */
    public String getInstrumentName(Long instrumentId) {
        try {
            if (instrumentId == null || instrumentId == 0) {
                return "音乐";
            } else {
                Instrument musicInstrument = this.getEntityById(Instrument.class, instrumentId);
                if (musicInstrument == null) {
                    return "未知";
                } else {
                    return musicInstrument.getInstrumentName();
                }
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取乐器名称异常", e);
        }
    }

    /**
     * 获取曲子列表，统计聚合
     *
     * @return
     */
    public List<String> getTuneList(MusicPracticeDetailTreeSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String hql ="select distinct tune from MusicPracticeDetail ";
            hql+=pr.getParameterString();
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取曲子列表异常", e);
        }
    }

    /**
     * 删除音乐练习记录
     *
     * @param musicPractice
     */
    public void deleteMusicPractice(MusicPractice musicPractice) {
        try {

            //删除曲子
            String hql = "delete from MusicPracticeDetail where practice.practiceId=?1 ";
            this.updateEntities(hql, musicPractice.getPracticeId());
            //删除记录
            this.removeEntity(musicPractice);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "删除音乐练习记录异常", e);
        }
    }

    /**
     * 获取音乐练习曲子记录
     *
     * @return
     */
    public List<MusicPracticeDetail> getDetailList(Long musicPracticeId) {
        try {
            String hql = "from MusicPracticeDetail where practice.practiceId=?1 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,MusicPracticeDetail.class, musicPracticeId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取音乐练习曲子记录异常", e);
        }
    }

    /**
     * 获取乐器列表
     *
     * @return
     */
    public List<Instrument> getInstrumentList(Long userId) {
        try {
            String hql = "from Instrument where userId=?1 order by orderIndex desc";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Instrument.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取乐器列表异常", e);
        }
    }

    /**
     * 获取音乐练习曲子记录
     *
     * @return
     */
    public void addMusicPractice(MusicPractice mp, List<MusicPracticeDetail> detailList) {
        try {
            this.saveEntity(mp);
            for (MusicPracticeDetail mpt : detailList) {
                mpt.setPractice(mp);
                this.saveEntity(mpt);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取音乐练习曲子记录异常", e);
        }
    }

    /**
     * 基于曲子名称的统计
     *
     * @param tune
     * @return
     */
    public MusicPracticeTuneNameStat getTuneNameStat(Long userId, String tune, Long musicInstrumentId, Boolean allMi) {
        try {
            List args = new ArrayList();
            args.add(userId);
            args.add(tune);
            String sql= """
                    select min(mp.practice_date) as minPracticeDate, max(mp.practice_date) as maxPracticeDate ,sum(mpt.times) as totalTimes,count(0) as totalCounts
                    from music_practice mp,music_practice_detail mpt
                    where mp.practice_id = mpt.practice_id and mpt.user_id=?1 and mpt.tune=?2
                    """;
            if (false == allMi) {
                sql+="and mp.instrument_id=?3 ";
                args.add(musicInstrumentId);
            }
            List<MusicPracticeTuneNameStat> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE, MusicPracticeTuneNameStat.class, args.toArray());
            if (StringUtil.isEmpty(list)) {
                return null;
            } else {
                MusicPracticeTuneNameStat ns = list.get(0);
                ns.setTune(tune);
                return ns;
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "基于曲子名称的统计异常", e);
        }
    }

    /**
     * 基于水平的统计
     *
     * @param tune
     * @return
     */
    public List<MusicPracticeTuneLevelStat> getTuneLevelStat(Long userId, String tune, Long musicInstrumentId) {
        try {
            String sql= """
                    select min(mp.practice_date) as minPracticeDate, max(mp.practice_date) as maxPracticeDate ,mpt.level as levelIndex
                    from music_practice mp,music_practice_detail mpt
                    where mp.practice_id = mpt.practice_id and mpt.user_id=?1 and mpt.tune=?2
                    and instrument_id=?3
                    group by mpt.level order by levelIndex
                    """;
            List<MusicPracticeTuneLevelStat> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,MusicPracticeTuneLevelStat.class, userId, tune, musicInstrumentId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "基于水平的统计异常", e);
        }
    }

    /**
     * 获取音乐练习总体统计
     *
     * @param sf
     * @return
     */
    public List<MusicPracticeOverallStat> getOverallStat(MusicPracticeOverallStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(true);
            DateGroupType dateGroupType = sf.getDateGroupType();
            String sql= """
                    select indexValue,music_instrument_id as musicInstrumentId,count(0) as totalCount,sum(minutes) as totalMinutes
                    from (select music_instrument_id,minutes,{date_group_field} as indexValue from music_practice
                    {query_para}
                    ) as res group by music_instrument_id,indexValue order by indexValue
                    """;
            sql = sql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("practice_start_time", dateGroupType))
                    .replace("{query_para}",pr.getParameterString());
            List<MusicPracticeOverallStat> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE, MusicPracticeOverallStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取音乐练习总体统计异常", e);
        }
    }

    /**
     * 获取时间列表
     * @param sf
     * @return
     */
    public List<Date> getPracticeDateList(MusicPracticeDateStatSH sf) {
        try {
            String sql = """
                    select practice_date from music_practice
                    {query_para}
                     order by practice_date
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
