package cn.mulanbay.pms.web.controller.read;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.BookCategory;
import cn.mulanbay.pms.util.BeanCopy;
import cn.mulanbay.pms.web.bean.req.CommonDeleteForm;
import cn.mulanbay.pms.web.bean.req.main.UserCommonFrom;
import cn.mulanbay.pms.web.bean.req.read.category.BookCategoryForm;
import cn.mulanbay.pms.web.bean.req.read.category.BookCategorySH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 乐器管理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/bookCategory")
public class BookCategoryController extends BaseController {

    private static Class<BookCategory> beanClass = BookCategory.class;

    /**
     * 分类树
     *
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(UserCommonFrom scts) {
        try {
            BookCategorySH sf = new BookCategorySH();
            sf.setUserId(scts.getUserId());
            sf.setPage(PageRequest.NO_PAGE);
            PageResult<BookCategory> pr = getResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<BookCategory> gtList = pr.getBeanList();
            for (BookCategory gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getCateId());
                tb.setText(gt.getCateName());
                list.add(tb);
            }
            return callback(list);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取书籍分类树异常",
                    e);
        }
    }

    /**
     * 获取运列表数据
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(BookCategorySH sf) {
        PageResult<BookCategory> qr = getResult(sf);
        return callbackDataGrid(qr);
    }

    private PageResult<BookCategory> getResult(BookCategorySH sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort s = new Sort("orderIndex", Sort.ASC);
        pr.addSort(s);
        PageResult<BookCategory> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid BookCategoryForm form) {
        BookCategory bean = new BookCategory();
        BeanCopy.copy(form, bean);
        baseService.saveObject(bean);
        return callback(bean);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@RequestParam(name = "cateId") Long cateId) {
        BookCategory br = baseService.getObject(beanClass,cateId);
        return callback(br);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid BookCategoryForm form) {
        BookCategory bean = baseService.getObject(beanClass,form.getCateId());
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
        baseService.deleteObjects(beanClass, NumberUtil.stringToLongArray(deleteRequest.getIds()));
        return callback(null);
    }

}
