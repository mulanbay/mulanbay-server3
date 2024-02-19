package cn.mulanbay.pms.web.controller.life;

import cn.mulanbay.ai.nlp.processor.NLPProcessor;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Experience;
import cn.mulanbay.pms.persistent.domain.ExperienceDetail;
import cn.mulanbay.pms.persistent.dto.life.*;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.ExperienceCostStatType;
import cn.mulanbay.pms.persistent.service.ExperienceService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.life.experience.*;
import cn.mulanbay.pms.web.bean.req.main.UserCommonFrom;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * 人生经历表
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/experience")
public class ExperienceController extends BaseController {

    @Autowired
    ExperienceService experienceService;

    @Autowired
    NLPProcessor nlpProcessor;

    private static Class<Experience> beanClass = Experience.class;

    /**
     * 获取类型树列表
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(ExperienceSH ts) {
        try {
            PageResult<Experience> qr = this.getResult(ts);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (Experience le : qr.getBeanList()) {
                TreeBean tb = new TreeBean();
                tb.setId(le.getExpId());
                tb.setText(le.getExpName());
                list.add(tb);
            }
            return callback(list);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取类型树列表异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ExperienceSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<Experience> getResult(ExperienceSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setPageSize(sf.getPageSize());
        pr.setBeanClass(beanClass);
        Sort s = new Sort("startDate", Sort.DESC);
        pr.addSort(s);
        PageResult<Experience> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ExperienceForm form) {
        Experience bean = new Experience();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "expId") Long expId) {
        Experience bean = baseService.getObject(beanClass,expId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ExperienceForm form) {
        Experience bean = baseService.getObject(beanClass,form.getExpId());
        BeanCopy.copy(form, bean);
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        String[] ss = deleteRequest.getIds().split(",");
        for(String s : ss){
            experienceService.deleteExperience(Long.valueOf(s));
        }
        return callback(null);
    }

    /**
     * 地图统计
     * @return
     */
    @RequestMapping(value = "/mapStat", method = RequestMethod.GET)
    public ResultBean mapStat(ExperienceMapStatSH sf) {
        switch (sf.getMapType() ){
            case LOCATION:
                List<ExperienceMapStat> list = experienceService.getMapStat(sf);
                return this.callback(createLocationMapStat(list, sf.getStatType(), sf.getUserId(), sf.getStartDate(), sf.getEndDate()));
            case LC_NAME:
                return this.callback(createLcNameMapStat(sf));
            case WORLD:
                return this.callback(createWorldMapData(sf));
            default:
                return this.callback(createChinaMapData(sf));
        }
    }

    /**
     * 生成统计图表
     * @param sf
     * @return
     */
    private MapStatChartData createChinaMapData(ExperienceMapStatSH sf){
        MapStatChartData chartData = new MapStatChartData();
        chartData.setMapName("china");
        chartData.setTitle("人生去过的地方统计");

        List<ExperienceMapStat> list = experienceService.getMapStat(sf);
        int ps = list.size();
        String subText = "一共去过" + ps + "个省或直辖市";
        subText += "(占比:" + (int) NumberUtil.getPercentValue(ps, 34, 0) + "%)";
        if (sf.getStartDate() != null && sf.getEndDate() != null) {
            subText += "," + DateUtil.getFormatDate(sf.getStartDate(), DateUtil.FormatDay1) + "~" + DateUtil.getFormatDate(sf.getEndDate(), DateUtil.FormatDay1);
        }
        chartData.setSubTitle(subText);
        int maxValue = 0;
        for (ExperienceMapStat dd : list) {
            String name = dd.getName();
            MapStatChartDetail detail = new MapStatChartDetail();
            detail.setName(name);
            if (sf.getStatType() == ExperienceMapStatSH.StatType.COUNT) {
                detail.setValue(dd.getTotalCount().intValue());
                if (dd.getTotalCount().intValue() > maxValue) {
                    maxValue = dd.getTotalCount().intValue();
                }
            } else if (sf.getStatType() == ExperienceMapStatSH.StatType.DAYS) {
                detail.setValue(dd.getTotalDays().intValue());
                if (dd.getTotalDays().intValue() > maxValue) {
                    maxValue = dd.getTotalDays().intValue();
                }
            } else {
                detail.setValue(dd.getTotalCost().intValue());
                if (dd.getTotalCost().intValue() > maxValue) {
                    maxValue = dd.getTotalCost().intValue();
                }
            }
            detail.setCounts(dd.getTotalCount().intValue());
            detail.setDays(dd.getTotalDays().intValue());
            detail.setCost(dd.getTotalCost().intValue());
            chartData.addDetail(detail);
        }
        chartData.setMaxValue(maxValue);
        return chartData;
    }

