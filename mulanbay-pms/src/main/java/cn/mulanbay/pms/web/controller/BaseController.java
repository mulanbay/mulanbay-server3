package cn.mulanbay.pms.web.controller;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.handler.MessageHandler;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.ValidateError;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.handler.TokenHandler;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.IdFieldType;
import cn.mulanbay.pms.web.bean.LoginUser;
import cn.mulanbay.pms.web.bean.req.BaseYoyStatSH;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.pms.web.bean.res.DataGrid;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller的基类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected BaseService baseService;

    @Autowired
    protected MessageHandler messageHandler;

    @Autowired
    protected TokenHandler tokenHandler;

    @Autowired
    CacheHandler cacheHandler;

    private static ResultBean defaultResultBean = new ResultBean();

    private static List emptyList = new ArrayList<>();

    /**
     * 获取当前用户的编号
     * 一般来说，当前用户编号都是在类ControllerHandler里设置，无需再各个controller手动获取
     * @see cn.mulanbay.pms.web.aop.ControllerHandler
     * todo 如果在jwt中保存当前用户编号，就可以不用再一次去redis里获取LoginUser再拿用户编号
     * @return
     */
    protected Long getCurrentUserId() {
        LoginUser lu = tokenHandler.getLoginUser(request);
        return lu == null ? null : lu.getUserId();
    }

    /**
     * 分页数据
     *
     * @param pr
     * @return
     */
    protected ResultBean callbackDataGrid(PageResult<?> pr) {
        ResultBean rb = new ResultBean();
        DataGrid dg = new DataGrid();
        dg.setPage(pr.getPage());
        dg.setTotal(pr.getMaxRow());
        dg.setRows(pr.getBeanList() == null ? emptyList : pr.getBeanList());
        rb.setData(dg);
        return rb;
    }


    protected ResultBean callback(Object o) {
        if (o == null) {
            return defaultResultBean;
        }
        ResultBean rb = new ResultBean();
        rb.setData(o);
        return rb;
    }

    /**
     * 直接返回错误代码
     *
     * @param errorCode
     * @return
     */
    protected ResultBean callbackErrorCode(int errorCode) {
        ResultBean rb = new ResultBean();
        rb.setCode(errorCode);
        ValidateError ve = messageHandler.getCodeInfo(errorCode);
        rb.setMessage(ve.getErrorInfo());
        return rb;
    }

    /**
     * 直接返回错误信息
     *
     * @param msg
     * @return
     */
    protected ResultBean callbackErrorInfo(String msg) {
        ResultBean rb = new ResultBean();
        rb.setCode(ErrorCode.DO_BUSS_ERROR);
        rb.setMessage(msg);
        return rb;
    }

    protected Serializable formatIdValue(IdFieldType idFieldType, String idValue) {
        Serializable bussId = null;
        if (idFieldType == IdFieldType.LONG) {
            bussId = Long.parseLong(idValue);
        } else if (idFieldType == IdFieldType.INTEGER) {
            bussId = Integer.parseInt(idValue);
        } else if (idFieldType == IdFieldType.SHORT) {
            bussId = Short.parseShort(idValue);
        } else {
            bussId = idValue;
        }
        return bussId;
    }


    /**
     * 获取日期的标题，只要用于报表的子标题
     *
     * @param sf
     * @return
     */
    protected String getDateTitle(DateStatSH sf) {
        if (sf.getStartDate() == null && sf.getEndDate() == null) {
            return "";
        } else if (sf.getStartDate() != null && sf.getEndDate() == null) {
            return "从" + DateUtil.getFormatDate(sf.getStartDate(), DateUtil.FormatDay1) + "开始";
        } else if (sf.getStartDate() == null && sf.getEndDate() != null) {
            return "截止" + DateUtil.getFormatDate(sf.getEndDate(), DateUtil.FormatDay1);
        } else {
            return DateUtil.getFormatDate(sf.getStartDate(), DateUtil.FormatDay1) + "~" +
                    DateUtil.getFormatDate(sf.getEndDate(), DateUtil.FormatDay1);
        }
    }

    /**
     * 初始化同期对比数据
     *
     * @param sf
     * @param title
     * @param subTitle
     * @return
     */
    protected ChartData initYoyCharData(BaseYoyStatSH sf, String title, String subTitle) {
        ChartData chartData = new ChartData();
        chartData.setTitle(title);
        chartData.setSubTitle(subTitle);
        if (sf.getDateGroupType() == DateGroupType.MONTH) {
            for (int i = 1; i <= Constant.MAX_MONTH; i++) {
                chartData.getIntXData().add(i);
                chartData.getXdata().add(i + "月份");
            }
        } else if (sf.getDateGroupType() == DateGroupType.WEEK) {
            for (int i = 1; i <= Constant.MAX_WEEK; i++) {
                chartData.getIntXData().add(i);
                chartData.getXdata().add("第" + i + "周");
            }
        }
        return chartData;
    }


    /**
     * 获取时间区间
     *
     * @param dateGroupType
     * @param date
     * @return
     */
    protected Date[] getStatDateRange(DateGroupType dateGroupType, Date date) {
        Date[] dd = new Date[2];
        if (dateGroupType == DateGroupType.DAY) {
            dd[0] = DateUtil.getFromMiddleNightDate(date);
            dd[1] = DateUtil.getTodayTillMiddleNightDate(date);
        } else if (dateGroupType == DateGroupType.MONTH) {
            dd[0] = DateUtil.getFromMiddleNightDate(DateUtil.getFirstDayOfMonth(date));
            Date endDate = DateUtil.getLastDayOfMonth(date);
            dd[1] = DateUtil.getTodayTillMiddleNightDate(endDate);
        } else {
            int year = Integer.parseInt(DateUtil.getFormatDate(date, "yyyy"));
            dd[0] = DateUtil.getDate(year + "-01-01 00:00:00", DateUtil.Format24Datetime);
            dd[1] = DateUtil.getDate(year + "-12-31 23:59:59", DateUtil.Format24Datetime);
        }
        return dd;
    }
}
