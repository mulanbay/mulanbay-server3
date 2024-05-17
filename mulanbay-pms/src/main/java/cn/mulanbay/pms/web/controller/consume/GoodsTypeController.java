package cn.mulanbay.pms.web.controller.consume;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.GoodsType;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.consume.goodsType.GoodsTypeForm;
import cn.mulanbay.pms.web.bean.req.consume.goodsType.GoodsTypeSH;
import cn.mulanbay.pms.web.bean.req.consume.goodsType.GoodsTypeTreeSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.bean.res.consume.goodsType.GoodsTypeVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/goodsType")
public class GoodsTypeController extends BaseController {

    private static Class<GoodsType> beanClass = GoodsType.class;

    /**
     * @return
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public ResultBean tree(GoodsTypeTreeSH sh) {
        try {
            sh.setPage(PageRequest.NO_PAGE);
            PageRequest pr = sh.buildQuery();
            pr.setBeanClass(beanClass);
            Sort sort = new Sort("orderIndex", Sort.ASC);
            pr.addSort(sort);
            List<GoodsType> gtList = baseService.getBeanList(pr);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (GoodsType gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getTypeId());
                tb.setText(gt.getTypeName());
                tb.setPid(gt.getPid());
                list.add(tb);
            }
            TreeBean root = TreeBean.creatRoot();
            TreeBean result = TreeBeanUtil.changToTree(root, list);
            List<TreeBean> resultList = new ArrayList<TreeBean>();
            resultList.add(result);
            return callback(resultList);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取商品类型树异常",
                    e);
        }
    }

    /**
     * 获取商品类型树
     *
     * @return
     */
    @RequestMapping(value = "/treeList", method = RequestMethod.GET)
    public ResultBean treeList(GoodsTypeSH sf) {
        if(sf.getPid()==null){
            sf.setPid(0L);
        }
        sf.setPage(PageRequest.NO_PAGE);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("orderIndex", Sort.ASC);
        pr.addSort(sort);
        List<GoodsType> list = baseService.getBeanList(pr);
        List<GoodsTypeVo> voList = list.stream().map(gt -> {
            GoodsTypeVo vo = new GoodsTypeVo();
            BeanCopy.copy(gt,vo);
            return vo;
        }).collect(Collectors.toList());
        return callback(voList);
    }

    /**
     * 获取子列表
     * @param pid
     * @param list
     * @return
     */
    private List<GoodsTypeVo> getChildren(long pid,List<GoodsType> list){
        List<GoodsTypeVo> children = new ArrayList<>();
        for(GoodsType cc : list){
            long myPid = cc.getPid();
            if(myPid==pid){
                GoodsTypeVo child = new GoodsTypeVo();
                BeanCopy.copy(cc, child);
                child.setPid(cc.getPid());
                //child.setParentName();
                //寻找下一个子列表
                List<GoodsTypeVo> c2 = getChildren(cc.getTypeId(), list);
                child.setChildren(c2);
                //加入到结果集
                children.add(child);
            }
        }
        return children;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid GoodsTypeForm form) {
        GoodsType bean = new GoodsType();
        BeanCopy.copy(form, bean);
        if (bean.getBehaviorName() == null) {
            bean.setBehaviorName(bean.getTypeName());
        }
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "typeId") Long typeId) {
        GoodsType bean = baseService.getObject(beanClass,typeId);;
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid GoodsTypeForm form) {
        GoodsType bean = baseService.getObject(beanClass,form.getTypeId());
        BeanCopy.copy(form,bean);
        if (bean.getBehaviorName() == null) {
            bean.setBehaviorName(bean.getTypeName());
        }
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

}
