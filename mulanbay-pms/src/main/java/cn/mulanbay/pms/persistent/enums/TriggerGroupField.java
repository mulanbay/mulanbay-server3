package cn.mulanbay.pms.persistent.enums;

/**
 * 账号调整类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum TriggerGroupField {

    DEPLOY_ID(0,"部署点","deploy_id"),
    DISTRIABLE(1,"分布式支持","distriable"),
    REDO_TYPE(2,"重做类型","redo_type"),
    GROUP_NAME(3,"组名","group_name"),
    TRIGGER_TYPE(4,"触发器类别","trigger_type"),
    UNIQUE_TYPE(5,"唯一性类别","unique_type"),
    LOGGABLE(6,"记录日志","loggable"),
    NOTIFIABLE(7,"提醒消息","notifiable"),
    LAST_EXECUTE_RESULT(8,"最后一次执行结果","last_execute_result");

    private int value;

    private String name;

    private String field;

    TriggerGroupField(int value, String name) {
        this.value = value;
        this.name = name;
    }

    TriggerGroupField(int value, String name, String field) {
        this.value = value;
        this.name = name;
        this.field = field;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
