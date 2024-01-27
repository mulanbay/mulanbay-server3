package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.persistent.domain.Budget;
import cn.mulanbay.pms.persistent.enums.BudgetLogSource;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.enums.GoodsConsumeType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.ConsumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 预算处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class BudgetHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(BudgetHandler.class);

    //年的时间格式化
    public static final String YEARLY_DATE_FORMAT = "yyyy";

    //月的时间格式化
    public static final String MONTHLY_DATE_FORMAT = "yyyyMM";

    @Autowired
    ConsumeService consumeService;

    public String createBussKey(PeriodType period, Date date) {
        String dateFormat = DateUtil.Format24Datetime2;
        if (period == PeriodType.YEARLY) {
            dateFormat = "yyyy";
        } else if (period == PeriodType.MONTHLY) {
            dateFormat = "yyyyMM";
        } else if (period == PeriodType.DAILY) {
            dateFormat = "yyyyMMdd";
        } else if (period == PeriodType.WEEKLY) {
            dateFormat = "yyyy";
        } else if (period == PeriodType.QUARTERLY) {
            dateFormat = "yyyyMM";
        }
        return DateUtil.getFormatDate(date, dateFormat);
    }

}
