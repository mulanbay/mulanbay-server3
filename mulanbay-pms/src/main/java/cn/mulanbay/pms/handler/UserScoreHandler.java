package cn.mulanbay.pms.handler;

import cn.mulanbay.ai.ml.processor.UserScoreEvaluateProcessor;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.persistent.domain.ScoreConfig;
import cn.mulanbay.pms.persistent.domain.UserScore;
import cn.mulanbay.pms.persistent.domain.UserScoreDetail;
import cn.mulanbay.pms.persistent.domain.UserSet;
import cn.mulanbay.pms.persistent.enums.CompareType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.UserScoreService;
import cn.mulanbay.pms.web.bean.req.score.UserScoreSH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

import static cn.mulanbay.persistent.query.PageRequest.NO_PAGE;
import static cn.mulanbay.pms.common.Constant.DEFAULT_SCORE_GROUP_ID;
import static cn.mulanbay.pms.common.Constant.SCALE;

/**
 * @author fenghong
 * @title: UserScoreHandler
 * @description: 用户评分管理
 * 评分和积分的不同：
 * 1. 评分是个百分制，主要评价各个方面的综合值
 * 2. 积分是个累计值，是各项活动的累计分，分数越大代表越好
 * @date 2019-09-09 17:15
 */
@Component
public class UserScoreHandler extends BaseHandler {

    /**
     * 用户默认评分
     */
    @Value("${mulanbay.score.default:60}")
    private int defaultUserScore;

    @Value("${mulanbay.score.statDays:30}")
    private int statDays;

    /**
     * 最新评分的缓存时间
     */
    @Value("${mulanbay.score.cacheTime:300}")
    private int scoreCacheTime;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    AuthService authService;

    @Autowired
    UserScoreService userScoreService;

    @Autowired
    BaseService baseService;

    @Autowired
    UserScoreEvaluateProcessor userScoreEvaluateProcessor;

    public UserScoreHandler() {
        super("用户评分");
    }

    public List<UserScoreDetail> calcUseScore(Long userId, Date bussDay) {
        Date[] dates = this.getDays(bussDay);
        return this.calcUseScore(userId, dates[0], dates[1]);
    }

    public List<UserScoreDetail> calcUseScore(Long userId, Long scoreGroupId, Date bussDay) {
        Date[] dates = this.getDays(bussDay);
        return this.calcUseScore(userId, scoreGroupId, dates[0], dates[1]);
    }

    public Date[] getDays(Date bussDay) {
        Date endTime = DateUtil.tillMiddleNight(bussDay);
        Date startTime = DateUtil.getDate(- statDays, bussDay);
        return new Date[]{startTime, endTime};
    }

    public List<UserScoreDetail> calcUseScore(Long userId, Date startTime, Date endTime) {
        /**
         * todo 对于key，后期可以根据年龄或者不同阶段来设置的值，即可以分不同的组别
         * 比如：不同的年龄阶段可以用不同的评分标准
         */
        UserSet us = baseService.getObject(UserSet.class,userId);
        Long scoreGroupId = us.getScoreGroupId();
        if (scoreGroupId==null) {
            scoreGroupId = DEFAULT_SCORE_GROUP_ID;
        }
        return this.calcUseScore(userId, scoreGroupId, startTime, endTime);
    }

    /**
     * 计算评分
     *
     * @param userId
     * @param scoreGroupId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<UserScoreDetail> calcUseScore(Long userId, Long scoreGroupId, Date startTime, Date endTime) {
        List<ScoreConfig> scList = userScoreService.selectActiveScoreConfigList(scoreGroupId);
        List<UserScoreDetail> list = new ArrayList<>();
        for (ScoreConfig sc : scList) {
            double vv = userScoreService.getScoreValue(sc.getSqlContent(), userId, startTime, endTime);
            vv = NumberUtil.getValue(vv / statDays, SCALE);
            //要除以天数，其实算的是每天的值
            int score = this.getScore(sc, vv);
            UserScoreDetail bean = new UserScoreDetail();
            bean.setScoreConfig(sc);
            bean.setStatValue(vv);
            bean.setScore(score);
            bean.setUserId(userId);
            list.add(bean);
        }
        return list;
    }

    /**
     * 保存用户评分
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @param redo
     */
    public void saveUseScore(Long userId, Date startTime, Date endTime, boolean redo) {
        List<UserScoreDetail> list = this.calcUseScore(userId, startTime, endTime);
        int totalScore = 0;
        for (UserScoreDetail usd : list) {
            totalScore += usd.getScore();
        }
        UserScore us = new UserScore();
        us.setUserId(userId);
        us.setStartTime(startTime);
        us.setEndTime(endTime);
        us.setScore(totalScore);
        userScoreService.saveUserScore(us, list, redo);
    }

