package cn.mulanbay.pms.web.controller.auth;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.dto.auth.SysFuncDTO;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.SysFuncService;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

}
