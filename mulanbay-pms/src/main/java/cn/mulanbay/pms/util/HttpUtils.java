package cn.mulanbay.pms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * http请求返回类
 *
 * @author fenghong
 * @create 2017-10-11 22:45
 **/
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * post请求
     *
     * @param url
     * @param data
     * @return
     */
    public static HttpResult doPost(String url, String data) {
        HttpResult res = new HttpResult();
        try {
            // 创建默认的HTTP客户端对象
            HttpClient client = HttpClient.newHttpClient();
            // 创建一个自定义的HTTP请求对象
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)) // 待调用的url地址
                    .POST(HttpRequest.BodyPublishers.ofString(data)) // 调用方式为POST，且请求报文为字符串
                    .header("Content-Type", "application/json") // 设置头部参数，内容类型为json
                    .build();
            // 客户端传递请求信息，且返回字符串形式的应答报文
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 获取HTTP调用的应答状态码和应答报文
            res.setStatusCode(response.statusCode());
            res.setBody(response.body());
            logger.debug("http post请求,url={},返回StatusCode={}", url, res.getStatusCode());
        } catch (Exception e) {
            logger.error("调用Http post 异常,url=" + url, e);
            res.setStatusCode(500);
            res.setErrorInfo(e.getMessage());
        }
        return res;
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static HttpResult doGet(String url) {
        HttpResult res = new HttpResult();
        try {
            // 创建默认的HTTP客户端对象
            HttpClient client = HttpClient.newHttpClient();
            // 创建默认的HTTP请求对象（默认GET调用）
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
            // 客户端传递请求信息，且返回字符串形式的应答报文
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 获取HTTP调用的应答状态码和应答报文
            res.setStatusCode(response.statusCode());
            res.setBody(response.body());
            logger.debug("http get请求,url={},返回StatusCode={}", url, res.getStatusCode());
        } catch (Exception e) {
            logger.error("调用Http get 异常,url=" + url, e);
            res.setStatusCode(500);
            res.setErrorInfo(e.getMessage());
        }
        return res;
    }
}
