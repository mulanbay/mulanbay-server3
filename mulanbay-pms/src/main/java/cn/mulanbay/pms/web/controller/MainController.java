package cn.mulanbay.pms.web.controller;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.BudgetHandler;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.TokenHandler;
import cn.mulanbay.pms.handler.bean.fund.BudgetAmountBean;
import cn.mulanbay.pms.handler.bean.fund.FundStatBean;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.domain.SysFunc;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.enums.FamilyMode;
import cn.mulanbay.pms.persistent.enums.FunctionDataType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.enums.UserStatus;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.BudgetService;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import cn.mulanbay.pms.persistent.service.IncomeService;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.util.IPUtil;
import cn.mulanbay.pms.util.bean.PeriodDateBean;
import cn.mulanbay.pms.web.bean.LoginUser;
import cn.mulanbay.pms.web.bean.req.main.LoginForm;
import cn.mulanbay.pms.web.bean.req.main.UserCommonForm;
import cn.mulanbay.pms.web.bean.req.main.UserGeneralStatSH;
import cn.mulanbay.pms.web.bean.res.main.GeneralStatVo;
import cn.mulanbay.pms.web.bean.res.main.MyInfoVo;
import cn.mulanbay.pms.web.bean.res.main.RouterMetaVo;
import cn.mulanbay.pms.web.bean.res.main.RouterVo;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.ROOT_ID;

