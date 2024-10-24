package cn.mulanbay.pms.handler.bean.data;

import cn.mulanbay.pms.persistent.enums.BussSource;

import java.util.Date;

/**
 * @author fenghong
 * @date 2024/3/4
 */
public class CommonDataBean {

    /**
     * 业务类型
     */
    private BussSource source;

    /**
     * 源id
     */
    private Long sourceId;

    /**
     * 业务名称
     */
    private String bussName;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;
    /**
     * 业务时间
     */
    private Date bussDay;

    private Date createdTime;

    private Date modifyTime;

    /**
     * 原始数据
     */
    private Object originData;

    public CommonDataBean() {
    }

    public CommonDataBean(BussSource source, Long sourceId) {
        this.source = source;
        this.sourceId = sourceId;
    }

    public BussSource getSource() {
        return source;
    }

    public void setSource(BussSource source) {
        this.source = source;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getBussName() {
        return bussName;
    }

    public void setBussName(String bussName) {
        this.bussName = bussName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getBussDay() {
        return bussDay;
    }

    public void setBussDay(Date bussDay) {
        bussDay = bussDay;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Object getOriginData() {
        return originData;
    }

    public void setOriginData(Object originData) {
        this.originData = originData;
    }
}
