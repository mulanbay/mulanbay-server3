package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.dto.body.BodyAbnormalDateStat;
import cn.mulanbay.pms.persistent.dto.body.BodyAbnormalStat;
import cn.mulanbay.pms.persistent.dto.body.BodyInfoAvgStat;
import cn.mulanbay.pms.persistent.dto.body.BodyInfoDateStat;
import cn.mulanbay.pms.persistent.enums.BodyAbnormalGroupType;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.health.body.BodyAbnormalDateStatSH;
import cn.mulanbay.pms.web.bean.req.health.body.BodyAbnormalStatSH;
import cn.mulanbay.pms.web.bean.req.health.body.BodyInfoDateStatSH;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author fenghong
 * @date 2024/2/9
 */
@Service
@Transactional
public class BodyService  extends BaseHibernateDao {


    /**
     * 获取身体不适统计
     *
     * @param sf
     * @return
     */
    public List<BodyAbnormalStat> getAbnormalStat(BodyAbnormalStatSH sf) {
        try {
            BodyAbnormalGroupType groupField = sf.getGroupField();
            PageRequest pr = sf.buildQuery();
            List args = pr.getParameterValueList();
            StringBuffer sb = new StringBuffer();
            sb.append("select name,count(0) as totalCount,sum(days) as totalDays,max(occur_date) as maxOccurDate,min(occur_date) as minOccurDate ");
            sb.append("from (select ");
            if (groupField == BodyAbnormalGroupType.DISEASE || groupField == BodyAbnormalGroupType.ORGAN) {
                sb.append(groupField.getField() + " as name");
            } else {
                //数字转换为字符
                sb.append("CAST(" + groupField.getField() + " AS CHAR) as name");
            }
            sb.append(",last_days,occur_date from body_abnormal_record ");
            sb.append(pr.getParameterString());
            int lastIndex = pr.getNextIndex();
            if (!StringUtil.isEmpty(sf.getName())) {
                //添加检索
                sb.append(" and " + groupField.getField() + " like ?" + lastIndex);
                args.add("%" + sf.getName() + "%");
            }
            sb.append(") tt group by name order by totalCount desc");
            List<BodyAbnormalStat> list = this.getEntityListSI(sb.toString(), NO_PAGE,NO_PAGE_SIZE, BodyAbnormalStat.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取身体不适统计异常", e);
        }
    }

    /**
     * 统计身体不适的基于时间的统计
     *
     * @param sf
     * @return
     */
    public List<BodyAbnormalDateStat> getAbnormalDateStat(BodyAbnormalDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String sql= """
                    select indexValue,count(0) as totalCount,sum(days) as totalDays
                    from ( select {date_group_field} as indexValue,days from body_abnormal
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString())
                     .replace("{date_group_field}",MysqlUtil.dateTypeMethod("occur_date", dateGroupType));
            List<BodyAbnormalDateStat> list = this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE, BodyAbnormalDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "身体不适的基于时间的统计异常", e);
        }
    }

    /**
     * 统计身体不适的基于时间的统计
     *
     * @param sf
     * @return
     */
    public BodyInfoAvgStat getInfoAvgStat(BodyInfoDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            StringBuffer sb = new StringBuffer();
            sb.append("select avg(weight) as avgWeight,avg(height) as avgHeight ");
            sb.append("from body_info ");
            sb.append(pr.getParameterString());
            return this.getEntitySQL(sb.toString(), BodyInfoAvgStat.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "身体不适的基于时间的统计异常", e);
        }
    }

    /**
     * 统计身体基本情况的基于时间的统计
     *
     * @param sf
     * @return
     */
    public List<BodyInfoDateStat> getInfoDateStat(BodyInfoDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String sql= """
                    select indexValue,count(0) as totalCount,sum(weight) as totalWeight,sum(height) as totalHeight,sum(bmi) as totalBmi
                    from ( select {date_group_field} as indexValue,weight,height,bmi from body_info
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString())
                     .replace("{date_group_field}",MysqlUtil.dateTypeMethod("record_time", dateGroupType));
            List<BodyInfoDateStat> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE, BodyInfoDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计身体基本情况的基于时间的统计异常", e);
        }
    }

}
