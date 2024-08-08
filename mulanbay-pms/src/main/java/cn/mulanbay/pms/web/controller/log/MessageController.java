package cn.mulanbay.pms.web.controller.log;

import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.common.PmsCode;
import cn.mulanbay.pms.handler.MessageSendHandler;
import cn.mulanbay.pms.handler.NotifyHandler;
import cn.mulanbay.pms.handler.WXHandler;
import cn.mulanbay.pms.persistent.domain.Message;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.domain.UserSet;
import cn.mulanbay.pms.persistent.enums.LogLevel;
import cn.mulanbay.pms.persistent.service.AuthService;
import cn.mulanbay.pms.persistent.service.MessageService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.log.message.MessageForm;
import cn.mulanbay.pms.web.bean.req.log.message.MessageGetByUserReq;
import cn.mulanbay.pms.web.bean.req.log.message.MessageMySH;
import cn.mulanbay.pms.web.bean.req.log.message.MessageSH;
import cn.mulanbay.pms.web.bean.res.log.message.MessageDetailVo;
import cn.mulanbay.pms.web.bean.res.log.message.MessageUserVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户消息
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/message")
public class MessageController extends BaseController {

    private static Class<Message> beanClass = Message.class;

    @Autowired
    AuthService authService;

    @Autowired
    MessageService messageService;

    @Autowired
    MessageSendHandler messageSendHandler;

    @Autowired
    NotifyHandler notifyHandler;

    @Autowired
    WXHandler wxHandler;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(MessageSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<Message> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 移动端个人中心使用
     *
     * @return
     */
    @RequestMapping(value = "/myList", method = RequestMethod.GET)
    public ResultBean myList(MessageMySH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<Message> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 获取详情（管理员使用）
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "msgId") Long msgId) {
        Message bean = baseService.getObject(beanClass, msgId);
        return callback(bean);
    }

    /**
     * 获取详情（管理员使用）
     *
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBean detail(@RequestParam(name = "msgId") Long msgId) {
        MessageDetailVo vo = new MessageDetailVo();
        Message bean = baseService.getObject(beanClass, msgId);
        vo.setMessage(bean);
        MessageUserVo userVo = new MessageUserVo();
        User user = baseService.getObject(User.class,bean.getUserId());
        BeanCopy.copy(user,userVo);
        UserSet us = baseService.getObject(UserSet.class,bean.getUserId());
        BeanCopy.copy(us,userVo);
        vo.setUser(userVo);
        return callback(vo);
    }

    /**
     * 获取详情（个人使用）
     *
     * @return
     */
    @RequestMapping(value = "/getByUser", method = RequestMethod.GET)
    public ResultBean getByUser(@Valid MessageGetByUserReq req) {
        Message bean = messageService.getMessage(req.getUserId(),req.getMsgId());
        return callback(bean);
    }

    /**
     * 重新发送
     *
     * @return
     */
    @RequestMapping(value = "/resend", method = RequestMethod.GET)
    public ResultBean resend(@RequestParam(name = "msgId") Long msgId) {
        Message bean = baseService.getObject(beanClass, msgId);
        messageSendHandler.sendMessage(bean);
        return callback(bean);
    }

    /**
     * 发送
     *
     * @return
     */
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResultBean send(@RequestBody @Valid MessageForm um) {
        User user = authService.getUserByUsernameOrPhone(um.getUsername());
        if (user == null) {
            return callbackErrorInfo("未找到相关用户");
        } else {
            Integer code = um.getCode();
            if (code == null) {
                //直接发送
                boolean b = wxHandler.sendTemplateMessage(null,user.getUserId(), um.getTitle(), um.getContent(), um.getNotifyTime(), LogLevel.NORMAL, null);
                return callback(b);
            } else {
                if (code == 0) {
                    code = PmsCode.MESSAGE_NOTIFY_COMMON_CODE;
                }
                notifyHandler.addMessage(code, um.getTitle(), um.getContent(),
                        user.getUserId(), um.getNotifyTime());
                return callback(null);
            }
        }
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonDeleteForm deleteRequest) {
        String[] ids = deleteRequest.getIds().split(",");
        for (String s : ids) {
            Integer code =  Integer.parseInt(s);
            baseService.deleteObject(beanClass,code);
        }
        return callback(null);
    }


}
