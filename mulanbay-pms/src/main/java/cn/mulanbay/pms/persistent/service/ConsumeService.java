package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.consume.*;
import cn.mulanbay.pms.persistent.enums.*;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.consume.consume.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.ROOT_ID;

/**
 * 消费记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Service
@Transactional
public class ConsumeService extends BaseHibernateDao {

    /**
     * 商品类型分组时是否以顶层分组
     */
    @Value("${mulanbay.consume.stat.groupTop:true}")
    boolean groupTop;

    /**
     * 新增消费记录
     *
     * @param consume
     */
    public void saveConsume(Consume consume) {
        try {
            this.saveEntity(consume);
            if (consume.getSoldPrice() != null) {
                addNewIncome(consume);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存消费记录异常", e);
        }
    }

    /**
     * 更新消费记录
     *
     * @param consume
     */
    public void updateConsume(Consume consume) {
        try {
            this.updateEntity(consume);
            if (consume.getSoldPrice() != null) {
                //查询收入
                String hql = "from ConsumeRefer where consumeId=?1 and type=?2";
                ConsumeRefer refer = this.getEntity(hql,ConsumeRefer.class,consume.getConsumeId(), BussType.INCOME);
                if (refer == null) {
                    addNewIncome(consume);
                } else {
                    Income income = this.getEntityById(Income.class,refer.getReferId());
                    if (!NumberUtil.priceEquals(consume.getSoldPrice(), income.getAmount())) {
                        //价格有改变更新
                        income.setAmount(consume.getSoldPrice());
                        this.updateEntity(income);
                    }
                }
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存消费记录异常", e);
        }
    }

    /**
     * 删除消费
     * @param consumeId
     * @return
     */
    public void deleteConsume(Long consumeId) {
        try {
            //删除消费关联
            String hql="delete from ConsumeRefer where consumeId=?1 ";
            this.updateEntities(hql,consumeId);

            //删除消费
            String hql2="delete from Consume where consumeId=?1 ";
            this.updateEntities(hql2,consumeId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除消费异常", e);
        }
    }

    /**
     * 更新消费记录
     *
     * @param consume
     */
    private void addNewIncome(Consume consume) {
        try {
            //自动增加一条收入
            Income income = new Income();
            income.setAmount(consume.getSoldPrice());
            income.setIncomeName(consume.getGoodsName() + " 出售");
            income.setOccurTime(consume.getInvalidTime());
            income.setRemark("保存消费记录时自动增加");
            income.setStatus(CommonStatus.ENABLE);
            income.setType(IncomeType.SECONDHAND_SOLD);
            income.setUserId(consume.getUserId());
            this.saveEntity(income);
            //保存关联记录
            ConsumeRefer refer = new ConsumeRefer();
            refer.setConsumeId(consume.getConsumeId());
            refer.setReferId(income.getIncomeId());
            refer.setType(BussType.INCOME);
            this.saveEntity(refer);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存收入异常", e);
        }
    }

    /**
     * 商品类型列表
     * @param userId
     * @return
     */
    public List<GoodsType> getGoodsTypeList(Long userId) {
        try {
            String hql="from GoodsType where userId=?1 and status=?2 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,GoodsType.class,userId,CommonStatus.ENABLE);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取商品类型列表异常", e);
        }
    }

    /**
     * 最近消费列表
     * @param userId
     * @param size
     * @return
     */
    public List<Consume> getLastestCosumeList(Long userId,int size) {
        try {
            String hql="from Consume where userId=?1 order by buyTime desc ";
            return this.getEntityListHI(hql,1,size,Consume.class,userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取最近消费列表异常", e);
        }
    }

    /**
     * 设置上级
     * @param consumeId
     * @param pid
     */
    public void setParent(Long consumeId,Long pid) {
        try {
            String hql = "update Consume set pid=?1 where consumeId=?2 ";
            this.updateEntities(hql,pid,consumeId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "设置上级异常", e);
        }
    }

    /**
     * 取消上级
     * @param consumeId
     */
    public void deleteParent(Long consumeId) {
        try {
            String hql = "update Consume set pid=null where consumeId=?1 ";
            this.updateEntities(hql,consumeId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "取消上级异常", e);
        }
    }

    /**
     * 取消下级
     * @param pid
     */
    public void deleteChildren(Long pid) {
        try {
            String hql = "update Consume set pid=null where pid=?1 ";
            this.updateEntities(hql,pid);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "取消下级异常", e);
        }
    }

    /**
     * 获取需要作废需要提醒的商品
     *
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    public List<Consume> getExpectInvalidList(Date startDate, Date endDate, Long userId) {
        try {
            String hql="from Consume where userId=?1 and expectInvalidTime>=?2 and expectInvalidTime<=?3 and invalidTime is null";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Consume.class,userId,startDate,endDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要作废需要提醒的商品异常", e);
        }
    }

    /**
     * 实时的分析统计
     *
     * @param sf
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<ConsumeRealTimeStat> getAnalyseStat(ConsumeAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String groupField = sf.getGroupField();
            GroupType type = sf.getType();
            StringBuffer sql = new StringBuffer();
            sql.append("select " + groupField);
            if (type == GroupType.COUNT) {
                //统计次数
                sql.append(" ,count(*) as cc from consume ");
            } else if (type == GroupType.TOTAL_PRICE) {
                //价格
                sql.append(" ,sum(total_price) as cc from consume ");
            } else if (type == GroupType.SHIPMENT) {
                //运费
                sql.append(" ,sum(shipment) as cc from consume ");
            }
            sql.append(pr.getParameterString());
            if (sf.isStat()) {
                sql.append(getStatCondition());
            }
            sql.append(" group by " + groupField);
            if (sf.getChartType() == ChartType.BAR) {
                sql.append(" order by cc desc");
            }
            List<Object[]> list = this.getEntityListSI(sql.toString(),NO_PAGE,NO_PAGE_SIZE,Object[].class, pr.getParameterValue());
            boolean gt = sf.getGroupTop()==null ? groupTop:sf.getGroupTop();
            if(gt && groupField.equals(GroupField.GOODS_TYPE)){
                return this.groupGoodsType(list,sf.getUserId());
            }
            List<ConsumeRealTimeStat> result = new ArrayList();
            for (Object[] oo : list) {
                ConsumeRealTimeStat bb = new ConsumeRealTimeStat();
                Object nameFiled = oo[0];
                if (nameFiled == null) {
                    bb.setName("未知");
                } else {
                    Object serierIdObj = oo[0];
                    if (serierIdObj == null) {
                        //防止为NULL
                        serierIdObj = "0";
                    }
                    String name = getSeriesName(serierIdObj.toString(), groupField);
                    bb.setName(name);
                }
                bb.setValue(new BigDecimal(oo[1].toString()));
                result.add(bb);
            }
            return result;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取实时统计异常", e);
        }
    }

    private List<ConsumeRealTimeStat> groupGoodsType(List<Object[]> list,Long userId){
        Map<Long,GoodsType> goodsTypeMap = this.getGoodsTypeMap(userId);
        Map<String,ConsumeRealTimeStat> resMap = new HashMap<>();
        for(Object[] oo : list){
            Object serierIdObj = oo[0];
            if (serierIdObj == null) {
                //防止为NULL
                serierIdObj = "0";
            }
            String topTypeName = this.findTopType(goodsTypeMap,Long.valueOf(serierIdObj.toString()));
            ConsumeRealTimeStat stat = resMap.get(topTypeName);
            if(stat==null){
                stat = new ConsumeRealTimeStat();
            }
            stat.setName(topTypeName);
            stat.add(new BigDecimal(oo[1].toString()));
            resMap.put(topTypeName,stat);
        }
        return resMap.values().stream().toList();
    }

    /**
     * 查询顶层类型
     *
     * @param map
     * @param typeId
     * @return
     */
    private String findTopType(Map<Long,GoodsType> map,Long typeId){
        GoodsType g = map.get(typeId);
        if(g==null){
            return "未知";
        }else{
            if(g.getPid()==ROOT_ID){
                return g.getTypeName();
            }else{
                return findTopType(map,g.getPid());
            }
        }
    }

    private Map<Long,GoodsType> getGoodsTypeMap(Long userId){
        Map<Long,GoodsType> map = new HashMap<>();
        List<GoodsType> typeList = this.getGoodsTypeList(userId);
        for(GoodsType g : typeList){
            map.put(g.getTypeId(),g);
        }
        return map;
    }

    /**
     * 实时的分析统计(基于树形结构)
     *
     * @param sf
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<ConsumeRealTimeTreeStat> getAnalyseTreeStat(ConsumeAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            GroupType type = sf.getType();
            StringBuffer sql = new StringBuffer();
            sql.append("select goods_type_id as goodsTypeId");
            if (type == GroupType.COUNT) {
                //统计次数
                sql.append(" ,count(0) as value from consume ");
            } else if (type == GroupType.TOTAL_PRICE) {
                //价格
                sql.append(" ,sum(total_price) as value from consume ");
            } else if (type == GroupType.SHIPMENT) {
                //运费
                sql.append(" ,sum(shipment) as value from consume ");
            }
            sql.append(pr.getParameterString());
            sql.append(" group by pid,goods_type_id ");
            sql.append(" order by pid ");
            List<ConsumeRealTimeTreeStat> list = this.getEntityListSI(sql.toString(),NO_PAGE,NO_PAGE_SIZE,ConsumeRealTimeTreeStat.class, pr.getParameterValue());
            if(StringUtil.isEmpty(list)){
                return new ArrayList<>();
            }
            for (ConsumeRealTimeTreeStat ts : list) {
                GoodsType t = this.getEntityById(GoodsType.class,ts.getGoodsTypeId());
                ts.setGoodsName(t.getTypeName());
                GoodsType parent = this.getEntityById(GoodsType.class,t.getPid());
                ts.setParentGoodsTypeId(parent.getTypeId());
                ts.setParentGoodsTypeName(parent.getTypeName());
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "实时的分析统计(基于树形结构)异常", e);
        }
    }

    /**
     * 使用时间统计
     *
     * @param sf
     * @return
     */
    public List<ConsumeUseTimeStat> getUseTimeStat(ConsumeUseTimeStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String groupField = sf.getGroupField();
            StringBuffer sql = new StringBuffer();
            sql.append("select " + groupField + " as name,sum(duration) as totalDuration,count(0) as totalCount from consume ");
            sql.append(pr.getParameterString());
            //sql.append(" and "+groupField+" is not null");
            sql.append(" group by " +groupField);
            List<ConsumeUseTimeStat> list = this.getEntityListSI(sql.toString(),pr.getPage(),pr.getPageSize(),ConsumeUseTimeStat.class,pr.getParameterValue());
            for (ConsumeUseTimeStat bean : list) {
                if(bean.getName()==null){
                    bean.setName("未知");
                    continue;
                }
                String name = bean.getName().toString();
                if(groupField.equals(GroupField.GOODS_TYPE)){
                    GoodsType gt = this.getEntityById(GoodsType.class,Long.valueOf(name));
                    bean.setName(gt.getTypeName());
                }else if(groupField.equals(GroupField.SOURCE)){
                    ConsumeSource bt = this.getEntityById(ConsumeSource.class,Long.valueOf(name));
                    bean.setName(bt.getSourceName());
                }else if(groupField.equals(GroupField.SECONDHAND)){
                    if(name.equals("1")){
                        bean.setName("二手");
                    }else{
                        bean.setName("非二手");
                    }
                }
            }
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "使用时间统计异常", e);
        }

    }

    /**
     * 获取统计图表中的系列名称
     *
     * @param seriesIdObj
     * @param groupField
     * @return
     */
    private String getSeriesName(String seriesIdObj, String groupField) {
        try {
            if (GroupField.SHOP_NAME.equals(groupField) || GroupField.BRAND.equals(groupField)) {
                return seriesIdObj;
            }
            Long seriesId = Long.parseLong(seriesIdObj);
            if (GroupField.SOURCE.equals(groupField)) {
                ConsumeSource bt = this.getEntityById(ConsumeSource.class, seriesId);
                if (bt == null) {
                    return "未知";
                } else {
                    return bt.getSourceName();
                }
            } else if (GroupField.GOODS_TYPE.equals(groupField)) {
                GoodsType gt =this.getEntityById(GoodsType.class, seriesId);
                if (gt == null) {
                    return "未知";
                } else {
                    return gt.getTypeName();
                }
            } else if (GroupField.PAYMENT.equals(groupField)) {
                Payment payment = Payment.getPayment(seriesId.intValue());
                if (payment == null) {
                    return "未知";
                } else {
                    return payment.getName();
                }
            }
            return "未知";
        } catch (BaseException e) {

            return "未知";
        }
    }

    /**
     * 获取购买记录关键字
     *
     * @return
     */
    public List<String> getTagsList(ConsumeTagSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql = "select distinct tags from consume ";
            sql += pr.getParameterString();
            sql += " and tags is not null ";
            return this.getEntityListSI(sql,sf.getPage(),sf.getPageSize(),String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取购买记录关键字异常", e);
        }
    }

    /**
     * 获取需要统计的条件
     *
     * @return
     */
    private String getStatCondition() {
        StringBuffer sb = new StringBuffer();
        //排除不需要统计的记录
        sb.append(" and goods_type_id in (select type_id from goods_type where stat =1 and pid=0 ) ");
        sb.append(" and stat != 0 ");
        return sb.toString();
    }

    /**
     * 获取消费记录的总成本（关联子集）
     * 不包含自身的价格成本
     *
     * @param rootId 消费记录ID
     */
    public ConsumeChildrenCostStat getChildrenTotalDeepCost(Long rootId) {
        try {
            String childrenIds = this.getConsumeChildrenIds(rootId);
            if(StringUtil.isEmpty(childrenIds)){
                return new ConsumeChildrenCostStat();
            }
            String statSql = """
                    select sum(total_price) as totalPrice,sum(sold_price) as soldPrice,count(0) as totalCount FROM consume WHERE consume_id in
                    ({ids})
                    """;
            statSql = statSql.replace("{ids}",childrenIds);
            ConsumeChildrenCostStat res = this.getEntitySQL(statSql,ConsumeChildrenCostStat.class);
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "获取消费记录的总成本异常", e);
        }
    }

    /**
     * 获取商品类型的子集ID
     * @param pid
     * @return
     */
    private String getGoodsTypeChildrenIds(Long pid){
        try {
            String sql = "select getGoodsTypeChildren("+pid+")";
            List<String> ll = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,String.class);
            String ids = ll.get(0);
            if(StringUtil.isEmpty(ids)){
                return null;
            }else{
                return ids.substring(1);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取商品类型的子集ID异常", e);
        }
    }

    /**
     * 获取消费的子集ID
     * @param pid
     * @return
     */
    private String getConsumeChildrenIds(Long pid){
        try {
            String sql = "select getConsumeChildren("+pid+")";
            List<String> ll = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,String.class);
            String ids = ll.get(0);
            if(StringUtil.isEmpty(ids)){
                return null;
            }else{
                return ids.substring(1);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取消费的子集ID异常", e);
        }
    }



    /**
     * 获取消费记录的总成本（只关联一层）
     * 不包含自身的价格成本
     *
     * @param rootId 消费记录ID
     */
    public ConsumeChildrenCostStat getChildrenTotalCost(Long rootId) {
        try {
            String sql = "select sum(total_price) as totalPrice,sum(sold_price) as soldPrice,count(0) as totalCount from consume where pid = ?1";
            ConsumeChildrenCostStat res = this.getEntitySQL(sql,ConsumeChildrenCostStat.class,rootId);
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "获取消费记录的总成本异常", e);
        }
    }

    /**
     * 获取子商品列表
     * @param rootId
     * @return
     */
    public List<ConsumeCascadeDTO> getChildrenDeepList(Long rootId){
        try {
            String childrenIds = this.getConsumeChildrenIds(rootId);
            if(StringUtil.isEmpty(childrenIds)){
                return new ArrayList<>();
            }
            String statSql = """
                    select consume_id as consumeId,pid,goods_name as goodsName,total_price as totalPrice FROM consume WHERE consume_id in
                    ({ids})
                    """;
            statSql = statSql.replace("{ids}",childrenIds);
            List<ConsumeCascadeDTO> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE,ConsumeCascadeDTO.class);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取子商品列表异常", e);
        }
    }


    /**
     * 按时间来统计
     *
     * @param sf
     * @return
     */
    public List<ConsumeDateStat> getDateStat(ConsumeDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            GroupType groupType = sf.getGroupType();
            String sql = """
                    select indexValue,count(0) as totalCount,sum(price) as totalPrice
                    from ( select {date_group_field} as indexValue,
                    {group_field} as price
                    from consume
                    {query_para}
                    ) tt group by indexValue
                     order by indexValue
                    """;
            sql = sql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("buy_time", dateGroupType))
                     .replace("{group_field}",groupType.getField())
                     .replace("{query_para}",pr.getParameterString());
            List<ConsumeDateStat> list = this.getEntityListSI(sql, pr.getPage(), pr.getPageSize(), ConsumeDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "按时间来统计异常", e);
        }
    }

    /**
     * 获取时间列表
     * @param sf
     * @return
     */
    public List<Date> getDateList(ConsumeDateStatSH sf) {
        try {
            String sql = """
                    select buy_time from consume
                    {query_para}
                     order by buy_time
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

    /**
     * 按标签来统计购买记录
     *
     * @param sf 查询条件
     * @return
     */
    public List<ConsumeTagsStat> getTagsStat(ConsumeTagsStatSH sf) {
        try {
            String sql = """
                    select tags ,count(0) as totalCount,sum(price) as totalPrice from consume
                    {query_para}
                    and tags is not null
                    group by tags
                    order by totalPrice desc
                    """;
            PageRequest pr = sf.buildQuery();
            sql = sql.replace("{query_para}",pr.getParameterString());
            List<ConsumeTagsStat> list = this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE, ConsumeTagsStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "按标签来统计购买记录异常", e);
        }
    }

    /**
     * 词云统计
     * @param sf
     * @return
     */
    public List<String> getWordCloudStat(ConsumeWordCloudSH sf) {
        try {
            String hql = """
                    select {field} from Consume
                    {query_para}
                    and  {field} is not null
                    """;
            PageRequest pr = sf.buildQuery();
            hql = hql.replace("{query_para}",pr.getParameterString())
                     .replace("{field}",sf.getField());
            List<String> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "词云统计异常", e);
        }
    }


    /**
     * 获取消费总值
     * @param startTime
     * @param endTime
     * @param userId
     * @param consumeType
     * @return
     */
    public BigDecimal statConsumeAmount(Date startTime, Date endTime, Long userId, Short consumeType) {
        try {
            String sql = "select sum(total_price) from consume where buy_time>=?1 and buy_time<=?2 and user_id=?3 ";
            if (consumeType != null) {
                sql += " and consume_type=" + consumeType;
            }
            List<BigDecimal> list = this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE,BigDecimal.class,startTime, endTime, userId);
            if (list.isEmpty() || list.get(0) == null) {
                return new BigDecimal(0);
            } else {
                return list.get(0);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取消费总值异常", e);
        }
    }

    /**
     * 获取消费总值
     * @param startTime
     * @param endTime
     * @param userId
     * @param goodsTypeId
     * @param tags
     * @return
     */
    public ConsumeBudgetStat statConsumeAmount(Date startTime, Date endTime, Long userId, Long goodsTypeId,String tags,Boolean icg) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select sum(total_price) as totalPrice,max(buy_time) as maxBuyDate from consume where buy_time>=?1 and buy_time<=?2 and user_id=?3");
            List args = new ArrayList();
            args.add(startTime);
            args.add(endTime);
            args.add(userId);
            int index =4;
            if(goodsTypeId!=null){
                if(icg==null||!icg){
                    sb.append(" and goods_type_id=?"+(index++));
                    args.add(goodsTypeId);
                }else{
                    String childrenIds = this.getGoodsTypeChildrenIds(goodsTypeId);
                    if(StringUtil.isEmpty(childrenIds)){
                        sb.append(" and goods_type_id=?"+(index++));
                        args.add(goodsTypeId);
                    }else{
                        sb.append(" and (goods_type_id=?"+(index++)+" or goods_type_id in ("+childrenIds+"))");
                        args.add(goodsTypeId);
                    }
                }
            }
            if(StringUtil.isNotEmpty(tags)){
                sb.append(" and (goods_name like?"+(index++)+" or tags like?"+(index++)+")");
                args.add("%"+tags+"%");
                args.add("%"+tags+"%");
            }
            List<ConsumeBudgetStat> list = this.getEntityListSI(sb.toString(),NO_PAGE,NO_PAGE_SIZE,ConsumeBudgetStat.class, args.toArray());
            if (list.isEmpty() || list.get(0) == null) {
                return new ConsumeBudgetStat(null,null);
            } else {
                return list.get(0);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取消费总值异常", e);
        }
    }

    /**
     * 获取根据消费类型分组的消费总额
     *
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     */
    public List<ConsumeConsumeTypeStat> getConsumeTypeAmountStat(Date startTime, Date endTime, Long userId) {
        try {
            String sql = "select consume_type as consumeType,count(0) as totalCount,sum(total_price) as totalPrice from consume where buy_time>=?1 and buy_time<=?2 and user_id=?3 group by consume_type";
            List args = new ArrayList();
            args.add(startTime);
            args.add(endTime);
            args.add(userId);
            List<ConsumeConsumeTypeStat> list = this.getEntityListSI(sql, NO_PAGE, NO_PAGE_SIZE, ConsumeConsumeTypeStat.class, startTime, endTime, userId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取根据消费类型分组的消费总额异常", e);
        }
    }

    /**
     * 按时间来统计出售
     *
     * @param sf
     * @return
     */
    public List<ConsumeSoldDateStat> getDateSoldStat(ConsumeSoldStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            DateGroupType dateGroupType = sf.getDateGroupType();
            String sql = """
                    select indexValue,count(0) as totalCount,sum(price) as totalPrice
                    from ( select {date_group_field} as indexValue,
                    sold_price as price
                    from consume
                    where sold_price is not null
                    {query_para}
                    ) tt group by indexValue
                     order by indexValue
                    """;
            sql = sql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("invalid_time", dateGroupType))
                    .replace("{query_para}",pr.getParameterString());
            List<ConsumeSoldDateStat> list = this.getEntityListSINP(sql, ConsumeSoldDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "按时间来统计出售异常", e);
        }
    }

    /**
     * 按是否二手来统计出售
     *
     * @param sf
     * @return
     */
    public List<ConsumeSoldSecondhandStat> getSoldSecondhandStat(ConsumeSoldStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            String sql = """
                    select secondhand,count(0) as totalCount,sum(sold_price) as totalPrice
                    from consume
                    where sold_price is not null
                    {query_para}
                    group by secondhand
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString());
            List<ConsumeSoldSecondhandStat> list = this.getEntityListSINP(sql, ConsumeSoldSecondhandStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "按是否二手来统计出售异常", e);
        }
    }

    /**
     * 统计出售
     *
     * @param sf
     * @return
     */
    public List<ConsumeSoldStat> getSoldStat(ConsumeSoldStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql = """
                    select secondhand,sold,count(0) as totalCount
                    from
                    (select secondhand,
                        CASE
                           WHEN sold_price is null THEN 0
                           WHEN sold_price is not null THEN 1
                           ELSE 0
                        END AS sold
                   from consume
                   {query_para}
                   ) as res
                   group by secondhand,sold
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString());
            List<ConsumeSoldStat> list = this.getEntityListSINP(sql, ConsumeSoldStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计出售异常", e);
        }
    }

    /**
     * 统计出售的折扣比例
     *
     * @param sf
     * @return
     */
    public List<ConsumeSoldRateStat> getSoldRateStat(ConsumeSoldStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            String sql = """
                   select rate,count(0) as totalCount
                   from (
                   select ROUND(sold_price/total_price*10) as rate
                   from consume
                   where sold_price is not null
                    {query_para}
                    ) as res
                    group by rate
                    """;
            sql = sql.replace("{query_para}",pr.getParameterString());
            List<ConsumeSoldRateStat> list = this.getEntityListSINP(sql, ConsumeSoldRateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "统计出售的折扣比例异常", e);
        }
    }

    /**
     * 获取商品名称列表，相识度比对使用
     *
     * @return
     */
    public List<String> getGoodsNameList(ConsumeSimilaritySH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String sql = "select goods_name from consume ";
            sql += pr.getParameterString();
            sql += " order by " + sf.getOrderByField();
            return this.getEntityListSI(sql, sf.getPage(), sf.getPageSize(),String.class, pr.getParameterValue());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取商品名称列表异常", e);
        }
    }


    public static class GroupField{

        public static String GOODS_TYPE="goods_type_id";

        public static String SOURCE="source_id";

        public static String SECONDHAND="secondhand";

        public static String SHOP_NAME="shop_name";

        public static String BRAND="brand";

        public static String PAYMENT="payment";


    }

}
