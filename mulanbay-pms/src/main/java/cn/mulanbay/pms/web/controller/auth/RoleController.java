package cn.mulanbay.pms.web.controller.auth;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.persistent.domain.Role;
import cn.mulanbay.pms.persistent.dto.auth.RoleFunctionDTO;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.auth.role.RoleForm;
import cn.mulanbay.pms.web.bean.req.auth.role.RoleFunctionForm;
import cn.mulanbay.pms.web.bean.req.auth.role.RoleSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.mulanbay.pms.common.Constant.ROOT_ID;
import static cn.mulanbay.pms.common.Constant.ROOT_NAME;

/**
 * 角色
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    private static Class<Role> beanClass = Role.class;

    @Autowired
    AuthService authService;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    /**
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree() {
        try {
            RoleSH sf = new RoleSH();
            sf.setPage(PageRequest.NO_PAGE);
            PageResult<Role> pr = this.getRoleResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<Role> gtList = pr.getBeanList();
            for (Role gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getRoleId());
                tb.setText(gt.getRoleName());
                list.add(tb);
            }
            return callback(list);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取角色树异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(RoleSH sf) {
        return callbackDataGrid(getRoleResult(sf));
    }

    private PageResult<Role> getRoleResult(RoleSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        PageResult<Role> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid RoleForm formRequest) {
        Role bean = new Role();
        BeanCopy.copy(formRequest, bean);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@Valid @RequestParam(name = "roleId") Long roleId) {
        Role bean = baseService.getObject(beanClass, roleId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid RoleForm formRequest) {
        Role bean = baseService.getObject(beanClass, formRequest.getRoleId());
        BeanCopy.copy(formRequest, bean);
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
        String[] ids = deleteRequest.getIds().split(",");
        for (String id : ids) {
            authService.deleteRole(Long.valueOf(id));
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
        systemConfigHandler.reloadRoleFunctions();
        return callback(null);
    }

    /**
     * 保存角色功能点
     *
     * @return
     */
    @RequestMapping(value = "/saveRoleFunction", method = RequestMethod.POST)
    public ResultBean saveRoleFunction(@RequestBody @Valid RoleFunctionForm ur) {
        authService.saveRoleFunction(ur.getRoleId(), ur.getFunctionIds());
        return callback(null);
    }

    /**
     * 获取角色功能点树
     *
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/roleFunctionTree")
    public ResultBean roleFunctionTree(@RequestParam(name = "roleId") Long roleId) {
        try {
            List<RoleFunctionDTO> rfList = authService.selectRoleFunctionList(roleId);
            List<TreeBean> list = new ArrayList<TreeBean>();
            TreeBean root = new TreeBean();
            root.setId(ROOT_ID);
            root.setText(ROOT_NAME);
            root.setChildren(getFunctionTree(root, rfList));
            root.setChecked(false);
            list.add(root);
            Map map = new HashMap<>();
            map.put("treeData", list);
            List checkedKeys = new ArrayList();
            for (RoleFunctionDTO sf : rfList) {
                if (sf.getRoleFunctionId() != null) {
                    checkedKeys.add(sf.getFuncId());
                }
            }
            map.put("checkedKeys", checkedKeys);
            return callback(map);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取角色功能点树异常",
                    e);
        }
    }

    private List<TreeBean> getFunctionTree(TreeBean tb, List<RoleFunctionDTO> list) {
        List<TreeBean> treeBeanList = new ArrayList<>();
        for (RoleFunctionDTO sf : list) {
            Long pid = sf.getPid();
            if (pid != null && tb.getId().equals(pid)) {
                TreeBean child = new TreeBean();
                child.setId(sf.getFuncId());
                child.setText(sf.getFuncName());
                if (sf.getRoleFunctionId() != null) {
                    tb.setChecked(true);
                }
                treeBeanList.add(child);
                List<TreeBean> children = getFunctionTree(child, list);
                child.setChildren(children);
            }
        }
        return treeBeanList;
    }

}