    /**
     * 生成世界统计图表
     * @param sf
     * @return
     */
    private WorldMapStatChartData createWorldMapData(ExperienceMapStatSH sf){
        WorldMapStatChartData chartData = new WorldMapStatChartData();
        chartData.setMapName("world");
        chartData.setTitle("人生去过的国家统计");

        List<ExperienceWorldMapStat> list = experienceService.getWorldMapStat(sf);
        int maxValue = 0;
        Map<String, double[]> geoMapData = new HashMap<>();
        for (ExperienceWorldMapStat dd : list) {
            String name = dd.getCountryName();
            MapStatChartDetail detail = new MapStatChartDetail();
            detail.setName(name);
            if (sf.getStatType() == ExperienceMapStatSH.StatType.COUNT) {
                detail.setValue(dd.getTotalCount().intValue());
                if (dd.getTotalCount().intValue() > maxValue) {
                    maxValue = dd.getTotalCount().intValue();
                }
            } else if (sf.getStatType() == ExperienceMapStatSH.StatType.DAYS) {
                detail.setValue(dd.getTotalDays().intValue());
                if (dd.getTotalDays().intValue() > maxValue) {
                    maxValue = dd.getTotalDays().intValue();
                }
            } else {
                detail.setValue(dd.getTotalCost().intValue());
                if (dd.getTotalCost().intValue() > maxValue) {
                    maxValue = dd.getTotalCost().intValue();
                }
            }
            //地理位置
            if(StringUtil.isEmpty(dd.getCountryLocation())){
                geoMapData.put(name, new double[]{0, 0});
            }else{
                String[] geo = dd.getCountryLocation().split(",");
                geoMapData.put(name,new double[]{Double.valueOf(geo[0]), Double.valueOf(geo[1])});
            }
            detail.setCounts(dd.getTotalCount().intValue());
            detail.setDays(dd.getTotalDays().intValue());
            detail.setCost(dd.getTotalCost().intValue());
            chartData.addDetail(detail);
        }
        ExperienceMapStatSH.StatType statType = sf.getStatType();
        if (statType == ExperienceMapStatSH.StatType.COUNT){
            chartData.setUnit("次");
        }else if (statType == ExperienceMapStatSH.StatType.DAYS){
            chartData.setUnit("天");
        }else{
            chartData.setUnit("元");
        }
        chartData.setMaxValue(maxValue);
        chartData.setGeoCoordMapData(geoMapData);
        return chartData;
    }

    /**
     * 基于地点的统计
     *
     * @param list
     * @return
     */
    private LocationMapStatChartData createLocationMapStat(List<ExperienceMapStat> list, ExperienceMapStatSH.StatType statType, Long userId, Date startDate, Date endDate) {
        LocationMapStatChartData chartData = new LocationMapStatChartData();
        chartData.setTitle("人生去过的地方统计");
        if (statType == ExperienceMapStatSH.StatType.COUNT){
            chartData.setName("旅行次数");
            chartData.setUnit("次");
        }else if (statType == ExperienceMapStatSH.StatType.DAYS){
            chartData.setName("旅行天数");
            chartData.setUnit("天");
        }else{
            chartData.setName("旅行花费");
            chartData.setUnit("元");
        }
        List<MapData> dataList = new ArrayList<>();
        List<ExperienceMapStat> newList = convertMapStatLocation(list);
        int maxValue = 0;
        int minValue = 0;
        for (ExperienceMapStat dd : newList) {
            if (statType == ExperienceMapStatSH.StatType.COUNT) {
                int count = dd.getTotalCount().intValue();
                MapData c = new MapData(dd.getName(), count);
                dataList.add(c);
                if (count > maxValue) {
                    maxValue = count;
                }
                if (count < minValue) {
                    minValue = count;
                }
            }else if (statType == ExperienceMapStatSH.StatType.DAYS) {
                int days = dd.getTotalDays().intValue();
                MapData c = new MapData(dd.getName(), days );
                dataList.add(c);
                if (days > maxValue) {
                    maxValue = days;
                }
                if (days < minValue) {
                    minValue = days;
                }
            } else {
                double money = dd.getTotalCost()==null ? 0:dd.getTotalCost().doubleValue();
                MapData c = new MapData(dd.getName(), money);
                dataList.add(c);
                if (money > maxValue) {
                    maxValue = (int) money;
                }
                if (money < minValue) {
                    minValue = (int) money;
                }
            }
        }
        chartData.setDataList(dataList);
        chartData.setMax(maxValue);
        chartData.setMin(minValue);
        chartData.setGeoCoordMapData(getGeoMapData(userId, startDate, endDate));
        return chartData;

    }

