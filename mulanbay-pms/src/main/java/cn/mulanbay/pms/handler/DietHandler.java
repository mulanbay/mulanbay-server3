package cn.mulanbay.pms.handler;

import cn.mulanbay.ai.ml.processor.DietPriceEvaluateMProcessor;
import cn.mulanbay.ai.nlp.processor.NLPProcessor;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.persistent.dto.food.DietPriceAnalyseTypeStat;
import cn.mulanbay.pms.persistent.dto.food.DietTypeFoodStat;
import cn.mulanbay.pms.persistent.enums.DietSource;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.DietService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.food.diet.DietCateSH;
import cn.mulanbay.pms.web.bean.req.food.diet.DietPriceAnalyseSH;
import cn.mulanbay.pms.web.bean.req.food.diet.DietStatField;
import cn.mulanbay.pms.web.bean.req.food.diet.DietVarietySH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 饮食处理器
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class DietHandler extends BaseHandler {

    @Autowired
    DietService dietService;

    @Autowired
    NLPProcessor nlpProcessor;

    @Autowired
    DietPriceEvaluateMProcessor dietPriceEvaluateMProcessor;

    public DietHandler() {
        super("饮食处理器");
    }

    /**
     * 获取食物平均相似度
     *
     * @param sf
     * @return
     */
    public float getFoodsAvgSim(DietVarietySH sf) {
        List<String> foodsList = this.getFoodsList(sf);
        return nlpProcessor.avgSentenceSimilarity(foodsList);
    }

    private List<String> getFoodsList(DietVarietySH sf) {
        if (sf.getDietType() != null) {
            DietCateSH dcsh = new DietCateSH();
            BeanCopy.copy(sf,dcsh);
            dcsh.setFieldName("foods");
            return dietService.getDietCateList(dcsh);
        } else {
            List<DietTypeFoodStat> list = dietService.getDietTypeFoodStat(sf);
            List<String> res = new ArrayList<>();
            for (DietTypeFoodStat ss : list) {
                String foods = ss.getBreakfast()+","+ss.getLunch()+","+ss.getDinner()+","+ss.getOther();
                res.add(foods);
            }
            return res;
        }
    }

    /**
     * 预测消费价格
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public double predictPrice(Long userId,int month, Date startDate, Date endDate, PeriodType period){
        DietPriceAnalyseSH sf = new DietPriceAnalyseSH();
        sf.setUserId(userId);
        sf.setStartDate(startDate);
        sf.setEndDate(DateUtil.tillMiddleNight(endDate));
        sf.setStatField(DietStatField.DIET_SOURCE);
        List<DietPriceAnalyseTypeStat> list = dietService.getDietPriceAnalyseTypeStat(sf);
        //计算总价
        BigDecimal totalPrice = new BigDecimal(0);
        for(DietPriceAnalyseTypeStat dp:list){
            totalPrice = totalPrice.add(dp.getTotalPrice());
        }
        double smRate =0;
        double rtRate=0;
        double toRate=0;
        double mkRate=0;
        double otRate=0;
        for(DietPriceAnalyseTypeStat dp:list){
            String name = dp.getName();
            double rate = (dp.getTotalPrice().divide(totalPrice,BigDecimal.ROUND_HALF_UP)).doubleValue();
            if(name.equals(DietSource.SELF_MADE.getName())){
                smRate = rate;
            }else if(name.equals(DietSource.RESTAURANT.getName())){
                rtRate = rate;
            }else if(name.equals(DietSource.TAKE_OUT.getName())){
                toRate = rate;
            }else if(name.equals(DietSource.MARKET.getName())){
                mkRate = rate;
            }else if(name.equals(DietSource.OTHER.getName())){
                otRate = rate;
            }
        }
        if(period==PeriodType.MONTHLY){
            return dietPriceEvaluateMProcessor.evaluate(month,smRate,rtRate,toRate,mkRate,otRate);
        }
        return 0;
    }

}
