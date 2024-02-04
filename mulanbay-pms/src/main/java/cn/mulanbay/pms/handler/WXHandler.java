package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.*;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.bean.wx.AccessToken;
import cn.mulanbay.pms.handler.bean.wx.JsApiTicket;
import cn.mulanbay.pms.handler.bean.wx.JsApiTicketAuth;
import cn.mulanbay.pms.handler.bean.wx.MessageContent;
import cn.mulanbay.pms.persistent.domain.WxAccount;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.persistent.service.WxAccountService;
import cn.mulanbay.pms.util.HttpResult;
import cn.mulanbay.pms.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 微信相关处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class WXHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(WXHandler.class);

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();// 读写锁

    public WXHandler() {
        super("微信");
    }

    /**
     * 公众号编号
     */
    @Value("${mulanbay.wx.appId}")
    private String appId;

    @Value("${mulanbay.wx.secret}")
    private String secret;

    @Value("${mulanbay.wx.token}")
    private String token;

    /**
     * 消息模版编号
     */
    @Value("${mulanbay.wx.msgTemplateId}")
    private String msgTemplateId;

    /**
     * 公众号地址
     */
    @Value("${mulanbay.wx.oaUrl}")
    private String oaUrl;

    /**
     * 公众号图片地址(移动端使用)
     */
    @Value("${mulanbay.wx.oaPicUrl}")
    private String oaPicUrl;

    @Value("${mulanbay.mobile.baseUrl}")
    private String mobileBaseUrl;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    BaseService baseService;

    @Autowired
    WxAccountService wxAccountService;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    /**
     * 授权回调地址
     */
    @Value("${mulanbay.wx.accessAuthRedirectUrl}")
    String accessAuthRedirectUrl;

    public String getAccessAuthRedirectUrl() {
        return accessAuthRedirectUrl;
    }

    public String getOaUrl() {
        return oaUrl;
    }

    public String getQrUrl() {
        return systemConfigHandler.getPictureFullUrl(oaPicUrl);
    }

    /**
     * 获取微信信息
     *
     * @param userId
     * @return
     */
    public WxAccount geAccount(Long userId) {
        WxAccount uw = wxAccountService.getAccount(userId, appId);
        return uw;
    }

    /**
     *
     * @param msgId 微信不支持过长的消息内容显示，因此公众号消息先跳转到消息详情页
     * @param userId
     * @param title
     * @param content
     * @param time
     * @param level
     * @param url
     * @return
     */
    public boolean sendTemplateMessage(Long msgId,Long userId, String title, String content, Date time, LogLevel level, String url) {
        try {
            WxAccount uw = this.geAccount(userId);
            if (uw == null) {
                logger.warn("无法获取到userId=" + userId + "的用户微信信息");
                return false;
            }
            String color = null;
            if (level == LogLevel.WARNING) {
                color = "#FF6347";
            } else if (level == LogLevel.ERROR) {
                color = "#FF1493";
            } else if (level == LogLevel.FATAL) {
                color = "#9A32CD";
            } else {
                logger.debug("不需要设置模板消息的颜色属性");
            }
            MessageContent mc = new MessageContent();
            mc.setTouser(uw.getOpenId());
            mc.setTemplate_id(msgTemplateId);
            mc.addMessageData("title", title, color);
            mc.addMessageData("content", content);
            if (time != null) {
                mc.addMessageData("time", DateUtil.getFormatDate(time, DateUtil.Format24Datetime));
            }
            String mcUrl =null;
            if(msgId!=null){
                mcUrl = mobileBaseUrl+"/user/message/detail/"+msgId;
            }else{
                mcUrl = this.getFullUrl(url);
            }
            mc.setUrl(mcUrl);
            HttpResult hr = this.sendTemplateMessage(mc);
            //logger.debug("发送模板消息,mc:"+JsonUtil.beanToJson(mc));
            //logger.debug("发送模板消息,hr:"+JsonUtil.beanToJson(hr));
            if (hr.getStatusCode() == Constant.SC_OK) {
                return true;
            } else {
                logger.error("发送模板消息失败，" + hr.getBody());
                return false;
            }
        } catch (Exception e) {
            logger.error("发送微信模板消息异常，",e);
            return false;
        }
    }

    private String getFullUrl(String url) {
        if (StringUtil.isEmpty(url)) {
            url = mobileBaseUrl;
        }
        if (url.startsWith("http")) {
            return url;
        } else {
            return mobileBaseUrl + url;
        }
    }

    /**
     * 发送模板消息
     *
     * @param mc
     * @return
     */
    public HttpResult sendTemplateMessage(MessageContent mc) {
        String accessToken = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
        String jsonData = JsonUtil.beanToJson(mc);
        return HttpUtils.doPost(url,jsonData);
    }

    /**
     * 获取模板列表
     *
     * @param accessToken
     * @return
     */
    public HttpResult getTemplateList(String accessToken) {
        String url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=" + accessToken;
        HttpResult hr = HttpUtils.doGet(url);
        return hr;
    }

    /**
     * 获取授权token
     *
     * @return
     */
    public String getAccessToken() {
        String accessToken = null;
        String cacheKey = CacheKey.getKey(CacheKey.WX_ACCESS_TOKEN,appId);
        try{
            //lock.readLock().lock();
            boolean b = lock.readLock().tryLock(5, TimeUnit.SECONDS);
            if(!b){
                logger.error("无法获取到获取授权token的读锁");
                return null;
            }
            accessToken = cacheHandler.getForString(cacheKey);
            if (accessToken == null) {
                /**
                 * Must release read lock before acquiring write lock
                 * 参考ReentrantReadWriteLock源码，要想拿写锁，必须先要释放读锁
                 */
                lock.readLock().unlock();
                lock.writeLock().lock();
                try {
                    // 获取accessToken
                    String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
                    HttpResult hr = HttpUtils.doGet(url);
                    if (hr.getStatusCode() == Constant.SC_OK) {
                        AccessToken at = (AccessToken) JsonUtil.jsonToBean(hr.getBody(), AccessToken.class);
                        accessToken = at.getAccess_token();
                        cacheHandler.set(cacheKey, accessToken, at.getExpires_in() - 10);
                    } else {
                        logger.warn("无法获取到AccessToken");
                    }
                    lock.readLock().lock();
                } catch (Exception e) {
                    logger.error("从微信获取AccessToken异常",e);
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }catch (Exception e) {
            logger.error("获取AccessToken异常",e);
        } finally {
            lock.readLock().unlock();
        }
        return accessToken;
    }

    /**
     * 根据access_token获取jsapi_ticket
     *
     * @param accessToken
     * @return
     */
    public JsApiTicket getJsapiTicket(String accessToken) {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
        HttpResult hr = HttpUtils.doGet(url);
        if (hr.getStatusCode() == Constant.SC_OK) {
            logger.debug("getJsapiTicket:" + hr.getBody());
            JsApiTicket at = (JsApiTicket) JsonUtil.jsonToBean(hr.getBody(), JsApiTicket.class);
            return at;
        } else {
            throw new ApplicationException(PmsCode.WXPAY_JSAPITOCKEN_ERROR);
        }
    }

    /**
     * 获取授权
     *
     * @param jsapiTicket
     * @param url
     * @return
     */
    public JsApiTicketAuth getJsapiTicketAuth(String jsapiTicket, String url) {
        try {
            String noncestr = RandomStringGenerator.getRandomStringByLength(16);
            long timestamp = System.currentTimeMillis() / 1000;
            String toBeSign = "jsapi_ticket=" + jsapiTicket +
                    "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url;
            logger.debug("JsapiTicketAuth to be sign:" + toBeSign);
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(toBeSign.getBytes("UTF-8"));
            String sign = Md5Util.byteArrayToHexString(crypt.digest());
            logger.debug("JsapiTicketAuth sign:" + sign);
            JsApiTicketAuth jta = new JsApiTicketAuth();
            jta.setNoncestr(noncestr);
            jta.setTimestamp(timestamp);
            jta.setAppId(appId);
            jta.setSign(sign);
            jta.setUrl(url);
            return jta;
        } catch (Exception e) {
            logger.error("getJsapiTicketAuth error", e);
            throw new ApplicationException(PmsCode.WXPAY_JSAPITOCKEN_ERROR);
        }
    }

    /**
     * 获取JsapiTicketAuth
     *
     * @param url
     * @return
     */
    public JsApiTicketAuth getJsapiTicketAuthWithCache(String url) {
        //先取缓存
        String key = CacheKey.getKey(CacheKey.WX_JSAPI_TICKET, appId);
        String jsapiTicket = cacheHandler.getForString(key);
        if (jsapiTicket == null) {
            String accessToken = getAccessToken();
            JsApiTicket jt = this.getJsapiTicket(accessToken);
            jsapiTicket = jt.getTicket();
            if (jt.getExpires_in() <= 0) {
                throw new ApplicationException(PmsCode.WXPAY_JSAPITOCKEN_ERROR);
            }
            cacheHandler.set(key, jt.getTicket(), jt.getExpires_in() - 60);
        }
        JsApiTicketAuth res = this.getJsapiTicketAuth(jsapiTicket, url);
        return res;
    }

    /**
     * 检查微信公众号的token
     *
     * @param signature
     * @param timestrap
     * @param nonce
     * @return
     */
    public boolean checkMpSignature(String signature, String timestrap,
                                    String nonce) {
        String tt = token;
        String[] arr = new String[]{tt, timestrap, nonce};
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            buf.append(arr[i]);
        }
        String temp =
                getSha1(buf.toString());
        return temp.equals(signature);
    }

    public String getSha1(String str) {
        try {
            if (null == str || str.length() == 0) {
                return null;
            }
            char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f'};
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byTemp = md[i];
                buf[k++] = hexDigits[byTemp >>> 4 & 0xf];
                buf[k++] = hexDigits[byTemp & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            logger.error("getSha1 异常", e);
            throw new ApplicationException(PmsCode.WXPAY_TOKEN_SHA_ERROR);
        }
    }

    /**
     * 处理微信公众号来的微信消息
     *
     * @param resultMap
     * @return
     */
    public String handlerWxMessage(Map<String, Object> resultMap) {
        String s = this.createWxMessageResponse(resultMap);
        return s;
    }

    /**
     * 处理微信公众号消息
     * 后期可以换成异步模式
     *
     * @param resultMap
     */
    public String createWxMessageResponse(Map<String, Object> resultMap) {
        String msgType = resultMap.get("MsgType").toString();
        String fromUserName = resultMap.get("FromUserName").toString();
        String toUserName = resultMap.get("ToUserName").toString();
        if ("event".equals(msgType)) {
            String event = resultMap.get("Event").toString();
            if ("unsubscribe".equals(event)) {
                logger.info("取消关注:" + fromUserName);
                updateSubscribe(false, fromUserName);
            } else if ("subscribe".equals(event)) {
                logger.info("增加关注:" + fromUserName);
                updateSubscribe(true, fromUserName);
                Object eventKey = resultMap.get("EventKey");
                if (eventKey != null && StringUtil.isNotEmpty(eventKey.toString())) {
                    //带参数的二维码地址
                    String id = eventKey.toString().replace("qrscene_", "");
                    logger.info("带参数的二维码地址:" + fromUserName);
                } else {
                    logger.info("普通的关注:" + fromUserName);
                }
            } else if ("SCAN".equals(event)) {
                Object eventKey = resultMap.get("EventKey");
                if (eventKey != null) {
                    logger.info("扫码:" + fromUserName);
                }
            }
        } else if ("text".equals(msgType)) {
            String content = resultMap.get("Content").toString();
            logger.info("文本消息:" + fromUserName + ",content:" + content);
            String res = this.handleTextMessage(content, fromUserName);
            String reply = this.createTextWxReply(res, fromUserName, toUserName);
            return reply;
        } else {
            String reply = this.createTextWxReply("未能处理的消息类别:" + msgType, fromUserName, toUserName);
            return reply;
        }
        return null;
    }

    /**
     * todo 处理文本消息
     *
     * @param content
     * @param fromUserName
     * @return
     */
    private String handleTextMessage(String content, String fromUserName) {
        //先判断权限
        WxAccount wx = wxAccountService.getAccount(appId, fromUserName);
        if (wx == null || wx.getUserId() == null) {
            return "你的微信尚未在系统中绑定";
        }
        String sessionId = "WxOpenId_" + fromUserName;
        //QaResult cr = qaHandler.handleMessage(QaMessageSource.WECHAT, content, wx.getUserId(), sessionId);
        return "";
    }

    private void updateSubscribe(boolean subscribe, String openId) {
        WxAccount uw = wxAccountService.getAccount(appId, openId);
        if (uw == null) {
            uw = new WxAccount();
            uw.setAppId(appId);
            uw.setOpenId(openId);
            uw.setRemark("微信消息回调加入");
            uw.setSubscribe(subscribe);
            uw.setSubscribeTime(new Date());
        } else {
            uw.setSubscribe(subscribe);
            uw.setSubscribeTime(new Date());
        }
        wxAccountService.saveOrUpdateAccount(uw);
    }

    private String createTextWxReply(String content, String ToUserName, String FromUserName) {
        if (StringUtil.isEmpty(content)) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        sb.append("<ToUserName>" + "<![CDATA[" + ToUserName + "]]></ToUserName>");
        sb.append("<FromUserName>" + "<![CDATA[" + FromUserName + "]]></FromUserName>");
        long createTime = System.currentTimeMillis() / 1000;
        sb.append("<CreateTime>" + "<![CDATA[" + createTime + "]]></CreateTime>");
        sb.append("<MsgType>" + "<![CDATA[text]]></MsgType>");
        sb.append("<Content>" + "<![CDATA[" + content + "]]></Content>");
        sb.append("</xml>");
        return sb.toString();
    }

    public String getAppId() {
        return appId;
    }

    public String getSecret() {
        return secret;
    }
}
