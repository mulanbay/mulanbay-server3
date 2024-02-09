package cn.mulanbay.pms.web.bean.res.health;

import cn.mulanbay.pms.persistent.domain.TreatDrug;
import cn.mulanbay.pms.util.BeanCopy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/3/5
 */
public class TreatDrugCalendarVo {

    private String drugName;

    private int startHour;

    private int endHour;

    public TreatDrugCalendarVo() {
    }

    public TreatDrugCalendarVo(String drugName, int startHour, int endHour) {
        this.drugName = drugName;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    /**
     * 用药列表
     */
    private List<TreatDrugCalendarDetailVo> detailList = new ArrayList<>();

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public List<TreatDrugCalendarDetailVo> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<TreatDrugCalendarDetailVo> detailList) {
        this.detailList = detailList;
    }

    private TreatDrugCalendarDetailVo getDetailVo(Long drugId){
        for(TreatDrugCalendarDetailVo vo : detailList){
            if(vo.getDrugId().longValue()==drugId.longValue()){
                return vo;
            }
        }
        return null;
    }

    /**
     * 新增记录
     * @param td
     */
    public void addTreatDrug(TreatDrug td){
        TreatDrugCalendarDetailVo vo = new TreatDrugCalendarDetailVo();
        BeanCopy.copy(td,vo);
        vo.setDrugId(td.getDrugId());
        detailList.add(vo);
    }

    /**
     * 添加明细
     * @param detailId
     * @param occurTime
     */
    public boolean appendDetail(Long detailId, Date occurTime,Long drugId ){
        TreatDrugCalendarDetailVo vo = this.getDetailVo(drugId);
        if(vo!=null){
            vo.setOccurTime(occurTime);
            vo.setDetailId(detailId);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 添加明细
     * @param detailId
     * @param occurTime
     */
    public void addDetail(TreatDrug td,Long detailId, Date occurTime,Long drugId ){
        TreatDrugCalendarDetailVo vo = new TreatDrugCalendarDetailVo();
        BeanCopy.copy(td,vo);
        vo.setDrugId(drugId);
        vo.setOccurTime(occurTime);
        vo.setDetailId(detailId);
        vo.setMatch(false);
        detailList.add(vo);
    }
}
