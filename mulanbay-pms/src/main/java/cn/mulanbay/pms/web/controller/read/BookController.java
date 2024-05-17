package cn.mulanbay.pms.web.controller.read;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.Book;
import cn.mulanbay.pms.persistent.domain.BookCategory;
import cn.mulanbay.pms.persistent.domain.Country;
import cn.mulanbay.pms.persistent.domain.ReadDetail;
import cn.mulanbay.pms.persistent.dto.read.*;
import cn.mulanbay.pms.persistent.enums.BookStatus;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.service.ReadService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.GroupType;
import cn.mulanbay.pms.web.bean.req.read.book.*;
import cn.mulanbay.pms.web.bean.req.read.readDetail.ReadDetailOverallStatSH;
import cn.mulanbay.pms.web.bean.req.read.readDetail.ReadDetailSH;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.bean.res.read.book.BookStatVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 阅读记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/book")
public class BookController extends BaseController {

    private static Class<Book> beanClass = Book.class;

    @Autowired
    ReadService readService;

    /**
     * 获取列表列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(BookSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<Book> getResult(BookSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort1 = new Sort("status", Sort.ASC);
        pr.addSort(sort1);
        Sort sort2 = new Sort("modifyTime", Sort.DESC);
        pr.addSort(sort2);
        Sort sort3 = new Sort("expertFinishDate", Sort.ASC);
        pr.addSort(sort3);
        PageResult<Book> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid BookForm form) {
        Book bean = new Book();
        BeanCopy.copy(form, bean);
        BookCategory bookCategory = baseService.getObject(BookCategory.class,form.getCateId());
        bean.setCate(bookCategory);
        Country country = baseService.getObject(Country.class,form.getCountryId());
        bean.setCountry(country);
        checkAndSetBook(bean);
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "bookId") Long bookId) {
        Book bean = baseService.getObject(beanClass,bookId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid BookForm form) {
        Book bean = baseService.getObject(beanClass,form.getBookId());
        BookStatus bs = bean.getStatus();
        BeanCopy.copy(form, bean);
        BookCategory bookCategory = baseService.getObject(BookCategory.class,form.getCateId());
        bean.setCate(bookCategory);
        Country country = baseService.getObject(Country.class,form.getCountryId());
        bean.setCountry(country);
        checkAndSetBook(bean);
        if (bs != BookStatus.READED && form.getStatus() == BookStatus.READED) {
            //如果状态改变为已完成，则自动算出开始、结束日期
            ReadDetailTimeStat rrts = readService.getReadTimeStat(bean.getBookId());
            if (bean.getBeginDate() == null) {
                bean.setBeginDate(rrts.getMinDate());
            }
            if (bean.getFinishDate() == null) {
                bean.setFinishDate(rrts.getMaxDate());
            }
        }
        baseService.updateObject(bean);
        return callback(bean);
    }

    /**
     * 检查完成情况
     *
     * @param bean
     */
    private void checkAndSetBook(Book bean) {
        //检查isbn是否存在
        Book rr = readService.getBook(bean.getIsbn(), bean.getUserId(), bean.getBookId());
        if (rr != null) {
            throw new ApplicationException(PmsCode.BOOK_ISBN_EXIT);
        }
        if (bean.getFinishDate() != null && bean.getBeginDate() != null) {
            if (bean.getStatus() == null || bean.getStatus() != BookStatus.READED.READED) {
                //状态不对
                throw new ApplicationException(PmsCode.BOOK_STATUS_ERROR);
            }
            if (bean.getScore() == null) {
                throw new ApplicationException(PmsCode.BOOK_SCORE_NULL);
            }
            //需要加1，因为同天的是一天
            int costDays = DateUtil.getIntervalDays(bean.getBeginDate(), bean.getFinishDate()) + 1;
            bean.setCostDays(costDays);
        }
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        Long[] ids = NumberUtil.stringToLongArray(deleteRequest.getIds());
        for (Long id : ids) {
            readService.deleteBook(id);
        }
        return callback(null);
    }

