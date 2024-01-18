package cn.mulanbay.pms.web.filter;

import cn.mulanbay.web.filter.MultipleRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 * @author fenghong
 * @create 2017-09-28 21:38
 **/
@Component
@WebFilter(urlPatterns = "/*",filterName = "channelFilter")
public class ChannelFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ChannelFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("in filter");
        ServletRequest requestWrapper = null;
        if(servletRequest instanceof HttpServletRequest) {
            HttpServletRequest hsr = (HttpServletRequest) servletRequest;
            String method = hsr.getMethod();
            String contentType = hsr.getContentType();
            if(method.equalsIgnoreCase("GET")||(contentType!=null&&contentType.startsWith("multipart/form-data"))){
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            requestWrapper = new MultipleRequestWrapper(hsr);
        }
        if(requestWrapper == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(requestWrapper, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
