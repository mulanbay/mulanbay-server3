package cn.mulanbay.pms.persistent.dto.report;

import cn.mulanbay.pms.persistent.enums.CasCadeType;

import java.util.ArrayList;
import java.util.List;

public class StatBindConfigDTO {

    private String name;

    private String msg;

    private Boolean tree;

    private CasCadeType casCadeType;

    private List<StatBindConfigDetail> list = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getTree() {
        return tree;
    }

    public void setTree(Boolean tree) {
        this.tree = tree;
    }

    public List<StatBindConfigDetail> getList() {
        return list;
    }

    public void setList(List<StatBindConfigDetail> list) {
        this.list = list;
    }

    public void addStatValueConfigDetail(StatBindConfigDetail detail) {
        list.add(detail);
    }

    public CasCadeType getCasCadeType() {
        return casCadeType;
    }

    public void setCasCadeType(CasCadeType casCadeType) {
        this.casCadeType = casCadeType;
    }
}
