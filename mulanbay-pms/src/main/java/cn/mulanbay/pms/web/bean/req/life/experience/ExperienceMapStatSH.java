package cn.mulanbay.pms.web.bean.req.life.experience;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.persistent.query.QueryBuilder;
import cn.mulanbay.pms.common.Constant;
import cn.mulanbay.pms.persistent.enums.DateGroupType;
import cn.mulanbay.pms.persistent.enums.MapField;
import cn.mulanbay.pms.web.bean.req.DateStatSH;
import cn.mulanbay.pms.web.bean.req.GroupType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ExperienceMapStatSH extends QueryBuilder implements DateStatSH, BindUser {

    @Query(fieldName = "country_id", op = Parameter.Operator.EQ, referFieldName = "countryField")
    private Long countryId;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_date", op = Parameter.Operator.GTE)
    private Date startDate;

    @DateTimeFormat(pattern = Constant.DATE_FORMAT)
    @Query(fieldName = "occur_date", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "user_id", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "international", op = Parameter.Operator.EQ)
    private Boolean international;

    private MapField field;

    private GroupType groupType;

    /**
     * 使用明细，直接安装列表数据进行设置
     */
    private Boolean ud;

    /**
     * 省份的字段名称，根据配置文件不同，字段也不同，分出发和抵达两种
     */
    private String countryField;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public DateGroupType getDateGroupType() {
        return null;
    }

    @Override
    public Boolean isCompleteDate() {
        return null;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getInternational() {
        return international;
    }

    public void setInternational(Boolean international) {
        this.international = international;
    }

    public MapField getField() {
        return field;
    }

    public void setField(MapField field) {
        this.field = field;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public Boolean getUd() {
        return ud;
    }

    public void setUd(Boolean ud) {
        this.ud = ud;
    }

    public String getCountryField() {
        return countryField;
    }

    public void setCountryField(String countryField) {
        this.countryField = countryField;
    }
}
