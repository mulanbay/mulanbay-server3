package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.Command;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommandService extends BaseHibernateDao {

    /**
     * 通过code查询CommandConfig
     *
     * @param code
     * @return
     */
    public Command getCommand(String code) {
        try {
            String hql = "from Command where code = ?1 ";
            Command cc = this.getEntity(hql,Command.class, code);
            return cc;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "通过code查询Command异常", e);
        }
    }

    /**
     * 通过scode查询CommandConfig
     *
     * @param scode
     * @return
     */
    public Command getCommandByScode(String scode) {
        try {
            String hql = "from Command where scode = ?1 ";
            Command cc = this.getEntity(hql,Command.class, scode);
            return cc;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "通过scode查询CommandConfig异常", e);
        }
    }

}
