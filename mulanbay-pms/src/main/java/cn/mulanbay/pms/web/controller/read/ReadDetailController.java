package cn.mulanbay.pms.web.controller.read;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Book;
import cn.mulanbay.pms.persistent.domain.ReadDetail;
import cn.mulanbay.pms.persistent.dto.read.ReadDetailDateStat;
import cn.mulanbay.pms.persistent.service.ReadService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.read.readDetail.ReadDetailDateStatSH;
import cn.mulanbay.pms.web.bean.req.read.readDetail.ReadDetailForm;
import cn.mulanbay.pms.web.bean.req.read.readDetail.ReadDetailSH;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 阅读明细
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/readDetail")
public class ReadDetailController extends BaseController {

    private static Class<ReadDetail> beanClass = ReadDetail.class;

    @Autowired
    ReadService readService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ReadDetailSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<ReadDetail> getResult(ReadDetailSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort2 = new Sort("readTime", Sort.DESC);
        pr.addSort(sort2);
        PageResult<ReadDetail> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ReadDetailForm form) {
        ReadDetail bean = new ReadDetail();
        BeanCopy.copy(form, bean);
        Book book = baseService.getObject(Book.class,form.getBookId());
        bean.setBook(book);
        readService.saveOrUpdateRead(bean, false);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "detailId") Long detailId) {
        ReadDetail bean = baseService.getObject(beanClass,detailId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ReadDetailForm form) {
        ReadDetail bean = baseService.getObject(beanClass,form.getDetailId());
        BeanCopy.copy(form, bean);
        Book book = baseService.getObject(Book.class,form.getBookId());
        bean.setBook(book);
        readService.saveOrUpdateRead(bean, true);
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
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(ReadDetailDateStatSH sf) {
        List<ReadDetailDateStat> list = readService.getReadDateStat(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("阅读统计");
        //chartData.setSubTitle(this.getDateTitle(sf));
        chartData.setLegendData(new String[]{"时长","次数"});
        //混合图形下使用
        chartData.addYAxis("时长","小时");
        chartData.addYAxis("次数","次");
        ChartYData yData1 = new ChartYData("次数","次");
        ChartYData yData2 = new ChartYData("时长","小时");
        //总的值
        BigDecimal totalCounts = new BigDecimal(0);
        BigDecimal totalMinutes = new BigDecimal(0);
        for (ReadDetailDateStat bean : list) {
            chartData.addXData(bean, sf.getDateGroupType());
            yData1.getData().add(bean.getTotalCount());
            double hours = NumberUtil.getAvg(bean.getTotalDuration(), 60L, 1);
            yData2.getData().add(hours);
            totalCounts = totalCounts.add(new BigDecimal(bean.getTotalCount()));
            totalMinutes = totalMinutes.add(bean.getTotalDuration());
        }
        chartData.getYdata().add(yData2);
        chartData.getYdata().add(yData1);
        String total = totalCounts.longValue() + "次," + NumberUtil.getAvg(totalMinutes, 60L, 1) + "小时";
        String subTitle = ChartUtil.getDateTitle(sf, total);
        chartData.setSubTitle(subTitle);
        chartData = ChartUtil.completeDate(chartData, sf);
        return callback(chartData);
    }
}
