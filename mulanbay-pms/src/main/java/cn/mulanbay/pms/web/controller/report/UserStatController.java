package cn.mulanbay.pms.web.controller.report;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.UserStatHandler;
import cn.mulanbay.pms.persistent.domain.StatTemplate;
import cn.mulanbay.pms.persistent.domain.UserStat;
import cn.mulanbay.pms.persistent.dto.report.StatResultDTO;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.service.StatService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.report.stat.UserStatDeleteCacheForm;
import cn.mulanbay.pms.web.bean.req.report.stat.UserStatForm;
import cn.mulanbay.pms.web.bean.req.report.stat.UserStatSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static cn.mulanbay.persistent.dao.BaseHibernateDao.NO_PAGE;

/**
 * 用户提醒数据
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/userStat")
public class UserStatController extends BaseController {

    private static Class<UserStat> beanClass = UserStat.class;

    @Autowired
    StatService statService;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    UserStatHandler userStatHandler;

    /**
     * 获取用户提醒列表树
     *
     * @return
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public ResultBean tree(UserStatSH sf) {
        try {
            sf.setPage(NO_PAGE);
            PageRequest pr = sf.buildQuery();
            pr.setBeanClass(beanClass);
            Sort s = new Sort("template.bussType", Sort.ASC);
            pr.addSort(s);
            List<UserStat> unList = baseService.getBeanList(pr);
            List<TreeBean> result = new ArrayList<>();
            BussType current = unList.get(0).getTemplate().getBussType();
            TreeBean typeTreeBean = new TreeBean();
            typeTreeBean.setId(current.name());
            typeTreeBean.setText(current.getName());
            int n = unList.size();
            for (int i = 0; i < n; i++) {
                UserStat pc = unList.get(i);
                TreeBean tb = new TreeBean();
                tb.setId(pc.getStatId());
                tb.setText(pc.getTitle());
                BussType m = pc.getTemplate().getBussType();
                if (m == current) {
                    typeTreeBean.addChild(tb);
                }else{
                    current = m;
                    result.add(typeTreeBean);
                    typeTreeBean = new TreeBean();
                    typeTreeBean.setId(current.name());
                    typeTreeBean.setText(current.getName());
                    typeTreeBean.addChild(tb);
                }
                if (i == n - 1) {
                    //最后一个
                    result.add(typeTreeBean);
                }
            }
            return callback(result);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取用户提醒列表树异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(UserStatSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        PageResult<UserStat> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid UserStatForm form) {
        UserStat bean = new UserStat();
        BeanCopy.copy(form, bean);
        StatTemplate template = statService.getStatTemplate(form.getTemplateId(), form.getLevel());
        if (template == null) {
            return callbackErrorCode(PmsCode.USER_ENTITY_NOT_ALLOWED);
        }
        bean.setTemplate(template);
        statService.saveOrUpdateUserStat(bean);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "statId") Long statId) {
        UserStat bean = baseService.getObject(beanClass,statId);
        return callback(bean);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@RequestParam(name = "statId") Long statId) {
        UserStat bean = baseService.getObject(beanClass,statId);
        StatResultDTO dto = statService.getStatResult(bean);
        return callback(dto);
    }


    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/statList", method = RequestMethod.GET)
    public ResultBean statList(UserStatSH sf) {
        PageRequest pr = sf.buildQuery();
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        pr.setBeanClass(beanClass);
        PageResult<UserStat> unResult = baseService.getBeanResult(pr);
        List<StatResultDTO> list = new ArrayList<>();
        for (UserStat un : unResult.getBeanList()) {
            StatResultDTO nr = userStatHandler.getStatResult(un);
            list.add(nr);
        }
        PageResult<StatResultDTO> res = new PageResult<>(sf.getPage(), sf.getPageSize());
        res.setBeanList(list);
        res.setMaxRow(unResult.getMaxRow());
        return callbackDataGrid(res);
    }


    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid UserStatForm form) {
        UserStat bean = baseService.getObject(beanClass,form.getStatId());
        BeanCopy.copy(form, bean);
        StatTemplate template = statService.getStatTemplate(form.getTemplateId(), form.getLevel());
        if (template == null) {
            return callbackErrorCode(PmsCode.USER_ENTITY_NOT_ALLOWED);
        }
        bean.setTemplate(template);
        statService.saveOrUpdateUserStat(bean);
        return callback(bean);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        String[] ss = deleteRequest.getIds().split(",");
        for(String s :ss){
            statService.deleteUserStat(Long.valueOf(s));
        }
        return callback(null);
    }

    /**
     * 清除缓存
     *
     * @return
     */
    @RequestMapping(value = "/deleteStatCache", method = RequestMethod.POST)
    public ResultBean deleteStatCache(UserStatDeleteCacheForm ucf) {
        Long statId = ucf.getStatId();
        if(statId==null){
            userStatHandler.deleteCache(ucf.getUserId());
        }else{
            userStatHandler.deleteCache(ucf.getUserId(),statId);
        }
        return callback(null);
    }

}
