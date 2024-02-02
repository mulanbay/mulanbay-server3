package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.common.exception.CommonResult;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.handler.CommandHandler;
import cn.mulanbay.pms.persistent.domain.Command;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.system.command.CommandExeForm;
import cn.mulanbay.pms.web.bean.req.system.command.CommandForm;
import cn.mulanbay.pms.web.bean.req.system.command.CommandSH;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 命令配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/command")
public class CommandController extends BaseController {

    private static Class<Command> beanClass = Command.class;

    @Autowired
    CommandHandler commandHandler;

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(CommandSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        PageResult<Command> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 发送（不能同时操作同一个命令）
     *
     * @return
     */
    @RequestMapping(value = "/exe", method = RequestMethod.POST)
    public ResultBean exe(@RequestBody @Valid CommandExeForm csr) {
        CommonResult cr = commandHandler.handleCmdCode(csr.getCode(), csr.isSync(), csr.getUserId());
        if (cr.getCode() != ErrorCode.SUCCESS) {
            return callbackErrorCode(cr.getCode());
        } else {
            return callback(cr.getInfo());
        }
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid CommandForm formRequest) {
        Command bean = new Command();
        BeanCopy.copy(formRequest, bean, true);
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
        Command br = baseService.getObject(beanClass, id);
        return callback(br);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid CommandForm formRequest) {
        Command bean = baseService.getObject(beanClass, formRequest.getId());
        BeanCopy.copy(formRequest, bean, true);
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
        baseService.deleteObjects(beanClass, NumberUtil.stringArrayToLongArray(deleteRequest.getIds().split(",")));
        return callback(null);
    }

}
