package cn.mulanbay.pms.web.bean.req.ai;

import cn.mulanbay.pms.persistent.enums.MLAlgorithm;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class AIModelForm {

    private Long modelId;
    /**
     * 名称
     */
    @NotEmpty(message = "模型名称不能为空")
    private String modelName;
    /**
     * 唯一标识代码
     */
    @NotEmpty(message = "唯一标识代码不能为空")
    private String code;

    /**
     * 文件名
     */
    @NotEmpty(message = "文件名不能为空")
    private String fileName;

    /**
     * 是否区分用户
     */
    private Boolean du;

    /**
     * 算法
     */
    @NotNull(message = "算法类型不能为空")
    private MLAlgorithm algorithm;

    private String remark;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public MLAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(MLAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
