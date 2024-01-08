package cn.mulanbay.pms.web.controller.auth;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.persistent.domain.MonitorUser;
import cn.mulanbay.pms.persistent.enums.MonitorBussType;
import cn.mulanbay.pms.persistent.service.MonitorUserService;
import cn.mulanbay.pms.web.bean.req.auth.user.MonitorUserForm;
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

/**
 * 监控用户
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/monitorUser")
public class MonitorUserController extends BaseController {

    private static Class<MonitorUser> beanClass = MonitorUser.class;

    @Autowired
    MonitorUserService monitorUserService;

    /**
     * 获取用户系统监控树
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(@RequestParam(name = "userId") Long userId) {
        try {
            List<MonitorUser> urList = monitorUserService.selectList(userId);
            List<TreeBean> treeBeans = new ArrayList<>();
            List checkedKeys = new ArrayList();
            for (MonitorBussType sfb : MonitorBussType.values()) {
                if (sfb == MonitorBussType.ALL) {
                    continue;
                }
                TreeBean treeBean = new TreeBean();
                treeBean.setId(sfb.getValue());
                treeBean.setText(sfb.getName());
                if (checkMonitorExit(sfb, urList)) {
                    treeBean.setChecked(true);
                    checkedKeys.add(sfb.getValue());
                }
                treeBeans.add(treeBean);
            }
            Map map = new HashMap<>();
            map.put("treeData", treeBeans);
            map.put("checkedKeys", checkedKeys);
            return callback(map);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取用户系统监控树异常",
                    e);
        }
    }

    private boolean checkMonitorExit(MonitorBussType v, List<MonitorUser> urList) {
        if (StringUtil.isEmpty(urList)) {
            return false;
        } else {
            for (MonitorUser smu : urList) {
                if (smu.getBussType() == v) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 保存用户监控
     *
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultBean save(@RequestBody @Valid MonitorUserForm ur) {
        monitorUserService.save(ur.getUserId(), ur.getBussTypes());
        return callback(null);
    }

}
