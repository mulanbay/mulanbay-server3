package cn.mulanbay.pms.persistent.dto.report;

import java.util.List;

public class StatBindConfigDetail {

    private String id;

    private String text;

    private List<StatBindConfigDetail> children;

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

    public List<StatBindConfigDetail> getChildren() {
        return children;
    }

    public void setChildren(List<StatBindConfigDetail> children) {
        this.children = children;
    }
}
