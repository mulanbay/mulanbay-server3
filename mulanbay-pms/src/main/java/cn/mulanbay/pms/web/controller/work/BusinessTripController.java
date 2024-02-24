package cn.mulanbay.pms.web.controller.work;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.work.BusinessTripDateStat;
import cn.mulanbay.pms.persistent.dto.work.BusinessTripMapStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.MapField;
import cn.mulanbay.pms.persistent.service.WorkService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.work.businessTrip.BusinessTripDateStatSH;
import cn.mulanbay.pms.web.bean.req.work.businessTrip.BusinessTripForm;
import cn.mulanbay.pms.web.bean.req.work.businessTrip.BusinessTripMapStatSH;
import cn.mulanbay.pms.web.bean.req.work.businessTrip.BusinessTripSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 出差管理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/businessTrip")
public class BusinessTripController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessTripController.class);

    private static Class<BusinessTrip> beanClass = BusinessTrip.class;

    @Autowired
    WorkService workService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(BusinessTripSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<BusinessTrip> getResult(BusinessTripSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("tripDate", Sort.DESC);
        pr.addSort(sort);
        PageResult<BusinessTrip> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid BusinessTripForm form) {
        BusinessTrip bean = new BusinessTrip();
        this.formToBean(form,bean);
        baseService.saveObject(bean);
        return callback(null);
    }

    private void formToBean(BusinessTripForm form,BusinessTrip bean){
        BeanCopy.copy(form,bean);
        Company company = baseService.getObject(Company.class,form.getCompanyId());
        bean.setCompany(company);
        Country country = baseService.getObject(Country.class,form.getCountryId());
        bean.setCountry(country);
        Province province = baseService.getObject(Province.class,form.getProvinceId());
        bean.setProvince(province);
        City city = baseService.getObject(City.class,form.getCityId());
        bean.setCity(city);
        District district = baseService.getObject(District.class,form.getDistrictId());
        bean.setDistrict(district);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "tripId") Long tripId) {
        BusinessTrip bean = baseService.getObject(beanClass,tripId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid BusinessTripForm form) {
        BusinessTrip bean = baseService.getObject(beanClass,form.getTripId());
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
        baseService.deleteObjects(beanClass, NumberUtil.stringToLongArray(deleteRequest.getIds()));
        return callback(null);
    }


    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat", method = RequestMethod.GET)
    public ResultBean dateStat(BusinessTripDateStatSH sf) {
        switch (sf.getDateGroupType()){
            case DAYCALENDAR :
                return callback(createChartCalendarData(sf));
            default:
                return callback(createChartData(sf));
        }
    }

    private ChartData createChartData(BusinessTripDateStatSH sf){
        ChartData chartData = new ChartData();
        chartData.setTitle("出差统计");
        chartData.setSubTitle(this.getDateTitle(sf));
        chartData.setLegendData(new String[]{ "天数","次数"});
        //混合图形下使用
        chartData.addYAxis("天数","天");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("天数","天");
        List<BusinessTripDateStat> list = workService.getBusinessTripDateStat(sf);
        for (BusinessTripDateStat bean : list) {
            chartData.getIntXData().add(bean.getDateIndexValue());
            if (sf.getDateGroupType() == DateGroupType.MONTH) {
                chartData.getXdata().add(bean.getDateIndexValue() + "月份");
            } else if (sf.getDateGroupType() == DateGroupType.YEAR) {
                chartData.getXdata().add(bean.getDateIndexValue() + "年");
            } else if (sf.getDateGroupType() == DateGroupType.WEEK) {
                chartData.getXdata().add("第" + bean.getDateIndexValue() + "周");
            } else {
                chartData.getXdata().add(bean.getDateIndexValue().toString());
            }
            yData1.getData().add(bean.getTotalCount());
            yData2.getData().add(bean.getTotalDays().doubleValue());
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData1);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    /**
     * 日历
     * @param sf
     * @return
     */
    private ChartCalendarData createChartCalendarData(BusinessTripDateStatSH sf){
        //日历
        List<BusinessTripDateStat> list = workService.getBusinessTripDateStat(sf);
        return ChartUtil.createChartCalendarData("出差统计", "次数", "次", sf, list);
    }

    /**
     * 地图统计
     *
     * @return
     */
    @RequestMapping(value = "/mapStat", method = RequestMethod.GET)
    public ResultBean mapStat(BusinessTripMapStatSH sf) {
        MapField field = sf.getField();
        switch (field){
            case COUNTRY -> {
                return callback(this.createWorldMapData(sf));
            }
            case PROVINCE -> {
                return callback(this.createProvinceMapData(sf));
            }
            case CITY, DISTRICT -> {
                return callback(this.createLocationMapStat(sf));
            }
        }
        return callback(null);
    }


    /**
     * 生成统计图表
     * @param sf
     * @return
     */
    private MapStatChartData createProvinceMapData(BusinessTripMapStatSH sf){
        MapStatChartData chartData = new MapStatChartData();
        chartData.setMapName("china");
        chartData.setTitle("出差统计");
        List<BusinessTripMapStat> list = workService.getBusinessTripMapStat(sf);
        int maxValue = 0;
        GroupType groupType = sf.getGroupType();
        for (BusinessTripMapStat dd : list) {
            String name = dd.getName();
            MapStatChartDetail detail = new MapStatChartDetail();
            detail.setName(name);
            if (groupType == GroupType.COUNT) {
                detail.setValue(dd.getTotalCount().intValue());
                if (dd.getTotalCount().intValue() > maxValue) {
                    maxValue = dd.getTotalCount().intValue();
                }
            } else if (groupType == GroupType.VALUE) {
                detail.setValue(dd.getTotalDays().intValue());
                if (dd.getTotalDays().intValue() > maxValue) {
                    maxValue = dd.getTotalDays().intValue();
                }
            }
            detail.setCounts(dd.getTotalCount().intValue());
            detail.setDays(dd.getTotalDays().intValue());
            chartData.addDetail(detail);
        }
        chartData.setMaxValue(maxValue);
        return chartData;
    }

    /**
     * 基于地点的统计
     *
     * @param sf
     * @return
     */
    private LocationMapStatChartData createLocationMapStat(BusinessTripMapStatSH sf) {
        LocationMapStatChartData chartData = new LocationMapStatChartData();
        chartData.setTitle("出差统计");
        GroupType groupType = sf.getGroupType();
        if (groupType == GroupType.COUNT){
            chartData.setName("出差次数");
            chartData.setUnit("次");
        }else{
            chartData.setName("出差天数");
            chartData.setUnit("天");
        }
        List<MapData> dataList = new ArrayList<>();
        List<BusinessTripMapStat> list = workService.getBusinessTripMapStat(sf);
        int maxValue = 0;
        int minValue = 0;
        Map<String, double[]> geoMap= new HashMap<>();
        for (BusinessTripMapStat dd : list) {
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
            }else{
                int days = dd.getTotalDays().intValue();
                MapData c = new MapData(dd.getName(), days );
                dataList.add(c);
                if (days > maxValue) {
                    maxValue = days;
                }
                if (days < minValue) {
                    minValue = days;
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
     * 生成世界统计图表
     * @param sf
     * @return
     */
    private WorldMapStatChartData createWorldMapData(BusinessTripMapStatSH sf){
        WorldMapStatChartData chartData = new WorldMapStatChartData();
        chartData.setMapName("world");
        chartData.setTitle("出差统计");
        GroupType groupType = sf.getGroupType();
        if (groupType == GroupType.COUNT){
            chartData.setUnit("次");
        }else{
            chartData.setUnit("天");
        }
        List<BusinessTripMapStat> list = workService.getBusinessTripMapStat(sf);
        int maxValue = 0;
        Map<String, double[]> geoMap = new HashMap<>();
        for (BusinessTripMapStat dd : list) {
            String name = dd.getName();
            MapStatChartDetail detail = new MapStatChartDetail();
            detail.setName(name);
            if (groupType == GroupType.COUNT) {
                detail.setValue(dd.getTotalCount().intValue());
                if (dd.getTotalCount().intValue() > maxValue) {
                    maxValue = dd.getTotalCount().intValue();
                }
            } else{
                detail.setValue(dd.getTotalDays().intValue());
                if (dd.getTotalDays().intValue() > maxValue) {
                    maxValue = dd.getTotalDays().intValue();
                }
            }
            //地理位置
            if(StringUtil.isNotEmpty(dd.getLocation())){
                String[] geo = dd.getLocation().split(",");
                geoMap.put(dd.getName(),new double[]{Double.parseDouble(geo[0]), Double.parseDouble(geo[1])});
            }else{
                geoMap.put(name, new double[]{0, 0});
                logger.warn("未找到{}的地理位置信息",dd.getName());
            }
            detail.setCounts(dd.getTotalCount().intValue());
            detail.setDays(dd.getTotalDays().intValue());
            chartData.addDetail(detail);
        }
        chartData.setMaxValue(maxValue);
        chartData.setGeoCoordMapData(geoMap);
        return chartData;
    }
}
