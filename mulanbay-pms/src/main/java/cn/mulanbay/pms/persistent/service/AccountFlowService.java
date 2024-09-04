package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.dto.fund.AccountFlowSnapshotStat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 账户流水
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Service
@Transactional
public class AccountFlowService extends BaseHibernateDao {

    /**
     * 获取账户流水的快照统计，统计每个时间的总额
     *
     * @param userId
     * @return
     */
    public List<AccountFlowSnapshotStat> statSnapshot(Long userId, Date startTime, Date endTime) {
        try {
            StringBuffer sb = new StringBuffer();
            List args = new ArrayList();
            sb.append("select asi.buss_key as bussKey,sum(af.before_amount) as beforeAmount,sum(af.after_amount) as afterAmount from account_flow af,account_snapshot asi ");
            sb.append("where af.user_id =?1 and af.snapshot_id is not null and af.snapshot_id= asi.snapshot_id ");
            args.add(userId);
            int index = 2;
            if (startTime != null) {
                sb.append("and af.created_time>=?" + (index++) + " ");
                args.add(startTime);
            }
            if (endTime != null) {
                sb.append("and af.created_time<=?" + (index++) + " ");
                args.add(endTime);
            }
            sb.append("group by asi.buss_key order by asi.buss_key ");
            List<AccountFlowSnapshotStat> list = this.getEntityListSI(sb.toString(), NO_PAGE,NO_PAGE_SIZE,
                    AccountFlowSnapshotStat.class, args.toArray());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取账户流水的快照统计异常", e);
        }
    }

    /**
     * 统计账户值
     * @param bussKey
     * @param userId
     * @return
     */
    public BigDecimal statAccountAmount(String bussKey, Long userId) {
        try {
            String sql = "select sum(after_amount) from account_flow where snapshot_id =(select snapshot_id from account_snapshot where user_id =?1 and buss_key =?2 ) and status=1 ";
            List<BigDecimal> list = this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,BigDecimal.class, userId, bussKey);
            if (list.isEmpty() || list.get(0) == null) {
                return null;
            } else {
                return list.get(0);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "统计账户值异常", e);
        }
    }
}
