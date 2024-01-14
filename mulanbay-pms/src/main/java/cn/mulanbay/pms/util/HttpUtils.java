package cn.mulanbay.pms.util;

import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

/**
 * http请求返回类
 * @author fenghong
 * @create 2017-10-11 22:45
 **/
public class HttpUtils {

    /**
     * post请求
     * @param url
     * @param data
     * @return
     */
    public static HttpResult doPost(String url,String data){
        HttpResponse hr = HttpRequest.post(url).timeout(HttpGlobalConfig.getTimeout()).body(data).execute();
        HttpResult res = new HttpResult();
        res.setStatusCode(hr.getStatus());
        res.setBody(hr.body());
        return res;
    }

    /**
     * get请求
     * @param url
     * @return
     */
    public static HttpResult doGet(String url){
        HttpResponse hr = HttpRequest.get(url).timeout(HttpGlobalConfig.getTimeout()).execute();
        HttpResult res = new HttpResult();
        res.setStatusCode(hr.getStatus());
        res.setBody(hr.body());
        return res;
    }
}
