package cn.mulanbay.pms.web.bean.res;

/**
 * @author fenghong
 * @date 2024/6/13
 */
public class NameValueVo {

    private String name;

    private Object value;

    public NameValueVo() {
    }

    public NameValueVo(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
