package cn.mulanbay.common.util;

import cn.hutool.core.net.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * IP地址获取工具类
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class IPAddressUtil {

    private static final Logger logger = LoggerFactory.getLogger(IPAddressUtil.class);

    /**
     * 获取本地的IP地址
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            Set<String> ips = NetUtil.localIpv4s();
            return ips.iterator().next();
        } catch (Exception e) {
            logger.error("获取本地IP地址异常", e);
            return null;
        }
    }

}
