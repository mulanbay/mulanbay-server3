package cn.mulanbay.pms.persistent.dto.report;

import java.util.ArrayList;
import java.util.List;

public class StatSQLDTO {

    private String sqlContent;

    /**
     * 绑定值
     */
    private List args = new ArrayList();

    public String getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public List getArgs() {
        return args;
    }

    /**
     * 数组
     * @return
     */
    public Object[] getArgArray() {
        return args.toArray();
    }

    public void addArg(Object arg) {
        this.args.add(arg);
    }
}
