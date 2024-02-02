package cn.mulanbay.pms.web.controller.life;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Archive;
import cn.mulanbay.pms.persistent.service.ArchiveService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.life.archive.ArchiveForm;
import cn.mulanbay.pms.web.bean.req.life.archive.ArchiveSH;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 人生档案
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/archive")
public class ArchiveController extends BaseController {

    private static Class<Archive> beanClass = Archive.class;

    @Autowired
    ArchiveService archiveService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ArchiveSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        if(StringUtil.isNotEmpty(sf.getSortField())){
            Sort sort = new Sort(sf.getSortField(), sf.getSortType());
            pr.addSort(sort);
        }
        PageResult<Archive> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid ArchiveForm form) {
        Archive bean = new Archive();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(null);
    }

    /**
     * 同步
     *
     * @return
     */
    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    public ResultBean sync(@RequestBody @Valid ArchiveForm form) {
        Archive bean = archiveService.getArchive(form.getUserId(), form.getBussType(), form.getSourceId());
        if (bean == null) {
            bean = new Archive();
            BeanCopy.copy(form, bean);
            baseService.saveObject(bean);
        } else {
            Long archiveId = bean.getArchiveId();
            BeanCopy.copy(form, bean);
            bean.setArchiveId(archiveId);
            baseService.updateObject(bean);
        }
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "archiveId") Long archiveId) {
        Archive bean = baseService.getObject(beanClass,archiveId);
        return callback(bean);
    }

    /**
     * 获取原始信息
     *
     * @return
     */
    @RequestMapping(value = "/getSource", method = RequestMethod.GET)
    public ResultBean getSource(@RequestParam(name = "archiveId") Long archiveId) {
        Archive bean = baseService.getObject(beanClass,archiveId);
        Class clz = bean.getBussType().getBeanClass();
        Object o = baseService.getObject(clz, bean.getSourceId());
        return callback(o);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid ArchiveForm form) {
        Archive bean = baseService.getObject(beanClass,form.getArchiveId());
        BeanCopy.copy(form, bean);
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
        baseService.deleteObjects(beanClass, NumberUtil.stringArrayToLongArray(deleteRequest.getIds().split(",")));
        return callback(null);
    }

}
