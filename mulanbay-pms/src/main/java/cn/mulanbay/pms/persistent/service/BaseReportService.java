package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.StatBindConfig;
import cn.mulanbay.pms.persistent.dto.report.StatSQLDTO;
import cn.mulanbay.pms.persistent.enums.StatBussType;
import cn.mulanbay.pms.persistent.enums.StatValueClass;
import cn.mulanbay.pms.util.BussUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static cn.mulanbay.pms.common.Constant.EXTRA_SQL_RPC;

/**
 * 报表基类
 *
 * @author fenghong
 * @date 2024/2/28
 */
public class BaseReportService extends BaseHibernateDao {

    protected Serializable formatBindValue(StatValueClass vc, String v){
        return BussUtil.formatBindValue(vc,v);
    }

    /**
     * 绑定值的类型
     * @param fid
     * @param type
     * @return
     */
    protected List<StatValueClass> getBindValueClassList(Long fid,StatBussType type){
        try {
            String hql = "select valueClass from StatBindConfig where fid=?1 and type=?2";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,StatValueClass.class, fid, type);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "查找绑定值的类型异常", e);
        }
    }

    /**
     * 获取绑定配置
     * @param configId
     * @return
     */
    protected StatBindConfig getStatBindConfig(Long configId){
        try {
            return this.getEntityById(StatBindConfig.class,configId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取绑定配置异常", e);
        }
    }

    /**
     * 获取额外的查询SQL
     * @param fid 目前未使用
     * @param type 目前未使用
     * @param bindValues
     * @param beginIndex
     * @return
     */
    protected StatSQLDTO appendExtraBindSQL(Long fid, StatBussType type, String bindValues,int beginIndex,StatSQLDTO dto ) {
        if(StringUtil.isEmpty(bindValues)){
            return dto;
        }
        Map<String,String> map = (Map<String, String>) JsonUtil.jsonToBean(bindValues,Map.class);
        if(map==null||map.isEmpty()){
            return dto;
        }
        StringBuffer sql = new StringBuffer();
        int index = beginIndex;
        for(String key : map.keySet()){
            String v = map.get(key);
            if(StringUtil.isEmpty(v)){
                continue;
            }
            StatBindConfig sbc = this.getStatBindConfig(Long.valueOf(key));
            Serializable bv = this.formatBindValue(sbc.getValueClass(),v);
            String condition = sbc.getFormField();
            sql.append(" and "+condition+" ?"+(++index));
            if(condition.contains("like")){
                //模糊匹配类型由后端添加%匹配符，前端用户只需要输入关键字
                bv = "'%"+bv+"%'";
            }
            dto.addArg(bv);
        }
        String sqlContent = dto.getSqlContent();
        String extraSQL = sql.toString();
        if(StringUtil.isNotEmpty(extraSQL)){
            extraSQL+=" ";
            if(sqlContent.contains(EXTRA_SQL_RPC)){
                sqlContent = sqlContent.replace(EXTRA_SQL_RPC," "+extraSQL);
            }else{
                sqlContent +=extraSQL;
            }
        }else{
            sqlContent = sqlContent.replace(EXTRA_SQL_RPC,"");
        }
        dto.setSqlContent(sqlContent);
        return dto;
    }
}
