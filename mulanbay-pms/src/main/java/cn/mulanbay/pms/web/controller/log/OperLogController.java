package cn.mulanbay.pms.web.controller.log;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.OperLog;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.dto.log.OperLogDateStat;
import cn.mulanbay.pms.persistent.dto.log.OperLogStat;
import cn.mulanbay.pms.persistent.dto.log.OperLogTreeStat;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.LogCompareType;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.LogService;
import cn.mulanbay.pms.util.ChartUtil;
import cn.mulanbay.pms.util.ClazzUtils;
import cn.mulanbay.pms.web.bean.req.log.operLog.*;
import cn.mulanbay.pms.web.bean.res.chart.*;
import cn.mulanbay.pms.web.bean.res.log.operLog.OperLogCompareVo;
import cn.mulanbay.pms.web.bean.res.log.sysLog.OperBeanDetailVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 操作日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/operLog")
public class OperLogController extends BaseController {

    private static Class<OperLog> beanClass = OperLog.class;

    @Autowired
    AuthService authService;

    @Autowired
    LogService logService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(OperLogSH sf) {
        return callbackDataGrid(getOperLogResult(sf));
    }

    private PageResult<OperLog> getOperLogResult(OperLogSH sf) {
        String beanName = sf.getBeanName();
        if (StringUtil.isNotEmpty(beanName)) {
            String beanNameSql = "sysFunc.id in (select id from SysFunc where beanName='" + beanName + "' )";
            sf.setBeanName(beanNameSql);
        }
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("occurEndTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<OperLog> qr = baseService.getBeanResult(pr);
        return qr;
    }


    /**
     * 获取流水列表数据
     *
     * @return
     */
    @RequestMapping(value = "/flow", method = RequestMethod.GET)
    public ResultBean flow(@Valid OperLogFlowSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setPageSize(sf.getPageSize());
        pr.setBeanClass(beanClass);
        Sort s = new Sort("occurEndTime", Sort.ASC);
        pr.addSort(s);
        PageResult<OperLog> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "id") Long id) {
        OperLog br = baseService.getObject(beanClass, id);
        return callback(br);
    }

    /**
     * 获取请求参数
     *
     * @return
     */
    @RequestMapping(value = "/paras", method = RequestMethod.GET)
    public ResultBean paras(@RequestParam(name = "id") Long id) {
        OperLog br = baseService.getObject(beanClass, id);
        return callback(br.getParas());
    }

    /**
     * 获取返回数据
     *
     * @return
     */
    @RequestMapping(value = "/returnData", method = RequestMethod.GET)
    public ResultBean returnData(@RequestParam(name = "id") Long id) {
        OperLog br = baseService.getObject(beanClass, id);
        return callback(br.getReturnData());
    }

    /**
     * 查询被操作的业务对象的数据
     *
     * @param id 为操作日志的记录号
     * @return
     */
    @RequestMapping(value = "/beanDetail", method = RequestMethod.GET)
    public ResultBean beanDetail(@RequestParam(name = "id") Long id)  {
        OperLog br = baseService.getObject(beanClass, id);
        String idValue = br.getIdValue();
        if (StringUtil.isEmpty(idValue)) {
            throw new ApplicationException(PmsCode.OPERATION_LOG_BEAN_ID_NULL);
        } else {
            OperBeanDetailVo response = new OperBeanDetailVo();
            response.setIdValue(idValue);
            response.setBeanName(br.getSysFunc().getBeanName());
            Serializable bussId = formatIdValue(br.getSysFunc().getIdFieldType(), idValue);
            String idFiled = this.formatIdField(br.getSysFunc().getIdField());
            Class clz = ClazzUtils.getClass(br.getSysFunc().getBeanName());
            Object o = baseService.getObject(clz, bussId, idFiled);
            response.setBeanData(o);
            return callback(response);
        }
    }

