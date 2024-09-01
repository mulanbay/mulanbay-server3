package cn.mulanbay.pms.web.controller.log;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.SysLog;
import cn.mulanbay.pms.persistent.dto.log.SysLogAnalyseStat;
import cn.mulanbay.pms.persistent.service.LogService;
import cn.mulanbay.pms.web.bean.req.log.sysLog.SysLogAnalyseStatSH;
import cn.mulanbay.pms.web.bean.req.log.sysLog.SysLogSH;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieData;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieSerieData;
import cn.mulanbay.pms.web.bean.res.chart.ChartPieSerieDetailData;
import cn.mulanbay.pms.web.bean.res.log.sysLog.OperBeanDetailVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/sysLog")
public class SysLogController extends BaseController {

    private static Class<SysLog> beanClass = SysLog.class;

    @Autowired
    LogService logService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(SysLogSH sf) {
        return callbackDataGrid(getSysLogResult(sf));
    }

    private PageResult<SysLog> getSysLogResult(SysLogSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("occurTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<SysLog> qr = baseService.getBeanResult(pr);
        return qr;
    }


    /**
     * 获取日志参数
     *
     * @return
     */
    @RequestMapping(value = "/paras", method = RequestMethod.GET)
    public ResultBean paras(@RequestParam(name = "id") Long id) {
        SysLog log = baseService.getObject(beanClass,id);
        return callback(log.getParas());
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "id") Long id) {
        SysLog log = baseService.getObject(beanClass,id);
        return callback(log);
    }

    /**
     * 查询被操作的业务对象的数据
     *
     * @param id 为操作日志的记录号
     * @return
     */
    @RequestMapping(value = "/beanDetail", method = RequestMethod.GET)
    public ResultBean beanDetail(@RequestParam(name = "id") Long id) {
        SysLog log = baseService.getObject(beanClass,id);
        String idValue = log.getIdValue();
        if (StringUtil.isEmpty(idValue)) {
            throw new ApplicationException(PmsCode.OPERATION_LOG_BEAN_ID_NULL);
        } else {
            String beanName = log.getSysFunc().getBeanName();
            OperBeanDetailVo response = new OperBeanDetailVo();
            response.setIdValue(idValue);
            response.setBeanName(beanName);
            Object o = logService.getBeanData(beanName,idValue);
            response.setBeanData(o);
            return callback(response);
        }
    }

    /**
     * 统计分析
     *
     * @return
     */
    @RequestMapping(value = "/analyseStat")
    public ResultBean analyseStat(SysLogAnalyseStatSH sf) {
        ChartPieData chartPieData = new ChartPieData();
        chartPieData.setTitle("系统日志分析");
        chartPieData.setUnit("次");
        ChartPieSerieData serieData = new ChartPieSerieData();
        serieData.setName("次数");
        List<SysLogAnalyseStat> list = logService.analyseStatSysLog(sf);
        for (SysLogAnalyseStat bean : list) {
            chartPieData.getXdata().add(bean.getName());
            ChartPieSerieDetailData dataDetail = new ChartPieSerieDetailData();
            dataDetail.setName(bean.getName());
            dataDetail.setValue(bean.getTotalCount());
            serieData.getData().add(dataDetail);
        }
        chartPieData.getDetailData().add(serieData);
        return callback(chartPieData);
    }
}
