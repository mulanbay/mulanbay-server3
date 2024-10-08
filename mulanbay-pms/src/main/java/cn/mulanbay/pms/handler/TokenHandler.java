package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.DigestUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.persistent.domain.FamilyUser;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.domain.UserRole;
import cn.mulanbay.pms.persistent.enums.FamilyMode;
import cn.mulanbay.pms.persistent.enums.FamilyUserStatus;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.FamilyService;
import cn.mulanbay.pms.web.bean.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * token处理
 * @link <a href="https://blog.csdn.net/qq_50969362/article/details/134100542">参考</a>
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class TokenHandler extends BaseHandler {

    // 令牌取值位置
    @Value("${mulanbay.security.token.way}")
    private String tokenWay;

    // 令牌自定义标识
    @Value("${mulanbay.security.token.header}")
    private String header;

    // 令牌秘钥
    @Value("${mulanbay.security.token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${mulanbay.security.token.expireTime}")
    private int expireTime;

    // 令牌有效期（默认20分钟）
    @Value("${mulanbay.security.token.verifyTime}")
    private int verifyTime;

    @Value("${mulanbay.security.login.persist}")
    boolean loginPersist;

    @Value("${mulanbay.security.password.salt}")
    String md5Salt;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    AuthService authService;

    @Autowired
    FamilyService familyService;

    public TokenHandler() {
        super("token处理");
    }

    /**
     * 计算密码值
     *
     * @param originalPwd
     * @return
     */
    public String encodePassword(String originalPwd) {
        if (md5Salt == null) {
            return DigestUtil.md5(originalPwd);
        } else {
            return DigestUtil.md5(originalPwd + md5Salt);
        }
    }

    /**
     * 获取用户身份信息
     *
     * @param request
     * @return
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        return this.getLoginUser(request, true);
    }

    /**
     * 获取用户身份信息
     *
     * @param request
     * @param autoLoad 是否自动从数据库加载
     * @return 存在jwt中的uuid值
     */
    public LoginUser getLoginUser(HttpServletRequest request, boolean autoLoad) {
        String loginUserKey = this.getLoginUserKey(request);
        if (StringUtil.isNotEmpty(loginUserKey)) {
            String userKey = getTokenKey(loginUserKey);
            LoginUser user = cacheHandler.get(userKey, LoginUser.class);
            if (user == null && loginPersist && autoLoad) {
                //从数据库加载
                user = this.loadLoginUserFromDb(loginUserKey);
            }
            return user;
        }
        return null;
    }

    /**
     * 获取token中的uuid
     *
     * @param request
     * @return
     */
    private String getLoginUserKey(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            Claims claims = parseToken(token);
            // 解析对应的权限以及用户信息
            String uuid = (String) claims.get(CacheKey.LOGIN_USER_KEY);
            return uuid;
        }
        return null;
    }

    /**
     * 从数据库加载信息
     *
     * @param loginToken
     * @return
     */
    private LoginUser loadLoginUserFromDb(String loginToken) {
        User user = this.getUserByLastToken(loginToken);
        if (user == null) {
            return null;
        } else {
            return createLoginUser(user);
        }
    }

    /**
     * 生成LoginUser
     *
     * @param user
     * @return
     */
    public LoginUser createLoginUser(User user) {
        LoginUser lu = new LoginUser();
        if (FamilyMode.F == user.getLastFamilyMode()) {
            FamilyUser fu = familyService.getAdminFamilyUser(user.getUserId());
            if (fu == null) {
                throw new ApplicationException(PmsCode.USER_LOGIN_FAMILY_FAIL);
            } else {
                //设置组成员
                lu.setFamilyMode(FamilyMode.F);
                List<Long> userIdList = new ArrayList<>();
                List<FamilyUser> fuList = familyService.getFamilyUserList(fu.getFamilyId(), FamilyUserStatus.PASSED);
                for (FamilyUser f : fuList) {
                    userIdList.add(f.getUserId());
                }
                lu.setUserIdList(userIdList);
            }
        }
        lu.setUser(user);
        List<UserRole> userRoleList = authService.selectUserRoleList(user.getUserId());
        if (StringUtil.isNotEmpty(userRoleList)) {
            //只取第一个
            UserRole role = userRoleList.get(0);
            lu.setRoleId(role.getRoleId());
        }
        return lu;
    }

    /**
     * 从数据库获取用户信息
     *
     * @param loginToken
     * @return
     */
    private User getUserByLastToken(String loginToken) {
        return authService.getUserByLastLoginToken(loginToken);
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (loginUser != null && StringUtils.isNotEmpty(loginUser.getLoginToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void deleteLoginUser(HttpServletRequest request) {
        String loginUserKey = this.getLoginUserKey(request);
        if (StringUtils.isNotEmpty(loginUserKey)) {
            String userKey = getTokenKey(loginUserKey);
            cacheHandler.delete(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String token = StringUtil.genUUID();
        loginUser.setLoginToken(token);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CacheKey.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= verifyTime * 1000) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    private void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * 1000);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getLoginToken());
        cacheHandler.set(userKey, loginUser, expireTime);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        SecretKey keys = Keys.hmacShaKeyFor(secret.getBytes());
        JwtBuilder jwtBuilder =  Jwts.builder();
        jwtBuilder.header().add("alg","HS256");
        for(String cc : claims.keySet()){
            jwtBuilder.claim(cc,claims.get(cc));
        }
        String token = jwtBuilder.signWith(keys,Jwts.SIG.HS256).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        SecretKey keys = Keys.hmacShaKeyFor(secret.getBytes());
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(keys)
                .build()
                .parseSignedClaims(token);
        return claimsJws.getPayload();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        if ("header".equals(tokenWay)) {
            return this.getTokenFromHeader(request);
        } else {
            return this.getTokenFromCookie(request);
        }
    }

    /**
     * 从header里面获取
     *
     * @param request
     * @return
     */
    private String getTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(CacheKey.TOKEN_PREFIX)) {
            token = token.replace(CacheKey.TOKEN_PREFIX, "");
        }
        return token;
    }

    /**
     * 从cookie里面获取
     *
     * @param request
     * @return
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CacheKey.TOKEN_KEY)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getTokenKey(String uuid) {
        return CacheKey.getKey(CacheKey.LOGIN_TOKEN_KEY,uuid) ;
    }
}