    /**
     * 总的概要统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(BookStatSH sf) {
        sf.setDateQueryType("finish_date");
        BookAnalyseStatSH statSearch = new BookAnalyseStatSH();
        BeanCopy.copy(sf, statSearch);
        List<BookAnalyseStat> list = readService.getBookAnalyseStat(statSearch);
        ChartPieData chartPieData = this.createAnalyseStatPieData(statSearch, list);
        BookStat stat = readService.getBookStat(sf);
        BookStatVo vo = new BookStatVo();
        BeanCopy.copy(stat, vo);
        vo.setChartData(chartPieData);
        return callback(vo);
    }

    /**
     * 完整的统计（对应页面图形为旭日图）
     *
     * @return
     */
    @RequestMapping(value = "/fullStat", method = RequestMethod.GET)
    public ResultBean fullStat(BookFullStatSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        List<Book> list = baseService.getBeanList(pr);
        ChartSunburstData chartData = new ChartSunburstData();
        List<ChartSunburstDetailData> voList = new ArrayList<>();
        for(Book rr : list){
            //第一层：书籍类型
            ChartSunburstDetailData vo = this.getFullStatVo(rr.getBookType().getName(),voList);
            if(vo ==null){
                vo = new ChartSunburstDetailData();
                vo.setName(rr.getBookType().getName());
                voList.add(vo);
            }
            //第二层：书籍分类
            String category = rr.getCate().getCateName();
            ChartSunburstDetailData categoryVo = this.getFullStatVo(category,vo.getChildren());
            if(categoryVo==null){
                categoryVo = new ChartSunburstDetailData();
                categoryVo.setName(category);
                vo.addChild(categoryVo);
            }
            //第三层：书籍评分
            String score = String.valueOf(NumberUtil.getValue(rr.getScore(),0));
            ChartSunburstDetailData scoreVo = this.getFullStatVo(score,categoryVo.getChildren());
            if(scoreVo==null){
                scoreVo = new ChartSunburstDetailData();
                scoreVo.setName(score);
                categoryVo.addChild(scoreVo);
            }
            //第四层：书籍名称
            String bookName = rr.getBookName();
            ChartSunburstDetailData bookNameVo = new ChartSunburstDetailData();
            bookNameVo.setName(bookName);
            scoreVo.addChild(bookNameVo);
        }
        chartData.setDataList(voList);
        return callback(chartData);
    }

    /**
     * 查找
     * @param name
     * @param list
     * @return
     */
    private ChartSunburstDetailData getFullStatVo(String name, List<ChartSunburstDetailData> list){
        for(ChartSunburstDetailData vo : list){
            if(vo.getName().equals(name)){
                return vo;
            }
        }
        return null;
    }

    /**
     * 总阅读时间（分钟）
     *
     * @return
     */
    @RequestMapping(value = "/getCostTimes", method = RequestMethod.GET)
    public ResultBean getCostTimes(@RequestParam(name = "bookId") Long bookId) {
        BigDecimal minutes = readService.getCostTimes(bookId);
        return callback(minutes);
    }

    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(BookDateStatSH sf) {
        switch (sf.getDateGroupType()){
            case HOURMINUTE :
                //散点图
                return callback(this.createScatterChartData(sf));
            default:
                return callback(this.createChartData(sf));
        }
    }

