package cn.mulanbay.pms.web.bean.req.system.backup;

import jakarta.validation.constraints.NotEmpty;

public class BackupFileDeleteForm {

    @NotEmpty(message = "文件名不能为空")
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
