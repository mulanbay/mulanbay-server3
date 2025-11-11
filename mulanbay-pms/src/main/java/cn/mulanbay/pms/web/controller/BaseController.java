package cn.mulanbay.pms.web.controller;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.handler.MessageHandler;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.ValidateError;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.handler.TokenHandler;
import cn.mulanbay.pms.persistent.domain.TreatTest;
import cn.mulanbay.pms.web.bean.LoginUser;
import cn.mulanbay.pms.web.bean.res.DataGrid;
import cn.mulanbay.web.bean.request.PageSearch;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
    protected CacheHandler cacheHandler;

    /**
     * 默认的返回结果
     * 针对正常的无具体返回数据的
     * 不能修改该对象
     */
    private static final ResultBean defaultResultBean = new ResultBean();

    /**
     * 避免列表数据接口返回null
     * 不能修改该对象
     */
    private static final List emptyList = new ArrayList<>();

    /**
     * 获取当前用户的编号
     * 一般来说，当前用户编号都是在类ControllerHandler里设置，无需再各个controller手动获取
     * @see cn.mulanbay.pms.web.aop.ControllerHandler
     * @return
     */
    protected Long getCurrentUserId() {
        LoginUser lu = tokenHandler.getLoginUser(request);
        return lu == null ? null : lu.getUserId();
    }

    /**
     * 封装分页数据
     * @param ps
     * @param beanClass
     * @param s
     * @return
     * @param <T>
     */
    protected <T> ResultBean queryDataGrid(PageSearch ps, Class<T> beanClass, Sort s) {
        PageRequest pr = ps.buildQuery();
        pr.setBeanClass(beanClass);
        pr.addSort(s);
        PageResult<T> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 分页数据
     *
     * @param pr
     * @return
     */
    protected ResultBean callbackDataGrid(PageResult<?> pr) {
        ResultBean rb = new ResultBean();
        DataGrid dg = this.createDataGrid(pr);
        rb.setData(dg);
        return rb;
    }

    /**
     * 创建列表数据封装
     * @param pr
     * @return
     */
    private DataGrid createDataGrid(PageResult<?> pr) {
        DataGrid dg = new DataGrid();
        dg.setPage(pr.getPage());
        dg.setTotal(pr.getMaxRow());
        dg.setRows(pr.getBeanList() == null ? emptyList : pr.getBeanList());
        return dg;
    }

    /**
     * 返回结果
     * @param o
     * @return
     */
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

}
