package cn.mulanbay.pms.web.bean.res.auth.sysFunc;

import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.web.bean.res.NameValueVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fenghong
 * @date 2024/6/25
 */
public class SysFuncCacheInfoVo {

    /**
     * 数据库中信息
     */
    private SysFunc dbData;

    /**
     * 缓存中信息
     */
    private SysFunc cacheData;

    /**
     * 用户限流缓存
     */
    private List<NameValueVo> userLimitList = new ArrayList<>();

    /**
     * 系统限流缓存
     */
    private List<NameValueVo> sysLimitList = new ArrayList<>();


    public void addUserLimit(String name,Object v){
        userLimitList.add(new NameValueVo(name,v));
    }

    public void addSysLimit(String name,Object v){
        sysLimitList.add(new NameValueVo(name,v));
    }

    public SysFunc getDbData() {
        return dbData;
    }

    public void setDbData(SysFunc dbData) {
        this.dbData = dbData;
    }

    public SysFunc getCacheData() {
        return cacheData;
    }

    public void setCacheData(SysFunc cacheData) {
        this.cacheData = cacheData;
    }

    public List<NameValueVo> getUserLimitList() {
        return userLimitList;
    }

    public void setUserLimitList(List<NameValueVo> userLimitList) {
        this.userLimitList = userLimitList;
    }

    public List<NameValueVo> getSysLimitList() {
        return sysLimitList;
    }

    public void setSysLimitList(List<NameValueVo> sysLimitList) {
        this.sysLimitList = sysLimitList;
    }
}
