package cn.mulanbay.pms.web.controller.auth;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.FileUtil;
import cn.mulanbay.common.util.MimeTypeUtils;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.*;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.auth.UserRoleDTO;
import cn.mulanbay.pms.persistent.enums.AuthType;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.FamilyService;
import cn.mulanbay.pms.persistent.service.UserLevelService;
import cn.mulanbay.pms.persistent.service.WxAccountService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.LoginUser;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.auth.user.*;
import cn.mulanbay.pms.web.bean.req.main.UserCommonFrom;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.auth.user.UserProfileVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static cn.mulanbay.pms.common.Constant.DEFAULT_SCORE_GROUP_ID;

/**
 * 用户
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    private static Class<User> beanClass = User.class;

    @Value("${mulanbay.picture.folder}")
    String avatarFilePath;

    @Autowired
    AuthService authService;

    @Autowired
    TokenHandler tokenHandler;

    @Autowired
    FamilyService familyService;

    @Autowired
    ThreadPoolHandler threadPoolHandler;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @Autowired
    UserLevelService userLevelService;

    @Autowired
    WxAccountService wxAccountService;

    @Autowired
    WXHandler wxHandler;

    @Autowired
    UserHandler userHandler;

    /**
     * 用户树
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree() {
        try {
            UserSH sf = new UserSH();
            PageResult<User> pageResult = getUserResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<User> gtList = pageResult.getBeanList();
            for (User gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getUserId());
                tb.setText(gt.getUsername());
                list.add(tb);
            }
            return callback(list);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取用户树异常",
                    e);
        }
    }

    /**
     * 获取用户角色树
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userRoleTree")
    public ResultBean userRoleTree(@RequestParam(name = "userId") Long userId) {
        try {
            List<UserRoleDTO> urList = authService.selectUserRoleBeanList(userId);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (UserRoleDTO ur : urList) {
                TreeBean tb = new TreeBean();
                tb.setId(ur.getRoleId());
                tb.setText(ur.getRoleName());
                if (ur.getUserRoleId() != null) {
                    tb.setChecked(true);
                }
                list.add(tb);
            }
            Map map = new HashMap<>();
            map.put("treeData", list);
            List checkedKeys = new ArrayList();
            for (UserRoleDTO sf : urList) {
                if (sf.getUserRoleId() != null) {
                    checkedKeys.add(sf.getRoleId());
                }
            }
            map.put("checkedKeys", checkedKeys);
            return callback(map);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取用户角色树异常",
                    e);
        }
    }

    /**
     * 保存用户角色
     *
     * @return
     */
    @RequestMapping(value = "/saveUserRole", method = RequestMethod.POST)
    public ResultBean saveUserRole(@RequestBody @Valid UserRoleForm ur) {
        authService.saveUserRole(ur.getUserId(), ur.getRoleIds());
        return callback(null);
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(UserSH sf) {
        PageResult<User> pageResult = getUserResult(sf);
        return callbackDataGrid(pageResult);
    }

    private PageResult<User> getUserResult(UserSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.ASC);
        pr.addSort(sort);
        PageResult<User> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid UserForm bean) {
        User user = new User();
        BeanCopy.copy(bean, user);
        // 密码设置
        String encodePassword = tokenHandler.encodePassword(bean.getPassword());
        user.setPassword(encodePassword);
        user.setSecAuthType(bean.getSecAuthType());
        user.setLevel(Constant.USER_LEVEL);
        user.setPoints(0);
        UserSet us = new UserSet();
        us.setSendWx(true);
        us.setSendEmail(true);
        us.setScoreGroupId(DEFAULT_SCORE_GROUP_ID);
        authService.createUser(user, us);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "userId") Long userId) {
        User br = baseService.getObject(beanClass, userId);
        return callback(br);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid UserForm bean) {
        User user = baseService.getObject(beanClass, bean.getUserId());
        String op = user.getPassword();
        BeanCopy.copy(bean, user);
        String password = bean.getPassword();
        if (StringUtil.isNotEmpty(password)) {
            // 密码设置
            String encodePassword = tokenHandler.encodePassword(bean.getPassword());
            user.setPassword(encodePassword);
        } else {
            user.setPassword(op);
        }
        UserSet us = baseService.getObject(UserSet.class, bean.getUserId());
        authService.updateUser(user, us);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        String[] ss = deleteRequest.getIds().split(",");
        for(String s : ss){
            Long userId = Long.valueOf(s);
            this.deleteData(userId);
            authService.deleteUser(userId);
        }
        return callback(null);
    }

    /**
     * 获取用户信息（新版本使用）
     *
     * @return
     */
    @RequestMapping(value = "/getProfile", method = RequestMethod.GET)
    public ResultBean getProfile() {
        LoginUser loginUser = tokenHandler.getLoginUser(request);
        Long roleId = loginUser.getRoleId();
        Long userId = loginUser.getUserId();
        UserProfileVo ups = new UserProfileVo();
        User user = baseService.getObject(User.class, userId);
        UserSet us = baseService.getObject(UserSet.class, userId);
        BeanCopy.copy(user, ups);
        BeanCopy.copy(us, ups);
        if (roleId != null) {
            Role role = baseService.getObject(Role.class, roleId);
            ups.setRoleName(role.getRoleName());
        }
        LevelConfig lc = userLevelService.getUserLevel(user.getLevel());
        ups.setAvatar(systemConfigHandler.getPictureFullUrl(ups.getAvatar()));
        ups.setLevelName(lc.getLevelName());
        return callback(ups);
    }

    /**
     * 更新用户信息（新版本使用）
     *
     * @return
     */
    @RequestMapping(value = "/editProfile", method = RequestMethod.POST)
    public ResultBean editProfile(@RequestBody @Valid UserProfileForm upr) {
        User user = baseService.getObject(beanClass, upr.getUserId());
        BeanCopy.copy(upr, user);
        baseService.updateObject(user);
        return callback(null);
    }

    /**
     * 用户自己修改密码
     *
     * @return
     */
    @RequestMapping(value = "/editPassword", method = RequestMethod.POST)
    public ResultBean editPassword(@RequestBody @Valid UserPasswordForm eui) {
        User user = baseService.getObject(beanClass, eui.getUserId());
        String pp = tokenHandler.encodePassword(eui.getOldPassword());
        if (!user.getPassword().equals(pp)) {
            return callbackErrorCode(PmsCode.USER_PASSWORD_ERROR);
        }
        String newPP = tokenHandler.encodePassword(eui.getNewPassword());
        user.setPassword(newPP);
        baseService.updateObject(user);
        return callback(null);
    }

    /**
     * 用户自己修改个人信息
     *
     * @return
     */
    @RequestMapping(value = "/editMyInfo", method = RequestMethod.POST)
    public ResultBean editMyInfo(@RequestBody @Valid MyInfoForm eui) {
        User user = baseService.getObject(beanClass, eui.getUserId());
        String pp = tokenHandler.encodePassword(eui.getPassword());
        if (!user.getPassword().equals(pp)) {
            return callbackErrorCode(PmsCode.USER_PASSWORD_ERROR);
        }
        if (eui.getSecAuthType() == AuthType.SMS && StringUtil.isEmpty(eui.getPhone())) {
            return callbackErrorCode(PmsCode.USER_SEC_AUTH_PHONE_NULL_);
        }
        if (eui.getSecAuthType() == AuthType.EMAIL && StringUtil.isEmpty(eui.getEmail())) {
            return callbackErrorCode(PmsCode.USER_SEC_AUTH_EMAIL_NULL_);
        }
        UserSet us = baseService.getObject(UserSet.class,eui.getUserId());
        user.setUsername(eui.getUsername());
        user.setNickname(eui.getNickname());
        user.setBirthday(eui.getBirthday());
        user.setPhone(eui.getPhone());
        user.setEmail(eui.getEmail());
        user.setSecAuthType(eui.getSecAuthType());
        if (!StringUtil.isEmpty(eui.getNewPassword())) {
            // 密码设置
            String encodePassword = tokenHandler.encodePassword(eui.getNewPassword());
            user.setPassword(encodePassword);
        }
        baseService.updateObject(user);
        BeanCopy.copy(eui, us);
        baseService.updateObject(us);
        userHandler.expireUserSet(us.getUserId());
        return callback(null);
    }


    /**
     * 离线
     *
     * @return
     */
    @RequestMapping(value = "/offline", method = RequestMethod.POST)
    public ResultBean offline(@RequestBody @Valid UserDataForm ucr) {
        return callback(null);
    }

    /**
     * 删除用户数据
     *
     * @return
     */
    @RequestMapping(value = "/deleteUserData", method = RequestMethod.POST)
    public ResultBean deleteUserData(@RequestBody @Valid UserDataForm ucr) {
        this.deleteData(ucr.getUserId());
        return callback(null);
    }

    /**
     * 删除用户数据
     * @param userId
     */
    private void deleteData(Long userId){
        threadPoolHandler.pushThread(() -> baseService.updateJobProcedure("delete_user_data", userId));
    }

    /**
     * 初始化用户数据
     *
     * @return
     */
    @RequestMapping(value = "/initUserData", method = RequestMethod.POST)
    public ResultBean initUserData(@RequestBody @Valid UserDataForm ucr) {
        threadPoolHandler.pushThread(new Runnable() {
            @Override
            public void run() {
                //dataService.initUserData(ucr.getUserId());
            }
        });
        return callback(null);
    }

    /**
     * 获取默认城市
     *
     * @return
     */
    @RequestMapping(value = "/getResidentCity", method = RequestMethod.GET)
    public ResultBean getResidentCity(UserCommonFrom ucr) {
        UserSet us = baseService.getObject(UserSet.class,ucr.getUserId());
        return callback(us.getResidentCity());
    }

    /**
     * 头像上传
     */
    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    public ResultBean uploadAvatar(@RequestParam("avatarfile") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            // 获取文件存储路径（绝对路径）
            String path = avatarFilePath;
            // 获取原文件名
            String extractFilename = this.extractFilename(file);
            // 创建文件实例
            File filePath = new File(path, extractFilename);
            FileUtil.checkPathExits(filePath);
            // 写入文件
            file.transferTo(filePath);
            //更新数据库
            authService.updateAvatar(this.getCurrentUserId(), extractFilename);
            return callback(extractFilename);
        } else {
            return callbackErrorInfo("文件为空");
        }
    }

    /**
     * 编码文件名
     */
    private final String extractFilename(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        fileName = DateUtil.getFormatDate(new Date(), "yyyyMMdd") + "/" + StringUtil.genUUID() + "." + extension;
        return "/" + fileName;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtil.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

    /**
     * 获取微信账号
     *
     * @return
     */
    @RequestMapping(value = "/getWxAccount", method = RequestMethod.GET)
    public ResultBean getWxAccount( @RequestParam(name = "userId") Long userId) {
        WxAccount uw = wxAccountService.getAccount(userId, wxHandler.getAppId());
        if (uw == null) {
            uw = new WxAccount();
            uw.setUserId(userId);
        }
        return callback(uw);
    }

    /**
     * 修改用户微信信息
     *
     * @return
     */
    @RequestMapping(value = "/editWxAccount", method = RequestMethod.POST)
    public ResultBean editWxAccount(@RequestBody @Valid WxAccountForm eui) {
        if (eui.getId() == null) {
            WxAccount uw = new WxAccount();
            uw.setOpenId(eui.getOpenId());
            uw.setUserId(eui.getUserId());
            uw.setAppId(wxHandler.getAppId());
            baseService.saveObject(uw);
        } else {
            WxAccount uw = baseService.getObject(WxAccount.class, eui.getId());
            uw.setOpenId(eui.getOpenId());
            uw.setUserId(eui.getUserId());
            baseService.updateByMergeObject(uw);
        }
        return callback(null);
    }

}