    /**
     * 获取操作日志的比对数据：最新的数据、当前的数据、往前（或往后）的数据
     *
     * @param operLogId 为OperationLog的主键
     * @return
     */
    @RequestMapping(value = "/compareData", method = RequestMethod.GET)
    public ResultBean compareData(@RequestParam(name = "operLogId")Long operLogId,@RequestParam(name = "compareType") LogCompareType compareType) {
        OperLogCompareVo vo = new OperLogCompareVo();
        //获取当前日志记录的数据
        OperLog log = baseService.getObject(beanClass, operLogId);
        vo.setCurrentData(log);
        vo.setBussId(log.getIdValue());
        vo.setBeanName(log.getSysFunc().getBeanName());
        SysFunc sf = log.getSysFunc();
        String idValue = getAndUpdateIdValue(log);
        if (sf != null) {
            //获取业务表最新的数据
            Serializable bussId = formatIdValue(sf.getIdFieldType(), idValue);
            String idFiled = this.formatIdField(sf.getIdField());
            Class clz = ClazzUtils.getClass(sf.getBeanName());
            Object o = baseService.getObject(clz, bussId, idFiled);
            if (o != null) {
                vo.setLatestData(o);
            }
        }
        OperLog nearest = logService.getNearestCompareLog(log, compareType);
        vo.setCompareData(nearest);
        return callback(vo);
    }

    /**
     * 获取某个具体业务bean的比对数据：最新的数据、当前的数据、往前（或往后）的数据
     *
     * @param gr:id 为beanName的主键
     * @return
     */
    @RequestMapping(value = "/editLogData", method = RequestMethod.GET)
    public ResultBean editLogData(@Valid OperLogGetEditReq gr) {
        OperLogCompareVo vo = new OperLogCompareVo();
        SysFunc sf = logService.getEditSysFunc(gr.getBeanName());
        if (sf == null) {
            throw new ApplicationException(PmsCode.SYSTEM_FUNCTION_NOT_DEFINE, gr.getBeanName() + "修改类功能点没有定义");
        }
        //获取业务表最新的数据
        Serializable bussId = formatIdValue(sf.getIdFieldType(), gr.getId());
        vo.setBussId(gr.getId());
        String idFiled = this.formatIdField(sf.getIdField());
        Class clz = ClazzUtils.getClass(sf.getBeanName());
        Object o = baseService.getObject(clz, bussId, idFiled);
        if (o != null) {
            vo.setLatestData(o);
        }
        //获取最近一次修改
        OperLog latest = logService.getLatestOperLog(gr.getId(), gr.getBeanName());
        if (latest != null) {
            vo.setCurrentData(latest);
            //获取最近的修改记录比较
            OperLog nearest = logService.getNearestCompareLog(latest, gr.getCompareType());
            vo.setCompareData(nearest);
        }
        return callback(vo);
    }

    /**
     * 删除操作因为支持多个，索引会多加了s，实际上字段是id
     * @param idField
     * @return
     */
    private String formatIdField(String idField) {
        if("ids".equals(idField)){
            return "id";
        }
        return idField;
    }

    private String getAndUpdateIdValue(OperLog br) {
        String idValue = br.getIdValue();
        if (StringUtil.isEmpty(idValue)) {
            //从paras重新获取
            Map map = (Map) JsonUtil.jsonToBean(br.getParas(), Map.class);
            Object o = map.get(br.getSysFunc().getIdField());
            if (o != null) {
                idValue = o.toString();
                br.setIdValue(idValue);
                //更新
                baseService.updateObject(br);
            }
        }
        return idValue;
    }

