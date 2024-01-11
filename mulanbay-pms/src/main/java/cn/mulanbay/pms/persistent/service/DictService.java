package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.DictGroup;
import cn.mulanbay.pms.persistent.domain.DictItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据字典
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class DictService extends BaseHibernateDao {

    /**
     * 删除数据字典组数据
     * @param groupId
     */
    public void deleteDictGroup(Long groupId) {
        try {
            String hql = "delete from DictItem where group.groupId = ?1 ";
            this.updateEntities(hql,groupId);
            String hql2 = "delete from DictGroup where groupId = ?1 ";
            this.updateEntities(hql2,groupId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "删除数据字典组数据异常", e);
        }
    }

    /**
     * 获取字典项列表
     * @param groupId
     * @return
     */
    public List<DictItem> getItemList(Long groupId){
        try {
            String hql = "from DictItem where group.groupId = ?1 order by orderIndex";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,DictItem.class,groupId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取字典项列表异常", e);
        }
    }

    /**
     * 保持字典数据
     *
     * @param group
     * @param itemList
     */
    public void copyDict(DictGroup group, List<DictItem> itemList) {
        try {
            group.setGroupId(null);
            this.saveEntity(group);
            for(DictItem item : itemList){
                item.setItemId(null);
                item.setGroup(group);
                this.saveEntity(item);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "删除数据字典组数据异常", e);
        }
    }

}
