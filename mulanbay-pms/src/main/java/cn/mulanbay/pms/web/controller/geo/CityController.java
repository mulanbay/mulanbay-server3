package cn.mulanbay.pms.web.controller.geo;

import cn.mulanbay.pms.persistent.domain.City;
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
 * 城市管理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/city")
public class CityController extends BaseController {

    @Autowired
    GeoService geoService;

    /**
     * 获取城市列表
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(@RequestParam(name = "provinceId") Long provinceId) {
        List<TreeBean> list = new ArrayList<TreeBean>();
        List<City> gtList = geoService.getCityList(provinceId);
        for (City gt : gtList) {
            TreeBean tb = new TreeBean();
            tb.setId(gt.getCityId());
            tb.setText(gt.getCityName());
            list.add(tb);
        }
        return callback(list);
    }

}
