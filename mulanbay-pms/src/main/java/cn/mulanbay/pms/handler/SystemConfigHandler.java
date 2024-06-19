package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.business.handler.HandlerInfo;
import cn.mulanbay.business.handler.HandlerMethod;
import cn.mulanbay.common.util.IPAddressUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.persistent.domain.RoleFunction;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class SystemConfigHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigHandler.class);

    private String hostIpAddress;

    @Value("${mulanbay.nodeId}")
    private String nodeId;

    @Value("${mulanbay.picture.baseUrl}")
    private String pictureBaseUrl;

    /**
     * 是否采用内存缓存
     */
    @Value("${mulanbay.cache.config.byMemory:false}")
    boolean byMemoryCache = false;

    @Autowired
    BaseService baseService;

    @Autowired
    CacheHandler cacheHandler;

    /**
     * key:urlAddress_supportMethod,例如：/buyRecord/edit_POST
     */
    private static Map<String, SysFunc> functionMap = new HashMap<>();

    /**
     * 角色功能点缓存
     */
    private static Map<String, String> roleFunctionMap = new HashMap<>();

    public SystemConfigHandler() {
        super("系统配置");
    }

    /**
     * 重载功能点
     */
    @HandlerMethod(desc = "重载功能点")
    public void reloadFunctions() {
        //获取所有的功能点
        List<SysFunc> list = baseService.getBeanList(SysFunc.class, 0, 0, null);
        functionMap.clear();
        int urlMapSize = 0;
        //封装
        for (SysFunc sf : list) {
            if (sf.getUrlAddress() == null || sf.getSupportMethods() == null) {
                continue;
            } else {
                String methods = sf.getSupportMethods();
                String[] ss = methods.split(",");
                for (String s : ss) {
                    // 数据库中功能点路径不需要设置项目名，因为项目名称在实际过程中会被修改过
                    String hashKey = getUrlMethodKey(sf.getUrlAddress(), s);
                    functionMap.put(hashKey, sf);
                    urlMapSize++;
                }
            }
        }
        if (!byMemoryCache) {
            cacheHandler.delete(CacheKey.SYS_FUNC);
            cacheHandler.setHash(CacheKey.SYS_FUNC, functionMap, 0);
        }
        logger.debug("初始化了" + urlMapSize + "条功能点记录");
    }

    public void reloadFunction(Long funcId){
        SysFunc func = baseService.getObject(SysFunc.class,funcId);
        if(func==null){
            logger.warn("找不到编号为{}的系统功能点",funcId);
        }else{
            String methods = func.getSupportMethods();
            String[] ss = methods.split(",");
            for (String s : ss) {
                String hashKey = getUrlMethodKey(func.getUrlAddress(), s);
                if (!byMemoryCache) {
                    cacheHandler.setHash(CacheKey.SYS_FUNC, hashKey, func);
                }else{
                    functionMap.put(hashKey, func);
                }
            }
            logger.info("刷新了编号为{}的系统功能点缓存数据",funcId);
        }
    }

    /**
     * 重载角色功能点
     */
    @HandlerMethod(desc = "重载角色功能点")
    public void reloadRoleFunctions() {
        //获取所有的功能点
        List<RoleFunction> list = baseService.getBeanList(RoleFunction.class, 0, 0, null);
        roleFunctionMap.clear();
        //封装
        for (RoleFunction rf : list) {
            String rfKey = generateRoleFunctionKey(rf.getRoleId(), rf.getFunctionId());
            roleFunctionMap.put(rfKey, "1");
        }
        if (!byMemoryCache) {
            cacheHandler.delete(CacheKey.ROLE_FUNC);
            cacheHandler.setHash(CacheKey.ROLE_FUNC, roleFunctionMap, 0);
        }
        logger.debug("初始化了" + list.size() + "条角色功能点记录");
    }

    @Override
    public void init() {
        super.init();
        //初始化
        hostIpAddress = IPAddressUtil.getLocalIpAddress();
        reloadFunctions();
        reloadRoleFunctions();
    }

    @Override
    public void reload() {
        super.reload();
        reloadFunctions();
        reloadRoleFunctions();
    }

    /**
     * 通过URL查询
     *
     * @param url
     * @param method
     * @return
     */
    public SysFunc getFunction(String url, String method) {
        String hashKey = getUrlMethodKey(url, method);
        if (byMemoryCache) {
            return functionMap.get(hashKey);
        } else {
            return cacheHandler.getHash(CacheKey.SYS_FUNC, hashKey, SysFunc.class);
        }
    }

    /**
     * 获取key
     *
     * @param url
     * @param method
     * @return
     */
    private String getUrlMethodKey(String url, String method) {
        return url + "_" + method.toUpperCase();
    }

    /**
     * 角色是否授权
     *
     * @param functionId
     * @param roleId
     * @return
     */
    public boolean isRoleAuth(Long roleId, Long functionId) {
        String rfKey = generateRoleFunctionKey(roleId, functionId);
        String s = null;
        if (byMemoryCache) {
            s = roleFunctionMap.get(rfKey);
        } else {
            s = cacheHandler.getHash(CacheKey.ROLE_FUNC, rfKey, String.class);
        }
        boolean b = (s == null ? false : true);
        logger.debug("角色是否授权,key:" + rfKey + ",auth:" + b);
        return b;
    }

    private String generateRoleFunctionKey(Long roleId, Long functionId) {
        return roleId.toString() + "_" + functionId.toString();
    }

    public String getHostIpAddress() {
        return hostIpAddress;
    }

    public String getNodeId() {
        return nodeId;
    }

    public BaseService getBaseService() {
        return baseService;
    }

    /**
     * 图片的全路径
     * @param url
     * @return
     */
    public String getPictureFullUrl(String url){
        if(StringUtil.isEmpty(url)){
            return null;
        }else{
            return pictureBaseUrl+url;
        }
    }

    @Override
    public HandlerInfo getHandlerInfo() {
        HandlerInfo hi = super.getHandlerInfo();
        hi.addDetail("nodeId", nodeId);
        hi.addDetail("serverIp", hostIpAddress);
        hi.addDetail("byMemoryCache", String.valueOf(byMemoryCache));
        hi.addDetail("functionMap size", String.valueOf(functionMap.size()));
        hi.addDetail("roleFunctionMap size", String.valueOf(roleFunctionMap.size()));
        return hi;
    }

}