    /**
     * 获取比对数据：最新的数据、当前的数据、往前（或往后）的数据
     *
     * @param currentCompareId 目前正在比较的OperationLog的主键(比较页面的中间那个区域)
     * @return
     */
    @RequestMapping(value = "/nearestCompareData", method = RequestMethod.GET)
    public ResultBean nearestCompareData(@RequestParam(name = "currentCompareId")Long currentCompareId,@RequestParam(name = "compareType") LogCompareType compareType) {
        OperLogCompareVo response = new OperLogCompareVo();
        if (currentCompareId == null) {
            return callback(response);
        }
        OperLog currentCompareLog = baseService.getObject(beanClass, currentCompareId);
        if (StringUtil.isEmpty(currentCompareLog.getIdValue())) {
            //idValue无法比较
            throw new ApplicationException(PmsCode.OPERATION_LOG_COMPARE_ID_VALUE_NULL);
        }
        OperLog nextCompareLog = logService.getNearestCompareLog(currentCompareLog, compareType);
        response.setCompareData(nextCompareLog);
        OperLog currentLog = baseService.getObject(OperLog.class, currentCompareId);
        response.setCurrentData(currentLog);
        return callback(response);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/treeStat", method = RequestMethod.GET)
    public ResultBean treeStat(OperLogTreeStatSH sf) {
        //用户查询
        if(StringUtil.isNotEmpty(sf.getUsername())){
            User user = authService.getUserByUsernameOrPhone(sf.getUsername());
            if (user == null) {
                return callbackErrorCode(PmsCode.USER_NOTFOUND);
            }
            sf.setUserId(user.getUserId());
        }
        List<OperLogTreeStat> list = logService.treeStatOperLog(sf);
        ChartTreeDetailData data = new ChartTreeDetailData(0, "根");
        for (OperLogTreeStat sl : list) {
            String parentName = sl.getPname();
            if (StringUtil.isEmpty(parentName)) {
                parentName = "未分类";
            }
            ChartTreeDetailData level1 = data.findChild(parentName);
            if (level1 == null) {
                level1 = new ChartTreeDetailData(0, parentName);
                level1.addChild(sl.getTotalCount().doubleValue(), sl.getName());
                data.addChild(level1);
            } else {
                level1.addChild(sl.getTotalCount().doubleValue(), sl.getName());
            }
        }
        ChartTreeData treeData = new ChartTreeData();
        treeData.setData(data);
        treeData.setUnit("次");
        treeData.setTitle("操作日志关系图");
        return callback(treeData);
    }

    /**
     * 基于分页的统计
     * @param sf
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(OperLogStatSH sf) {
        //用户查询
        if(StringUtil.isNotEmpty(sf.getUsername())){
            User user = authService.getUserByUsernameOrPhone(sf.getUsername());
            if (user == null) {
                return callbackErrorCode(PmsCode.USER_NOTFOUND);
            }
            sf.setUserId(user.getUserId());
        }
        List<OperLogStat> list = logService.statOperLog(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("操作日志统计");
        chartData.setLegendData(new String[]{"次数"});
        ChartYData yData1 = new ChartYData("次数","次");
        for (OperLogStat bean : list) {
            chartData.getXdata().add(bean.getFuncName());
            yData1.getData().add(bean.getTotalCount());
        }
        chartData.getYdata().add(yData1);
        return callback(chartData);
    }

    /**
     * 按照日期统计
     *
     * @return
     */
    @RequestMapping(value = "/dateStat")
    public ResultBean dateStat(OperLogDateStatSH sf) {
        //用户查询
        if(StringUtil.isNotEmpty(sf.getUsername())){
            User user = authService.getUserByUsernameOrPhone(sf.getUsername());
            if (user == null) {
                return callbackErrorCode(PmsCode.USER_NOTFOUND);
            }
            sf.setUserId(user.getUserId());
        }
        if (sf.getDateGroupType() == DateGroupType.DAYCALENDAR) {
            return callback(createChartCalendarData(sf));
        }
        List<OperLogDateStat> list = logService.statDateOperLog(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle("操作日志统计");
        chartData.setUnit("次");
        chartData.setLegendData(new String[]{"次数"});
        ChartYData yData1 = new ChartYData();
        yData1.setName("次数");
        for (OperLogDateStat bean : list) {
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
        }
        chartData.getYdata().add(yData1);
        chartData = ChartUtil.completeDate(chartData, sf);
        return callback(chartData);
    }

    private ChartCalendarData createChartCalendarData(OperLogDateStatSH sf) {
        List<OperLogDateStat> list = logService.statDateOperLog(sf);
        ChartCalendarData calendarData = ChartUtil.createChartCalendarData("操作日志统计", "操作次数", "次", sf, list);
        calendarData.setTop(3);
        return calendarData;
    }

}
