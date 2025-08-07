package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.ConsumeRefer;
import cn.mulanbay.pms.persistent.dto.fund.IncomeDateStat;
import cn.mulanbay.pms.persistent.dto.fund.IncomeSummaryStat;
import cn.mulanbay.pms.persistent.dto.fund.IncomeTypeStat;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.fund.income.IncomeDateStatSH;
import cn.mulanbay.pms.web.bean.req.fund.income.IncomeStatSH;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class IncomeService extends BaseHibernateDao {

    /**
     * 获取收入总的统计
     *
     * @param userId
     * @return
     */
    @SuppressWarnings("unchecked")
    public IncomeSummaryStat incomeSummaryStat(Long userId, Date startTime, Date endTime) {
        try {
            String sql = "select count(*) as totalCount,sum(amount) as totalAmount from income where user_id=?1 and occur_time>=?2 and occur_time<=?3 ";
            List<IncomeSummaryStat> list = this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE, IncomeSummaryStat.class, userId, startTime, endTime);
            return list.get(0);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取收入总的统计计异常", e);
        }
    }

    /**
     * 收入统计
     *
     * @param sf
     * @return
     */
    public List<IncomeDateStat> statDateIncome(IncomeDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String sql = """
                    select indexValue,sum(amount) as totalAmount ,count(0) as totalCount
                    from (
                    select {date_group_field} as indexValue,
                    amount from income
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            sql = sql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("occur_time", dateGroupType))
                     .replace("{query_para}",pr.getParameterString());
            List<IncomeDateStat> list = this.getEntityListSI(sql, NO_PAGE, NO_PAGE_SIZE, IncomeDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "收入统计异常", e);
        }
    }

    /**
     * 收入统计
     *
     * @param sf
     * @return
     */
    public List<IncomeTypeStat> statIncome(IncomeStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql = """
                    select type as indexValue,sum(amount) as totalAmount ,count(0) as totalCount
                    from income
                    {query_para}
                    group by type
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString());
            List<IncomeTypeStat> list = this.getEntityListSI(sql, NO_PAGE, NO_PAGE_SIZE, IncomeTypeStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "收入统计异常", e);
        }
    }

    /**
     * 查询消费记录关联
     *
     * @param incomeId
     * @return
     */
    public ConsumeRefer getConsumeRefer(Long incomeId) {
        try {
            String hql = "from ConsumeRefer where referId=?1 and type=?2 ";
            return this.getEntity(hql,ConsumeRefer.class,incomeId, BussType.INCOME);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "查询消费记录关联异常", e);
        }
    }

    /**
     * 删除收入
     *
     * @param incomeId
     * @return
     */
    public void deleteIncome(Long incomeId) {
        try {
            String hql = "delete from ConsumeRefer where referId=?1 and type=?2 ";
            this.updateEntities(hql,incomeId, BussType.INCOME);

            String hql2 = "delete from Income where incomeId = ?1  ";
            this.updateEntities(hql2,incomeId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除收入异常", e);
        }
    }

    /**
     * 获取时间列表
     * @param sf
     * @return
     */
    public List<Date> getDateList(IncomeDateStatSH sf) {
        try {
            String sql = """
                    select occur_time from income
                    {query_para}
                     order by occur_time
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
