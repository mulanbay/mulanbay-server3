package cn.mulanbay.pms.web.controller.auth;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.dto.auth.SysFuncDTO;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.SysFuncService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.auth.sysFunc.SysFuncForm;
import cn.mulanbay.pms.web.bean.req.auth.sysFunc.SysFuncSH;
import cn.mulanbay.pms.web.bean.req.auth.sysFunc.SysFuncTreeReq;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 系统功能点
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/sysFunc")
public class SysFuncController extends BaseController {

    private static Class<SysFunc> beanClass = SysFunc.class;

    @Autowired
    SysFuncService sysFuncService;

    @Autowired
    AuthService authService;

    @Autowired
    SystemConfigHandler systemConfigHandler;


    /**
     * 功能点菜单树
     * 用于下拉框的选择
     *
     * @return
     */
    @RequestMapping(value = "/menuTree")
    public ResultBean menuTree() {
        try {
            List<SysFuncDTO> list = sysFuncService.getMenu();
            List<TreeBean> treeBeans = new ArrayList<>();
            for (SysFuncDTO sfb : list) {
                TreeBean treeBean = new TreeBean();
                treeBean.setId(sfb.getFuncId());
                treeBean.setText(sfb.getFuncName());
                treeBean.setPid(sfb.getPid());
                treeBeans.add(treeBean);
            }
            TreeBean root = new TreeBean();
            root.setId(0L);
            root.setText("根");
            TreeBean result = TreeBeanUtil.changToTree(root, treeBeans);
            List<TreeBean> resultList = new ArrayList<TreeBean>();
            resultList.add(result);
            return callback(resultList);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取功能点菜单树异常",
                    e);
        }
    }

    /**
     * 获取功能点树，包含所有功能点
     *
     * @return
     */
    @RequestMapping(value = "/funcTree")
    public ResultBean funcTree(SysFuncTreeReq cts) {
        try {
            List<TreeBean> list = new ArrayList<TreeBean>();
            PageRequest pr = cts.buildQuery();
            pr.setBeanClass(beanClass);
            List<SysFunc> gtList = baseService.getBeanList(pr);
            TreeBean root = new TreeBean();
            root.setId(0L);
            root.setText("根");
            root.setChildren(getFunctionTree2(root, gtList));
            list.add(root);
            return callback(list);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取功能点树异常",
                    e);
        }
    }

    public List<TreeBean> getFunctionTree2(TreeBean tb, List<SysFunc> list) {
        List<TreeBean> treeBeanList = new ArrayList<>();
        for (SysFunc sf : list) {
            SysFunc parent = sf.getParent();
            if (parent != null && tb.getId().equals(parent.getFuncId())) {
                TreeBean child = new TreeBean();
                child.setId(sf.getFuncId());
                child.setText(sf.getFuncName());
                if (sf.getSecAuth()) {
                    child.setIconCls("icon-2");
                } else if (sf.getPermissionAuth()) {
                    child.setIconCls("icon-auth");
                }
                treeBeanList.add(child);
                List<TreeBean> children = getFunctionTree2(child, list);
                child.setChildren(children);
            }
        }
        return treeBeanList;
    }

    /**
     * 获取列表（树形结构页面使用）
     *
     * @return
     */
    @RequestMapping(value = "/treeList", method = RequestMethod.GET)
    public ResultBean treeList(SysFuncSH sf) {
        if(sf.getPid()==null){
            sf.setPid(0L);
        }
        PageRequest pr = sf.buildQuery();
        pr.setPage(PageRequest.NO_PAGE);
        pr.setBeanClass(beanClass);
        Sort sort1 = new Sort("parent.funcId", Sort.ASC);
        pr.addSort(sort1);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        List<SysFunc> list = baseService.getBeanList(pr);
        return callback(list);
    }

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(SysFuncSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        PageResult<SysFunc> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 检查表单属性
     *
     * @param formRequest
     */
    private void checkFormBean(SysFuncForm formRequest) {
        if (true == formRequest.getRouter()) {
            if (StringUtil.isEmpty(formRequest.getPath())) {
                throw new ApplicationException(ErrorCode.DO_BUSS_ERROR, "路由地址不能为空");
            }
            if (false == formRequest.getFrame()) {
                if (StringUtil.isEmpty(formRequest.getComponent())) {
                    throw new ApplicationException(ErrorCode.DO_BUSS_ERROR, "组件路径不能为空");
                }
            }
        }
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid SysFuncForm formRequest) {
        checkFormBean(formRequest);
        SysFunc bean = new SysFunc();
        BeanCopy.copy(formRequest, bean, true);
        if (formRequest.getParentId() != null) {
            SysFunc parent = baseService.getObject(beanClass, formRequest.getParentId());
            bean.setParent(parent);
        }
        bean.setCreatedTime(new Date());
        baseService.saveObject(bean);
        return callback(null);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "funcId") Long funcId) {
        SysFunc br = baseService.getObject(beanClass, funcId);
        return callback(br);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid SysFuncForm formRequest) {
        checkFormBean(formRequest);
        SysFunc bean = baseService.getObject(beanClass, formRequest.getFuncId());
        BeanCopy.copy(formRequest, bean, true);
        if (formRequest.getParentId() != null) {
            SysFunc parent = baseService.getObject(beanClass, formRequest.getParentId());
            bean.setParent(parent);
        }
        bean.setModifyTime(new Date());
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
        Long[] ids = NumberUtil.stringArrayToLongArray(deleteRequest.getIds().split(","));
        for(Long id : ids){
            sysFuncService.deleteFunctions(id);
        }
        return callback(null);
    }

    /**
     * 刷新系统缓存
     *
     * @return
     */
    @RequestMapping(value = "/refreshCache", method = RequestMethod.POST)
    public ResultBean refreshCache() {
        systemConfigHandler.reloadFunctions();
        return callback(null);
    }

}
