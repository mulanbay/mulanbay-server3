package cn.mulanbay.pms.web.controller.music;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.MusicPractice;
import cn.mulanbay.pms.persistent.domain.MusicPracticeDetail;
import cn.mulanbay.pms.persistent.dto.music.MusicPracticeTuneLevelStat;
import cn.mulanbay.pms.persistent.dto.music.MusicPracticeTuneNameStat;
import cn.mulanbay.pms.persistent.dto.music.MusicPracticeTuneStat;
import cn.mulanbay.pms.persistent.service.MusicPracticeService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.music.musicPracticeDetail.MusicPracticeDetailForm;
import cn.mulanbay.pms.web.bean.req.music.musicPracticeDetail.MusicPracticeDetailSH;
import cn.mulanbay.pms.web.bean.req.music.musicPracticeDetail.MusicPracticeDetailTreeSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 练习曲子记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/musicPracticeDetail")
public class MusicPracticeDetailController extends BaseController {

    private static Class<MusicPracticeDetail> beanClass = MusicPracticeDetail.class;

    @Autowired
    MusicPracticeService musicPracticeService;

    /**
     * 获取曲子列表树
     *
     * @return
     */
    @RequestMapping(value = "/tuneTree")
    public ResultBean tuneTree(MusicPracticeDetailTreeSH sf) {
        try {
            List<String> tuneList = musicPracticeService.getTuneList(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            int i = 0;
            for (String ss : tuneList) {
                TreeBean tb = new TreeBean();
                tb.setId(ss);
                tb.setText(ss);
                list.add(tb);
                i++;
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取曲子列表树异常",
                    e);
        }
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public ResultBean list(MusicPracticeDetailSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("createdTime", Sort.DESC);
        pr.addSort(s);
        PageResult<MusicPracticeDetail> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid MusicPracticeDetailForm form) {
        MusicPracticeDetail bean = new MusicPracticeDetail();
        BeanCopy.copy(form, bean);
        MusicPractice musicPractice = baseService.getObject(MusicPractice.class,form.getPracticeId());
        bean.setPractice(musicPractice);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "detailId") Long detailId) {
        MusicPracticeDetail bean = baseService.getObject(beanClass, detailId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid MusicPracticeDetailForm form) {
        MusicPracticeDetail bean = baseService.getObject(beanClass, form.getDetailId());
        BeanCopy.copy(form, bean);
        MusicPractice musicPractice = baseService.getObject(MusicPractice.class,form.getPracticeId());
        bean.setPractice(musicPractice);
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
        baseService.deleteObjects(beanClass, NumberUtil.stringToLongArray(deleteRequest.getIds()));
        return callback(null);
    }

    /**
     * 统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(MusicPracticeDetailSH sf) {
        List<MusicPracticeTuneStat> list = musicPracticeService.getTuneStat(sf);
        ChartData chartData = new ChartData();
        chartData.setTitle(musicPracticeService.getInstrumentName(sf.getInstrumentId()) + "练习曲子统计");
        chartData.setUnit("次");
        chartData.setLegendData(new String[]{"次数"});
        ChartYData yData1 = new ChartYData("次数","次");
        for (MusicPracticeTuneStat bean : list) {
            chartData.getXdata().add(bean.getName());
            yData1.getData().add(bean.getTotalTimes());
        }
        chartData.getYdata().add(yData1);
        return callback(chartData);
    }

    /**
     * 曲子名称统计
     *
     * @return
     */
    @RequestMapping(value = "/nameStat", method = RequestMethod.GET)
    public ResultBean nameStat(MusicPracticeDetailSH sf) {
        MusicPracticeDetail bean = baseService.getObject(beanClass, sf.getDetailId());
        MusicPracticeTuneNameStat stat = musicPracticeService.getTuneNameStat(sf.getUserId(), bean.getTune(), bean.getPractice().getInstrument().getInstrumentId(), sf.getAllMi());
        return callback(stat);
    }

    /**
     * 曲子水平统计
     *
     * @return
     */
    @RequestMapping(value = "/levelStat", method = RequestMethod.GET)
    public ResultBean levelStat(MusicPracticeDetailSH sf) {
        MusicPracticeDetail bean = baseService.getObject(beanClass, sf.getDetailId());
        List<MusicPracticeTuneLevelStat> list = musicPracticeService.getTuneLevelStat(sf.getUserId(), bean.getTune(), bean.getPractice().getInstrument().getInstrumentId());
        return callback(list);
    }
}
