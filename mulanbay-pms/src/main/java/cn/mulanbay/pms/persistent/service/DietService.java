package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.Diet;
import cn.mulanbay.pms.persistent.domain.FoodCategory;
import cn.mulanbay.pms.persistent.dto.food.*;
import cn.mulanbay.pms.persistent.dto.life.NameCountDTO;
import cn.mulanbay.pms.persistent.enums.*;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.food.diet.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DietService extends BaseHibernateDao {

    /**
     * 获取最后一次饮食
     * @param sf
     * @return
     */
    public Diet getLastDiet(LastDietSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String hql = """
                    from Diet
                    {query_para}
                    order by occurTime desc
                    """;
            hql = hql.replace("{query_para}",pr.getParameterString());
            Diet diet = this.getEntity(hql, Diet.class,pr.getParameterValue());
            return diet;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取最后一次饮食异常", e);
        }
    }

    /**
     * 饮食分析
     *
     * @param sf 查询条件
     * @return
     */
    public List<DietAnalyseStat> getDietAnalyseStat(DietAnalyseSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String fieldName = DietStatField.FOODS.getFieldName();
            if (sf.getField() != DietStatField.CLASS_NAME && sf.getField() != DietStatField.TYPE) {
                fieldName = sf.getField().getFieldName();
            }
            String statSql = """
                    select * from (
                    select name,count(0) as totalCount from
                    (
                    select substring_index(substring_index(a.{field_name},',',b.help_topic_id+1),',',-1)  as name
                    from diet a join mysql.help_topic b
                    on b.help_topic_id < (length(a.{field_name}) - length(replace(a.{field_name},',',''))+1)
                    {query_para}
                    and a.{field_name} is not null
                    ) as res
                    group by name
                    ) as aaa
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{field_name}",fieldName);
            if (sf.getMinCount() > 0) {
                statSql+=" where totalCount >= " + sf.getMinCount();
            }
            if (sf.getChartType() == ChartType.BAR || sf.getPage() > 0) {
                statSql+=" order by totalCount desc ";
            }
            List<DietAnalyseStat> list = this.getEntityListSI(statSql, pr.getPage(), pr.getPageSize(), DietAnalyseStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "饮食分析异常", e);
        }
    }

    /**
     * 饮食分析
     *
     * @param sf 查询条件
     * @return
     */
    public List<DietAnalyseStat> getDietAnalyseTypeStat(DietAnalyseSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String fieldName = sf.getField().getFieldName();
            String statSql = """
                    select * from (
                    select {field_name} as type,count(0) as totalCount from diet a
                    {query_para}
                    group by {field_name} ) as res
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{field_name}",fieldName);
            if (sf.getMinCount() > 0) {
                statSql+=" where totalCount >= " + sf.getMinCount();
            }
            if (sf.getChartType() == ChartType.BAR || sf.getPage() > 0) {
                statSql+=" order by totalCount desc ";
            }
            List<Object[]> list = this.getEntityListSI(statSql, pr.getPage(), pr.getPageSize(),Object[].class, pr.getParameterValue());
            List<DietAnalyseStat> res = new ArrayList<>();
            for (Object[] oo : list) {
                DietAnalyseStat das = new DietAnalyseStat();
                das.setTotalCount(Long.valueOf(oo[1].toString()));
                String name = this.getTypeName(oo[0],sf.getField());
                if(name==null){
                    das.setName("类型" + oo[0].toString());
                }else{
                    das.setName(name);
                }
                res.add(das);
            }
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "饮食分析异常", e);
        }
    }

    private String getTypeName(Object index,DietStatField statField){
        if(index==null){
            return null;
        }
        int type = Integer.parseInt(index.toString());
        switch (statField){
            case FOOD_TYPE -> {
                FoodType ft = FoodType.getFoodType(type);
                return ft.getName();
            }
            case DIET_SOURCE -> {
                DietSource ft = DietSource.getDietSource(type);
                return ft.getName();
            }
            case DIET_TYPE -> {
                DietType ft = DietType.getDietType(type);
                return ft.getName();
            }

        }
        return null;
    }

    /**
     * 饮食比较
     *
     * @param sf 查询条件
     * @return
     */
    public List<DietCompareStat> getDietCompareStat(DietCompareSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select diet_type as dietType,diet_source as dietSource ,count(0) as totalCount,sum(price) as totalPrice,sum(score) as totalScore from diet
                    {query_para}
                    group by diet_type,diet_source
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<Object[]> list = this.getEntityListSI(statSql, pr.getPage(), pr.getPageSize(),Object[].class, pr.getParameterValue());
            List<DietCompareStat> res = new ArrayList<>();
            for (Object[] oo : list) {
                DietCompareStat das = new DietCompareStat();
                das.setTotalCount(Long.valueOf(oo[2].toString()));
                DietType dietType = DietType.getDietType(Integer.parseInt(oo[0].toString()));
                das.setDietType(dietType);
                DietSource dietSource = DietSource.getDietSource(Integer.parseInt(oo[1].toString()));
                das.setDietSource(dietSource);
                das.setTotalPrice(BigDecimal.valueOf(Double.parseDouble(oo[3].toString())));
                if (oo[4] != null) {
                    das.setTotalScore(BigInteger.valueOf(Long.parseLong(oo[4].toString())));
                } else {
                    das.setTotalScore(BigInteger.ZERO);
                }
                res.add(das);
            }
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "饮食比较异常", e);
        }
    }

    /**
     * 饮食分析
     *
     * @param sf 查询条件
     * @return
     */
    public List<DietPriceAnalyseTypeStat> getDietPriceAnalyseTypeStat(DietPriceAnalyseSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String fieldName = sf.getStatField().getFieldName();
            String statSql = """
                    select * from (
                    select {field_name} as type,count(0) as totalCount,sum(price) as totalPrice from diet a
                    {query_para}
                    group by {field_name} ) as res
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{field_name}",fieldName);
            List<Object[]> list = this.getEntityListSI(statSql, pr.getPage(), pr.getPageSize(),Object[].class, pr.getParameterValue());
            List<DietPriceAnalyseTypeStat> res = new ArrayList<>();
            for (Object[] oo : list) {
                DietPriceAnalyseTypeStat das = new DietPriceAnalyseTypeStat();
                das.setTotalCount(Long.valueOf(oo[1].toString()));
                das.setTotalPrice(BigDecimal.valueOf(Double.parseDouble(oo[2].toString())));
                String name = this.getTypeName(oo[0],sf.getStatField());
                if(name==null){
                    das.setName("类型" + oo[0].toString());
                }else{
                    das.setName(name);
                }
                res.add(das);
            }
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "饮食分析异常", e);
        }
    }

    /**
     * 获取饮食分类
     *
     * @return
     */
    public List<String> getDietCateList(DietCateSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select distinct {field_name} from diet
                    {query_para}
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{field_name}",sf.getFieldName());
            return this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取饮食分类异常", e);
        }
    }

    /**
     * 获取总价
     *
     * @return
     */
    public BigDecimal getTotalPrice(Long userId, Date startTime, Date endTime) {
        try {
            String sql = "select sum(price) from diet where user_id=?1 and occur_time>=?2 and occur_time<=?3 ";
            BigDecimal dd = this.getEntity(sql, BigDecimal.class, userId, startTime, endTime);
            return dd;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取总价异常", e);
        }
    }

    /**
     * 获取饮食(不区分餐次)，相识度比对使用
     *
     * @return
     */
    public List<DietTypeFoodStat> getDietTypeFoodStat(DietVarietySH sf) {
        try {
            //sf.setDietType(null);
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    SELECT
                    MAX(CASE diet_type WHEN 0 THEN foods ELSE 0 END ) breakfast,
                    MAX(CASE diet_type WHEN 1 THEN foods ELSE 0 END ) lunch,
                    MAX(CASE diet_type WHEN 2 THEN foods ELSE 0 END ) dinner,
                    MAX(CASE diet_type WHEN 3 THEN foods ELSE 0 END ) other
                    FROM
                    (SELECT DATE_FORMAT(occur_time,'%Y-%m-%d') as ot,diet_type,foods FROM diet
                    {query_para}
                    ) as ss
                    GROUP BY ot order by ot
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            return this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, DietTypeFoodStat.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取饮食异常", e);
        }
    }

    /**
     * 获取分类列表
     *
     * @return
     */
    public List<FoodCategory> getFoodCategoryList() {
        try {
            String hql = "from FoodCategory where status=?1 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,FoodCategory.class,CommonStatus.ENABLE);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取分类列表异常", e);
        }
    }


    /**
     * 获取用户ID列表
     *
     * @return
     */
    public List<Long> getUserIdList(Date startDate, Date endDate) {
        try {
            String hql = "select distinct userId from Diet where occurTime>=?1 and occurTime<=?2 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Long.class, startDate, endDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户ID列表异常", e);
        }
    }


    /**
     * 按价格来统计
     *
     * @param sf
     * @return
     */
    public List<DietPriceAnalyseStat> getDietPriceAnalyseStat(DietPriceAnalyseSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,count(0) as totalCount,sum(price) as totalPrice
                    from (
                    select {date_group_field} as indexValue,price from diet
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{date_group_field}",MysqlUtil.dateTypeMethod("occur_time", dateGroupType));
            List<DietPriceAnalyseStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, DietPriceAnalyseStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "按价格来统计异常", e);
        }
    }

    /**
     * 饮食点统计
     *
     * @param sf
     * @return
     */
    public List<DietTimeStat> timeStatDiet(DietTimeStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select {time_group_field} as timeGroup,{date_group_field} as value from diet
                    {query_para}
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{time_group_field}",MysqlUtil.dateTypeMethod("occur_time", sf.getGroupType()))
                             .replace("{date_group_field}",MysqlUtil.dateTypeMethod("occur_time", DateGroupType.HOURMINUTE));
            List args = pr.getParameterValueList();
            List<DietTimeStat> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE, DietTimeStat.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "饮食点统计异常", e);
        }
    }

    /**
     * 获取标签列表
     *
     * @param sf
     * @return
     */
    public List<NameCountDTO> statTags(DietWordCloudSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String field = sf.getField();
            String statSql = """
                    select name,count(0) as counts from
                    (
                    select (substring_index(substring_index(a.col,',',b.help_topic_id+1),',',-1)) as name
                    from
                    (select {field_name} as col from diet
                    {query_para}
                    ) as a join
                    mysql.help_topic as b
                    on b.help_topic_id < (char_length(a.col) - char_length(replace(a.col,',',''))+1)
                    ) as res group by name
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString())
                             .replace("{field_name}",field);
            List<NameCountDTO> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE,NameCountDTO.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取标签列表异常", e);
        }
    }
}
