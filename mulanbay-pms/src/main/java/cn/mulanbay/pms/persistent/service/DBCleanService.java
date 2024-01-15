package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.DBClean;
import cn.mulanbay.pms.persistent.enums.CleanType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DBCleanService extends BaseHibernateDao {

    /**
     * 获取最大记录数
     *
     * @return
     */
    public long getCounts(String tableName) {
        try {
            String sql = "select count(0) from " + tableName;
            return this.getCountSQL(sql);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "清空数据库表异常", e);
        }
    }

    /**
     * 清空数据库表
     *
     * @return
     */
    public void truncateTable(Long id) {
        try {
            DBClean dc = this.getEntityById(DBClean.class, id);
            String sql = "truncate table " + dc.getTableName();
            this.execSqlUpdate(sql);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "清空数据库表异常", e);
        }
    }

    /**
     * 获取有效的数据库清理列表
     *
     * @return
     */
    public List<DBClean> getActiveList() {
        try {
            String hql = "from DatabaseClean where status=?1 order by orderIndex";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,DBClean.class, CommonStatus.ENABLE);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取有效的数据库清理列表异常", e);
        }
    }

    /**
     * 执行数据库清理
     *
     * @param dc
     */
    public void exeClean(DBClean dc) {
        try {
            String sql = null;
            int n = 0;
            if (dc.getCleanType() == CleanType.TRUNCATE) {
                sql = "truncate table " + dc.getTableName();
                n = this.execSqlUpdate(sql);
            } else {
                Date date = DateUtil.getDate(0 - dc.getDays());
                sql = "delete from " + dc.getTableName() + " where " + dc.getDateField() + "<=?1 ";
                if (!StringUtil.isEmpty(dc.getExtraCondition())) {
                    sql += "and " + dc.getExtraCondition();
                }
                n = this.execSqlUpdate(sql, date);
            }
            //更新
            dc.setLastCleanCounts(n);
            dc.setLastCleanTime(new Date());
            this.updateEntity(dc);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "执行数据库清理异常", e);
        }
    }

    /**
     * 手动执行数据库清理
     *
     * @param dc
     */
    public int manualClean(DBClean dc, int days, boolean update) {
        try {
            String sql = null;
            Date date = DateUtil.getDate(-days);
            sql = "delete from " + dc.getTableName() + " where " + dc.getDateField() + "<=? ";
            if (!StringUtil.isEmpty(dc.getExtraCondition())) {
                sql += "and " + dc.getExtraCondition();
            }
            int n = this.execSqlUpdate(sql, date);
            //更新
            if (update) {
                dc.setLastCleanCounts(n);
                dc.setLastCleanTime(new Date());
                this.updateEntity(dc);
            }
            return n;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "手动执行数据库清理异常", e);
        }
    }

}
