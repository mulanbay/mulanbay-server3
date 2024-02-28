package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.StatTemplate;
import cn.mulanbay.pms.persistent.domain.UserStat;
import cn.mulanbay.pms.persistent.dto.report.StatSQLDTO;
import cn.mulanbay.pms.persistent.enums.StatBussType;
import cn.mulanbay.pms.persistent.enums.StatValueClass;

import java.io.Serializable;
import java.util.List;

/**
 * 报表基类
 *
 * @author fenghong
 * @date 2024/2/28
 */
public class BaseReportService extends BaseHibernateDao {


    /**
     * 计算封装SQL
     *
     * @param un
     * @return
     */
    protected StatSQLDTO assembleSQL(UserStat un) {
        StatSQLDTO dto = new StatSQLDTO();
        StatTemplate template = un.getTemplate();
        dto.setSqlContent(template.getSqlContent());
        String bindValues = un.getBindValues();
        if(StringUtil.isNotEmpty(bindValues)){
            List<StatValueClass> vcs = this.getBindValueClassList(template.getTemplateId(), StatBussType.STAT);
            String[] bs = bindValues.split(",");
            int n = bs.length;
            for(int i=0;i<n;i++){
                dto.addArg(this.formatBindValue(vcs.get(i),bs[i]));
            }
        }
        //UserField目前只用来判断是否需要绑定userId
        if (!StringUtil.isEmpty(template.getUserField())) {
            dto.addArg(un.getUserId());
        }
        return dto;
    }

    private Serializable formatBindValue(StatValueClass vc, String v){
        switch (vc){
            case LONG -> {
                return Long.parseLong(v);
            }
            case INTEGER -> {
                return Integer.parseInt(v);
            }
            case SHORT -> {
                return Short.parseShort(v);
            }
            case STRING -> {
                return v;
            }
        }
        return v;
    }

    /**
     * 绑定值的类型
     * @param fid
     * @param type
     * @return
     */
    private List<StatValueClass> getBindValueClassList(Long fid,StatBussType type){
        try {
            String hql = "select valueClass from StatBindConfig where fid=?1 and type=?2";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,StatValueClass.class, fid, type);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "查找绑定值的类型异常", e);
        }
    }
}
