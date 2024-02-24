package cn.mulanbay.pms.web.controller;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.pms.persistent.domain.User;
import cn.mulanbay.pms.persistent.enums.AccountType;
import cn.mulanbay.pms.persistent.enums.BussType;
import cn.mulanbay.pms.persistent.service.GeoService;
import cn.mulanbay.pms.util.ClazzUtils;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.common.EnumDictSH;
import cn.mulanbay.pms.web.bean.req.common.YearListSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.schedule.domain.TaskTrigger;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 公共接口
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    private static List<TreeBean> domainClassList;

    private static List<TreeBean> enumClassList;

    @Autowired
    GeoService geoService;

    @PostConstruct
    public void init(){
        logger.info("init domainClassList");
        domainClassList = new ArrayList<>();
        String packageName1 = User.class.getPackage().getName();
        List<String> list = ClazzUtils.getClazzName(packageName1, false);
        String packageName2 = TaskTrigger.class.getPackage().getName();
        List<String> listSc = ClazzUtils.getClazzName(packageName2, false);
        list.addAll(listSc);
        Collections.sort(list);
        for (String s : list) {
            TreeBean treeBean = new TreeBean();
            int n = s.lastIndexOf(".");
            String className = s.substring(n + 1, s.length());
            treeBean.setId(className);
            treeBean.setText(className);
            domainClassList.add(treeBean);
        }

        logger.info("init enumClassList");
        enumClassList = new ArrayList<>();
        //根据指定的一个枚举类
        String enumPackageName1 = AccountType.class.getPackage().getName();
        List<String> enumList = ClazzUtils.getClazzName(enumPackageName1, false);
        Collections.sort(enumList);
        for (String s : enumList) {
            TreeBean treeBean = new TreeBean();
            int n = s.lastIndexOf(".");
            String className = s.substring(n + 1, s.length());
            treeBean.setId(className);
            treeBean.setText(className);
            enumClassList.add(treeBean);
        }
    }

    /**
     * 获取月统计年份列表
     *
     * @return
     */
    @RequestMapping(value = "/yearList")
    public ResultBean yearList(YearListSH sf) {
        try {
            User user = baseService.getObject(User.class, sf.getUserId());
            //最小年份由注册时间决定
            int minYear = Integer.parseInt(DateUtil.getFormatDate(user.getCreatedTime(), "yyyy"));
            int maxYear = Integer.parseInt(DateUtil.getFormatDate(new Date(), "yyyy"));
            //最大和最小年份之间最少间隔5个
            if(maxYear-minYear<4){
                minYear = maxYear-4;
            }
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (int i = maxYear; i >= minYear; i--) {
                TreeBean tb = new TreeBean();
                tb.setId(i);
                tb.setText(i + "年");
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取年份列表树异常",
                    e);
        }
    }

    /**
     * 业务类别列表
     *
     * @return
     */
    @RequestMapping(value = "/bussTypeList")
    public ResultBean bussTypeList(Boolean needRoot) {
        try {
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (BussType bt : BussType.values()) {
                TreeBean tb = new TreeBean();
                tb.setId(bt.getBeanClass().getSimpleName());
                tb.setText(bt.getName());
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, needRoot));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取业务类别列表异常",
                    e);
        }
    }

    /**
     * 获取枚举字典
     *
     * @return
     */
    @RequestMapping(value = "/enumDict", method = RequestMethod.GET)
    public ResultBean enumDict(EnumDictSH etr) {
        List<TreeBean> list = TreeBeanUtil.createTree(etr.getEnumClass(), etr.getIdType(), etr.getNeedRoot());
        return callback(list);
    }

    /**
     * 映射实体
     *
     * @return
     */
    @RequestMapping(value = "/enumClassList", method = RequestMethod.GET)
    public ResultBean enumClassList() {
        return callback(enumClassList);
    }

    /**
     * 映射实体
     *
     * @return
     */
    @RequestMapping(value = "/domainClassList", method = RequestMethod.GET)
    public ResultBean domainClassList() {
        return callback(domainClassList);
    }

    @RequestMapping(value = "/initGeo", method = RequestMethod.GET)
    public ResultBean initGeo() throws Exception{
//        FileInputStream file = new FileInputStream(new File("geo.xlsx"));
//        Workbook workbook = WorkbookFactory.create(file);
//        Sheet sheet = workbook.getSheet("adcode_lng_lat");
//        Iterator<Row> rowIterator = sheet.iterator();
//        while (rowIterator.hasNext()) {
//            Row row = rowIterator.next();
//            Cell id = row.getCell(0);
//            Cell code = row.getCell(1);
//            String s_code = String.valueOf((int)code.getNumericCellValue());
//            Cell name = row.getCell(2);
//            String s_name = name.getStringCellValue();
//            Cell longitude = row.getCell(3);
//            Cell latitude = row.getCell(4);
//            String location = longitude.getNumericCellValue()+","+latitude.getNumericCellValue();
//            System.out.println(s_name+" "+s_code+" "+location);
//            geoService.updateGeo(s_name,s_code,location);
//        }
        return callback(null);
    }
}
