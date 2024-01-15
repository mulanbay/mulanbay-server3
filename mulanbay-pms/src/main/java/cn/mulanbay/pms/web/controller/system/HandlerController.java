package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.HandlerManager;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.pms.web.bean.req.system.handler.HandlerClassForm;
import cn.mulanbay.pms.web.bean.req.system.handler.HandlerMethodForm;
import cn.mulanbay.pms.web.bean.res.system.handler.HandlerVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.request.PageSearch;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理器
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/handler")
public class HandlerController extends BaseController {

    @Autowired
    HandlerManager handlerManager;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(PageSearch sf) {
        int p = sf.getPage();
        int ps = sf.getPageSize();
        int fromIndex = (p - 1) * ps;
        int toIndex = p * ps;
        List<BaseHandler> handlerList = handlerManager.getHandlerList();
        if (StringUtil.isEmpty(handlerList)) {
            return callbackDataGrid(new PageResult<>(p, ps));
        }
        int l = handlerList.size();
        if (toIndex > l) {
            toIndex = l;
        }
        PageResult<HandlerVo> res = new PageResult<>(p, ps);
        List<HandlerVo> beanList = new ArrayList();
        for (int i = fromIndex; i < toIndex; i++) {
            HandlerVo tb = new HandlerVo();
            BaseHandler bh = handlerList.get(i);
            tb.setId(i+1L);
            tb.setHandlerName(bh.getHandlerName());
            tb.setClassName(bh.getClass().getName());
            tb.setHash(bh.hashCode());
            tb.setCheckResult(bh.getCheckResult());
            beanList.add(tb);
        }
        res.setBeanList(beanList);
        res.setMaxRow(handlerList.size());
        return callbackDataGrid(res);
    }

    /**
     * 获取处理器详情
     * @param className
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResultBean info(@RequestParam(name = "className")String className) {
        BaseHandler baseHandler = handlerManager.getHandler(className);
        return callback(baseHandler.getHandlerInfo());
    }

    /**
     * 获取处理器执行的命令
     * @param className
     * @return
     */
    @RequestMapping(value = "/methodList", method = RequestMethod.GET)
    public ResultBean methodList(@RequestParam(name = "className")String className) {
        return callback(handlerManager.getMethodList(className));
    }

    /**
     * 处理器接受命令
     * @param hc
     * @return
     */
    @RequestMapping(value = "/invokeMethod", method = RequestMethod.POST)
    public ResultBean invokeMethod(@RequestBody @Valid HandlerMethodForm hc) {
        return callback(handlerManager.invokeMethod(hc.getMethod(),hc.getClassName()));
    }

    /**
     * 自检
     * @param hc
     * @return
     */
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResultBean check(@RequestBody @Valid HandlerClassForm hc) {
        BaseHandler baseHandler = handlerManager.getHandler(hc.getClassName());
        Boolean b = baseHandler.selfCheck();
        return callback(b);
    }

    /**
     * 重载
     * @param hc
     * @return
     */
    @RequestMapping(value = "/reload", method = RequestMethod.POST)
    public ResultBean reload(@RequestBody @Valid HandlerClassForm hc) {
        BaseHandler baseHandler = handlerManager.getHandler(hc.getClassName());
        baseHandler.reload();
        return callback(null);
    }

}
