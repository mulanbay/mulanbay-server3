package cn.mulanbay.pms.web.bean.res;

import cn.mulanbay.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TreeBean {

    private Object id;

    private Object pid;

    private String text;

    private boolean checked;

    private String iconCls;

    private String group;

    private Attribute attributes;

    public TreeBean() {
    }

    public TreeBean(Object id, String text) {
        this.id = id;
        this.text = text;
    }

    List<TreeBean> children;

    /**
     * 创建根节点
     * @return
     */
    public static TreeBean creatRoot(){
        TreeBean root = new TreeBean();
        root.setId(0L);
        root.setText("根");
        return root;
    }
    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getPid() {
        return pid;
    }

    public void setPid(Object pid) {
        this.pid = pid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<TreeBean> getChildren() {
        return children;
    }

    public void setChildren(List<TreeBean> children) {
        this.children = children;
    }

    public void addChild(TreeBean child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public Attribute getAttributes() {
        return attributes;
    }

    public void setAttributes(Attribute attributes) {
        this.attributes = attributes;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * 支持vue-Treeselect
     *
     * @return
     */
    public String getLabel() {
        return text;
    }

    /**
     * 支持 vant
     *
     * @return
     */
    public String getName() {
        return text;
    }

    public boolean hasChildren() {
        return StringUtil.isEmpty(this.children) ? false : true;
    }
}
