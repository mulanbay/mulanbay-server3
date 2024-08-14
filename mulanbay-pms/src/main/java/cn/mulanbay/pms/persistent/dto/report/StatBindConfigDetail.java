package cn.mulanbay.pms.persistent.dto.report;

public class StatBindConfigDetail {

    /**
     * 选中的ID值
     */
    private String id;

    /**
     * 名称
     */
    private String text;

    /**
     * 父级ID
     */
    private String pid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
