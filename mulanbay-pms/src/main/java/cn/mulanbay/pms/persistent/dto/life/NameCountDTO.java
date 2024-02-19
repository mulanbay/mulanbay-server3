package cn.mulanbay.pms.persistent.dto.life;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/3/15
 */
public class NameCountDTO {

    private String name;

    private Long counts;

    public NameCountDTO(String name, Long counts) {
        this.name = name;
        this.counts = counts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCounts() {
        return counts;
    }

    public void setCounts(Long counts) {
        this.counts = counts;
    }
}
