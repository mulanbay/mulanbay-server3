package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.ResourcesHandler;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.persistent.domain.DBClean;
import cn.mulanbay.pms.persistent.domain.Resources;
import cn.mulanbay.pms.util.FileUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.system.backup.BackupFileDeleteForm;
import cn.mulanbay.pms.web.bean.req.system.dbClean.DBCleanSH;
import cn.mulanbay.pms.web.bean.req.system.resources.ResourcesSH;
import cn.mulanbay.pms.web.bean.res.system.backup.FileVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.request.PageSearch;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

/**
 * 资源管理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/resources")
public class ResourcesController extends BaseController {

    private static Class<Resources> beanClass = Resources.class;

    @Autowired
    ResourcesHandler resourcesHandler;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(ResourcesSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.ASC);
        pr.addSort(sort);
        PageResult<Resources> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
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
            resourcesHandler.deleteResource(Long.valueOf(s));
        }
        return callback(null);
    }
}
