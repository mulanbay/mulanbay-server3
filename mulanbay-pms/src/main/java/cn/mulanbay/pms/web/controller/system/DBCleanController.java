package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.DBClean;
import cn.mulanbay.pms.persistent.enums.CleanType;
import cn.mulanbay.pms.persistent.service.DBCleanService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.system.dbClean.DBCleanForm;
import cn.mulanbay.pms.web.bean.req.system.dbClean.DBCleanSH;
import cn.mulanbay.pms.web.bean.req.system.dbClean.DBManualCleanForm;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据库清理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/dbClean")
public class DBCleanController extends BaseController {

    private static Class<DBClean> beanClass = DBClean.class;

    @Autowired
    DBCleanService dbCleanService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(DBCleanSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        PageResult<DBClean> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid DBCleanForm formRequest) {
        DBClean bean = new DBClean();
        BeanCopy.copy(formRequest, bean, true);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "id") Long id) {
        DBClean br = baseService.getObject(beanClass, id);
        return callback(br);
    }

    /**
     * 手动执行
     *
     * @return
     */
    @RequestMapping(value = "/manualClean", method = RequestMethod.POST)
    public ResultBean manualClean(@RequestBody @Valid DBManualCleanForm dmc) {
        DBClean bean = baseService.getObject(beanClass, dmc.getId());
        CleanType cleanType = dmc.getCleanType();
        if(cleanType==CleanType.TRUNCATE){
            dbCleanService.truncateTable(bean.getTableName());
            return callback(null);
        }else{
            int n = dbCleanService.manualClean(bean, dmc.getDays(),dmc.getUseEc(), true);
            return callback(n);
        }
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid DBCleanForm formRequest) {
        DBClean bean = baseService.getObject(beanClass, formRequest.getId());
        BeanCopy.copy(formRequest, bean, true);
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
     * 获取总记录数
     *
     * @return
     */
    @RequestMapping(value = "/counts", method = RequestMethod.GET)
    public ResultBean counts(@RequestParam(name = "id") Long id) {
        DBClean br = baseService.getObject(beanClass, id);
        long n = dbCleanService.getCounts(br.getTableName());
        return callback(n);
    }

}
