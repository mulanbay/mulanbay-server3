package cn.mulanbay.pms.persistent.dto.stat;

import java.util.ArrayList;
import java.util.List;

public class CommonSqlDTO {

    private String sqlContent;

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

    public void addArg(Object arg) {
        this.args.add(arg);
    }
}
