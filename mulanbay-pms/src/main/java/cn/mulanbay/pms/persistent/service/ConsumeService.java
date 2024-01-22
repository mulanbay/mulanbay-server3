package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.consume.*;
import cn.mulanbay.pms.persistent.enums.*;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.util.PriceUtil;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.consume.consume.ConsumeAnalyseStatSH;
import cn.mulanbay.pms.web.bean.req.consume.consume.ConsumeTagSH;
import cn.mulanbay.pms.web.bean.req.consume.consume.ConsumeUseTimeStatSH;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 消费记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Service
@Transactional
public class ConsumeService extends BaseHibernateDao {

    private static class GroupField{

        public static String GOODS_TYPE="goods_type_id";

        public static String SOURCE="source_id";

        public static String SECONDHAND="secondhand";

        public static String SHOP_NAME="shop_name";

        public static String BRAND="brand";

        public static String PAYMENT="payment";


    }

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
                    if (!PriceUtil.priceEquals(consume.getSoldPrice(), income.getAmount())) {
                        //价格有改变更新
                        income.setAmount(consume.getSoldPrice());
                        income.setModifyTime(new Date());
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
     * 更新消费记录
     *
     * @param consume
     */
    private void addNewIncome(Consume consume) {
        try {
            //自动增加一条收入
            Income income = new Income();
            income.setCreatedTime(new Date());
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
            refer.setCreatedTime(new Date());
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
     * 获取雷达统计分组中的最大值
     *
     * @param sf
     * @return
     */
    public Long getMaxValue(ConsumeAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String paraStr = pr.getParameterString();
            StringBuffer sb = new StringBuffer();
            if (sf.getType() == GroupType.COUNT) {
                sb.append("select max(vv) from (");
                sb.append("select " + sf.getGroupField() + ",count(*) vv from consume ");
                sb.append(paraStr);
                sb.append(" group by " + sf.getGroupField());
                sb.append(") as aa ");
            } else if (sf.getType() == GroupType.TOTALPRICE) {
                sb.append("select max(total_price) as vv from consume ");
                sb.append(paraStr);
            } else {
                sb.append("select max(shipment) as vv from consume ");
                sb.append(paraStr);
            }
            List list = this.getEntityListSI(sb.toString(),NO_PAGE,NO_PAGE_SIZE,Object.class, pr.getParameterValue());
            if (list.isEmpty()) {
                return 0L;
            }
            Object oo = list.get(0);
            if (sf.getType() == GroupType.COUNT) {
                BigInteger vv = (BigInteger) oo;
                return vv.longValue();
            } else {
                BigDecimal vv = (BigDecimal) oo;
                return vv.longValue();
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取雷达统计分组中的最大值异常", e);
        }
    }

    /**
     * 实时的分析雷达统计
     *
     * @param sf
     * @return
     */
    public List<ConsumeRadarStat> getRadarStat(ConsumeAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String groupField = sf.getGroupField();
            GroupType type = sf.getType();
            StringBuffer sql = new StringBuffer();
            sql.append("select groupId,indexValue,count(*) totalCount,sum(pp) totalPrice from ( ");
            if ("price_region".equals(groupField)) {
                sql.append("select getPriceRegionId(total_price," + sf.getUserId() + ") as groupId,");
            } else {
                sql.append("select " + groupField + " as groupId,");
            }
            sql.append(MysqlUtil.dateTypeMethod("buy_time", sf.getDateGroupType()) + " as indexValue");
            if (type == GroupType.COUNT || type == GroupType.TOTALPRICE) {
                //统计次数
                sql.append(" ,total_price as pp from consume ");
            } else if (type == GroupType.SHIPMENT) {
                //运费
                sql.append(" ,shipment as pp from consume ");
            }
            sql.append(pr.getParameterString());
            if (sf.isStat()) {
                sql.append(getStatCondition());
            }
            sql.append(") as aa ");
            sql.append("group by groupId,indexValue ");
            sql.append("order by indexValue,groupId ");
            List<ConsumeRadarStat> list = this.getEntityListSI(sql.toString(), pr.getPage(), pr.getPageSize(), ConsumeRadarStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取实时的分析雷达统计异常", e);
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
            if ("price_region".equals(groupField)) {
                sql.append("select getPriceRegionId(total_price," + sf.getUserId() + ") as priceRegion");
            } else {
                sql.append("select " + groupField);
            }
            if (type == GroupType.COUNT) {
                //统计次数
                sql.append(" ,count(*) as cc from consume ");
            } else if (type == GroupType.TOTALPRICE) {
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
            if ("price_region".equals(groupField)) {
                sql.append(" group by priceRegion");
            } else {
                sql.append(" group by " + groupField);
            }
            if (sf.getChartType() == ChartType.BAR) {
                sql.append(" order by cc desc");
            }
            List<Object[]> list = this.getEntityListSI(sql.toString(),NO_PAGE,NO_PAGE_SIZE,Object[].class, pr.getParameterValue());
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
                    // ID值大部分情况下不需要
                    //bb.setId(Integer.valueOf(serierIdObj.toString()));
                }
                double value = Double.valueOf(oo[1].toString());
                bb.setValue(value);
                result.add(bb);
            }
            return result;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取实时统计异常", e);
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
            return this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,String.class, pr.getParameterValue());
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
            String sql = "select getConsumeChildren("+rootId+")";
            List<String> ll = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,String.class);
            String ids = ll.get(0);
            if(StringUtil.isEmpty(ids)){
                return new ConsumeChildrenCostStat();
            }
            ids = ids.substring(1);
            String statSql = """
                    select sum(total_price) as totalPrice,sum(sold_price) as soldPrice,count(0) as totalCount FROM consume WHERE consume_id in
                    ({ids})
                    """;
            statSql = statSql.replace("{ids}",ids);
            ConsumeChildrenCostStat res = this.getEntitySQL(statSql,ConsumeChildrenCostStat.class);
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "获取消费记录的总成本异常", e);
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
            String sql = "select getConsumeChildren("+rootId+")";
            List<String> ll = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,String.class);
            String ids = ll.get(0);
            if(StringUtil.isEmpty(ids)){
                return new ArrayList<>();
            }
            ids = ids.substring(1);
            String statSql = """
                    select consume_id as consumeId,pid,goods_name as goodsName,total_price as totalPrice FROM consume WHERE consume_id in
                    ({ids})
                    """;
            statSql = statSql.replace("{ids}",ids);
            List<ConsumeCascadeDTO> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE,ConsumeCascadeDTO.class);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取子商品列表异常", e);
        }
    }

}
