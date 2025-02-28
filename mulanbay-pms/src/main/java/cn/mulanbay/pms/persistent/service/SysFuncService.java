package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.dto.auth.SysFuncDTO;
import cn.mulanbay.pms.persistent.enums.FunctionDataType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            //查询的列顺序要和SysFuncDTO构造器中的顺序一致
            String sql = "select func_id as funcId,pid as pid,func_name as funcName from sys_func where func_data_type=?1 or func_data_type =?2 order by pid,order_index ";
            return this.getEntityListSI(sql, NO_PAGE,NO_PAGE_SIZE,
                    SysFuncDTO.class, FunctionDataType.M.ordinal(), FunctionDataType.C.ordinal());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取功能点的菜单列表异常", e);
        }
    }

    /**
     * 删除功能点
     * @param rootId
     */
    public void deleteFunctions(Long rootId) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("delete FROM sys_func WHERE func_id in ");
            sb.append("(select func_id from ");
            sb.append("(SELECT func_id FROM sys_func WHERE FIND_IN_SET(func_id, getFuncChildren("+rootId+"))) as aa ");
            sb.append(") ");
            this.execSqlUpdate(sb.toString());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除功能点异常", e);
        }
    }

    /**
     * 路径是否已经存在
     * @param funcId
     * @param path
     * @return
     */
    public boolean checkPathExit(Long funcId,String path) {
        try {
            Long n =0L;
            if(funcId==null){
                String hql = "select count(0) from SysFunc where path=?1";
                n = this.getCount(hql,path);
            }else{
                String hql = "select count(0) from SysFunc where path=?1 and funcId!=?2 ";
                n = this.getCount(hql,path,funcId);
            }
            return n==0 ? false:true;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "检测路径是否已经存在异常", e);
        }
    }

}
