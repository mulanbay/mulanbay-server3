package cn.mulanbay.pms.handler.bean.data;

import java.util.Date;

/**
 * @author fenghong
 * @date 2024/3/4
 */
public class CommonDataBean {

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
    private Date BussDay;

    private Date createdTime;

    private Date modifyTime;

    /**
     * 原始数据
     */
    private Object originData;

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
        return BussDay;
    }

    public void setBussDay(Date bussDay) {
        BussDay = bussDay;
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
