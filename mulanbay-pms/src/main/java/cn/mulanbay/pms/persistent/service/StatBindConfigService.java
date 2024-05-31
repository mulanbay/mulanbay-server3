package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.DictItem;
import cn.mulanbay.pms.persistent.domain.StatBindConfig;
import cn.mulanbay.pms.persistent.dto.report.StatBindConfigDTO;
import cn.mulanbay.pms.persistent.dto.report.StatBindConfigDetail;
import cn.mulanbay.pms.persistent.enums.StatBussType;
import cn.mulanbay.pms.persistent.enums.StatValueSource;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StatBindConfigService extends BaseHibernateDao {

    /**
     * 获取配置列表
     *
     * @param fid
     * @param type
     * @return
     */
    public List<StatBindConfig> getConfigList(Long fid, StatBussType type) {
        try {
            String hql = "from StatBindConfig where fid=?1 and type =?2 order by orderIndex";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,StatBindConfig.class,fid,type);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取配置列表异常", e);
        }
    }


    /**
     * 获取统计类的配置值列表
     *
     * @param fid  报表类型id或者提醒配置id或者计划配置id
     * @param type 类型
     * @return
     */
    public List<StatBindConfigDTO> getConfigs(Long fid, StatBussType type, Long userId) {
        try {
            String hql = "from StatBindConfig where fid=?1 and type=?2 order by orderIndex";
            List<StatBindConfig> configs = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,StatBindConfig.class, fid, type);
            if (configs.isEmpty()) {
                return new ArrayList<>();
            } else {
                List<StatBindConfigDTO> list = new ArrayList<>();
                for (StatBindConfig svc : configs) {
                    StatBindConfigDTO scb = getStatValueConfigBean(svc, null, userId);
                    list.add(scb);
                }
                return list;
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取统计类的配置值列表异常", e);
        }

    }

    /**
     * 获取单个
     *
     * @param svc
     * @return
     */
    public StatBindConfigDTO getStatValueConfigBean(StatBindConfig svc, String pid, Long userId) {
        StatBindConfigDTO scb = new StatBindConfigDTO();
        scb.setConfigId(svc.getConfigId());
        if(svc.getType()==StatBussType.CHART){
            scb.setModel(svc.getFormField());
        }else{
            /**
             * 使用在 BaseReportService 的appendExtraBindSQL方法
             */
            scb.setModel(svc.getConfigId().toString());
        }
        scb.setName(svc.getConfigName());
        scb.setMsg(svc.getMsg());
        scb.setTree(svc.getTree());
        scb.setCasCadeType(svc.getCasCadeType());
        scb.setFormField(svc.getFormField());
        scb.setDefaultValue(svc.getDefaultValue());
        scb.setNullable(svc.getNullable());
        StatValueSource source = svc.getSource();
        List<StatBindConfigDetail> details = null;
        switch (source) {
            case SQL: {
                details = this.getSqlDetails(svc, pid, userId);
                break;
            }
            case ENUM: {
                details = this.getEnumDetails(svc);
                break;
            }
            case DATA_DICT: {
                details = this.getDataDictDetails(svc);
                break;
            }
            case JSON: {
                details = this.getJsonDetails(svc);
                break;
            }
            case ANY: {
                details = this.getAnyDetails(svc);
                break;
            }
            default:
                break;
        }
        scb.setList(details);
        return scb;
    }

    /**
     * 数据库类型
     *
     * @param svc
     * @param pid
     * @param userId
     * @return
     */
    private List<StatBindConfigDetail> getSqlDetails(StatBindConfig svc, String pid, Long userId) {
        try {
            String sql = svc.getConfigValue();
            List args = new ArrayList();
            if (!StringUtil.isEmpty(pid)) {
                args.add(pid);
            }
            if (svc.getBindUser()) {
                args.add(userId);
            }
            List<StatBindConfigDetail> res = new ArrayList<>();
            List<Object[]> vcs = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,Object[].class,args.toArray());
            if(StringUtil.isEmpty(vcs)){
                return res;
            }
            if(svc.getTree()){
                //树形结构
                return this.createTree(vcs);
            }
            for (Object[] o : vcs) {
                StatBindConfigDetail detail = new StatBindConfigDetail();
                detail.setId(o[0].toString());
                detail.setText(o[1].toString());
                res.add(detail);
            }
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "查询配置加载项异常", e);
        }
    }

    /**
     * 创建树
     *
     * @param vcs
     * @return
     */
    private List<StatBindConfigDetail> createTree(List<Object[]> vcs){
        return this.getChildren("0",vcs);
    }

    private List<StatBindConfigDetail> getChildren(String pid,List<Object[]> vcs){
        List<StatBindConfigDetail> children = new ArrayList<>();
        for (Object[] o : vcs) {
            String myPid = o[2].toString();
            if(myPid.equals(pid)){
                StatBindConfigDetail child = new StatBindConfigDetail();
                child.setId(o[0].toString());
                child.setText(o[1].toString());
                //寻找下一个子列表
                List<StatBindConfigDetail> c2 = getChildren(child.getId(), vcs);
                child.setChildren(c2);
                //加入到结果集
                children.add(child);
            }
        }
        return children;
    }

    /**
     * 枚举类型
     *
     * @param svc
     * @return
     */
    private List<StatBindConfigDetail> getEnumDetails(StatBindConfig svc) {
        List<TreeBean> list = TreeBeanUtil.createTree(svc.getConfigValue(), svc.getEnumIdType(), false);
        List<StatBindConfigDetail> res = new ArrayList<>();
        for (TreeBean tb : list) {
            StatBindConfigDetail detail = new StatBindConfigDetail();
            detail.setId(tb.getId().toString());
            detail.setText(tb.getText());
            res.add(detail);
        }
        return res;
    }

    /**
     * 数据字典类型
     *
     * @param svc
     * @return
     */
    private List<StatBindConfigDetail> getDataDictDetails(StatBindConfig svc) {
        try {
            String hql = "from DictItem where group.code=?1 and status =1 order by orderIndex";
            List<StatBindConfigDetail> res = new ArrayList<>();
            List<DictItem> vcs = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,DictItem.class, svc.getConfigValue());
            for (DictItem o : vcs) {
                StatBindConfigDetail detail = new StatBindConfigDetail();
                detail.setId(o.getCode());
                detail.setText(o.getItemName());
                res.add(detail);
            }
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "根据数据字典组查询配置加载项异常", e);
        }
    }

    /**
     * 枚举类型
     *
     * @param svc
     * @return
     */
    private List<StatBindConfigDetail> getJsonDetails(StatBindConfig svc) {
        String jsonData = svc.getConfigValue();
        List<StatBindConfigDetail> res = JsonUtil.jsonToBeanList(jsonData, StatBindConfigDetail.class);
        return res;
    }

    /**
     * 任意类型
     *
     * @param svc
     * @return
     */
    private List<StatBindConfigDetail> getAnyDetails(StatBindConfig svc) {
        return null;
    }

}