    private ChartData createChartData(BookDateStatSH sf){
        List<BookDateStat> list = readService.getBookDateStat(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("阅读统计");
        //chartData.setSubTitle(this.getDateTitle(sf));
        chartData.setUnit("本");
        chartData.setLegendData(new String[]{"本数"});
        ChartYData yData1 = new ChartYData("本数","本");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        for (BookDateStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            yData1.getData().add(bean.getTotalCount());
            totalValue = totalValue.add(new BigDecimal(bean.getTotalCount()));
        }
        chartData.getYdata().add(yData1);
        String subTitle = ChartUtil.getDateTitle(sf, String.valueOf(totalValue.longValue()) + "本");
        chartData.setSubTitle(subTitle);
        chartData = ChartUtil.completeDate(chartData, sf);
        return chartData;
    }

    private ScatterChartData createScatterChartData(BookDateStatSH sf){
        ReadDetailSH rrds = new ReadDetailSH();
        BeanCopy.copy(sf,rrds);
        PageRequest pr = rrds.buildQuery();
        //阅读散点图统计的是阅读明细的数据
        pr.setBeanClass(ReadDetail.class);
        List<Date> dateList = readService.getReadDateList(rrds);
        return ChartUtil.createHMChartData(dateList,"阅读分析","阅读时间点");
    }


    /**
     * 统计分析
     *
     * @return
     */
    @RequestMapping(value = "/analyseStat")
    public ResultBean analyseStat(BookAnalyseStatSH sf) {
        List<BookAnalyseStat> list;
        if (sf.getGroupType() == BookAnalyseStatSH.GroupType.PERIOD) {
            List<BigInteger> pl = readService.getBookAnalyseDayStat(sf);
            Map<String, Integer> map = new HashMap<>();
            for (BigInteger b : pl) {
                int m = b.intValue();
                String key;
                if (m <= 3) {
                    key = "3天内";
                } else if (m <= 7) {
                    key = "3-7天";
                } else if (m <= 15) {
                    key = "7-15天";
                } else if (m <= 30) {
                    key = "15-30天";
                } else {
                    key = "超过1个月";
                }
                Integer v = map.get(key);
                if (v == null) {
                    map.put(key, 1);
                } else {
                    map.put(key, v + 1);
                }
            }
            list = new ArrayList<>();
            for (String kk : map.keySet()) {
                BookAnalyseStat as = new BookAnalyseStat();
                as.setName(kk);
                as.setValue(map.get(kk));
                list.add(as);
            }
        } else if (sf.getGroupType() == BookAnalyseStatSH.GroupType.TIME) {
            List<BigDecimal> pl = readService.getBookAnalyseTimeStat(sf);
            Map<String, Integer> map = new HashMap<>();
            for (BigDecimal b : pl) {
                int m = b.intValue();
                String key;
                if (m <= 60) {
                    key = "1小时内";
                } else if (m <= 3 * 60) {
                    key = "1-3小时";
                } else if (m <= 24 * 60) {
                    key = "4小时-1天";
                } else if (m <= 3 * 24 * 60) {
                    key = "1-3天";
                } else if (m <= 7 * 24 * 60) {
                    key = "3-7天";
                } else if (m <= 30 * 24 * 60) {
                    key = "7天-1个月";
                } else {
                    key = "超过1个月";
                }
                Integer v = map.get(key);
                if (v == null) {
                    map.put(key, 1);
                } else {
                    map.put(key, v + 1);
                }
            }
            list = new ArrayList<>();
            for (String kk : map.keySet()) {
                BookAnalyseStat as = new BookAnalyseStat();
                as.setName(kk);
                as.setValue(map.get(kk));
                list.add(as);
            }
        } else {
            list = readService.getBookAnalyseStat(sf);
        }
        ChartPieData chartPieData = this.createAnalyseStatPieData(sf, list);
        return callback(chartPieData);
    }

    private ChartPieData createAnalyseStatPieData(BookAnalyseStatSH sf, List<BookAnalyseStat> list) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("阅读记录分析");
        chartPieData.setUnit("本");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("本数");
        serieData.setUnit("本");
        //总的值
        BigDecimal totalValue = new BigDecimal(0);
        for (BookAnalyseStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getValue());
            serieData.getData().add(dataDetail);
            totalValue = totalValue.add(new BigDecimal(bean.getValue()));
        }
        String subTitle = ChartUtil.getDateTitle(sf, String.valueOf(totalValue.intValue()) + "本");
        chartPieData.setSubTitle(subTitle);
        chartPieData.getDetailData().add(serieData);
        return chartPieData;
    }

    /**
     * 总体统计
     *
     * @return
     */
    @RequestMapping(value = "/overallStat")
    public ResultBean overallStat(@Valid BookOverallStatSH sf) {
        GroupType groupType = sf.getValueType();
        if(groupType== GroupType.COUNT){
            //阅读本数
            return this.overallStatBook(sf);
        }else{
            //阅读时间
            ReadDetailOverallStatSH df = new ReadDetailOverallStatSH();
            BeanCopy.copy(sf,df);
            return this.overallStatRead(df);
        }
    }

     /**
     * @param sf
     * @return
     */
    public ResultBean overallStatBook(BookOverallStatSH sf) {
        ChartHeatmapData chartData = new ChartHeatmapData();
        chartData.setTitle("阅读统计");
        DateGroupType dateGroupType = sf.getDateGroupType();
        int[] minMax = ChartUtil.getMinMax(dateGroupType,sf.getStartDate(),sf.getEndDate());
        int min = minMax[0];
        int max = minMax[1];
        List<String> xdata = ChartUtil.getStringXdataList(dateGroupType,min, max);
        chartData.setXdata(xdata);
        //Y轴
        List<BookCategory> bookCategoryList = readService.getBookCategoryList(sf.getUserId());
        Map<String,OverallYIndex> yMap = new HashMap<>();
        int stn = bookCategoryList.size();
        for(int i=0;i<stn;i++){
            BookCategory st = bookCategoryList.get(i);
            yMap.put(st.getCateId().toString(),new OverallYIndex(st.getCateId().toString(),st.getCateName(),"本",i));
            chartData.addYData(st.getCateName());
        }
        List<BookOverallStat> list = readService.getBookOverallStat(sf);
        GroupType valueType = sf.getValueType();
        ChartHeatmapSerieData serieData = new ChartHeatmapSerieData(valueType.getName());
        int vn = list.size();
        for (int i=0;i<vn;i++) {
            BookOverallStat seos = list.get(i);
            int indexValue = seos.getIndexValue().intValue();
            if(dateGroupType==DateGroupType.DAY){
                indexValue = DateUtil.getDayOfYear(DateUtil.getDate(indexValue+"","yyyyMMdd"));
            }
            int xIndex = ChartUtil.getXIndex(dateGroupType,indexValue,min,xdata) ;
            OverallYIndex yi = yMap.get(seos.getCateId().toString());
            int yIndex = yi.getIndex();
            double value = seos.getTotalCount().doubleValue();
            String unit ="本";
            chartData.updateMinMaxValue(value);
            serieData.addData(new Object[]{xIndex,yIndex,value,unit});
        }
        chartData.addSerieData(serieData);
        return callback(chartData);
    }

    /**
     * @param sf
     * @return
     */
    public ResultBean overallStatRead(ReadDetailOverallStatSH sf) {
        ChartHeatmapData chartData = new ChartHeatmapData();
        chartData.setTitle("阅读统计");
        DateGroupType dateGroupType = sf.getDateGroupType();
        int[] minMax = ChartUtil.getMinMax(dateGroupType,sf.getStartDate(),sf.getEndDate());
        int min = minMax[0];
        int max = minMax[1];
        List<String> xdata = ChartUtil.getStringXdataList(dateGroupType,min, max);
        chartData.setXdata(xdata);
        //Y轴
        List<BookCategory> bookCategoryList = readService.getBookCategoryList(sf.getUserId());
        Map<String,OverallYIndex> yMap = new HashMap<>();
        int stn = bookCategoryList.size();
        for(int i=0;i<stn;i++){
            BookCategory st = bookCategoryList.get(i);
            yMap.put(st.getCateId().toString(),new OverallYIndex(st.getCateId().toString(),st.getCateName(),"本",i));
            chartData.addYData(st.getCateName());
        }
        List<ReadDetailOverallStat> list = readService.getReadOverallStat(sf);
        GroupType valueType = sf.getValueType();
        ChartHeatmapSerieData serieData = new ChartHeatmapSerieData(valueType.getName());
        int vn = list.size();
        for (int i=0;i<vn;i++) {
            ReadDetailOverallStat seos = list.get(i);
            int indexValue = seos.getIndexValue().intValue();
            if(dateGroupType==DateGroupType.DAY){
                indexValue = DateUtil.getDayOfYear(DateUtil.getDate(indexValue+"","yyyyMMdd"));
            }
            int xIndex = ChartUtil.getXIndex(dateGroupType,indexValue,min,xdata) ;
            OverallYIndex yi = yMap.get(seos.getCateId().toString());
            int yIndex = yi.getIndex();
            double value = NumberUtil.getValue(seos.getTotalDuration().doubleValue()/60,1);
            String unit ="小时";
            chartData.updateMinMaxValue(value);
            serieData.addData(new Object[]{xIndex,yIndex,value,unit});
        }
        chartData.addSerieData(serieData);
        return callback(chartData);
    }


}
