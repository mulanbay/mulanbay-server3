package cn.mulanbay.pms.web.controller.config;

import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.persistent.domain.LevelConfig;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.domain.UserReward;
import cn.mulanbay.pms.persistent.domain.UserScore;
import cn.mulanbay.pms.persistent.service.LevelService;
import cn.mulanbay.pms.persistent.service.UserRewardService;
import cn.mulanbay.pms.persistent.service.UserScoreService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.config.levelConfig.JudgeLevelForm;
import cn.mulanbay.pms.web.bean.req.config.levelConfig.LevelConfigForm;
import cn.mulanbay.pms.web.bean.req.config.levelConfig.LevelConfigSH;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户等级
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/levelConfig")
public class LevelConfigController extends BaseController {

    private static Class<LevelConfig> beanClass = LevelConfig.class;

    @Value("${mulanbay.user.level.judgeDays}")
    int judgeDays;

    @Autowired
    UserRewardService userRewardService;

    @Autowired
    LevelService levelService;

    @Autowired
    UserScoreService userScoreService;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(LevelConfigSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("level", Sort.DESC);
        pr.addSort(sort);
        PageResult<LevelConfig> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid LevelConfigForm form) {
        LevelConfig bean = new LevelConfig();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "id") Long id) {
        LevelConfig bean = baseService.getObject(beanClass, id);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid LevelConfigForm form) {
        LevelConfig bean = baseService.getObject(beanClass, form.getId());
        BeanCopy.copy(form, bean);
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        baseService.deleteObjects(beanClass, NumberUtil.stringToLongArray(deleteRequest.getIds()));
        return callback(null);
    }

    /**
     * 用户自评
     *
     * @return
     */
    @RequestMapping(value = "/selfJudge", method = RequestMethod.POST)
    public ResultBean selfJudge(@RequestBody @Valid JudgeLevelForm jlr) {
        User user = baseService.getObject(User.class, jlr.getUserId());
        return this.judge(user, jlr.getUpdateLevel());
    }

    /**
     * 判定
     *
     * @param user
     * @param updateLevel
     * @return
     */
    private ResultBean judge(User user, boolean updateLevel) {
        UserScore us = userScoreService.getLatestScore(user.getUserId());
        if (us == null) {
            return callbackErrorCode(PmsCode.USER_SCORE_NULL);
        }
        LevelConfig preLevel = levelService.getPreJudgeLevel(us.getScore(), user.getPoints());
        if (preLevel == null) {
            return callbackErrorCode(PmsCode.USER_PRE_SCORE_NULL);
        }
        LevelConfig current = levelService.getLevelConfig(user.getLevel());
        LevelConfig lc = this.matchLevel(preLevel.getLevel(), user.getUserId());
        if (lc != null && true == updateLevel && current.getLevel().intValue() != lc.getLevel().intValue()) {
            user.setLevel(lc.getLevel());
            baseService.updateObject(user);
        }
        Map res = new HashMap<>();
        res.put("newLevel", lc);
        res.put("currentLevel", current);
        return callback(res);
    }

    private LevelConfig matchLevel(int maxLevel, Long userId) {
        List<UserScore> usList = userScoreService.getList(userId, judgeDays);
        List<UserReward> urList = userRewardService.getUserRewardList(userId, judgeDays);
        for (int i = maxLevel; i >= 1; i--) {
            LevelConfig lc = levelService.getLevelConfig(i);
            boolean scoreMatch = true;
            //没有足够数据直接判定为false
            if (lc.getScoreDays() <= usList.size()) {
                for (int j = 0; j < lc.getScoreDays(); j++) {
                    UserScore s = usList.get(j);
                    if (s.getScore() < lc.getScore()) {
                        scoreMatch = false;
                        break;
                    }
                }
            } else {
                scoreMatch = false;
            }

            boolean pointsMatch = true;
            //没有足够数据直接判定为false
            if (lc.getPointsDays() <= urList.size()) {
                for (int j = 0; j < lc.getPointsDays(); j++) {
                    UserReward r = urList.get(j);
                    if (r.getAfterPoints() < lc.getPoints()) {
                        pointsMatch = false;
                        break;
                    }
                }
            } else {
                pointsMatch = false;
            }
            if (scoreMatch && pointsMatch) {
                //两个都匹配
                return lc;
            }
        }
        return null;
    }
}
