package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.handler.bean.data.CommonDataBean;
import cn.mulanbay.pms.persistent.domain.*;
import cn.mulanbay.pms.persistent.enums.BussSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fenghong
 * @date 2024/3/4
 */
@Component
public class DataHandler extends BaseHandler {

    @Autowired
    BaseService baseService;

    public DataHandler() {
        super("数据处理器");
    }

    public CommonDataBean getSourceData(BussSource source, Long sourceId){
        CommonDataBean bean = new CommonDataBean();
        bean.setBussName(source.getName());
        if(source== BussSource.MANUAL){
            bean.setTitle("手动来源");
            bean.setContent("sourceId="+sourceId);
        }
        Object o = baseService.getObject(source.getBeanClass(),sourceId);
        if(o==null){
            bean.setTitle("未找到数据");
            bean.setContent("sourceId="+sourceId);
            return bean;
        }
        bean.setOriginData(o);
        switch (source){
            case STAT -> this.setUserStatData(bean, (UserStat) o);
            case PLAN -> this.setUserPlanData(bean, (UserPlan) o);
            case COMMON_DATA -> this.setCommonDataData(bean, (CommonData) o);
            case BUDGET -> this.setBudgetData(bean, (Budget) o);
            case TREAT_OPERATION -> this.setTreatOperationData(bean, (TreatOperation) o);
            case TREAT_DRUG -> this.setTreatDrugData(bean, (TreatDrug) o);
            case CONSUME -> this.setConsumeData(bean, (Consume) o);
            case BUDGET_LOG -> this.setBudgetLogData(bean, (BudgetLog) o);
            case DREAM -> this.setDreamData(bean, (Dream) o);
            case OPERATION -> this.setOperationData(bean, (OperLog) o);
            case EXERCISE -> this.setExerciseData(bean, (Exercise) o);
            case EXPERIENCE -> this.setExperienceData(bean, (Experience) o);
            case NOTE -> this.setNoteData(bean, (Note) o);
        }
        return bean;
    }

    private void setUserStatData(CommonDataBean bean, UserStat us){
        bean.setTitle(us.getTitle());
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setUserPlanData(CommonDataBean bean, UserPlan us){
        bean.setTitle(us.getTitle());
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setCommonDataData(CommonDataBean bean, CommonData us){
        bean.setTitle(us.getDataName());
        bean.setContent("类型:"+us.getType().getTypeName());
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setBudgetData(CommonDataBean bean, Budget us){
        bean.setTitle(us.getBudgetName());
        bean.setContent("周期:"+us.getPeriodName());
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setTreatOperationData(CommonDataBean bean, TreatOperation us){
        bean.setTitle(us.getOperationName());
        bean.setContent("复查时间:"+ DateUtil.getFormatDate(us.getReviewDate(),DateUtil.Format24Datetime));
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setTreatDrugData(CommonDataBean bean, TreatDrug us){
        bean.setTitle(us.getDrugName());
        bean.setContent("用药周期:每"+ us.getPerDay()+"天"+us.getPerTimes()+"次");
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setConsumeData(CommonDataBean bean, Consume us){
        bean.setTitle(us.getGoodsName());
        bean.setContent("购买时间:"+DateUtil.getFormatDate(us.getBuyTime(),DateUtil.Format24Datetime));
        bean.setBussDay(us.getBuyTime());
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setBudgetLogData(CommonDataBean bean, BudgetLog us){
        bean.setTitle(us.getBudget().getBudgetName());
        bean.setContent("预算金额:"+ NumberUtil.getValue(us.getBudgetAmount(),2)+"元");
        bean.setBussDay(us.getBussDay());
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setDreamData(CommonDataBean bean, Dream us){
        bean.setTitle(us.getDreamName());
        bean.setContent("期望完成时间:"+ DateUtil.getFormatDate(us.getExpectDate(),DateUtil.Format24Datetime));
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setOperationData(CommonDataBean bean, OperLog us){
        bean.setTitle(us.getSysFunc().getFuncName());
        bean.setContent("操作时间:"+ DateUtil.getFormatDate(us.getOccurStartTime(),DateUtil.Format24Datetime));
        bean.setBussDay(us.getOccurStartTime());
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setExerciseData(CommonDataBean bean, Exercise us){
        bean.setTitle(us.getSport().getSportName());
        bean.setContent("锻炼内容:"+ us.getDuration()+us.getSport().getUnit());
        bean.setBussDay(us.getExerciseTime());
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    private void setExperienceData(CommonDataBean bean, Experience us){
        bean.setTitle(us.getExpName());
        bean.setContent("类型:"+ us.getType().getName()+",天数:"+us.getDays());
        bean.setBussDay(us.getStartDate());
        bean.setCreatedTime(us.getCreatedTime());
        bean.setModifyTime(us.getModifyTime());
    }

    /**
     * 便签
     * @param bean
     * @param note
     */
    private void setNoteData(CommonDataBean bean, Note note){
        bean.setTitle(note.getTitle());
        bean.setContent(note.getContent());
        bean.setBussDay(note.getNotifyDate());
        bean.setCreatedTime(note.getCreatedTime());
        bean.setModifyTime(note.getModifyTime());
    }
}
