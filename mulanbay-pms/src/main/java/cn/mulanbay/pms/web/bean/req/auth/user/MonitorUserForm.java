package cn.mulanbay.pms.web.bean.req.auth.user;

public class MonitorUserForm {

    private Long userId;

    private String bussTypes;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBussTypes() {
        return bussTypes;
    }

    public void setBussTypes(String bussTypes) {
        this.bussTypes = bussTypes;
    }
}
