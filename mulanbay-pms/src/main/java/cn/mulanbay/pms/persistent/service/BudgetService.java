package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.BudgetLog;
import cn.mulanbay.pms.persistent.domain.BudgetSnapshot;
import cn.mulanbay.pms.persistent.domain.BudgetTimeline;
import cn.mulanbay.pms.persistent.dto.fund.BudgetStat;
import cn.mulanbay.pms.persistent.dto.fund.UserBudgetAndIncomeStat;
import cn.mulanbay.pms.persistent.enums.BudgetStatType;
import cn.mulanbay.pms.persistent.enums.BudgetType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.util.BeanCopy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 预算
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Service
@Transactional
public class BudgetService extends BaseHibernateDao {

    /**
     * 获取预算分析
     *
     * @param userId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<BudgetStat> statBudget(Long userId, BudgetType type, PeriodType period, BudgetStatType statType) {
        try {
            StringBuffer sql = new StringBuffer();
            List args = new ArrayList();
            if (statType == BudgetStatType.NAME) {
                sql.append("select name as name,sum(amount) as value from ");
            } else if (statType == BudgetStatType.TYPE) {
                sql.append("select type as id,sum(amount) as value from ");
            } else if (statType == BudgetStatType.PERIOD) {
                sql.append("select period as id,sum(amount) as value from ");
            }
            sql.append("(");
            sql.append("SELECT name,type,period,");
            sql.append("case when period=0 then amount ");
            sql.append("when period=1 then amount*365 ");
            sql.append("when period=2 then amount*52 ");
            sql.append("when period=3 then amount*12 ");
            sql.append("when period=4 then amount*4 ");
            sql.append("when period=5 then amount ");
            sql.append("end as amount ");
            sql.append("from budget ");
            sql.append("where user_id=?1 and status=1 ");
            args.add(userId);
            int index = 2;
            if (type != null) {
                sql.append("and type=?" + (index++) + " ");
                args.add(type.getValue());
            }
            if (period != null) {
                sql.append("and period=?" + (index++) + " ");
                args.add(period.getValue());
            }
            sql.append(") as aa ");
            if (statType == BudgetStatType.NAME) {
                sql.append("group by name ");
            } else if (statType == BudgetStatType.TYPE) {
                sql.append("group by type ");
            } else if (statType == BudgetStatType.PERIOD) {
                sql.append("group by period ");
            }
            List<BudgetStat> list = this.getEntityListSI(sql.toString(), NO_PAGE,NO_PAGE_SIZE,
                    BudgetStat.class, args.toArray());
            if (statType == BudgetStatType.TYPE) {
                for (BudgetStat aas : list) {
                    //logger.debug(aas.getId().getClass());
                    BudgetType it = BudgetType.getBudgetType(Integer.valueOf(aas.getId().toString()));
                    if (it == null) {
                        aas.setName("未知");
                    } else {
                        aas.setName(it.getName());
                    }
                }
            } else if (statType == BudgetStatType.PERIOD) {
                for (BudgetStat aas : list) {
                    PeriodType it = PeriodType.getPeriodType(Integer.valueOf(aas.getId().toString()));
                    if (it == null) {
                        aas.setName("未知");
                    } else {
                        aas.setName(it.getName());
                    }
                }
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取预算分析异常", e);
        }
    }

    /**
     * 查询月度预算在快照里的数量
     * @param budgetId
     * @param year
     * @return
     */
    public long countMonthBudgetSnapshot(Long budgetId, int year) {
        try {
            String sql = " select count(0) from budget_snapshot where from_id=?1 and buss_key >=?2 and buss_key<=?3 ";
            return this.getCountSQL(sql,budgetId,year+"01",year+"12");
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "查询月度预算在快照里的数量异常", e);
        }
    }

    /**
     * 获取用户预算快照列表
     *
     * @param userId
     * @return
     */
    public List<BudgetSnapshot> getBudgetSnapshotList(Long userId, Long budgetLogId) {
        try {
            String hql = "from BudgetSnapshot where userId=?1 and budgetLogId=?2 ";
            List<BudgetSnapshot> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,BudgetSnapshot.class, userId,budgetLogId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户预算快照列表异常", e);
        }
    }

    /**
     * 获取月度预算快照列表
     * @param userId
     * @param budgetId
     * @param year
     * @return
     */
    public List<BudgetSnapshot> getMonthBudgetSnapshotList(Long userId, Long budgetId,String year) {
        try {
            String hql = "from BudgetSnapshot where userId=?0 and fromId=?2 and period=?3 and budgetLogId in ";
            hql += "(select id from BudgetLog where period=?4 and userId=?4 and bussKey like ?6) ";
            String bussKeyPara = year+"%";
            List<BudgetSnapshot> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,BudgetSnapshot.class, userId,budgetId,PeriodType.MONTHLY,PeriodType.MONTHLY,userId,bussKeyPara);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取月度预算快照列表异常", e);
        }
    }

    /**
     * 获取用户预算列表
     *
     * @param userId
     * @return
     */
    public List<Budget> getActiveUserBudget(Long userId, Boolean bindFlow) {
        try {
            String hql = "from Budget where status=?1 ";
            List args = new ArrayList();
            args.add(CommonStatus.ENABLE);
            int index = 2;
            if (userId != null) {
                hql += " and userId=?" + (index++) + " ";
                args.add(userId);
            }
            if (bindFlow!=null&&true == bindFlow) {
                hql += " and goodsTypeId is not null" ;
            }
            hql += " order by userId";
            List<Budget> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Budget.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取预算分析异常", e);
        }
    }

    /**
     * @param userId
     * @param bindFlow
     * @param hasEpt   是否有期望付款时间
     * @return
     */
    public List<Budget> getActiveUserBudget(Long userId, String name, Boolean bindFlow, Boolean hasEpt) {
        try {
            String hql = "from Budget where status=?1 ";
            List args = new ArrayList();
            args.add(CommonStatus.ENABLE);
            int index = 2;
            if (userId != null) {
                hql += " and userId=?" + (index++) + " ";
                args.add(userId);
            }
            if (bindFlow!=null&&true == bindFlow) {
                hql += " and goodsTypeId is not null" ;
            }
            if (StringUtil.isNotEmpty(name)) {
                hql += " and name like '%" + name + "%' ";
            }
            if (hasEpt != null) {
                if (hasEpt) {
                    hql += " and expectPaidTime is not null";
                } else {
                    hql += " and expectPaidTime is null";
                }
            }
            hql += " order by userId";
            List<Budget> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Budget.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取预算分析异常", e);
        }
    }


    /**
     * 预算日志是否存在
     */
    public boolean isBudgetLogExit(String bussKey, Long userId, Long budgetLogId, Long budgetId) {
        BudgetLog budgetLog = this.selectBudgetLog(bussKey, userId, budgetLogId, budgetId);
        return budgetLog == null ? false : true;
    }

    /**
     * 根据bussKey查询预算日志
     * 统计类型的不需要指定某个具体的预算，直接根据bussKey和userId来决定
     */
    public BudgetLog selectBudgetLog(String bussKey, Long userId, Long budgetLogId, Long budgetId) {
        try {
            String hql = "from BudgetLog where bussKey=?1 and userId=?2 ";
            if (budgetLogId != null) {
                hql += " and logId!=" + budgetLogId;
            }
            if (budgetId != null) {
                hql += " and budget.budgetId=" + budgetId;
            }else{
                hql += " and budget.budgetId is null";
            }
            BudgetLog budgetLog = this.getEntity(hql,BudgetLog.class, bussKey, userId);
            return budgetLog;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "根据bussKey查询预算日志异常", e);
        }
    }

    /**
     * 保存预算日志(日终统计)
     */
    public void saveStatBudgetLog(List<Budget> ccList, BudgetLog budgetLog, boolean isRedo) {
        try {
            if (isRedo) {
                //删除快照
                BudgetLog hisBl = this.selectBudgetLog(budgetLog.getBussKey(), budgetLog.getUserId());
                if (hisBl != null) {
                    String hql = "delete from BudgetSnapshot where budgetLogId=?1 ";
                    this.updateEntities(hql, hisBl.getLogId());
                }
            }
            this.saveBudgetLog(budgetLog, isRedo);
            //保存预算快照
            List<BudgetSnapshot> ss = new ArrayList<>();
            for (Budget b : ccList) {
                BudgetSnapshot snapshot = new BudgetSnapshot();
                BeanCopy.copy(b, snapshot);
                snapshot.setSnapshotId(null);
                snapshot.setBudgetLogId(budgetLog.getLogId());
                snapshot.setFromId(b.getBudgetId());
                snapshot.setBussKey(budgetLog.getBussKey());
                snapshot.setCreatedTime(new Date());
                snapshot.setModifyTime(null);
                ss.add(snapshot);
            }
            this.saveEntities(ss.toArray());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    " 保存预算日志(日终统计)异常", e);
        }

    }

    /**
     * 保存预算日志
     */
    public void saveBudgetLog(BudgetLog budgetLog, boolean isRedo) {
        try {
            if (isRedo) {
                String hql = "delete from BudgetLog where bussKey=?1 and userId=?2 and budget is null";
                this.updateEntities(hql, budgetLog.getBussKey(), budgetLog.getUserId());
            }
            this.saveEntity(budgetLog);
            Budget budget = budgetLog.getBudget();
            if (budget != null) {
                budget.setLastPaidTime(budgetLog.getOccurDate());
                if (budget.getFirstPaidTime() == null) {
                    budget.setFirstPaidTime(budgetLog.getOccurDate());
                }
                if (budget.getPeriod() == PeriodType.ONCE) {
                    //关闭该预算
                    budget.setStatus(CommonStatus.DISABLE);
                }
                this.updateEntity(budget);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "保存预算日志异常", e);
        }
    }

    /**
     * 保存预算时间线
     */
    public void saveBudgetTimeline(BudgetTimeline timeline, boolean isRedo) {
        try {
            if (isRedo) {
                String hql = "delete from BudgetTimeline where bussKey=?1 and userId=?2 and bussDay=?3 ";
                this.updateEntities(hql, timeline.getBussKey(), timeline.getUserId(), timeline.getBussDay());
            }
            this.saveEntity(timeline);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "梦想统计异常", e);
        }
    }

    /**
     * 重新保存预算时间线
     */
    public void reSaveBudgetTimeline(List<BudgetTimeline> datas, String bussKey, Long userId) {
        try {
            String hql = "delete from BudgetTimeline where bussKey=?1 and userId=?2";
            this.updateEntities(hql, bussKey, userId);
            this.saveEntities(datas.toArray());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "重新保存预算时间线异常", e);
        }
    }

    /**
     * 根据bussKey查询预算流水
     */
    public List<BudgetTimeline> selectBudgetTimelineList(String bussKey, Long userId) {
        try {
            String hql = "from BudgetTimeline where bussKey=?1 and userId=?2 order by bussDay";

            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,BudgetTimeline.class, bussKey, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "根据bussKey查询预算流水异常", e);
        }
    }

    /**
     * 根据bussKey查询预算流水
     */
    public BudgetLog selectBudgetLog(String bussKey, Long userId) {
        try {
            String hql = "from BudgetLog where bussKey=?1 and userId=?2 and budget is null";
            return this.getEntity(hql,BudgetLog.class, bussKey, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "根据bussKey查询预算流水异常", e);
        }
    }

    /**
     * 获取用户预算即收入分析
     *
     * @param period
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserBudgetAndIncomeStat> statUserBudgetAndIncome(Date startTime, Date endTime, Long userId, PeriodType period) {
        try {
            String sql= """
                    select bl.user_id as userId,bl.occur_date as occurDate,bl.budget_amount as budgetAmount,
                    bl.nc_amount as ncAmount,bl.bc_amount as bcAmount,bl.tr_amount as trAmount,tt.totalIncome
                    from budget_log bl
                    left join
                    (select incomeDate,sum(amount) as totalIncome from (
                    select CAST(DATE_FORMAT(occur_time,'%Y%m') AS signed) as incomeDate,amount from income
                    where user_id=?1 and occur_time>=?2 and occur_time<=?3
                    as tt group by incomeDate) as tt
                    on CAST(DATE_FORMAT(bl.occur_date,'%Y%m') AS signed) = tt.incomeDate
                    where bl.user_id=?4 and bl.occur_date>=?5 and bl.occur_date<=?6
                    and bl.period=?6 and budget_id is null
                    order by bl.occur_date
                    """;
            List args = new ArrayList();
            args.add(userId);
            args.add(startTime);
            args.add(endTime);
            args.add(userId);
            args.add(startTime);
            args.add(endTime);
            args.add(period.getValue());
            List<UserBudgetAndIncomeStat> list = this.getEntityListSI(sql.toString(), NO_PAGE,NO_PAGE_SIZE,
                    UserBudgetAndIncomeStat.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取预算分析异常", e);
        }
    }

}
