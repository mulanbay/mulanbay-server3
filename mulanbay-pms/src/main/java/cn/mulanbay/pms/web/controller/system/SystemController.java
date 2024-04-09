package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.PmsScheduleHandler;
import cn.mulanbay.pms.handler.SystemStatusHandler;
import cn.mulanbay.pms.handler.job.SystemStatusJob;
import cn.mulanbay.pms.handler.job.SystemStatusJobPara;
import cn.mulanbay.pms.persistent.service.PmsScheduleService;
import cn.mulanbay.pms.web.bean.req.system.system.SystemAutoLockForm;
import cn.mulanbay.pms.web.bean.req.system.system.SystemLockForm;
import cn.mulanbay.pms.web.bean.req.system.system.SystemUnlockForm;
import cn.mulanbay.pms.web.bean.res.system.system.SystemAutoLockVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 系统
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/system")
public class SystemController extends BaseController {

    @Autowired
    SystemStatusHandler systemStatusHandler;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    PmsScheduleService pmsScheduleService;

    @Autowired
    PmsScheduleHandler pmsScheduleHandler;

    /**
     * 锁定
     * @param hc
     * @return
     */
    @RequestMapping(value = "/lock", method = RequestMethod.POST)
    public ResultBean lock(@RequestBody @Valid SystemLockForm hc) {
        Boolean b = systemStatusHandler.lock(hc.getStatus(),hc.getMessage(),hc.getExpireTime(),hc.getUnlockCode());
        return callback(b);
    }

    /**
     * 解锁
     * @param hc
     * @return
     */
    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    public ResultBean unlock(@RequestBody @Valid SystemUnlockForm hc) {
        //判定验证码
        String verifyKey = CacheKey.getKey(CacheKey.CAPTCHA_CODE, hc.getUuid());
        String serverCode = cacheHandler.getForString(verifyKey);
        if (StringUtil.isEmpty(serverCode) || !serverCode.equals(hc.getCode())) {
            return callbackErrorCode(PmsCode.USER_VERIFY_CODE_ERROR);
        }
        //人工解锁，状态码最大
        Boolean b = systemStatusHandler.unlock(hc.getUnlockCode(),hc.getStatus());
        return callback(b);
    }

    /**
     * 自动锁定配置
     * @return
     */
    @RequestMapping(value = "/getAutoLock", method = RequestMethod.GET)
    public ResultBean getAutoLock() {
        SystemAutoLockVo vo = new SystemAutoLockVo();
        TaskTrigger trigger = pmsScheduleService.getTaskTrigger(SystemStatusJob.class.getName());
        if(trigger!=null){
            vo.setTrigger(trigger);
            SystemStatusJobPara jobPara = (SystemStatusJobPara) JsonUtil.jsonToBean(trigger.getTriggerParas(),SystemStatusJobPara.class);
            String stopPeriod = jobPara.getStopPeriod();
            vo.setStopPeriod(stopPeriod);
            vo.setStopStatus(jobPara.getStopStatus());
            vo.setMessage(jobPara.getMessage());
        }
        return callback(vo);
    }

    /**
     * 修改自动锁定配置
     * @param hc
     * @return
     */
    @RequestMapping(value = "/editAutoLock", method = RequestMethod.POST)
    public ResultBean editAutoLock(@RequestBody @Valid SystemAutoLockForm hc) {
        TaskTrigger trigger = pmsScheduleService.getTaskTrigger(SystemStatusJob.class.getName());
        if(trigger!=null){
            SystemStatusJobPara jobPara = (SystemStatusJobPara) JsonUtil.jsonToBean(trigger.getTriggerParas(),SystemStatusJobPara.class);
            if(jobPara==null){
                jobPara = new SystemStatusJobPara();
            }
            jobPara.setStopPeriod(hc.getStopPeriod());
            jobPara.setStopStatus(hc.getStopStatus());
            jobPara.setMessage(hc.getMessage());
            trigger.setTriggerParas(JsonUtil.beanToJson(jobPara));
            trigger.setTriggerStatus(hc.getTriggerStatus());
            trigger.setModifyTime(new Date());
            baseService.updateObject(trigger);
            pmsScheduleHandler.refreshTask(trigger);
        }
        return callback(null);
    }
}
