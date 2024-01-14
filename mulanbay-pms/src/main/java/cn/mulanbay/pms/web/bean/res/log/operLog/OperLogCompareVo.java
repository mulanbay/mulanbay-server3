package cn.mulanbay.pms.web.bean.res.log.operLog;

import cn.mulanbay.pms.persistent.domain.OperLog;

/**
 * 消息
 *
 * @author 日志对比
 * @create 2017-07-10 21:44
 */
public class OperLogCompareVo {

    //业务的ID
    private String bussId;

    private String beanName;

    //最新的数据，业务数据表中的实时数据(json格式)（页面左边的数据）
    private Object latestData;

    //当前操作日记所记录的数据(json格式)，来源于paras字段，只针对edit类型有效，因为能找到业务ID(页面中间的数据)
    private OperLog currentData;

    //参与比对的数据，比如比当前操作记录早或者迟的数据(json格式)，来源于paras字段，只针对edit类型有效，因为能找到业务ID（页面右边的数据）
    private OperLog compareData;

    public String getBussId() {
        return bussId;
    }

    public void setBussId(String bussId) {
        this.bussId = bussId;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Object getLatestData() {
        return latestData;
    }

    public void setLatestData(Object latestData) {
        this.latestData = latestData;
    }

    public OperLog getCurrentData() {
        return currentData;
    }

    public void setCurrentData(OperLog currentData) {
        this.currentData = currentData;
    }

    public OperLog getCompareData() {
        return compareData;
    }

    public void setCompareData(OperLog compareData) {
        this.compareData = compareData;
    }
}
