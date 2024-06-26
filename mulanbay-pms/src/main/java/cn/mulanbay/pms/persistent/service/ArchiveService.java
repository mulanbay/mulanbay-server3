package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.Archive;
import cn.mulanbay.pms.persistent.enums.BussType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArchiveService extends BaseHibernateDao {

    /**
     * 获取人生档案
     *
     * @param userId
     * @param bussType
     * @param sourceId
     * @return
     */
    public Archive getArchive(Long userId, BussType bussType, Long sourceId) {
        try {
            String hql = "from Archive where userId=?1 and bussType=?2 and sourceId=?3";
            return this.getEntity(hql,Archive.class, userId, bussType, sourceId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取人生档案异常", e);
        }
    }


}
