package cn.mulanbay.pms.web.interceptor;

import cn.mulanbay.common.util.StringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单
 * @author fenghong
 * @date 2024/2/4
 */
@Configuration
@ConfigurationProperties(prefix = "mulanbay.security.lock")
public class LockProperties {

    private List<String> whiteList = new ArrayList<>();

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public boolean auth(String path){
        if(StringUtil.isEmpty(whiteList)){
            return false;
        }else{
            for(String s : whiteList){
                if(s.equals(path)){
                    return true;
                }
            }
        }
        return false;
    }
}
