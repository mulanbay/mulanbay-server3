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
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.life.ExperienceCostStat;
import cn.mulanbay.pms.persistent.dto.life.ExperienceDateStat;
import cn.mulanbay.pms.persistent.dto.life.ExperienceMapStat;
import cn.mulanbay.pms.persistent.dto.life.NameCountDTO;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.ExperienceCostStatType;
import cn.mulanbay.pms.persistent.enums.MapField;
import cn.mulanbay.pms.persistent.service.ExperienceService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.life.experience.*;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.bean.res.life.ExperienceLocationVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Logger logger = LoggerFactory.getLogger(ExperienceController.class);

    @Value("${mulanbay.experience.mapStat.dateFormat}")
    String dateFormat;

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
        this.formToBean(form,bean);
        baseService.saveObject(bean);
        return callback(bean);
    }

    private void formToBean(ExperienceForm form, Experience bean){
        BeanCopy.copy(form,bean);
        bean.setCost(new BigDecimal(0));
        Country country = baseService.getObject(Country.class,form.getCountryId());
        bean.setCountry(country);
        if(form.getProvinceId()!=null){
            Province province = baseService.getObject(Province.class,form.getProvinceId());
            bean.setProvince(province);
        }
        if(form.getCityId()!=null){
            City city = baseService.getObject(City.class,form.getCityId());
            bean.setCity(city);
        }
        if(form.getDistrictId()!=null){
            District district = baseService.getObject(District.class,form.getDistrictId());
            bean.setDistrict(district);
        }
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
        this.formToBean(form,bean);
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
        MapField field = sf.getField();
        switch (field){
            case COUNTRY:
                return this.callback(createWorldMapData(sf));
            case PROVINCE:
                return this.callback(createCountryMapData(sf));
            case CITY,DISTRICT:
                return this.callback(createLocationMapStat(sf));
        }
        return callback(null);
    }

    /**
     * 生成统计图表
     * @param sf
     * @return
     */
    private MapStatChartData createCountryMapData(ExperienceMapStatSH sf){
        MapStatChartData chartData = new MapStatChartData();
        //Country country = baseService.getObject(Country.class,sf.getCountryId());
        //todo 根据不同国家设置地图名称
        chartData.setMapName("china");
        chartData.setTitle("人生去过的地方统计");
        List<ExperienceMapStat> list = experienceService.getMapStat(sf);
        int ps = list.size();
        String subText = "一共去过" + ps + "个省或直辖市";
        subText += "(占比:" + (int) NumberUtil.getPercent(ps, 34, 0) + "%)";
        if (sf.getStartDate() != null && sf.getEndDate() != null) {
            subText += "," + DateUtil.getFormatDate(sf.getStartDate(), DateUtil.FormatDay1) + "~" + DateUtil.getFormatDate(sf.getEndDate(), DateUtil.FormatDay1);
        }
        chartData.setSubTitle(subText);
        GroupType groupType = sf.getGroupType();
        int maxValue = 0;
        for (ExperienceMapStat dd : list) {
            String name = dd.getName();
            MapStatChartDetail detail = new MapStatChartDetail();
            detail.setName(name);
            if (groupType == GroupType.COUNT) {
                detail.setValue(dd.getTotalCount().intValue());
                if (dd.getTotalCount().intValue() > maxValue) {
                    maxValue = dd.getTotalCount().intValue();
                }
            } else if (groupType == GroupType.DAYS) {
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
        List<ExperienceMapStat> list = experienceService.getMapStat(sf);
        int maxValue = 0;
        Map<String, double[]> geoMapData = new HashMap<>();
        GroupType groupType = sf.getGroupType();
        for (ExperienceMapStat dd : list) {
            String name = dd.getName();
            MapStatChartDetail detail = new MapStatChartDetail();
            detail.setName(name);
            if (groupType == GroupType.COUNT) {
                detail.setValue(dd.getTotalCount().intValue());
                if (dd.getTotalCount().intValue() > maxValue) {
                    maxValue = dd.getTotalCount().intValue();
                }
            } else if (groupType == GroupType.DAYS) {
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
            geoMapData.put(name,this.createGeo(dd.getLocation()));
            detail.setCounts(dd.getTotalCount().intValue());
            detail.setDays(dd.getTotalDays().intValue());
            detail.setCost(dd.getTotalCost().intValue());
            chartData.addDetail(detail);
        }
        chartData.setUnit(groupType.getUnit());
        chartData.setMaxValue(maxValue);
        chartData.setGeoCoordMapData(geoMapData);
        return chartData;
    }

    /**
     * 基于地点的统计
     *
     * @param sf
     * @return
     */
    private LocationMapStatChartData createLocationMapStat(ExperienceMapStatSH sf) {
        List<ExperienceMapStat> list = null;
        if(sf.getUd()){
            list = this.createDetailMapStat(sf);
        }else{
            list = experienceService.getMapStat(sf);
        }
        LocationMapStatChartData chartData = new LocationMapStatChartData();
        chartData.setTitle("人生去过的地方统计");
        GroupType groupType = sf.getGroupType();
        if (groupType == GroupType.COUNT){
            chartData.setName("旅行次数");
            chartData.setUnit("次");
        }else if (groupType == GroupType.DAYS){
            chartData.setName("旅行天数");
            chartData.setUnit("天");
        }else{
            chartData.setName("旅行花费");
            chartData.setUnit("元");
        }
        List<MapData> dataList = new ArrayList<>();
        Map<String, double[]> geoMap= new HashMap<>();
        int maxValue = 0;
        int minValue = 0;
        for (ExperienceMapStat dd : list) {
            if (groupType == GroupType.COUNT) {
                int count = dd.getTotalCount().intValue();
                MapData c = new MapData(dd.getName(), count);
                dataList.add(c);
                if (count > maxValue) {
                    maxValue = count;
                }
                if (count < minValue) {
                    minValue = count;
                }
            }else if (groupType == GroupType.DAYS) {
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
            if(StringUtil.isNotEmpty(dd.getLocation())){
                String[] geo = dd.getLocation().split(",");
                geoMap.put(dd.getName(),new double[]{Double.parseDouble(geo[0]), Double.parseDouble(geo[1])});
            }else {
                logger.warn("未找到{}的地理位置信息",dd.getName());
            }
        }
        chartData.setDataList(dataList);
        chartData.setMax(maxValue);
        chartData.setMin(minValue);
        chartData.setGeoCoordMapData(geoMap);
        return chartData;

    }

    /**
     * 创建明细的Map统计
     * @param sf
     * @return
     */
    private List<ExperienceMapStat> createDetailMapStat(ExperienceMapStatSH sf){
        //获取经历列表
        ExperienceSH les = new ExperienceSH();
        BeanCopy.copy(sf,les);
        les.setPage(PageRequest.NO_PAGE);
        PageRequest pr = les.buildQuery();
        pr.setBeanClass(beanClass);
        List<Experience> list = baseService.getBeanList(pr);
        List<ExperienceMapStat> result = new ArrayList<>();
        MapField field = sf.getField();
        for(Experience bt: list){
            ExperienceMapStat bean = new ExperienceMapStat();
            bean.setId(bt.getExpId());
            //bean.setName(DateUtil.getFormatDate(bt.getStartDate(),dateFormat)+","+bt.getExpName());
            bean.setName(bt.getExpName());
            if(field==MapField.CITY){
                bean.setLocation(bt.getCity().getLocation());
            }else{
                bean.setLocation(bt.getDistrict().getLocation());
            }
            bean.setTotalCount(1L);
            bean.setTotalDays(bt.getDays().longValue());
            bean.setTotalCost(bt.getCost());
            result.add(bean);
        }
        return result;
    }

    /**
     * 针对某个经历迁徙地图统计
     *
     * @param sf
     * @return
     */
    @RequestMapping(value = "/transferDetailMap", method = RequestMethod.GET)
    public ResultBean transferDetailMap(@Valid ExperienceTransferDetailMapSH sf) {
        Experience experience = baseService.getObject(beanClass,sf.getExpId());
        ExperienceDetailSH eds = new ExperienceDetailSH();
        eds.setExpId(sf.getExpId());
        eds.setMapStat(true);
        PageRequest pr = eds.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("occurDate", Sort.ASC);
        pr.addSort(s);
        pr.setBeanClass(ExperienceDetail.class);
        pr.setPage(PageRequest.NO_PAGE);
        List<ExperienceDetail> detailList = baseService.getBeanList(pr);
        ChinaTransferChartData chartData = this.createCountryTransferMap(detailList,sf.getField());
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
    public ResultBean transferMapStat(ExperienceTransferMapStatSH sf) {
        MapField field = sf.getField();
        switch (field){
            case COUNTRY:
                return this.callback(createWorldTransferMap(sf));
            case PROVINCE,CITY,DISTRICT:
                return this.callback(createCountryTransferMap(sf));
        }
        return callback(null);
    }

    /**
     * 中国迁移地图数据封装
     *
     * @param sf
     * @return
     */
    private ChinaTransferChartData createCountryTransferMap(ExperienceTransferMapStatSH sf) {
        ExperienceDetailSH ds = new ExperienceDetailSH();
        ds.setStartDate(sf.getStartDate());
        ds.setEndDate(sf.getEndDate());
        ds.setUserId(sf.getUserId());
        ds.setInternational(false);
        ds.setMapStat(true);
        PageRequest pr = ds.buildQuery();
        pr.setPage(PageRequest.NO_PAGE);
        pr.setBeanClass(ExperienceDetail.class);
        Sort s = new Sort("occurDate", Sort.ASC);
        pr.addSort(s);
        List<ExperienceDetail> list = baseService.getBeanList(pr);
        ChinaTransferChartData chartData = this.createCountryTransferMap(list,sf.getField());
        return chartData;
    }

    /**
     * 基于国家的迁移地图数据封装
     *
     * @param list
     * @return
     */
    private ChinaTransferChartData createCountryTransferMap(List<ExperienceDetail> list, MapField field) {
        Map<String, double[]> geoMap = new HashMap<>();
        ChinaTransferChartData chartData = new ChinaTransferChartData();
        chartData.setTitle("人生经历线路统计");
        for(ExperienceDetail dd : list){
            String year = DateUtil.getFormatDate(dd.getOccurDate(),"yyyy");
            ExperienceLocationVo lv = this.getLocationInfo(dd,field);
            chartData.addDetail(year,lv.getStart(),lv.getArrive(),1);
            double[] scGeo = geoMap.get(lv.getStart());
            if(scGeo==null){
                geoMap.put(lv.getStart(),createGeo(lv.getStartLocation()) );
            }
            double[] acGeo = geoMap.get(lv.getArrive());
            if(acGeo==null){
                geoMap.put(lv.getArrive(),createGeo(lv.getArriveLocation()) );
            }
        }
        chartData.setGeoCoordMapData(geoMap);
        chartData.setUnit("次");
        return chartData;
    }

    private ExperienceLocationVo getLocationInfo(ExperienceDetail dd,MapField field){
        String start = null;
        String arrive = null;
        String startLocation = null;
        String arriveLocation = null;
        switch (field){
            case COUNTRY -> {
                start = dd.getStartCountry().getCnName();
                arrive = dd.getArriveCountry().getCnName();
                startLocation = dd.getStartCountry().getLocation();
                arriveLocation = dd.getArriveCountry().getLocation();
            }
            case PROVINCE -> {
                start = dd.getStartProvince().getMapName();
                arrive = dd.getArriveProvince().getMapName();
                startLocation = dd.getStartProvince().getLocation();
                arriveLocation = dd.getArriveProvince().getLocation();
            }
            case CITY -> {
                start = dd.getStartCity().getCityName();
                arrive = dd.getArriveCity().getCityName();
                startLocation = dd.getStartCity().getLocation();
                arriveLocation = dd.getArriveCity().getLocation();
            }
            case DISTRICT -> {
                start = dd.getStartDistrict().getDistrictName();
                arrive = dd.getArriveDistrict().getDistrictName();
                startLocation = dd.getStartDistrict().getLocation();
                arriveLocation = dd.getArriveDistrict().getLocation();
            }
        }
        return new ExperienceLocationVo(start,arrive,startLocation,arriveLocation);
    }

    /**
     * 经纬度
     * @param location
     * @return
     */
    private double[] createGeo(String location){
        if(StringUtil.isEmpty(location)){
            return new double[]{0,0};
        }else{
            String[] geo = location.split(",");
            return new double[]{Double.parseDouble(geo[0]), Double.parseDouble(geo[1])};
        }
    }

    /**
     * 世界迁移地图数据封装
     *
     * @param sf
     * @return
     */
    private WorldTransferChartData createWorldTransferMap(ExperienceTransferMapStatSH sf) {
        ExperienceDetailSH ds = new ExperienceDetailSH();
        ds.setStartDate(sf.getStartDate());
        ds.setEndDate(sf.getEndDate());
        ds.setUserId(sf.getUserId());
        ds.setInternational(true);
        ds.setMapStat(true);
        PageRequest pr = ds.buildQuery();
        pr.setPage(PageRequest.NO_PAGE);
        pr.setBeanClass(ExperienceDetail.class);
        Sort s = new Sort("occurDate", Sort.ASC);
        pr.addSort(s);
        List<ExperienceDetail> list = baseService.getBeanList(pr);
        Map<String, double[]> geoMap = new HashMap<>();
        WorldTransferChartData chartData = new WorldTransferChartData();
        chartData.setTitle("人生经历线路统计");
        String centerCity = "北京";
        chartData.setCenterCity(centerCity);
        for(ExperienceDetail dd : list){
            ExperienceLocationVo lv = this.getLocationInfo(dd,sf.getField());
            chartData.addDetail(lv.getStart(),lv.getArrive(),1);
            double[] scGeo = geoMap.get(lv.getStart());
            if(scGeo==null){
                geoMap.put(lv.getStart(),createGeo(lv.getStartLocation()) );
            }
            double[] acGeo = geoMap.get(lv.getArrive());
            if(acGeo==null){
                geoMap.put(lv.getArrive(),createGeo(lv.getArriveLocation()) );
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
