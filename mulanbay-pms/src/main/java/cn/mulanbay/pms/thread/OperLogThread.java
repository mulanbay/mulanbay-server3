package cn.mulanbay.pms.thread;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.util.BeanFactoryUtil;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.handler.RewardHandler;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.handler.bean.data.UserOpBean;
import cn.mulanbay.pms.persistent.domain.OperLog;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.enums.BussSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * 操作记录记录线程
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
public class OperLogThread extends BaseLogThread {

    private static final Logger logger = LoggerFactory.getLogger(OperLogThread.class);

    private OperLog log;

    public OperLogThread(OperLog log) {
        super("操作日志");
        this.log = log;
    }

    @Override
    public void run() {
        handleLog(log);
    }

    /**
     * 增加操作日志
     *
     * @param log
     */
    private void handleLog(OperLog log) {
        try {
            SystemConfigHandler systemConfigHandler = BeanFactoryUtil.getBean(SystemConfigHandler.class);
            SysFunc sf = log.getSysFunc();
            int code = 0;
            String msgContent = "";
            if (log.getUrlAddress() != null) {
                msgContent = log.getUrlAddress();
            }
            if (sf == null) {
                logger.warn("找不到请求地址[" + log.getUrlAddress() + "],method[" + log.getMethod() + "]功能点配置信息");
            } else {
                code = sf.getCode();
                msgContent += "(" + sf.getFuncName() + ")";
                log.setSysFunc(sf);
                if (StringUtil.isNotEmpty(sf.getIdField())&&StringUtil.isEmpty(log.getIdValue())) {
                    Map<String, String> paraMap = (Map<String, String>) JsonUtil.jsonToBean(log.getParas(), Map.class);
                    log.setIdValue(this.getParaIdValue(sf, paraMap));
                }
            }
            Date now = new Date();
            log.setStoreTime(now);
            //会比较慢
            log.setHostIpAddress(systemConfigHandler.getHostIpAddress());
            log.setCreatedTime(now);
            //序列化比较耗时间
            //log.setParas(JsonUtil.beanToJson(changeToNormalMap(log.getParaMap())));
            log.setHandleDuration(log.getOccurEndTime().getTime() - log.getOccurStartTime().getTime());
            log.setStoreDuration(log.getStoreTime().getTime() - log.getOccurEndTime().getTime());
            if (log.getUserId() == null) {
                log.setUserId(0L);
                log.setUsername("未知");
            }
            BaseService baseService = BeanFactoryUtil.getBean(BaseService.class);
            baseService.saveObject(log);
            this.handleReward(sf, log);
            this.notifyError(log.getUserId(), code, msgContent);
        } catch (Exception e) {
            logger.error("增加操作日志异常", e);
        }
    }

    /**
     * 积分奖励
     *
     * @param sf
     * @param log
     */
    private void handleReward(SysFunc sf, OperLog log) {
        try {
            if (sf != null && sf.getRewardPoint() != 0 && log.getUserId() > 0) {
                //积分奖励(操作类的积分记录管理的messageId为操作记录的编号)
                RewardHandler rewardHandler = BeanFactoryUtil.getBean(RewardHandler.class);
                rewardHandler.reward(log.getUserId(), sf.getRewardPoint(), log.getId(),
                        BussSource.OPERATION, "功能操作奖励", log.getId());
                //连续操作奖励
                CacheHandler cacheHandler = BeanFactoryUtil.getBean(CacheHandler.class);
                String key = CacheKey.getKey(CacheKey.USER_CONTINUE_OP, log.getUserId().toString(), sf.getFuncId().toString());
                String dayString = DateUtil.getFormatDate(log.getOccurStartTime(), "yyyyMMdd");
                int day = Integer.parseInt(dayString);
                UserOpBean uco = cacheHandler.get(key, UserOpBean.class);
                //缓存失效时间
                Date now = new Date();
                Date end = DateUtil.tillMiddleNight(now);
                long leftExpired = end.getTime() - now.getTime();
                if (uco == null) {
                    uco = new UserOpBean();
                    uco.setFistDay(day);
                    uco.setLastDay(day);
                    uco.setDays(1);
                    cacheHandler.set(key, uco, (int) (leftExpired / 1000));
                } else {
                    if (day <= uco.getLastDay()) {
                        //一样，不操作
                        return;
                    } else {
                        uco.setLastDay(day);
                        uco.addDay();
                        cacheHandler.set(key, uco, (int) (leftExpired / 1000));
                        if (uco.getDays() >= 3) {
                            //奖励连续操作(相当于3天以上则双倍奖励，负分的也是一样)
                            int rewards = sf.getRewardPoint() * uco.getDays();
                            rewardHandler.reward(log.getUserId(), rewards, log.getId(),
                                    BussSource.OPERATION, "功能操作连续" + uco.getDays() + "天奖励", log.getId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("操作日志积分奖励处理异常", e);
        }

    }

}
