package cn.mulanbay.pms.web.bean.req;

import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.persistent.enums.DateGroupType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 同期对比查询基类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class BaseYoyStatSH extends QueryBuilder {

    private DateGroupType dateGroupType;

    @NotNull(message = "年份不能为空")
    @Size(min = 2, message = "至少选择两个年份")
    private List<Integer> years;

    private GroupType groupType;

    public DateGroupType getDateGroupType() {
        return dateGroupType;
    }

    public void setDateGroupType(DateGroupType dateGroupType) {
        this.dateGroupType = dateGroupType;
    }

    public List<Integer> getYears() {
        if (years != null) {
            //界面中可能传入空的year
            years.remove(null);
        }
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }
}
