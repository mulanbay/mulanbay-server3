package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.ChartTemplate;
import cn.mulanbay.pms.persistent.domain.StatBindConfig;
import cn.mulanbay.pms.persistent.enums.StatBussType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 图表配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Service
@Transactional
public class ChartService extends BaseHibernateDao {

    /**
     * 保存计划配置模板
     *
     * @param bean
     * @param configList
     */
    public void saveChartTemplate(ChartTemplate bean, List<StatBindConfig> configList) {
        try {
            this.saveEntity(bean);
            if (StringUtil.isNotEmpty(configList)) {
                for (StatBindConfig c : configList) {
                    c.setConfigId(null);
                    c.setFid(bean.getTemplateId());
                    c.setType(StatBussType.CHART);
                }
                this.saveEntities(configList.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存计划配置模板异常", e);
        }
    }

    /**
     * 删除图表模版
     *
     * @param templateId
     * @return
     */
    public void deleteChartTemplate(Long templateId) {
        try {
            //删除配置绑定
            String sql = "delete from stat_bind_config where type=?1 and fid=?2";
            this.execSqlUpdate(sql, StatBussType.CHART,templateId);

            //删除模版
            String sql2 = "delete from chart_template where template_id=?1 ";
            this.execSqlUpdate(sql2, templateId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除图表模版异常", e);
        }
    }

}
