package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.pms.persistent.domain.BehaviorTemplate;
import cn.mulanbay.pms.persistent.domain.StatBindConfig;
import cn.mulanbay.pms.persistent.dto.calendar.CalendarLogDTO;
import cn.mulanbay.pms.persistent.dto.report.StatSQLDTO;
import cn.mulanbay.pms.persistent.enums.SqlType;
import cn.mulanbay.pms.persistent.enums.StatBussType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BehaviorService extends BaseReportService {

    /**
     * 保存计划配置模板
     *
     * @param bean
     * @param configList
     */
    public void saveBehaviorTemplate(BehaviorTemplate bean, List<StatBindConfig> configList) {
        try {
            this.saveEntity(bean);
            if (StringUtil.isNotEmpty(configList)) {
                for (StatBindConfig c : configList) {
                    c.setFid(bean.getTemplateId());
                    c.setType(StatBussType.PLAN);
                }
                this.saveEntities(configList.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存计划配置模板异常", e);
        }
    }


    /**
     * 删除行为模版
     *
     * @param templateId
     * @return
     */
    public void deleteBehaviorTemplate(Long templateId) {
        try {
            //删除配置绑定
            String sql = "delete from stat_bind_config where type=?1 and fid=?2";
            this.execSqlUpdate(sql, StatBussType.BEHAVIOR,templateId);

            //删除模版
            String sql4 = "delete from behavior_template where template_id=?1 ";
            this.execSqlUpdate(sql4, templateId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除行为模版异常", e);
        }
    }

    /**
     * 获取用户日历的执行流水日志,分页
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @param template
     * @param bindValues
     * @param page
     * @param pageSize
     * @return
     */
    public List<CalendarLogDTO> getCalendarLogList(Long userId, Date startDate, Date endDate, BehaviorTemplate template, String bindValues, int page, int pageSize) {
        try {
            StatSQLDTO sqlDTO = this.assembleSQL(template,userId,bindValues,startDate,endDate);
            String sqlContent = sqlDTO.getSqlContent();
            List<Object[]> rr = null;
            if (template.getSqlType() == SqlType.HQL) {
                rr = this.getEntityListHI(sqlContent,page,pageSize,Object[].class, sqlDTO.getArgArray());
            } else {
                rr = this.getEntityListSI(sqlContent,page,pageSize,Object[].class, sqlDTO.getArgArray());
            }
            List<CalendarLogDTO> res = new ArrayList<>();
            for (Object[] oo : rr) {
                int n = oo.length;
                CalendarLogDTO clr = new CalendarLogDTO();
                clr.setSourceId(Long.parseLong(oo[0].toString()));
                clr.setDate((Date) oo[1]);
                clr.setName(oo[2].toString());
                if(n>3){
                    clr.setValue(oo[3]==null? null : oo[3].toString());
                }
                if(n>4){
                    clr.setUnit(oo[4]==null? null: oo[4].toString());
                }
                if(n>5){
                    clr.setDays(oo[5]==null? 0: Integer.parseInt(oo[5].toString()));
                }
                if(n>6){
                    clr.setContent(oo[6]==null? null: oo[6].toString());
                }
                res.add(clr);
            }
            return res;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户日历的执行流水日志异常", e);
        }
    }


    /**
     * 计算封装SQL
     * @param template
     * @param userId
     * @param bindValues
     * @return
     */
    protected StatSQLDTO assembleSQL(BehaviorTemplate template,Long userId,String bindValues, Date startTime,Date endTime) {
        StatSQLDTO dto = new StatSQLDTO();
        dto.setSqlContent(template.getSqlContent());
        //肯定绑定userId
        dto.addArg(userId);
        //绑定时间
        dto.addArg(startTime);
        dto.addArg(endTime);
        return this.appendExtraBindSQL(template.getTemplateId(), StatBussType.BEHAVIOR,bindValues,template.getParas(),dto);
    }
}
