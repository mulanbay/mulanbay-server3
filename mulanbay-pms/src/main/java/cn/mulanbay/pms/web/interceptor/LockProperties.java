package cn.mulanbay.pms.web.interceptor;

import java.util.ArrayList;
import java.util.List;

import cn.mulanbay.common.util.StringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 白名单
 * @author fenghong
 * @date 2024/2/4
 */
@Configuration
@ConfigurationProperties(prefix = "mulanbay.security.lock")
public class LockProperties {

    private List<String> whitList = new ArrayList<>();

    public List<String> getWhitList() {
        return whitList;
    }

    public void setWhitList(List<String> whitList) {
        this.whitList = whitList;
    }

    public boolean auth(String path){
        if(StringUtil.isEmpty(whitList)){
            return false;
        }else{
            for(String s : whitList){
                if(s.equals(path)){
                    return true;
                }
            }
        }
        return false;
    }
}
