package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.pms.handler.SystemStatusHandler;
import cn.mulanbay.pms.web.bean.req.system.system.SystemLockForm;
import cn.mulanbay.pms.web.bean.req.system.system.SystemUnlockForm;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 命令配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/system")
public class SystemController extends BaseController {

    @Autowired
    SystemStatusHandler systemStatusHandler;

    /**
     * 锁定
     * @param hc
     * @return
     */
    @RequestMapping(value = "/lock", method = RequestMethod.POST)
    public ResultBean lock(@RequestBody @Valid SystemLockForm hc) {
        Boolean b = systemStatusHandler.setStatus(hc.getCode(),hc.getMessage(),hc.getExpireTime());
        return callback(b);
    }

    /**
     * 锁定
     * @param hc
     * @return
     */
    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    public ResultBean unlock(@RequestBody @Valid SystemUnlockForm hc) {
        Boolean b = systemStatusHandler.unlock(hc.getUnlockCode());
        return callback(b);
    }

}
