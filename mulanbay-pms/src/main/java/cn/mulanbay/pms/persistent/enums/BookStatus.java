package cn.mulanbay.pms.persistent.enums;

/**
 * 阅读状态
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum BookStatus {

    READING(0, "正在读"),
    UNREAD(1, "未读"),
    READED(2, "已读"),
    GIVEUP(3, "放弃");
    private int value;
    private String name;

    BookStatus(int value, String name) {
        this.value = value;
        this.name = name;
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

    public static BookStatus getReadingStatus(int ordinal) {
        for (BookStatus readingStatus : BookStatus.values()) {
            if (readingStatus.ordinal() == ordinal) {
                return readingStatus;
            }
        }
        return null;
    }
}
