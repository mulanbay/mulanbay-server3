package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.common.exception.CommonResult;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.web.bean.req.system.command.CommandSH;
import cn.mulanbay.pms.web.bean.req.system.command.CommandExeForm;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.pms.persistent.domain.Command;
import cn.mulanbay.pms.handler.CommandHandler;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

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


}