    /**
     * 基于经历的统计(即采用LifeExperience表数据)
     *
     * @param sf
     * @return
     */
    private LocationMapStatChartData createLcNameMapStat(ExperienceMapStatSH sf) {
        LocationMapStatChartData chartData = new LocationMapStatChartData();
        chartData.setTitle("人生去过的地方统计");
        if (sf.getStatType() == ExperienceMapStatSH.StatType.COUNT){
            chartData.setName("旅行次数");
            chartData.setUnit("次");
        }else if (sf.getStatType() == ExperienceMapStatSH.StatType.DAYS){
            chartData.setName("旅行天数");
            chartData.setUnit("天");
        }else{
            chartData.setName("旅行花费");
            chartData.setUnit("元");
        }
        List<MapData> dataList = new ArrayList<>();
        //获取经历列表
        ExperienceSH les = new ExperienceSH();
        BeanCopy.copy(sf,les);
        les.setPage(PageRequest.NO_PAGE);
        PageRequest pr = les.buildQuery();
        pr.setBeanClass(beanClass);
        List<Experience> list = baseService.getBeanList(pr);
        Map<String, double[]> geoMap = new HashMap<>();
        int maxValue = 0;
        for (Experience dd : list) {
            if(StringUtil.isEmpty(dd.getLocation())){
                continue;
            }
            String name = dd.getExpName();
            if (sf.getStatType() == ExperienceMapStatSH.StatType.COUNT) {
                MapData c = new MapData(name, 1);
                dataList.add(c);
            }else if (sf.getStatType() == ExperienceMapStatSH.StatType.DAYS) {
                int days = dd.getDays();
                MapData c = new MapData(name, days);
                dataList.add(c);
                if (days > maxValue) {
                    maxValue = days;
                }
            }else {
                double m = dd.getCost()==null ? 0 : dd.getCost().doubleValue();
                MapData c = new MapData(name, m);
                dataList.add(c);
                if (m > maxValue) {
                    maxValue = (int) m;
                }
            }
            String[] geo = dd.getLocation().split(",");
            geoMap.put(name,new double[]{Double.parseDouble(geo[0]), Double.parseDouble(geo[1])});
        }
        chartData.setDataList(dataList);
        chartData.setMax(maxValue);
        chartData.setGeoCoordMapData(geoMap);
        return chartData;

    }

    /**
     * 获取地理位置数据定义
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    private Map<String, double[]> getGeoMapData(Long userId, Date startDate, Date endDate) {
        if (startDate == null) {
            startDate = new Date(0L);
        }
        if (endDate == null) {
            endDate = new Date();
        }
        List<CityLocationDTO> list = experienceService.statCityLocation(userId, startDate, endDate);
        Map<String, double[]> geoMapData = new HashMap<>();
        for (CityLocationDTO ss : list) {
            String[] geo = ss.getLocation().split(",");
            geoMapData.put(ss.getCity(), new double[]{Double.parseDouble(geo[0]), Double.parseDouble(geo[1])});

        }
        return geoMapData;
    }

    /**
     * 把location字段根据分割符重新统计
     *
     * @param list
     * @return
     */
    private List<ExperienceMapStat> convertMapStatLocation(List<ExperienceMapStat> list) {
        Map<String, ExperienceMapStat> map = new HashMap<>();
        for (ExperienceMapStat dd : list) {
            String name = dd.getName();
            ExperienceMapStat stat = map.get(name);
            if (stat == null) {
                ExperienceMapStat newStat = new ExperienceMapStat();
                newStat.setName(name);
                newStat.setTotalDays(dd.getTotalDays());
                newStat.setTotalCount(dd.getTotalCount());
                map.put(name, newStat);
            } else {
                stat.setTotalCount(stat.getTotalCount()+dd.getTotalCount());
                stat.setTotalDays(stat.getTotalDays().add(dd.getTotalDays()));
            }
        }
        List<ExperienceMapStat> result = new ArrayList<>();
        for (ExperienceMapStat stat : map.values()) {
            result.add(stat);
        }
        return result;
    }

