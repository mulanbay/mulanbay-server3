package cn.mulanbay.pms.web.controller.geo;

import cn.mulanbay.pms.persistent.domain.Country;
import cn.mulanbay.pms.persistent.domain.Province;
import cn.mulanbay.pms.persistent.service.GeoService;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 省份
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/province")
public class ProvinceController extends BaseController {

    @Autowired
    GeoService geoService;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree() {
        List<TreeBean> list = new ArrayList<TreeBean>();
        List<Province> gtList = geoService.getProvinceList();
        for (Province gt : gtList) {
            TreeBean tb = new TreeBean();
            tb.setId(gt.getProvinceId());
            tb.setText(gt.getProvinceName());
            list.add(tb);
        }
        return callback(list);
    }
}
