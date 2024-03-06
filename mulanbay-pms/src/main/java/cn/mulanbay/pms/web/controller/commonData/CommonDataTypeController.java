package cn.mulanbay.pms.web.controller.commonData;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.CommonDataType;
import cn.mulanbay.pms.persistent.enums.CommonStatus;
import cn.mulanbay.pms.persistent.service.CommonDataService;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.commonData.CommonDataTypeForm;
import cn.mulanbay.pms.web.bean.req.commonData.CommonDataTypeSH;
import cn.mulanbay.pms.web.bean.req.main.UserCommonFrom;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用记录类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/commonDataType")
public class CommonDataTypeController extends BaseController {

    private static Class<CommonDataType> beanClass = CommonDataType.class;

    @Autowired
    CommonDataService commonDataService;

    /**
     * 获取类型树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(CommonDataTypeSH sf) {
        try {
            sf.setStatus(CommonStatus.ENABLE);
            sf.setPage(PageRequest.NO_PAGE);
            PageRequest pr = sf.buildQuery();
            pr.setBeanClass(beanClass);
            Sort sort1 = new Sort("groupName", Sort.ASC);
            pr.addSort(sort1);
            Sort sort2 = new Sort("orderIndex", Sort.ASC);
            pr.addSort(sort2);
            List<CommonDataType> gtList = baseService.getBeanList(pr);
            List<TreeBean> result = new ArrayList<>();
            String current = gtList.get(0).getGroupName();
            TreeBean typeTreeBean = new TreeBean();
            typeTreeBean.setId("P_" + current);
            typeTreeBean.setText(current);
            int n = gtList.size();
            for (int i = 0; i < n; i++) {
                CommonDataType pc = gtList.get(i);
                TreeBean tb = new TreeBean();
                tb.setId(pc.getTypeId());
                tb.setText(pc.getTypeName());
                if (pc.getGroupName().equals(current)) {
                    typeTreeBean.addChild(tb);
                }
                if (!pc.getGroupName().equals(current)) {
                    current = pc.getGroupName();
                    result.add(typeTreeBean);
                    typeTreeBean = new TreeBean();
                    typeTreeBean.setId("P_" + current);
                    typeTreeBean.setText(current);
                    typeTreeBean.addChild(tb);
                }
                if (i == n - 1) {
                    //最后一个
                    result.add(typeTreeBean);
                }
            }
            return callback(TreeBeanUtil.addRoot(result, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "通用记录类型异常",
                    e);
        }
    }

    /**
     * 分组树
     *
     * @return
     */
    @RequestMapping(value = "/groupNameTree", method = RequestMethod.GET)
    public ResultBean groupNameTree(UserCommonFrom ucm) {
        List<String> gtList = commonDataService.getTypeGroupNameList(ucm.getUserId());
        List<TreeBean> list = new ArrayList<TreeBean>();
        for (String ng : gtList) {
            TreeBean tb = new TreeBean();
            tb.setId(ng);
            tb.setText(ng);
            list.add(tb);
        }
        return callback(list);
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(CommonDataTypeSH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        PageResult<CommonDataType> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid CommonDataTypeForm form) {
        CommonDataType bean = new CommonDataType();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "typeId") Long typeId) {
        CommonDataType bean = baseService.getObject(beanClass,typeId);
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid CommonDataTypeForm form) {
        CommonDataType bean = baseService.getObject(beanClass,form.getTypeId());
        BeanCopy.copy(form, bean);
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
        String[] ids = deleteRequest.getIds().split(",");
        for (String s : ids) {
            commonDataService.deleteCommonDataType(Long.valueOf(s));
        }
        return callback(null);
    }


}
