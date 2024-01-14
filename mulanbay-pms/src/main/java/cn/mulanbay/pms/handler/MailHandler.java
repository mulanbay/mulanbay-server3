package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 邮件发送处理
 *
 * @link <a href="https://blog.csdn.net/CSDN2497242041/article/details/115113213">参考</a>
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class MailHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(MailHandler.class);

    @Value("${spring.mail.from}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 简单文本邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    public boolean sendMail(String to, String subject, String content) {
        try {
            //创建SimpleMailMessage对象
            SimpleMailMessage message = new SimpleMailMessage();
            //邮件发送人
            message.setFrom(from);
            //邮件接收人
            message.setTo(to);
            //邮件主题
            message.setSubject(subject);
            //邮件内容
            message.setText(content);
            //发送邮件
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件异常",e);
            return false;
        }
    }

}
