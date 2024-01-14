package cn.mulanbay.pms.util;

/**
 * http请求返回类
 * @author fenghong
 * @create 2017-10-11 22:45
 **/
public class HttpResult {

    private int statusCode = 200;

    //返回数据
    private String body;

    //错误信息
    private String errorInfo;

    //异常情况下，类名
    private Class exceptionClass;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Class getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(Class exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionClassName(){
        if(exceptionClass==null){
            return null;
        }else {
            return exceptionClass.getName();
        }
    }
}
