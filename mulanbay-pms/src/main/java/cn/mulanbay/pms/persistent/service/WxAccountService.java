package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.WxAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class WxAccountService extends BaseHibernateDao {

    /**
     * 获取用户微信账户
     *
     * @param userId
     * @return
     */
    public WxAccount getAccount(Long userId, String appId) {
        try {
            String hql = "from WxAccount where userId = ?1 and appId=?2 ";
            return this.getEntity(hql,WxAccount.class, userId, appId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取用户微信账户异常", e);
        }
    }

    /**
     * 获取用户微信账户
     *
     * @return
     */
    public WxAccount getAccount(String appId, String openId) {
        try {
            String hql = "from WxAccount where appId=?1 and openId=?2 ";
            return this.getEntity(hql,WxAccount.class, appId, openId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取用户微信账户异常", e);
        }
    }

    /**
     * 保存微信信息
     *
     * @param uw
     */
    public void saveOrUpdateAccount(WxAccount account) {
        try {
            if (account.getId() == null) {
                this.saveEntity(account);
            } else {
                this.updateEntity(account);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "保存微信信息异常", e);
        }
    }
}
