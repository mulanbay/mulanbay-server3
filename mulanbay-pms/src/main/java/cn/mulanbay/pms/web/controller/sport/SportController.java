package cn.mulanbay.pms.web.controller.sport;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Sport;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.sport.sport.SportForm;
import cn.mulanbay.pms.web.bean.req.sport.sport.SportSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 运动类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/sport")
public class SportController extends BaseController {

    private static Class<Sport> beanClass = Sport.class;

    /**
     * 为锻炼管理界面的下拉菜单使用
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(SportSH sf) {

        try {
            sf.setStatus(CommonStatus.ENABLE);
            sf.setPage(PageRequest.NO_PAGE);
            PageResult<Sport> pr = getResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<Sport> gtList = pr.getBeanList();
            for (Sport gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getSportId());
                tb.setText(gt.getSportName());
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取运动类型树异常",
                    e);
        }
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(SportSH sf) {
        return callbackDataGrid(getResult(sf));
    }

    private PageResult<Sport> getResult(SportSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        PageResult<Sport> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid SportForm form) {
        Sport bean = new Sport();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "sportId") Long sportId) {
        Sport bean = baseService.getObject(beanClass,sportId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid SportForm form) {
        Sport bean = baseService.getObject(beanClass,form.getSportId());
        BeanCopy.copyProperties(form, bean);
        baseService.updateObject(bean);
        return callback(bean);
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
}