/**
 * 主功能
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/main")
public class MainController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Value("${mulanbay.version}")
    private String version;

    @Value("${mulanbay.security.login.maxFail}")
    private int loginMaxFail;

    @Value("${mulanbay.security.login.failLockTime}")
    private int failLockTime;

    @Autowired
    AuthService authService;

    @Autowired
    ConsumeService consumeService;

    @Autowired
    IncomeService incomeService;

    @Autowired
    BudgetService budgetService;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    TokenHandler tokenHandler;

    @Autowired
    NotifyHandler notifyHandler;

    @Autowired
    BudgetHandler budgetHandler;
    /**
     * 登陆
     *
     * @return
     */
    @RequestMapping(value = "/loginAuth", method = RequestMethod.POST)
    public ResultBean loginAuth(@RequestBody @Valid LoginForm login) {
        //判定验证码
        String verifyKey = CacheKey.getKey(CacheKey.CAPTCHA_CODE, login.getUuid());
        String serverCode = cacheHandler.getForString(verifyKey);
        if (StringUtil.isEmpty(serverCode) || !serverCode.equals(login.getCode())) {
            return callbackErrorCode(PmsCode.USER_VERIFY_CODE_ERROR);
        }
        //错误次数验证
        String username = login.getUsername();
        String failKey = CacheKey.getKey(CacheKey.USER_LOGIN_FAIL, username);
        Integer fails = cacheHandler.get(failKey, Integer.class);
        if (fails != null && fails >= loginMaxFail) {
            return callbackErrorCode(PmsCode.USER_LOGIN_FAIL_MAX);
        }
        //用户验证
        User user = authService.getUserByUsernameOrPhone(username);
        if (user == null) {
            return callbackErrorCode(PmsCode.USER_NOT_FOUND);
        } else {
            if (user.getStatus() == UserStatus.DISABLE) {
                return callbackErrorCode(PmsCode.USER_DISABLE);
            }
            if (user.getExpireTime() != null && user.getExpireTime().before(new Date())) {
                return callbackErrorCode(PmsCode.USER_EXPIRED);
            }
            // 检测密码
            String rp = user.getPassword();
            String encodePassword = tokenHandler.encodePassword(login.getPassword());
            if (!rp.equalsIgnoreCase(encodePassword)) {
                if (fails == null) {
                    fails = 1;
                } else {
                    fails++;
                }
                cacheHandler.set(failKey, fails, failLockTime);
                return callbackErrorCode(PmsCode.USER_PASSWORD_ERROR);
            }
            String token = doLogin(user, login.getFamilyMode());
            addLoginNotifyMsg(user.getUserId(), user.getUsername());
            Map map = new HashMap<>();
            map.put("token", token);
            return callback(map);
        }
    }

    private void addLoginNotifyMsg(Long userId, String username) {
        try {
            // 发送系统通知
            notifyHandler.addMessageToNotifier(PmsCode.USER_LOGIN, "用户登录系统", "用户[" + username + "]登录系统", new Date(), null);
            notifyHandler.addMessage(PmsCode.USER_LOGIN, "您的账户正在登录系统", "您的账户[" + username + "]登录系统", userId, new Date());
        } catch (Exception e) {
            logger.error("增加登录提醒日志异常", e);
        }

    }

    /**
     * 登录
     *
     * @param user
     */
    private String doLogin(User user, FamilyMode familyMode) {
        //更新登录信息
        user.setLastLoginIp(IPUtil.getIpAddress(request));
        user.setLastLoginTime(new Date());
        user.setLastFamilyMode(familyMode);
        LoginUser lu = tokenHandler.createLoginUser(user);
        String token = tokenHandler.createToken(lu);
        user.setLastLoginToken(lu.getLoginToken());
        baseService.updateObject(user);
        return token;
    }

    /**
     * 登出
     * @param uc
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResultBean logout(UserCommonForm uc) {
        tokenHandler.deleteLoginUser(request);
        if (uc.getUserId() != null) {
            authService.deleteLastLoginToken(uc.getUserId());
        }
        return callback(null);
    }

    /**
     * 获取用户信息（全局接口）
     *
     * @return
     */
    @RequestMapping(value = "/myInfo", method = RequestMethod.GET)
    public ResultBean myInfo() {
        LoginUser loginUser = tokenHandler.getLoginUser(request);
        Long roleId = loginUser.getRoleId();
        MyInfoVo user = new MyInfoVo();
        user.setUsername(loginUser.getUser().getUsername());
        user.setNickname(loginUser.getUser().getNickname());
        user.setVersion(version);
        Map map = new HashMap();
        map.put("user", user);
        map.put("roles", new String[]{"admin"});
        List<String> perms = authService.selectRoleFPermsList(roleId);
        Collections.sort(perms);
        map.put("permissions", perms);
        return callback(map);
    }


    /**
     * 路由表
     *
     * @return
     */
    @RequestMapping(value = "/getRouters", method = RequestMethod.GET)
    public ResultBean getRouters() {
        LoginUser loginUser = tokenHandler.getLoginUser(request);
        Long roleId = loginUser.getRoleId();
        List<SysFunc> sfList = authService.selectRoleFunctionMenuList(roleId, null);
        List<SysFunc> funcTree = this.getFunctionTree(sfList, ROOT_ID);
        return callback(buildMenus(funcTree));
    }

    /**
     * 完整的路由表，给一些模版使用，跳转连接
     *
     * @return
     */
    @RequestMapping(value = "/getFullRouters", method = RequestMethod.GET)
    public ResultBean getFullRouters() {
        List<SysFunc> sfList = authService.selectFunctionMenuList();
        List<SysFunc> funcTree = this.getFunctionTree(sfList, ROOT_ID);
        return callback(buildMenus(funcTree));
    }

    /**
     * 直接采用RuoYi的代码实现
     *
     * @param menus
     * @return
     */
    private List<RouterVo> buildMenus(List<SysFunc> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysFunc sf : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(sf.getVisible().booleanValue() == true ? false : true);
            router.setName(getRouteName(sf));
            router.setPath(getRouterPath(sf));
            router.setComponent(getComponent(sf));
            router.setMeta(new RouterMetaVo(sf.getFuncName(), sf.getImageName(),!sf.getCache()));
            List<SysFunc> cMenus = sf.getChildren();
            if (!cMenus.isEmpty() && cMenus.size() > 0 && FunctionDataType.M.equals(sf.getFuncDataType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(sf)) {
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(sf.getPath());
                children.setComponent(sf.getComponent());
                children.setName(StringUtils.capitalize(sf.getPath()));
                children.setMeta(new RouterMetaVo(sf.getFuncName(), sf.getImageName(),!sf.getCache()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    private List<SysFunc> getFunctionTree(List<SysFunc> list, long pid) {
        List<SysFunc> res = new ArrayList<>();
        for (SysFunc sf : list) {
            if (sf.getParentId() == pid) {
                res.add(sf);
                List<SysFunc> children = getFunctionTree(list, sf.getFuncId().longValue());
                sf.setChildren(children);
            }
        }
        return res;
    }

    /**
     * 获取路由名称
     * 如果配置的path包含斜杠/,则过滤掉，vue的组件名称不推荐斜杠/
     * 比如path：buyRecord/dateStat,则name:BuyRecordDateStat
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysFunc menu) {
        String path = menu.getPath();
        String[] ss = path.split("/");
        String routerName="";
        for(String s : ss){
            routerName += StringUtils.capitalize(s);
        }
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }

        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysFunc menu) {
        String routerPath = menu.getPath();
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && FunctionDataType.M.equals(menu.getFuncDataType())
                && (false == menu.getFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysFunc menu) {
        //去除菜单类型判断：&& FunctionDataType.C.equals(menu.getFunctionDataType())
        return menu.getParentId().intValue() == 0
                && (false == menu.getFrame());
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysFunc menu) {
        String component = "Layout";
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        }
        return component;
    }


    /**
     * 总的资金统计
     *
     * @return
     */
    @RequestMapping(value = "/generalStat", method = RequestMethod.GET)
    public ResultBean generalStat(@Valid UserGeneralStatSH ugs) {
        Long userId = ugs.getUserId();
        Date today = ugs.getDate();
        if(today==null){
            today = new Date();
        }
        Date monthBussDay = DateUtil.getMonthFirst(today);
        Date yearBussDay = DateUtil.getYearFirst(today);
        FundStatBean monthStat = this.generalStat(PeriodType.MONTHLY,monthBussDay,userId);
        FundStatBean yearStat = this.generalStat(PeriodType.YEARLY,yearBussDay,userId);
        //获取预算
        List<Budget> budgetList = budgetService.getActiveUserBudget(ugs.getUserId(), null);
        if (!budgetList.isEmpty()) {
            BudgetAmountBean bab = budgetHandler.calcBudgetAmount(budgetList, today);
            monthStat.setBudget(bab.getMonthBudget());
            yearStat.setBudget(bab.getYearBudget());

            //年度消费预测(最后一天)
            int monthDays = DateUtil.getMonthDays(today);
            int passMonthDays = DateUtil.getDayOfMonth(today);
            monthStat.setTotalDays(monthDays);
            monthStat.setPassDays(passMonthDays);
            //获取月度预测(最后一天)
            int month = DateUtil.getMonth(today)+1;
            Double monthRate = budgetHandler.predictMonthRate(userId,month,null,monthDays,false);
            monthStat.setConsumePredict(monthRate==null ? null : monthStat.getBudget().multiply(new BigDecimal(monthRate)));

            //年度消费预测(最后一天)
            int yearDays = DateUtil.getYearDays(today);
            int passYearDays = DateUtil.getDayOfYear(today);
            yearStat.setTotalDays(yearDays);
            yearStat.setPassDays(passYearDays);
            Double yearRate = budgetHandler.predictYearRate(userId,null,yearDays,false);
            yearStat.setConsumePredict(yearRate==null ? null : yearStat.getBudget().multiply(new BigDecimal(yearRate)));
        }
        GeneralStatVo res = new GeneralStatVo();
        res.setMonthStat(monthStat);
        res.setYearStat(yearStat);
        return callback(res);
    }

    private FundStatBean generalStat(PeriodType period, Date bussDay, Long userId){
        PeriodDateBean pdb = BussUtil.calPeriod(bussDay,period);
        return budgetHandler.statConsumeIncome(pdb.getStartDate(),pdb.getEndDate(),userId);
    }
}
