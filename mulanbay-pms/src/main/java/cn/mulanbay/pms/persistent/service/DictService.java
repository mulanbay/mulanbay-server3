package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
