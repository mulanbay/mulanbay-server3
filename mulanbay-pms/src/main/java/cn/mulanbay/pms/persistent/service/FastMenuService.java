package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.dto.auth.FastMenuDTO;
import cn.mulanbay.pms.persistent.domain.FastMenu;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

/**
 * @author fenghong
 * @date 2024/1/18
 */
@Service
@Transactional
public class FastMenuService  extends BaseHibernateDao {


    /**
     * 获取用户快捷菜单
     *
     * @param userId
     * @return
     */
    public List<FastMenuDTO> selectFastMenuList(Long userId) {
        try {
            String sql = """
                    SELECT fm.id,fm.menu_id as menuId,sf.func_name as menuName,sf.path
                    FROM fast_menu fm,sys_func sf
                    where fm.menu_id = sf.func_id
                    and fm.user_id=?1
                    order by fm.order_index
                    """;
            List<FastMenuDTO> list = this.getEntityListSI(sql, NO_PAGE, NO_PAGE_SIZE, FastMenuDTO.class, userId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户快捷菜单列表异常", e);
        }
    }

    /**
     * 保存快捷菜单
     *
     * @param userId
     * @return
     */
    public void save(Long userId, String menuIds) {
        try {
            String hql = "delete from FastMenu where userId=?1";
            this.updateEntities(hql, userId);
            if (StringUtil.isNotEmpty(menuIds)) {
                String[] ids = menuIds.split(",");
                List<FastMenu> list = new ArrayList<>();
                int i = 0;
                for (String s : ids) {
                    FastMenu rf = new FastMenu();
                    rf.setUserId(userId);
                    rf.setMenuId(Long.valueOf(s));
                    rf.setOrderIndex((short) i++);
                    list.add(rf);
                }
                this.saveEntities(list.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存快捷菜单异常", e);
        }
    }
}
