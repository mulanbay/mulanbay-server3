package cn.mulanbay.pms.web.controller.life;

import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.Note;
import cn.mulanbay.pms.persistent.domain.UserCalendar;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.enums.PeriodType;
import cn.mulanbay.pms.persistent.service.UserCalendarService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.BussUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.life.note.NoteForm;
import cn.mulanbay.pms.web.bean.req.life.note.NoteSH;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 便签
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/note")
public class NoteController extends BaseController {

    private static Class<Note> beanClass = Note.class;

    @Value("${mulanbay.note.expireDays:7}")
    int expireDays;

    @Autowired
    UserCalendarService userCalendarService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(NoteSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        if(StringUtil.isNotEmpty(sf.getSortField())){
            Sort sort = new Sort(sf.getSortField(), sf.getSortType());
            pr.addSort(sort);
        }
        PageResult<Note> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid NoteForm form) {
        Note bean = new Note();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        this.syncCalendar(bean);
        return callback(null);
    }

    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "noteId") Long noteId) {
        Note bean = baseService.getObject(beanClass,noteId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid NoteForm form) {
        Note bean = baseService.getObject(beanClass,form.getNoteId());
        BeanCopy.copy(form, bean);
        baseService.updateObject(bean);
        this.syncCalendar(bean);
        return callback(null);
    }

    /**
     * 同步日历
     *
     * @param note
     */
    private void syncCalendar(Note note){
        BussSource sourceType = BussSource.NOTE;
        UserCalendar calendar = userCalendarService.getUserCalendar(note.getUserId(),sourceType ,note.getNoteId());
        if(note.getNotifyDate()==null){
            if(calendar!=null){
                baseService.deleteObject(calendar);
            }
        }else{
            if(calendar==null){
                calendar = new UserCalendar();
                calendar.setUserId(note.getUserId());
                calendar.setTitle(StringUtil.isEmpty(note.getTitle())? "用户便签":note.getTitle());
                calendar.setContent(note.getContent());
                calendar.setDelays(0);
                calendar.setBussDay(note.getNotifyDate());
                calendar.setExpireTime(DateUtil.getDate(expireDays,note.getNotifyDate()));
                String bussIdentityKey = BussUtil.getCalendarBussIdentityKey(sourceType,note.getNoteId().toString());
                calendar.setBussIdentityKey(bussIdentityKey);
                calendar.setSourceType(sourceType);
                calendar.setSourceId(note.getNoteId());
                calendar.setAllDay(false);
                calendar.setPeriod(PeriodType.ONCE);
                baseService.saveObject(calendar);
            }else{
                if(!DateUtil.isSame(note.getNotifyDate(),calendar.getBussDay())){
                    calendar.setTitle(StringUtil.isEmpty(note.getTitle())? "用户便签":note.getTitle());
                    calendar.setContent(note.getContent());
                    calendar.setBussDay(note.getNotifyDate());
                    calendar.setExpireTime(DateUtil.getDate(expireDays,note.getNotifyDate()));
                    baseService.updateObject(calendar);
                }
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
        baseService.deleteObjects(beanClass, NumberUtil.stringToLongArray(deleteRequest.getIds()));
        return callback(null);
    }

}
