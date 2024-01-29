package cn.mulanbay.pms.persistent.domain;

import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.MLAlgorithm;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 机器学习模型配置
 *
 * @author fenghong
 * @create 2020-08-27 18:44
 */
@Entity
@Table(name = "ai_model")
public class AIModel implements java.io.Serializable {

    private static final long serialVersionUID = 7254329209805899896L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "model_id", unique = true, nullable = false)
    private Long modelId;

    /**
     * 名称
     */
    @Column(name = "model_name")
    private String modelName;
    /**
     * 唯一标识代码
     */
    @Column(name = "code")
    private String code;

    /**
     * 文件名
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 是否区分用户
     */
    @Column(name = "du")
    private Boolean du;
    /**
     * 算法
     */
    @Column(name = "algorithm")
    private MLAlgorithm algorithm;

    //状态
    @Column(name = "status")
    private CommonStatus status;
    @Column(name = "remark")
    private String remark;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT)
    @Column(name = "modify_time")
    private Date modifyTime;

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getDu() {
        return du;
    }

    public void setDu(Boolean du) {
        this.du = du;
    }

    public MLAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(MLAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Transient
    public String getStatusName() {
        return status.getName();
    }


    @Transient
    public String getAlgorithmName() {
        return algorithm.getName();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AIModel bean) {
            return bean.getModelId().equals(this.getModelId());
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(modelId);
    }
}
