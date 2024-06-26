package cn.mulanbay.pms.web.controller;

import cn.hutool.core.codec.Base64;
import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.web.bean.res.main.CaptchaImageVo;
import cn.mulanbay.web.bean.response.ResultBean;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * 验证码操作处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private CacheHandler cacheHandler;

    // 验证码类型
    @Value("${mulanbay.security.captcha.type}")
    private String captchaType;

    @Value("${mulanbay.security.captcha.expires:300}")
    private int expires;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public ResultBean captchaImage() {
        // 保存验证码信息
        String uuid = StringUtil.genUUID();
        String verifyKey = CacheKey.getKey(CacheKey.CAPTCHA_CODE, uuid);
        String capStr = null, code = null;
        BufferedImage image = null;

        // 生成验证码
        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }
        cacheHandler.set(verifyKey, code, expires);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (Exception e) {
            logger.error("生成验证码异常", e);
            return callbackErrorInfo("生成验证码异常");
        }
        CaptchaImageVo res = new CaptchaImageVo();
        res.setUuid(uuid);
        res.setImg(Base64.encode(os.toByteArray()));
        return callback(res);
    }
}
