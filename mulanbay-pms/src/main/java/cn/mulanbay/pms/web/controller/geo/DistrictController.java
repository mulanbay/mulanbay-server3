package cn.mulanbay.pms.web.controller.geo;

import cn.mulanbay.pms.persistent.domain.District;
import cn.mulanbay.pms.persistent.service.GeoService;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 县
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/district")
public class DistrictController extends BaseController {

    @Autowired
    GeoService geoService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(@RequestParam(name = "cityId") Long cityId) {
        List<TreeBean> list = new ArrayList<TreeBean>();
        List<District> gtList = geoService.getDistrictList(cityId);
        for (District gt : gtList) {
            TreeBean tb = new TreeBean();
            tb.setId(gt.getDistrictId());
            tb.setText(gt.getDistrictName());
            list.add(tb);
        }
        return callback(list);
    }

}
