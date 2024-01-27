package cn.mulanbay.pms.web.controller.fund;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.pms.persistent.domain.AccountSnapshot;
import cn.mulanbay.pms.persistent.service.AccountService;
import cn.mulanbay.pms.util.TreeBeanUtil;
import cn.mulanbay.pms.web.bean.req.fund.accountSnapshot.AccountSnapshotDeleteForm;
import cn.mulanbay.pms.web.bean.req.fund.accountSnapshot.AccountSnapshotSH;
import cn.mulanbay.pms.web.bean.res.TreeBean;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 账户快照信息
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/accountSnapshot")
public class AccountSnapshotController extends BaseController {

    private static Class<AccountSnapshot> beanClass = AccountSnapshot.class;

    @Autowired
    AccountService accountService;

    /**
     * 获取快照树
     * @return
     */
    @RequestMapping(value = "/tree")
    public ResultBean tree(AccountSnapshotSH sf) {
        try {
            //分页，获取最新的20页
            PageRequest pr = sf.buildQuery();
            pr.setBeanClass(beanClass);
            Sort s = new Sort("createdTime", Sort.DESC);
            pr.addSort(s);
            List<AccountSnapshot> gtList = baseService.getBeanList(pr);
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (AccountSnapshot gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getSnapshotId());
                tb.setText(gt.getSnapshotName());
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取账户树异常",
                    e);
        }
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid AccountSnapshotDeleteForm bdr) {
        accountService.deleteSnapshot(bdr.getUserId(), bdr.getSnapshotId());
        return callback(null);
    }
}
