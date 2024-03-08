package cn.mulanbay.pms.web.controller.behavior;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.handler.DataHandler;
import cn.mulanbay.pms.handler.bean.data.CommonDataBean;
import cn.mulanbay.pms.persistent.domain.BehaviorTemplate;
import cn.mulanbay.pms.persistent.dto.calendar.CalendarLogDTO;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.service.BehaviorService;
import cn.mulanbay.pms.web.bean.req.behavior.BehaviorTemplateSH;
import cn.mulanbay.pms.web.bean.req.behavior.UserBehaviorCalendarSH;
import cn.mulanbay.pms.web.bean.res.behavior.BehaviorCalendarVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.mulanbay.persistent.dao.BaseHibernateDao.NO_PAGE;


/**
 * @author fenghong
 * @date 2024/3/6
 */
@RestController
@RequestMapping("/userBehavior")
public class UserBehaviorController  extends BaseController {

    @Autowired
    BehaviorService behaviorService;

    @Autowired
    DataHandler dataHandler;

    /**
     * 日历列表
     *
     * @return
     */
    @RequestMapping(value = "/calendarList")
    public ResultBean calendarList(UserBehaviorCalendarSH sf) {
        BehaviorTemplateSH btsh = new BehaviorTemplateSH();
        btsh.setBussType(sf.getBussType());
        btsh.setStatus(CommonStatus.ENABLE);
        btsh.setPage(NO_PAGE);
        PageRequest pr = btsh.buildQuery();
        pr.setBeanClass(BehaviorTemplate.class);
        List<BehaviorCalendarVo> res = new ArrayList<>();
        List<BehaviorTemplate> templateList = baseService.getBeanList(pr);
        for(BehaviorTemplate template: templateList){
            List<CalendarLogDTO> dtoList = behaviorService.getCalendarLogList(sf.getUserId(),sf.getStartDate(),sf.getEndDate(),template,null,sf.getPage(),sf.getPageSize());
            for(CalendarLogDTO dto : dtoList){
                BehaviorCalendarVo bean = new BehaviorCalendarVo();
                bean.setSourceId(dto.getSourceId());
                bean.setBussType(template.getBussType());
                bean.setSource(template.getSource());
                bean.setId(StringUtil.genUUID());
                bean.setTitle(dto.getName());
                bean.setContent(dto.getContent());
                bean.setUnit(dto.getUnit());
                bean.setValue(dto.getValue());
                bean.setBussDay(dto.getDate());
                bean.setAllDay(template.getAllDay());
                int days = dto.getDays();
                if(days>1){
                    Date ex = DateUtil.getDate(days-1,dto.getDate());
                    bean.setExpireTime(ex);
                }
                res.add(bean);
            }
        }
        return callback(res);
    }

    /**
     * 查找源
     *
     * @return
     */
    @RequestMapping(value = "/sourceDetail", method = RequestMethod.GET)
    public ResultBean sourceDetail(@RequestParam(name = "sourceId") Long sourceId,@RequestParam(name = "source") BussSource source) {
        CommonDataBean bean = dataHandler.getSourceData(source,sourceId);
        return callback(bean);
    }

}
