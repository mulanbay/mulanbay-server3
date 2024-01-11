package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.OperLog;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.dto.log.OperLogDateStat;
import cn.mulanbay.pms.persistent.dto.log.OperLogStat;
import cn.mulanbay.pms.persistent.dto.log.OperLogTreeStat;
import cn.mulanbay.pms.persistent.dto.log.SysLogAnalyseStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.FunctionType;
import cn.mulanbay.pms.persistent.enums.LogCompareType;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.log.operLog.OperLogDateStatSH;
import cn.mulanbay.pms.web.bean.req.log.operLog.OperLogStatSH;
import cn.mulanbay.pms.web.bean.req.log.operLog.OperLogTreeStatSH;
import cn.mulanbay.pms.web.bean.req.log.sysLog.SysLogAnalyseStatSH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 日志
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class LogService extends BaseHibernateDao {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    /**
     * 获取最近的操作日志比较(根据比较主体)
     * 包含：新增、修改、删除
     *
     * @param target
     * @param compareType
     * @return
     */
    public OperLog getNearestCompareLog(OperLog target, LogCompareType compareType) {
        try {
            String hql = """
                    from OperLog where sysFunc.funcId in
                    (select id from SysFunc where beanName=?1 and funcType in (0,1,2) )
                    and id!=?2
                    and idValue=?3
                    """;
            if (compareType == LogCompareType.EARLY) {
                hql+="and occurEndTime<=?4 order by occurEndTime desc";
            } else {
                hql+="and occurEndTime>=?4 order by occurEndTime asc";
            }
            OperLog log = this.getEntity(hql,OperLog.class, target.getSysFunc().getBeanName(), target.getId(), target.getIdValue(), target.getOccurEndTime());
            return log;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取最近的操作日志比较异常", e);
        }
    }

    /**
     * 重新设置操作日志的功能点
     *
     * @param needReSet 如果true，说明以前已经有的也要重新设置
     * @return
     */
    public void setOperLogFuncId(boolean needReSet) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("update oper_log t set sys_func_id= ");
            sb.append("(select func_id from sys_func s where s.url_address=t.url_address and s.support_methods = t.method limit 1) ");
            if (!needReSet) {
                sb.append("where t.sys_func_id is null ");
            }
            this.execSqlUpdate(sb.toString());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "重新设置操作日志的功能点异常", e);
        }
    }

    /**
     * 获取最近一次的操作记录
     * 包含新增、修改、删除
     *
     * @param idValue
     * @param beanName
     * @return
     */
    public OperLog getLatestOperLog(String idValue, String beanName) {
        try {
            String hql= """
                    from OperLog where idValue=?1 and sysFunc.id in
                    (select id from SysFunc where beanName=?2 and funcType in (0,1,2) )
                    order by occurStartTime desc
                    """;
            return this.getEntity(hql,OperLog.class, idValue, beanName);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最近一次的修改记录异常", e);
        }
    }

    /**
     * 更新错误代码的执行次数
     *
     * @param code
     * @return
     */
    public void updateErrorCodeCount(Integer code, int addCount) {
        try {
            String sql = "update error_code_define set count=count+" + addCount + " where code=?1 ";
            this.execSqlUpdate(sql, code);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新错误代码的执行次数异常", e);
        }
    }

    /**
     * 操作日志统计
     *
     * @param sf
     * @return
     */
    public List<OperLogTreeStat> treeStatOperLog(OperLogTreeStatSH sf) {
        try {
            String sql = """
                    select rr.*,pp.func_name as pname from (
                    select syt.*,sf.func_name,sf.pid from
                    (SELECT sys_func_id as funcId,count(0) as totalCount FROM oper_log
                     {query_para}
                     group by sys_func_id) as syt,
                     sys_func sf where syt.funcId = sf.func_id and sf.tree_stat=1
                     ) as rr
                     left join sys_func pp
                     on rr.pid=pp.func_id
                    """;
            PageRequest pr = sf.buildQuery();
            String query_para = pr.getParameterString();
            sql = sql.replace("{query_para}",query_para);
            List args = pr.getParameterValueList();
            List<OperLogTreeStat> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE, OperLogTreeStat.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "操作日志统计异常", e);
        }
    }

    /**
     * 操作日志统计(分页)
     *
     * @param sf
     * @return
     */
    public List<OperLogStat> statOperLog(OperLogStatSH sf) {
        try {
            String sql = """
                    SELECT sf.func_name as funcName,count(0) as totalCount FROM oper_log ol,sys_func sf
                    where ol.sys_func_id = sf.func_id
                    {query_para}
                    group by sf.func_name
                    order by totalCount desc
                    """;
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            String query_para = pr.getParameterString();
            sql = sql.replace("{query_para}",query_para);
            List args = pr.getParameterValueList();
            List<OperLogStat> list = this.getEntityListSI(sql,sf.getPage(),sf.getPageSize(), OperLogStat.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "操作日志统计(分页)异常", e);
        }
    }

    /**
     * 操作日志统计
     *
     * @param sf
     * @return
     */
    public List<OperLogDateStat> statDateOperLog(OperLogDateStatSH sf) {
        try {
            String sql = """
                    select indexValue,count(0) as totalCount
                    from (
                    select {date_group_field} as indexValue
                    FROM oper_log ol,sys_func sf
                    where ol.sys_func_id = sf.func_id
                    {query_para}
                    ) as tt group by indexValue
                    order by indexValue
                    """;
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            DateGroupType dateGroupType = sf.getDateGroupType();
            sql = sql.replace("{query_para}",pr.getParameterString());
            sql = sql.replace("{date_group_field}", MysqlUtil.dateTypeMethod("ol.occur_end_time", dateGroupType));
            List<OperLogDateStat> list = this.getEntityListSI(sql, pr.getPage(), pr.getPageSize(), OperLogDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "操作日志统计异常", e);
        }
    }

    /**
     * 系统日志统计
     *
     * @param sf
     * @return
     */
    public List<SysLogAnalyseStat> analyseStatSysLog(SysLogAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            StringBuffer sb = new StringBuffer();
            String groupField = sf.getGroupField();
            sb.append("select "+groupField+" as id,count(0) as totalCount ");
            sb.append("from sys_log ");
            sb.append(pr.getParameterString());
            sb.append("group by "+groupField);
            List<SysLogAnalyseStat> list = this.getEntityListSI(sb.toString(), pr.getPage(), pr.getPageSize(), SysLogAnalyseStat.class, pr.getParameterValue());
            for(SysLogAnalyseStat as : list){
                if(as.getId()==null){
                    as.setName("未知");
                }else if("exception_class_name".equals(groupField)|| "error_code".equals(groupField)){
                    as.setName(as.getId().toString());
                }else if("log_level".equals(groupField)){
                    LogLevel ll = LogLevel.getLogLevel(Integer.valueOf(as.getId().toString()));
                    as.setName(ll.getName());
                }
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "系统日志统计异常", e);
        }
    }


    /**
     * 获取修改类的系统功能点，每个业务类只有一个
     *
     * @param beanName
     * @return
     */
    public SysFunc getEditSysFunc(String beanName) {
        try {
            String hql = "from SysFunc where beanName=?1 and funcType=?2 ";
            return this.getEntity(hql,SysFunc.class, beanName, FunctionType.EDIT);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取修改类的系统功能点异常", e);
        }
    }

}
