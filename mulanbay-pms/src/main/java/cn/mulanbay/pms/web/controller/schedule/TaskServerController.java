package cn.mulanbay.pms.web.controller.schedule;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.schedule.taskServer.TaskServerSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.schedule.domain.TaskServer;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 调度服务器
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/taskServer")
public class TaskServerController extends BaseController {

    private static Class<TaskServer> beanClass = TaskServer.class;

    /**
     * 获取服务器树
     * @return
     */
    @RequestMapping(value = "tree")
    public ResultBean tree() {
        try {
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<TaskServer> gtList = baseService.getBeanList(beanClass, null);
            for (TaskServer gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getDeployId());
                tb.setText(gt.getDeployId());
                list.add(tb);
            }
            return callback(list);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取服务器树异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     * @param sf
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(TaskServerSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s1 = new Sort("startTime", Sort.DESC);
        pr.addSort(s1);
        PageResult<TaskServer> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
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
