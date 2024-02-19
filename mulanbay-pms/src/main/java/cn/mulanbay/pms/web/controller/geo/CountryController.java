package cn.mulanbay.pms.web.controller.geo;

import cn.mulanbay.pms.persistent.domain.Country;
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
 * 国家
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/country")
public class CountryController extends BaseController {

    @Autowired
    GeoService geoService;

    /**
     * 分类树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree() {
        List<TreeBean> list = new ArrayList<TreeBean>();
        List<Country> gtList = geoService.getCountryList();
        for (Country gt : gtList) {
            TreeBean tb = new TreeBean();
            tb.setId(gt.getCountryId());
            tb.setText(gt.getCnName());
            list.add(tb);
        }
        return callback(list);
    }

}
