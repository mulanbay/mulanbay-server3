package cn.mulanbay.pms.web.bean.res.log.sysCode;

import cn.mulanbay.pms.web.bean.res.NameValueVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fenghong
 * @date 2024/6/25
 */
public class SysCodeCacheInfoVo {

    /**
     * 缓存区数量
     */
    private Long batchCounts;

    /**
     * 系统限流数量
     */
    private Integer limitCounts;

    /**
     * 用户限流缓存
     */
    private List<NameValueVo> userLimitList = new ArrayList<>();

    public void addUserLimit(String name,Object v){
        userLimitList.add(new NameValueVo(name,v));
    }

    public Long getBatchCounts() {
        return batchCounts;
    }

    public void setBatchCounts(Long batchCounts) {
        this.batchCounts = batchCounts;
    }

    public Integer getLimitCounts() {
        return limitCounts;
    }

    public void setLimitCounts(Integer limitCounts) {
        this.limitCounts = limitCounts;
    }

    public List<NameValueVo> getUserLimitList() {
        return userLimitList;
    }

    public void setUserLimitList(List<NameValueVo> userLimitList) {
        this.userLimitList = userLimitList;
    }
}
