package cn.mulanbay.pms.web.controller.log;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.handler.SysCodeHandler;
import cn.mulanbay.pms.persistent.domain.SysCode;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.log.sysCode.SysCodeForm;
import cn.mulanbay.pms.web.bean.req.log.sysCode.SysCodeSH;
import cn.mulanbay.pms.web.bean.res.log.sysCode.SysCodeCacheInfoVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 系统代码
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/sysCode")
public class SysCodeController extends BaseController {

    private static Class<SysCode> beanClass = SysCode.class;

    @Autowired
    SysCodeHandler sysCodeHandler;

    @Autowired
    CacheHandler cacheHandler;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(SysCodeSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        if (StringUtil.isEmpty(sf.getSortField())) {
            Sort s = new Sort("code", Sort.ASC);
            pr.addSort(s);
        } else {
            Sort s = new Sort(sf.getSortField(), sf.getSortType());
            pr.addSort(s);
        }
        PageResult<SysCode> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid SysCodeForm formRequest) {
        createBean(formRequest);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@Valid @RequestParam(name = "code") Integer code) {
        SysCode bean = baseService.getObject(beanClass, code);
        return callback(bean);
    }

    private void createBean(SysCodeForm formRequest) {
        SysCode bean = new SysCode();
        BeanCopy.copy(formRequest, bean);
        bean.setCount(0);
        baseService.saveObject(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid SysCodeForm formRequest) {
        SysCode bean = baseService.getObject(beanClass, formRequest.getCode());
        BeanCopy.copy(formRequest, bean);
        baseService.updateObject(bean);
        sysCodeHandler.refreshSysCode(bean.getCode());
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        String[] ids = deleteRequest.getIds().split(",");
        for (String s : ids) {
            Integer code =  Integer.parseInt(s);
            baseService.deleteObject(beanClass,code);
            sysCodeHandler.refreshSysCode(code);
        }
        return callback(null);
    }

    /**
     * 刷新缓存
     *
     * @return
     */
    @RequestMapping(value = "/refreshCache", method = RequestMethod.POST)
    public ResultBean refreshCache(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        String[] ids = deleteRequest.getIds().split(",");
        for (String s : ids) {
            sysCodeHandler.refreshSysCode(Integer.parseInt(s));
        }
        return callback(null);
    }

    /**
     * 获取缓存详情
     *
     * @return
     */
    @RequestMapping(value = "/cacheInfo", method = RequestMethod.GET)
    public ResultBean cacheInfo(@Valid @RequestParam(name = "code") Integer code) {
        SysCodeCacheInfoVo vo = new SysCodeCacheInfoVo();
        String key1 = CacheKey.getKey(CacheKey.SYS_CODE_COUNTS,code.toString());
        vo.setBatchCounts(cacheHandler.incre(key1,0));
        String key2 = sysCodeHandler.getLimitKey(code);
        vo.setLimitCounts(cacheHandler.get(key2,Integer.class));
        String limitKey = CacheKey.getKey(CacheKey.USER_CODE_LIMIT, code.toString(),"*");
        Set<String> userLimitKeys = cacheHandler.keys(limitKey);
        for(String key: userLimitKeys){
            Integer v = cacheHandler.get(key,Integer.class);
            vo.addUserLimit(key,v);
        }
        return callback(vo);
    }

}
