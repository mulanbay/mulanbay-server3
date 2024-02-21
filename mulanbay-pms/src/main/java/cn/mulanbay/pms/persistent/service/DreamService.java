package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.Dream;
import cn.mulanbay.pms.persistent.domain.DreamDelay;
import cn.mulanbay.pms.persistent.domain.DreamRemind;
import cn.mulanbay.pms.persistent.dto.dream.DreamStat;
import cn.mulanbay.pms.persistent.enums.DreamStatus;
import cn.mulanbay.pms.web.bean.req.dream.DreamStatSH;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DreamService extends BaseHibernateDao {

    public List<DreamStat> getDreamStat(DreamStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select {group_field} as id,count(0) as totalCount from dream
                    {query_para}
                    group by {group_field} order by totalCount desc
                    """;
            statSql = statSql.replace("{group_field}",sf.getGroupType().getColumn() )
                             .replace("{query_para}",pr.getParameterString());
            List<DreamStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, DreamStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "梦想统计异常", e);
        }
    }

    /**
     * 获取待刷新进度的梦想
     */
    public List<Dream> getNeedRefreshRateDream(Long userId) {
        try {
            String hql = "from Dream where planId is not null and status in(?1,?2)  ";
            if(userId!=null){
                hql+="and userId="+userId;
            }
            List<Dream> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Dream.class, DreamStatus.CREATED, DreamStatus.STARTED);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "梦想统计异常", e);
        }
    }


    /**
     * 查找梦想提醒
     *
     * @param dreamId
     */
    public DreamRemind getRemind(Long dreamId) {
        try {
            String hql = "from DreamRemind where dream.dreamId=?1 ";
            return this.getEntity(hql,DreamRemind.class, dreamId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "查找梦想提醒异常", e);
        }
    }

    /**
     * 获取需要提醒的梦想列表
     *
     * @return
     */
    public List<Dream> getNeedRemindDream() {
        try {
            String hql = "from Dream where status=?1 and remind=?2 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Dream.class, DreamStatus.STARTED, true);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要提醒的梦想列表异常", e);
        }
    }

    /**
     * 更新最后提醒时间
     *
     * @param remindId
     */
    public void updateLastRemindTime(Long remindId, Date date) {
        try {
            String hql = "update DreamRemind set lastRemindTime=?1 where remindId=?2 ";
            this.updateEntities(hql, date, remindId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新最后提醒时间异常", e);
        }
    }

    /**
     * 更新梦想
     * @param dream
     * @param delay
     */
    public void updateDream(Dream dream, DreamDelay delay) {
        try {
            this.updateEntities(dream);
            if(delay!=null){
                this.saveEntity(delay);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "查找梦想提醒异常", e);
        }
    }

    /**
     * 删除梦想
     * @param dreamId
     */
    public void deleteDream(Long dreamId) {
        try {
            String sql = "delete from dream_delay where dream_id=?1 ";
            this.execSqlUpdate(sql, dreamId);

            String sql2 = "delete from dream_remind where dream_id=?1 ";
            this.execSqlUpdate(sql2, dreamId);

            String sql3 = "delete from dream where dream_id=?1 ";
            this.execSqlUpdate(sql3, dreamId);

        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除梦想异常", e);
        }
    }
}
