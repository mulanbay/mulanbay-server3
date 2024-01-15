package cn.mulanbay.pms.web.bean.res.system.handler;

public class HandlerVo {

    private Long id;

    private String handlerName;

    private String className;

    private int hash;

    private Boolean checkResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public Boolean getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(Boolean checkResult) {
        this.checkResult = checkResult;
    }
}
