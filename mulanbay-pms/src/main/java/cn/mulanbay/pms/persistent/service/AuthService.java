package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.dto.auth.RoleFunctionDTO;
import cn.mulanbay.pms.persistent.dto.auth.UserRoleDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限、用户
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Service
@Transactional
public class AuthService extends BaseHibernateDao {

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    public void deleteUser(Long userId) {
        try {
            String hql = "delete from UserSet where userId = ?1  ";
            this.updateEntities(hql,userId);

            String hql2 = "delete from User where userId = ?1  ";
            this.updateEntities(hql2,userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除用户异常", e);
        }
    }

    /**
     * 通过手机号或者用户名查询用户
     *
     * @param username
     * @return
     */
    public User getUserByUsernameOrPhone(String username) {
        try {
            String hql = "from User where username = ?1 or phone=?2";
            User user = this.getEntity(hql,User.class, username, username);
            return user;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户信息异常", e);
        }
    }

    /**
     * 清除最后的登录token
     *
     * @param userId
     * @return
     */
    public void deleteLastLoginToken(Long userId) {
        try {
            String hql = "update User set lastLoginToken=null where userId = ?1";
            this.updateEntities(hql, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "清除最后的登录token异常", e);
        }
    }

    /**
     * 通过最后一次登录token获取用户
     *
     * @param token
     * @return
     */
    public User getUserByLastLoginToken(String token) {
        try {
            String hql = "from User where lastLoginToken =?1";
            User user = this.getEntity(hql,User.class, token);
            return user;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "通过最后一次登录token获取用户异常", e);
        }
    }

    /**
     * 获取用户当前积分
     *
     * @param userId
     * @return
     */
    public Integer getUserPoint(Long userId) {
        try {
            String hql = "select points from User where userId=?1";
            return this.getEntity(hql,Integer.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户当前积分异常", e);
        }
    }

    /**
     * 更新头像
     *
     * @param userId
     * @return
     */
    public void updateAvatar(Long userId, String avatar) {
        try {
            String hql = "update User set avatar=?1 where userId=?2";
            this.updateEntities(hql, avatar, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新头像异常", e);
        }
    }

    /**
     * 获取用户角色列表
     *
     * @param userId
     * @return
     */
    public List<UserRole> selectUserRoleList(Long userId) {
        try {
            String hql = "from UserRole where userId=?1";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE, UserRole.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户角色列表异常", e);
        }
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    public void deleteRole(Long roleId) {
        try {
            //step 1 ：删除用户角色
            String hql = "delete from UserRole where roleId=?1";
            this.updateEntities(hql, roleId);

            //step 2 ：删除角色功能点
            String hql2 = "delete from RoleFunction where roleId=?1";
            this.updateEntities(hql2, roleId);

            //step 3 ：删除角色
            String hql3 = "delete from Role where roleId=?1";
            this.updateEntities(hql3, roleId);

        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除角色异常", e);
        }
    }

    /**
     * 保存角色功能点
     *
     * @param roleId
     * @return
     */
    public void saveRoleFunction(Long roleId, String functionIds) {
        try {
            String hql = "delete from RoleFunction where roleId=?1";
            this.updateEntities(hql, roleId);
            if (StringUtil.isNotEmpty(functionIds)) {
                String[] ids = functionIds.split(",");
                List<RoleFunction> list = new ArrayList<>();
                for (String s : ids) {
                    RoleFunction rf = new RoleFunction();
                    rf.setRoleId(roleId);
                    rf.setFunctionId(Long.valueOf(s));
                    list.add(rf);
                }
                this.saveEntities(list.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存角色功能点异常", e);
        }
    }

    /**
     * 保存用户角色
     *
     * @param userId
     * @param roleIds
     */
    public void saveUserRole(Long userId, String roleIds) {
        try {
            String hql = "delete from UserRole where userId=?1";
            this.updateEntities(hql, userId);

            if (StringUtil.isNotEmpty(roleIds)) {
                List<UserRole> list = new ArrayList<>();
                String[] ids = roleIds.split(",");
                for (String s : ids) {
                    UserRole rf = new UserRole();
                    rf.setUserId(userId);
                    rf.setRoleId(Long.valueOf(s));
                    list.add(rf);
                }
                this.saveEntities(list.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存用户角色异常", e);
        }
    }

    /**
     * 修改用户
     *
     * @param user
     * @param us
     */
    public void updateUser(User user, UserSet us) {
        try {
            this.updateEntity(user);
            this.updateEntity(us);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "修改用户异常", e);
        }
    }

    /**
     * 获取角色功能点的菜单列表
     * 不需要授权的+需要授权且在用户角色功能里面的+需要授权但是始终显示的(目录类型)
     * @param roleId
     * @return
     */
    public List<SysFunc> selectRoleFunctionMenuList(Long roleId, Boolean visible) {
        try {
            String hql= """
                    from SysFunc \n
                    where router =true and \n
                    (permissionAuth=false or (permissionAuth=true and id in (select functionId from RoleFunction where roleId =?1)) or (permissionAuth=true and alwaysShow=true) ) \n
                    """;
             if (visible != null) {
                hql+="and visible=" + visible;
            }
            hql+=" order by parent,orderIndex ";
            List<SysFunc> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,SysFunc.class, roleId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取角色功能点的菜单列表", e);
        }
    }

    /**
     * 功能点的菜单列表
     * @return
     */
    public List<SysFunc> selectFunctionMenuList() {
        try {
            String hql= "from SysFunc where router =true order by parent,orderIndex ";
            List<SysFunc> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,SysFunc.class);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取角色功能点的菜单列表", e);
        }
    }

    /**
     * 获取角色功能点的按钮列表
     *
     * @param roleId
     * @return
     */
    public List<String> selectRoleFPermsList(Long roleId) {
        try {
            String hql = """
                    SELECT perms FROM SysFunc where perms is not null and
                    (permissionAuth=false or (permissionAuth=true and funcId in (select functionId from RoleFunction where roleId =?1)) )
                    """;
            List<String> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,String.class, roleId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取角色功能点的按钮列表列表", e);
        }
    }

    /**
     * 获取用户角色列表
     *
     * @param userId
     * @return
     */
    public List<UserRoleDTO> selectUserRoleBeanList(Long userId) {
        try {
            String sql = """
                    select r.role_id as roleId,r.role_name as roleName,ur.role_id as userRoleId
                    from role r
                    left join user_role ur
                    on r.role_id = ur.role_id
                    and ur.user_id=?1
                    order by r.role_id
                    """;
            List<UserRoleDTO> list = this.getEntityListSI(sql, NO_PAGE, NO_PAGE_SIZE, UserRoleDTO.class, userId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户角色列表异常", e);
        }
    }


    /**
     * 新增用户
     *
     * @param user
     * @param us
     */
    public void createUser(User user, UserSet us) {
        try {
            this.saveEntity(user);
            us.setUserId(user.getUserId());
            this.saveEntity(us);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "新增用户异常", e);
        }
    }


    /**
     * 获取角色功能点列表
     *
     * @param roleId
     * @return
     */
    public List<RoleFunctionDTO> selectRoleFunctionList(Long roleId) {
        try {
            String sql = """
                    select sf.func_id as funcId,sf.func_name as funcName,sf.pid as pid,rf.function_id as roleFunctionId
                    from sys_func sf
                    left join role_function rf
                    on sf.func_id = rf.function_id
                    and rf.role_id=?1
                    where sf.permission_auth=1 order by sf.pid,sf.order_index
                    """;
            List<RoleFunctionDTO> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE, RoleFunctionDTO.class, roleId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取角色功能点列表异常", e);
        }
    }


}
