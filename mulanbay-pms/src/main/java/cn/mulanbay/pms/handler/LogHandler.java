package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.handler.bean.data.UserOpBean;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.FunctionType;
import cn.mulanbay.pms.util.ClazzUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static cn.mulanbay.pms.common.Constant.ADMIN_USER_ID;

/**
 * 日志处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class LogHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogHandler.class);

    @Value("${mulanbay.log.operLog}")
    boolean enableOperLog;

    @Value("${mulanbay.log.sysLog}")
    boolean enableSysLog;

    /**
     * 修改类型是否同步
     */
    @Value("${mulanbay.cache.bean.syncEdit:false}")
    boolean syncEdit;

    /**
     * 删除类型是否同步
     */
    @Value("${mulanbay.cache.bean.syncDelete:false}")
    boolean syncDelete;


    @Autowired
    ThreadPoolHandler threadPoolHandler;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @Autowired
    RewardHandler rewardHandler;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    CommonCacheHandler commonCacheHandler;

    @Autowired
    SysCodeHandler sysCodeHandler;

    /**
     * 解决依赖循环
     */
    @Lazy
    @Autowired
    NotifyHandler notifyHandler;

    @Autowired
    UserHandler userHandler;

    @Autowired
    BaseService baseService;

    public LogHandler() {
        super("日志处理");
    }

    @Override
    public void init() {

    }

    /**
     * 操作日志
     *
     * @param log
     */
    public void addOperLog(OperLog log) {
        if(!enableOperLog){
            return;
        }
        this.handleOperLog(log);
    }

    /**
     * 操作日志
     * @param log
     */
    private void handleOperLog(OperLog log){
        threadPoolHandler.pushThread(() -> {
            try {
                SysFunc sf = log.getSysFunc();
                int code = 0;
                String msgContent = "";
                if (StringUtil.isNotEmpty(log.getUrlAddress())) {
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
                baseService.saveObject(log);
                this.handleReward(sf, log);
                this.notifyError(log.getUserId(), code, msgContent);
                //同步缓存
                this.syncCache(sf,log.getIdValue());
            } catch (Exception e) {
                logger.error("增加操作日志异常", e);
            }
        });
    }

    /**
     * 积分奖励
     *
     * @param sf
     * @param log
     */
    private void handleReward(SysFunc sf, OperLog log) {
        try {
            if (sf != null && sf.getRewardPoint() != 0 && log.getUserId() > ADMIN_USER_ID) {
                //积分奖励(操作类的积分记录管理的messageId为操作记录的编号)
                rewardHandler.reward(log.getUserId(), sf.getRewardPoint(), log.getId(),
                        BussSource.OPERATION, "功能操作奖励", log.getId());
                //连续操作奖励
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

    /**
     * 同步缓存
     *
     * @param sf
     * @param id
     */
    private void syncCache(SysFunc sf,String id){
        try {
            if(sf==null){
                return;
            }
            if(StringUtil.isEmpty(id)){
                return;
            }
            if(syncEdit&&sf.getFuncType() == FunctionType.EDIT){
                Class clz = ClazzUtils.getClass(sf.getBeanName());
                commonCacheHandler.removeBean(clz,id);
            }else if(syncDelete&&sf.getFuncType() == FunctionType.DELETE){
                String[] ids = id.split(",");
                for(String s:ids){
                    Class clz = ClazzUtils.getClass(sf.getBeanName());
                    commonCacheHandler.removeBean(clz,s);
                }
            }
        } catch (Exception e) {
            logger.error("同步缓存异常",e);
        }
    }

    /**
     * 系统日志
     *
     * @param log
     */
    public void addSysLog(SysLog log) {
        if(!enableSysLog){
            return;
        }
        if (log.getOccurTime() == null) {
            log.setOccurTime(new Date());
        }
        this.handleSysLog(log);
    }

    /**
     * 系统日志
     *
     * @param title
     * @param content
     * @param errorCode
     */
    public void addSysLog(String title, String content, int errorCode) {
        if(!enableSysLog){
            return;
        }
        SysLog log = new SysLog();
        log.setUserId(0L);
        log.setUsername("系统操作");
        log.setTitle(title);
        log.setContent(content);
        log.setErrorCode(errorCode);
        if (log.getOccurTime() == null) {
            log.setOccurTime(new Date());
        }
        this.handleSysLog(log);
    }

    /**
     * 系统日志
     * @param log
     */
    private void handleSysLog(SysLog log){
        threadPoolHandler.pushThread(() -> {
            try {
                SysCode ec = sysCodeHandler.getSysCode(log.getErrorCode());
                if (ec != null && ec.getLoggable()) {
                    SysFunc sf = log.getSysFunc();
                    if (sf != null) {
                        log.setSysFunc(sf);
                        log.setIdValue(this.getParaIdValue(sf, log.getParaMap()));
                    }
                    log.setLogLevel(ec.getLevel());
                    Date now = new Date();
                    log.setStoreTime(now);
                    //会比较慢
                    log.setHostIpAddress(systemConfigHandler.getHostIpAddress());
                    log.setCreatedTime(now);
                    Map map = log.getParaMap();
                    if (map != null && !map.isEmpty()) {
                        //序列化比较耗时间
                        log.setParas(JsonUtil.beanToJson(map));
                    }
                    log.setStoreDuration(log.getStoreTime().getTime() - log.getOccurTime().getTime());
                    String content = log.getContent();
                    if (content.length() >= 2000) {
                        content = content.substring(0, 2000);
                        log.setContent(content);
                    }
                    baseService.saveObject(log);
                    this.notifyError(log.getUserId(), ec, log.getContent());
                }else{
                    logger.warn("系统代码{}找不到配置",log.getErrorCode());
                }
            } catch (Exception e) {
                String msg = "增加系统日志异常，log=" + log.getContent();
                logger.error(msg, e);
            }
        });
    }

    /**
     * 获取参数ID值
     *
     * @param sf
     * @param paraMap
     * @return
     */
    protected String getParaIdValue(SysFunc sf, Map paraMap) {
        if (!StringUtil.isEmpty(sf.getIdField())) {
            if (paraMap != null && !paraMap.isEmpty()) {
                //设置key的值，方便后期查找比对使用,目前只对修改类有效
                Object oo = paraMap.get(sf.getIdField());
                if (oo != null) {
                    return oo.toString();
                }
            }
        }
        return null;
    }

    /**
     * 消息提醒
     * @param userId
     * @param code
     * @param message
     */
    protected void notifyError(Long userId,  int code, String message) {
        if (code == ErrorCode.SUCCESS) {
            //正常的代码不做处理
            return;
        }
        SysCode ec = sysCodeHandler.getSysCode(code);
        if(ec==null){
            logger.warn("系统代码{}未配置",code);
            return;
        }
        this.notifyError(userId,ec,message);
    }

    /**
     * 消息提醒
     * @param userId
     * @param ec
     * @param message
     */
    protected void notifyError(Long userId, SysCode ec, String message) {
        try {
            if (ec.getCode() == ErrorCode.SUCCESS) {
                return;
            }

            //通知
            notifyHandler.addMessageToNotifier(ec.getCode(), "系统代码["+ec.getName()+"]通知", message + "," + getUserInfo(userId),
                    null, null);
        } catch (Exception e) {
            logger.error("处理系统代码通知异常", e);
        }
    }

    private String getUserInfo(Long userId) {
        if (userId == null) {
            return "";
        } else {
            String s = "操作人UserId:" + userId;
            User user = userHandler.getUser(userId);
            if (user != null) {
                s += ",手机号:" + user.getPhone();
            }
            return s;
        }
    }

}
