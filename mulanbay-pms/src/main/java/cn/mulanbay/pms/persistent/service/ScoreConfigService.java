package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.ScoreConfig;
import cn.mulanbay.pms.persistent.domain.ScoreGroup;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.util.BeanCopy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户评分
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
@Service
@Transactional
public class ScoreConfigService extends BaseHibernateDao {

    /**
     * 获取评分配置
     *
     * @return
     */
    public List<ScoreConfig> selectActiveScoreConfigList(String code) {
        try {
            String hh = "from ScoreGroup where code=?1 and status=?2 ";
            ScoreGroup group = this.getEntity(hh,ScoreGroup.class, code,CommonStatus.ENABLE);
            String hql = "from ScoreConfig where status=?1 and groupId=?2 ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,ScoreConfig.class,CommonStatus.ENABLE, group.getGroupId());
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取积分配置异常", e);
        }
    }

    /**
     * 删除组
     *
     * @param groupId
     * @return
     */
    public void deleteGroup(Long groupId) {
        try {
            String sql1 = "delete from score_config where group_id=?1";
            this.execSqlUpdate(sql1,groupId);

            String sql2 = "delete from score_group where group_id=?1";
            this.execSqlUpdate(sql2,groupId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取用户最新的评分异常", e);
        }
    }

    /**
     * 获取需要积分统计的用户Id
     *
     * @return
     */
    public void copyGroup(Long templateId, String code, String groupName) {
        try {
            ScoreGroup group = new ScoreGroup();
            group.setCode(code);
            group.setGroupName(groupName);
            group.setStatus(CommonStatus.DISABLE);
            group.setRemark("从模板信息,templateId=" + templateId);
            this.saveEntity(group);
            String hql = "from ScoreConfig where groupId=?1";
            List<ScoreConfig> scList = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,ScoreConfig.class, templateId);
            for (ScoreConfig sc : scList) {
                ScoreConfig newSc = new ScoreConfig();
                BeanCopy.copy(sc, newSc);
                newSc.setConfigId(null);
                newSc.setGroupId(group.getGroupId());
                newSc.setModifyTime(null);
                this.saveEntity(newSc);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取需要积分统计的用户Id异常", e);
        }
    }

}
