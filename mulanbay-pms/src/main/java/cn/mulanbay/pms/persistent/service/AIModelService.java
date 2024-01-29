package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.ai.ml.dataset.ModelHandle;
import cn.mulanbay.ai.ml.dataset.bean.ModelFile;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.AIModel;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.util.BeanCopy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AIModelService extends BaseHibernateDao implements ModelHandle {

    /**
     * 发布模型
     *
     * @param bean
     */
    public void publish(AIModel bean) {
        try {
            //把其他的都设置为无效
            //todo 后期修改为同时支持多种算法类型，那么前端需要传入算法类型
            String hql = "update AIModel set status = ?1 where code=?2 ";
            this.updateEntities(hql, CommonStatus.DISABLE, bean.getCode());

            bean.setStatus(CommonStatus.ENABLE);
            bean.setModifyTime(new Date());
            this.updateEntity(bean);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "发布模型异常", e);
        }
    }

    /**
     * 查询有效的模型文件
     *
     * @param code
     * @return
     */
    @Override
    public ModelFile getModelFile(String code) {
        try {
            String hql = "from AIModel where status = ?1  and code=?2  ";
            //只查第一条
            AIModel mc = this.getEntity(hql,AIModel.class, CommonStatus.ENABLE, code);
            if (mc == null) {
                return null;
            } else {
                ModelFile mf = new ModelFile();
                BeanCopy.copy(mc, mf);
                return mf;
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "查询有效的模型文件异常", e);
        }
    }

    /**
     * 模型文件列表
     *
     * @return
     */
    @Override
    public List<ModelFile> getModelFileList() {
        try {
            String hql = "from AIModel where status = ?1 ";
            List<AIModel> mcList = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,AIModel.class, CommonStatus.ENABLE);
            List<ModelFile> list = mcList.stream().map(
                    mc -> {
                        ModelFile mf = new ModelFile();
                        BeanCopy.copy(mc, mf);
                        return mf;
                    }
            ).collect(Collectors.toList());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "查询模型文件列表异常", e);
        }
    }
}
