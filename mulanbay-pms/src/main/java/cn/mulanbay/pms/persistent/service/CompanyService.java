package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.pms.persistent.domain.Company;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CompanyService extends BaseHibernateDao {

    /**
     * 获取公司列表
     *
     * @param year
     * @return
     */
    public List<Company> selectCompanyList(int year, Long userId) {
        try {
            Date beginDate = DateUtil.getDate(year + "-01-01", DateUtil.FormatDay1);
            Date endDate = DateUtil.getDate(year + "-12-31", DateUtil.FormatDay1);

            String hql = "from Company where userId=?1 and entryDate<=?2 and quitDate>=?3  ";
            return this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Company.class, userId, endDate, beginDate);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取公司列表异常", e);
        }
    }
}