    /**
     * 获取出发城市树
     *
     * @return
     */
    @RequestMapping(value = "/startCityTree", method = RequestMethod.GET)
    public ResultBean startCityTree(UserCommonFrom sf) {
        try {
            List<String> citys = experienceService.getStartCityList(sf.getUserId());
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (String s : citys) {
                TreeBean tb = new TreeBean();
                tb.setId(s);
                tb.setText(s);
                list.add(tb);
            }
            return callback(list);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取出发城市树异常",
                    e);
        }
    }

    /**
     * 针对某个经历迁徙地图统计
     *
     * @param expId
     * @return
     */
    @RequestMapping(value = "/transferDetailMap", method = RequestMethod.GET)
    public ResultBean transferDetailMap(@RequestParam(name = "expId") Long expId) {
        Experience experience = baseService.getObject(beanClass,expId);
        ExperienceDetailSH sf = new ExperienceDetailSH();
        sf.setExpId(expId);
        sf.setMapStat(true);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("occurDate", Sort.ASC);
        pr.addSort(s);
        pr.setBeanClass(ExperienceDetail.class);
        pr.setPage(PageRequest.NO_PAGE);
        List<ExperienceDetail> detailList = baseService.getBeanList(pr);
        ChinaTransferChartData chartData = this.createChinaTransferMap(detailList);
        chartData.setTitle("["+experience.getExpName()+"]地图");
        return callback(chartData);
    }

    /**
     * 迁徙地图统计
     *
     * @param sf
     * @return
     */
    @RequestMapping(value = "/transferMapStat", method = RequestMethod.GET)
    public ResultBean transferMapStat(ExperienceMapStatSH sf) {
        switch (sf.getMapType()){
            case CHINA:
                return callback(this.createChinaTransferMap(sf));
            case WORLD:
                return callback(this.createWorldTransferMap(sf));
            default:
                return callbackErrorInfo("无效的地图类型");
        }
    }

    /**
     * 中国迁移地图数据封装
     *
     * @param sf
     * @return
     */
    private ChinaTransferChartData createChinaTransferMap(ExperienceMapStatSH sf) {
        ExperienceDetailSH ds = new ExperienceDetailSH();
        ds.setStartDate(sf.getStartDate());
        ds.setEndDate(sf.getEndDate());
        ds.setUserId(sf.getUserId());
        ds.setInternational(false);
        PageRequest pr = ds.buildQuery();
        pr.setPage(PageRequest.NO_PAGE);
        pr.setBeanClass(ExperienceDetail.class);
        Sort s = new Sort("occurDate", Sort.ASC);
        pr.addSort(s);
        List<ExperienceDetail> list = baseService.getBeanList(pr);
        ChinaTransferChartData chartData = this.createChinaTransferMap(list);
        return chartData;
    }

    /**
     * 中国迁移地图数据封装
     *
     * @param list
     * @return
     */
    private ChinaTransferChartData createChinaTransferMap(List<ExperienceDetail> list) {
        Map<String, double[]> geoMap = new HashMap<>();
        ChinaTransferChartData chartData = new ChinaTransferChartData();
        chartData.setTitle("人生经历线路统计");
        ExperienceDetailSH ds = new ExperienceDetailSH();
        for(ExperienceDetail dd : list){
            String year = DateUtil.getFormatDate(dd.getOccurDate(),"yyyy");
            chartData.addDetail(year,dd.getStartCity(),dd.getArriveCity(),1);
            double[] scGeo = geoMap.get(dd.getStartCity());
            if(scGeo==null){
                String[] geo = dd.getScLocation().split(",");
                geoMap.put(dd.getStartCity(), new double[]{Double.parseDouble(geo[0]), Double.parseDouble(geo[1])});
            }
            double[] acGeo = geoMap.get(dd.getArriveCity());
            if(acGeo==null){
                String[] geo = dd.getAcLocation().split(",");
                geoMap.put(dd.getArriveCity(), new double[]{Double.parseDouble(geo[0]), Double.parseDouble(geo[1])});
            }
        }

        chartData.setGeoCoordMapData(geoMap);
        chartData.setUnit("次");
        return chartData;
    }

    /**
     * 世界迁移地图数据封装
     *
     * @param sf
     * @return
     */
    private WorldTransferChartData createWorldTransferMap(ExperienceMapStatSH sf) {
        List<WorldTransferMapStat> list  = experienceService.getWorldTransMapStat(sf);
        Map<String, double[]> geoMap = new HashMap<>();
        WorldTransferChartData chartData = new WorldTransferChartData();
        chartData.setTitle("人生经历线路统计");
        String centerCity = "北京";
        chartData.setCenterCity(centerCity);
        for(WorldTransferMapStat dd : list){
            chartData.addDetail(dd.getStartCity(),dd.getArriveCity(),1);
            double[] scGeo = geoMap.get(dd.getStartCity());
            if(scGeo==null){
                String[] geo = dd.getScLocation().split(",");
                geoMap.put(dd.getStartCity(), new double[]{Double.parseDouble(geo[0]), Double.parseDouble(geo[1])});
            }
            double[] acGeo = geoMap.get(dd.getArriveCity());
            if(acGeo==null){
                String[] geo = dd.getAcLocation().split(",");
                geoMap.put(dd.getArriveCity(), new double[]{Double.parseDouble(geo[0]), Double.parseDouble(geo[1])});
            }
        }
        if(geoMap.get(centerCity)==null){
            geoMap.put(centerCity, new double[]{116.413315,39.912142});
        }
        chartData.setGeoCoordMapData(geoMap);
        chartData.setUnit("次");
        return chartData;
    }

    /**
     * 基于日期的统计
     * 界面上使用echarts展示图表，后端返回的是核心模块的数据，不再使用Echarts的第三方jar包封装（比较麻烦）
     *
     * @return
     */
    @RequestMapping(value = "/dateStat", method = RequestMethod.GET)
    public ResultBean dateStat(ExperienceDateStatSH sf) {
        sf.setIntTypes(sf.getTypes());
        List<ExperienceDateStat> list = experienceService.getDateStat(sf);
        if (sf.getDateGroupType() == DateGroupType.DAYCALENDAR) {
            return callback(ChartUtil.createChartCalendarData("人生经历统计", "次数", "次", sf, list));
        }
        ChartData chartData = new ChartData();
        chartData.setTitle("人生经历统计");
        chartData.setLegendData(new String[]{"天数","次数"});
        //混合图形下使用
        chartData.addYAxis("天数","天");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("天数","天");
        for (ExperienceDateStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalDays());
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData1);
        chartData = ChartUtil.completeDate(chartData, sf);
        return callback(chartData);
    }

    /**
     * 同期对比
     *
     * @return
     */
    @RequestMapping(value = "/yoyStat", method = RequestMethod.GET)
    public ResultBean yoyStat(@Valid ExperienceYoyStatSH sf) {
        ChartData chartData = initYoyCharData(sf, "人生经历同期对比", null);
        chartData.setUnit(sf.getGroupType().getUnit());
        String[] legendData = new String[sf.getYears().size()];
        for (int i = 0; i < sf.getYears().size(); i++) {
            legendData[i] = sf.getYears().get(i).toString();
            //数据,为了代码复用及统一，统计还是按照日期的统计
            ExperienceDateStatSH dateSearch = new ExperienceDateStatSH();
            dateSearch.setDateGroupType(sf.getDateGroupType());
            dateSearch.setStartDate(DateUtil.getDate(sf.getYears().get(i) + "-01-01", DateUtil.FormatDay1));
            dateSearch.setEndDate(DateUtil.getDate(sf.getYears().get(i) + "-12-31", DateUtil.FormatDay1));
            dateSearch.setUserId(sf.getUserId());
            dateSearch.setIntTypes(sf.getTypes());
            ChartYData yData = new ChartYData(sf.getYears().get(i).toString(),sf.getGroupType().getUnit());
            List<ExperienceDateStat> list = experienceService.getDateStat(dateSearch);
            //临时内容，作为补全用
            ChartData temp = new ChartData();
            for (ExperienceDateStat bean : list) {
                temp.addXData(bean, sf.getDateGroupType());
                if (sf.getGroupType() == GroupType.COUNT) {
                    yData.getData().add(bean.getTotalCount());
                } else if (sf.getGroupType() == GroupType.DAYS) {
                    yData.getData().add(bean.getTotalDays());
                }
            }
            //临时内容，作为补全用
            temp.getYdata().add(yData);
            dateSearch.setCompleteDate(true);
            temp = ChartUtil.completeDate(temp, dateSearch);
            //设置到最终的结果集中
            chartData.getYdata().add(temp.getYdata().get(0));
        }
        chartData.setLegendData(legendData);

        return callback(chartData);
    }

    /**
     * 修正花费和天数
     *
     * @param revise
     * @return
     */
    @RequestMapping(value = "/revise", method = RequestMethod.POST)
    public ResultBean revise(@RequestBody @Valid ExperienceReviseForm revise) {
        experienceService.reviseExperience(revise);
        return callback(null);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/costStat", method = RequestMethod.GET)
    public ResultBean costStat(ExperienceCostStatSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        String title = "人生经历花费分析";
        Long expId = sf.getExpId();
        if (expId != null) {
            Experience bean = baseService.getObject(beanClass,expId);
            title = "[" + bean.getExpName() + "]花费分析";
        }
        chartPieData.setTitle(title);
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("费用");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        List<ExperienceCostStat> list;
        ExperienceCostStatType statType = sf.getStatType();
        if (statType == ExperienceCostStatType.CONSUME_TYPE) {
            list = experienceService.getCostStatByConsume(sf);
        } else if (statType == ExperienceCostStatType.TYPE) {
            list = experienceService.getCostStatByType(sf);
        } else {
            list = experienceService.getCostStatByExp(sf);
        }
        for (ExperienceCostStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(NumberUtil.getValue(bean.getTotalCost(),SCALE));
            serieData.getData().add(dataDetail);
            totalValue = totalValue.add(bean.getTotalCost());
        }
        String subTitle = "总花费：" + NumberUtil.getValue(totalValue,SCALE) + "元";
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return callback(chartPieData);
    }

    /**
     * 人生经历的词云
     *
     * @return
     */
    @RequestMapping(value = "/wordCloudStat", method = RequestMethod.GET)
    public ResultBean wordCloudStat(@Valid ExperienceWouldCloudStatSH sf) {
        switch (sf.getField()){
            case "tags" :
                return callback(this.createTagsWordCloud(sf));
            case "city" :
                return callback(this.createCityWordCloud(sf));
            default:
                return callbackErrorInfo("暂时不支持该分组");
        }

    }

    /**
     * 以标签统计
     * @param sf
     * @return
     */
    private ChartWorldCloudData createTagsWordCloud(ExperienceWouldCloudStatSH sf){
        List<NameCountDTO> tagsList = experienceService.statTags(sf);
        ChartWorldCloudData chartData = new ChartWorldCloudData();
        for(NameCountDTO s : tagsList){
            ChartNameValueVo dd = new ChartNameValueVo();
            dd.setName(s.getName());
            dd.setValue(s.getCounts().intValue());
            chartData.addData(dd);
        }
        chartData.setTitle("人生经历标签词云统计");
        return chartData;
    }

    /**
     * 以城市分组
     * @param sf
     * @return
     */
    private ChartWorldCloudData createCityWordCloud(ExperienceWouldCloudStatSH sf){
        List<String> cityList = experienceService.statCityList(sf.getUserId(),sf.getStartDate(),sf.getEndDate());
        Map<String,Integer> statData = new HashMap<>();
        for(String s : cityList){
            Integer n = statData.get(s);
            if(n==null){
                statData.put(s,1);
            }else{
                statData.put(s,n+1);
            }
        }
        ChartWorldCloudData chartData = new ChartWorldCloudData();
        for(String key : statData.keySet()){
            ChartNameValueVo dd = new ChartNameValueVo();
            dd.setName(key);
            dd.setValue(statData.get(key));
            chartData.addData(dd);
        }
        chartData.setTitle("人生经历城市词云统计");
        return chartData;
    }
}
