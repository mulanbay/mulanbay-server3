package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.dto.auth.SysFuncDTO;
import cn.mulanbay.pms.persistent.enums.FunctionDataType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SysFuncService extends BaseHibernateDao {

    /**
     * 获取功能点的菜单列表
     *
     * @return 菜单列表
     */
    public List<SysFuncDTO> getMenu() {
        try {
            String sql = "select func_id as funcId,func_name as funcName,pid from sys_func where func_data_type=?1 or func_data_type =?2 order by pid,order_index ";
            return this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE,
                    SysFuncDTO.class, FunctionDataType.M.ordinal(), FunctionDataType.C.ordinal());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取功能点的菜单列表异常", e);
        }
    }

}
