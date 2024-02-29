package cn.mulanbay.pms.handler;

import cn.mulanbay.ai.ml.processor.PlanReportMEvaluateProcessor;
import cn.mulanbay.ai.ml.processor.PlanReportYEvaluateProcessor;
import cn.mulanbay.ai.ml.processor.bean.PlanReportER;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.handler.bean.consume.report.PlanReportPredictBean;
import cn.mulanbay.pms.persistent.domain.PlanReport;
import cn.mulanbay.pms.persistent.domain.PlanTemplate;
import cn.mulanbay.pms.persistent.enums.PlanType;
import cn.mulanbay.pms.util.BeanCopy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 报表处理类
 *
 * @author fenghong
 * @create 2023-08-09
 */
@Component
public class ReportHandler extends BaseHandler {

    @Autowired
    PlanReportMEvaluateProcessor mEvaluateProcessor;

    @Autowired
    PlanReportYEvaluateProcessor yEvaluateProcessor;

    @Autowired
    UserScoreHandler userScoreHandler;

    public ReportHandler() {
        super("报表处理类");
    }

    /**
     * 月度预测
     * @param userId
     * @param planConfigId
     * @param month
     * @param score
     * @param dayIndex
     * @return
     */
    public PlanReportER predictMonthRate(Long userId, long planConfigId, int month, Integer score, int dayIndex){
        if(score==null){
            score = userScoreHandler.getLatestScore(userId);
        }
        PlanReportER pm = mEvaluateProcessor.evaluateMulti(planConfigId,month,score,dayIndex);
        return pm;
    }

    /**
     * 年度预测
     * @param userId
     * @param planConfigId
     * @param score
     * @param dayIndex
     * @return
     */
    public PlanReportER predictYearRate(Long userId,long planConfigId,Integer score,int dayIndex){
        if(score==null){
            score = userScoreHandler.getLatestScore(userId);
        }
        PlanReportER pm = yEvaluateProcessor.evaluateMulti(planConfigId,score,dayIndex);
        return pm;
    }

    /**
     * 预测及设置计划报告
     * @param re
     * @return
     */
    public PlanReportPredictBean predictAndSetPlanReport(PlanReport  re){
        PlanReportPredictBean vo = new PlanReportPredictBean();
        BeanCopy.copy(re,vo);
        PlanReportER pv = this.predictPlanReport(re);
        if(pv!=null){
            vo.setPredictCount(pv.getCountRate()*re.getPlanCountValue());
            vo.setPredictValue(pv.getValueRate()*re.getPlanValue());
        }
        return vo;
    }

    /**
     * 预测计划报告
     * @param re
     * @return 返回的比例值
     */
    public PlanReportER predictPlanReport(PlanReport  re){
        PlanTemplate template = re.getPlan().getTemplate();
        PlanType planType = re.getPlan().getPlanType();
        Date bussDay = re.getBussDay();
        long userId = re.getUserId();
        int score = userScoreHandler.getScore(userId,bussDay);
        int month = DateUtil.getMonth(bussDay)+1;
        //预测
        PlanReportER pv = null;
        if(planType==PlanType.YEAR){
            int dayIndex = DateUtil.getDayOfYear(bussDay);
            pv = this.predictYearRate(userId,template.getTemplateId(),score,dayIndex);
        }else{
            int dayIndex = DateUtil.getDayOfMonth(bussDay);
            pv = this.predictMonthRate(userId,template.getTemplateId(),month,score,dayIndex);
        }
        return pv;
    }
}
