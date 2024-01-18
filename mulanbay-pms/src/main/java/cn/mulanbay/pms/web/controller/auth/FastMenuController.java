package cn.mulanbay.pms.web.controller.auth;

import cn.mulanbay.pms.persistent.domain.FastMenu;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.dto.auth.FastMenuDTO;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.FastMenuService;
import cn.mulanbay.pms.web.bean.LoginUser;
import cn.mulanbay.pms.web.bean.req.auth.fastMenu.FastMenuForm;
import cn.mulanbay.pms.web.bean.req.main.UserCommonFrom;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快捷菜单
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/fastMenu")
public class FastMenuController extends BaseController {

    private static Class<FastMenu> beanClass = FastMenu.class;

    @Autowired
    FastMenuService fastMenuService;

    @Autowired
    AuthService authService;

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(UserCommonFrom ucr) {
        List<FastMenuDTO> list = fastMenuService.selectFastMenuList(ucr.getUserId());
        return callback(list);
    }

    /**
     * 树形结构
     *
     * @return
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public ResultBean tree() {
        LoginUser loginUser = tokenHandler.getLoginUser(request);
        Long userId = loginUser.getUserId();
        Long roleId = loginUser.getRoleId();
        List<SysFunc> sfList = authService.selectRoleFunctionMenuList(roleId, true);
        List<TreeBean> funcTree = this.getFunctionTree(sfList, 0L);
        Map map = new HashMap<>();
        map.put("treeData", funcTree);
        List checkedKeys = new ArrayList();
        List<FastMenuDTO> list = fastMenuService.selectFastMenuList(userId);
        for (FastMenuDTO sf : list) {
            checkedKeys.add(sf.getMenuId());
        }
        map.put("checkedKeys", checkedKeys);
        return callback(map);
    }

    private List<TreeBean> getFunctionTree(List<SysFunc> list, long pid) {
        List<TreeBean> res = new ArrayList<>();
        for (SysFunc sf : list) {
            if (sf.getParentId() == pid) {
                TreeBean tb = new TreeBean();
                tb.setId(sf.getFuncId());
                tb.setText(sf.getFuncName());
                res.add(tb);
                List<TreeBean> children = getFunctionTree(list, sf.getFuncId().longValue());
                tb.setChildren(children);
            }
        }
        return res;
    }

    /**
     * 保存快捷菜单
     *
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultBean save(@RequestBody @Valid FastMenuForm ur) {
        fastMenuService.save(ur.getUserId(), ur.getMenuIds());
        return callback(null);
    }

}