    /**
     * 异步保存用户评分
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @param redo
     */
    @Async
    public void saveUseScoreAsync(Long userId, Date startTime, Date endTime, boolean redo) {
        this.saveUseScore(userId, startTime, endTime, redo);
    }

    private int getScore(ScoreConfig sc, double vv) {
        double limitValue = sc.getLimitValue();
        if (sc.getCompareType() == CompareType.MORE) {
            if (vv <= 0) {
                //0为0分
                return 0;
            } else if (vv >= limitValue) {
                //超过极限值为满分
                return sc.getMaxScore();
            } else {
                return (int) (vv / limitValue * sc.getMaxScore());
            }
        } else {
            if (vv <= 0) {
                //0为满分
                return sc.getMaxScore();
            } else if (vv >= limitValue) {
                //超过极限值为0分
                return 0;
            } else {
                return (int) ((limitValue - vv) / limitValue * sc.getMaxScore());
            }
        }
    }

    /**
     * 获取用户最新的评分
     * @param userId
     * @return
     */
    public int getLatestScore(Long userId){
        String key = CacheKey.getKey(CacheKey.USER_LATEST_SCORE,userId.toString());
        Integer s = cacheHandler.get(key,Integer.class);
        if(s==null){
            s = this.getLatestScoreR(userId);
            cacheHandler.set(key,s,scoreCacheTime);
        }
        return s;
    }

    private int getLatestScoreR(Long userId){
        UserScore us = userScoreService.getLatestScore(userId);
        if(us==null){
            return defaultUserScore;
        }else {
            return us.getScore();
        }
    }

    /**
     * 获取用户最新的评分
     * @param userId
     * @return
     */
    public int getScore(Long userId,Date date){
        UserScore us = userScoreService.getScore(userId,date);
        if(us==null){
            return this.getLatestScore(userId);
        }else {
            return us.getScore();
        }
    }

    /**
     * 预测
     * 该模型并不是很合理
     * @param preScore 上一次的评分
     * @return
     */
    public int predict(Long userId,int preScore){
        Double b = userScoreEvaluateProcessor.evaluate(preScore);
        if(b==null){
            return -1;
        }else{
            return b.intValue();
        }
    }

    /**
     * 获取score
     * @param scoreMap
     * @param index
     * @return
     */
    public Integer getScore(Map<String, Integer> scoreMap,int index){
        Integer score = scoreMap.get(this.createIndexKey(index));
        if(score==null){
            //取默认的最后一天的
            score = scoreMap.get(this.createIndexKey(0));
        }
        return score==null ? defaultUserScore : score;
    }

    /**
     * 用户评分
     * @param userId
     * @param startDate
     * @param endDate
     * @param period
     * @return
     */
    public Map<String, Integer> getUserScoreMap(Long userId, Date startDate, Date endDate, PeriodType period){
        UserScoreSH sf = new UserScoreSH();
        sf.setUserId(userId);
        sf.setStartDate(startDate);
        sf.setEndDate(DateUtil.tillMiddleNight(endDate));
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(UserScore.class);
        pr.setPage(NO_PAGE);
        List<UserScore> list = baseService.getBeanList(pr);
        int n = list.size();
        Map<String, Integer> map = new HashMap<>();
        for(int i=0;i<n;i++){
            UserScore us = list.get(i);
            int dayIndex = 0;
            if (PeriodType.YEARLY == period) {
                dayIndex = DateUtil.getDayOfYear(us.getStartTime());
            }else{
                dayIndex = DateUtil.getDayOfMonth(us.getStartTime());
            }
            map.put(this.createIndexKey(dayIndex),us.getScore());
        }
        //设置最后一个为默认值
        if(n>0){
            map.put(this.createIndexKey(0),list.get(n-1).getScore());
        }
        return map;
    }

    /**
     * key规则
     * @param index
     * @return
     */
    private String createIndexKey(int index){
        return index+"";
    }
}
