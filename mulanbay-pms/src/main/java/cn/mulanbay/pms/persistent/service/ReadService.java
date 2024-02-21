package cn.mulanbay.pms.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.pms.persistent.domain.Book;
import cn.mulanbay.pms.persistent.domain.BookCategory;
import cn.mulanbay.pms.persistent.domain.Country;
import cn.mulanbay.pms.persistent.domain.ReadDetail;
import cn.mulanbay.pms.persistent.dto.read.*;
import cn.mulanbay.pms.persistent.enums.*;
import cn.mulanbay.pms.persistent.util.MysqlUtil;
import cn.mulanbay.pms.web.bean.req.read.book.BookAnalyseStatSH;
import cn.mulanbay.pms.web.bean.req.read.book.BookDateStatSH;
import cn.mulanbay.pms.web.bean.req.read.book.BookOverallStatSH;
import cn.mulanbay.pms.web.bean.req.read.book.BookStatSH;
import cn.mulanbay.pms.web.bean.req.read.readDetail.ReadDetailDateStatSH;
import cn.mulanbay.pms.web.bean.req.read.readDetail.ReadDetailOverallStatSH;
import cn.mulanbay.pms.web.bean.req.read.readDetail.ReadDetailSH;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReadService extends BaseHibernateDao {

    /**
     * 阅读统计
     *
     * @param sf
     * @return
     */
    public BookStat getBookStat(BookStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select count(0) as totalCount,sum(cost_days) as totalCostDays
                    from book
                    {query_para}
                    and cost_days is not null
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<BookStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, BookStat.class, pr.getParameterValue());
            return list.get(0);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "阅读统计异常", e);
        }
    }

    /**
     * 阅读统计
     *
     * @param sf
     * @return
     */
    public List<BookDateStat> getBookDateStat(BookDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,count(0) as totalCount from (
                    select {date_group_field} as indexValue from book
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("finish_date", dateGroupType))
                             .replace("{query_para}",pr.getParameterString());
            List<BookDateStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, BookDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "阅读统计异常", e);
        }
    }

    /**
     * 阅读时间统计
     *
     * @param sf
     * @return
     */
    public List<ReadDetailDateStat> getReadDateStat(ReadDetailDateStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,count(0) as totalCount,sum(duration) as totalDuration from (
                    select {date_group_field} as indexValue,duration from read_detail
                    {query_para}
                    ) tt group by indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("read_time", dateGroupType))
                             .replace("{query_para}",pr.getParameterString());
            List<ReadDetailDateStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, ReadDetailDateStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "阅读时间统计异常", e);
        }
    }

    /**
     * 根据阅读的完成天数统计（结束时间--开始时间）
     *
     * @param sf
     * @return
     */
    public List<BigInteger> getBookAnalyseDayStat(BookAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            String statSql = """
                    select datediff(finish_date,begin_date) as days from book
                    where begin_date is not null and finish_date is not null
                    {query_para}
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<BigInteger> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, BigInteger.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "根据阅读的完成天数统计异常", e);
        }

    }

    /**
     * 根据阅读的阅读时间统计（结束时间--开始时间）
     *
     * @param sf
     * @return
     */
    public List<BigDecimal> getBookAnalyseTimeStat(BookAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            String statSql = """
                    select duration from (
                    select book_id,sum(duration) as duration from read_detail
                    where book_id in
                    ( select book_id from book
                    {query_para}
                    ) group by book_id ) as res
                    """;
            statSql = statSql.replace("{query_para}",pr.getParameterString());
            List<BigDecimal> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, BigDecimal.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "根据阅读的阅读时间统计异常", e);
        }

    }

    /**
     * 总阅读时间（分钟）
     *
     * @param bookId
     * @return
     */
    public BigDecimal getCostTimes(Long bookId) {
        try {
            String sql = """
                    select sum(duration) as duration from read_detail where book_id =?1
                    """;
            BigDecimal dd = this.getEntitySQL(sql,BigDecimal.class,bookId);
            return dd;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "总阅读时间异常", e);
        }

    }


    /**
     * 获取阅读分析
     *
     * @param sf
     * @return
     */
    public List<BookAnalyseStat> getBookAnalyseStat(BookAnalyseStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            BookAnalyseStatSH.GroupType groupType = sf.getGroupType();
            String statSql = """
                    select {field_name},count(*) cc from book
                    {query_para}
                    group by {field_name}
                    """;
            statSql = statSql.replace("{field_name}",groupType.getFieldName())
                             .replace("{query_para}",pr.getParameterString());
            List<Object[]> list = this.getEntityListSI(statSql,NO_PAGE,NO_PAGE_SIZE,Object[].class, pr.getParameterValue());
            List<BookAnalyseStat> result = new ArrayList<BookAnalyseStat>();
            for (Object[] oo : list) {
                BookAnalyseStat bb = new BookAnalyseStat();
                Object nameFiled = oo[0];
                if (nameFiled == null) {
                    bb.setName("未知");
                } else {
                    Object serierIdObj = oo[0];
                    if (serierIdObj == null) {
                        //防止为NULL
                        serierIdObj = "0";
                    }
                    String name = getSeriesName(serierIdObj.toString(), groupType);
                    bb.setName(name);
                }
                double value = Double.parseDouble(oo[1].toString());
                bb.setValue(value);
                result.add(bb);
            }
            return result;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取阅读分析异常", e);
        }
    }

    private String getSeriesName(String idStr, BookAnalyseStatSH.GroupType groupType) {
        try {
            if (groupType == BookAnalyseStatSH.GroupType.BOOK_CATEGORY) {
                BookCategory bookCategory = this.getEntityById(BookCategory.class, Long.valueOf(idStr));
                return bookCategory.getCateName();
            } else if (groupType == BookAnalyseStatSH.GroupType.SCORE) {
                return idStr;
            } else if (groupType == BookAnalyseStatSH.GroupType.BOOK_TYPE) {
                BookType bookType = BookType.getBookType(Integer.parseInt(idStr));
                return bookType == null ? idStr : bookType.getName();
            } else if (groupType == BookAnalyseStatSH.GroupType.LANGUAGE) {
                BookLanguage language = BookLanguage.getLanguage(Integer.parseInt(idStr));
                return language == null ? idStr : language.getName();
            } else if (groupType == BookAnalyseStatSH.GroupType.STATUS) {
                BookStatus bookStatus = BookStatus.getReadingStatus(Integer.parseInt(idStr));
                return bookStatus == null ? idStr : bookStatus.getName();
            } else if (groupType == BookAnalyseStatSH.GroupType.SOURCE) {
                BookSource source = BookSource.getBookSource(Integer.parseInt(idStr));
                return source == null ? idStr : source.getName();
            }else if (groupType == BookAnalyseStatSH.GroupType.COUNTRY) {
                Country c = this.getEntityById(Country.class,Long.parseLong(idStr));
                return c == null ? idStr : c.getCnName();
            } else {
                return idStr;
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取阅读分析分类名称异常", e);
        }
    }

    /**
     * 设置阅读状态
     * @param bookId
     * @param date
     * @param status
     */
    public void updateBookStatus(Long bookId, Date date,BookStatus status) {
        try {
            //无论是否已经读完，都更新为在读,应该加一个状态是否改变的判断
            String hql = "update Book set status=?1,modifyTime=?2 where bookId=?3 ";
            this.updateEntities(hql, status, date, bookId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "设置阅读状态异常", e);
        }
    }

    /**
     * 更新或者新增阅读明细
     *
     * @param bean
     * @param update
     */
    public void saveOrUpdateRead(ReadDetail bean, boolean update) {
        try {
            if (update) {
                this.updateEntity(bean);
            } else {
                this.saveEntity(bean);
            }
            this.updateBookStatus(bean.getBook().getBookId(), bean.getReadTime(),BookStatus.READING);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新或者新增阅读明细异常", e);
        }
    }

    /**
     * 删除图书
     *
     * @param bookId
     */
    public void deleteBook(Long bookId) {
        try {
            String sql = "delete from read_detail where book_id=?1 ";
            this.execSqlUpdate(sql, bookId);

            String sql2 = "delete from book where book_id=?1 ";
            this.execSqlUpdate(sql2, bookId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除阅读记录异常", e);
        }
    }

    /**
     * 获取总阅读时间
     *
     * @param startDate
     */
    public ReadDetailSummaryStat getReadSummaryStat(Date startDate, Date endDate, Long userId) {
        try {
            String sql = "select count(*) as totalCount,sum(duration) as totalDuration from read_detail where user_id=?1 and read_time>=?2 and read_time<=?3 ";
            ReadDetailSummaryStat stat = this.getEntitySQL(sql, ReadDetailSummaryStat.class, userId, startDate, endDate);
            return stat;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取总阅读时间异常", e);
        }
    }

    /**
     * 查询阅读记录
     */
    public Book getBook(String isbn, Long userId, Long bookId) {
        try {
            String hql = "from Book where isbn=?1 and userId=?2 ";
            if (bookId != null) {
                hql += " and bookId!=" + bookId;
            }
            Book rr = this.getEntity(hql,Book.class, isbn, userId);
            return rr;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "根据bussKey查询预算日志异常", e);
        }
    }

    /**
     * 查询阅读的最早与最晚时间
     *
     * @param bookId
     * @return
     */
    public ReadDetailTimeStat getReadTimeStat(Long bookId) {
        try {
            String sql = "select min(read_time) as minDate,max(read_time) as maxDate from read_detail where bookId=?1 ";
            ReadDetailTimeStat stat = this.getEntitySQL(sql, ReadDetailTimeStat.class, bookId);
            return stat;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "查询阅读的最早与最晚时间异常", e);
        }
    }

    /**
     * 图书类型列表
     *
     * @param userId
     */
    public List<BookCategory> getBookCategoryList(Long userId) {
        try {
            String hql = "from BookCategory where userId=?1 order by orderIndex desc";
            List<BookCategory> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,BookCategory.class, userId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取图书类型列表异常", e);
        }
    }

    /**
     * 获取按图书类型的统计
     *
     * @param sf
     * @return
     */
    public List<BookOverallStat> getBookOverallStat(BookOverallStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,count(0) as totalCount,cate_id as cateId from (
                    select cate_id,{date_group_field} as indexValue from book
                    where status={status}
                    {query_para}
                    ) as res group by cate_id,indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("finish_date", dateGroupType))
                             .replace("{status}",String.valueOf(BookStatus.READED.ordinal()))
                             .replace("{query_para}",pr.getParameterString());
            List<BookOverallStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, BookOverallStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取按图书类型的统计异常", e);
        }
    }

    /**
     * 获取按图书类型阅读明细的统计
     *
     * @param sf
     * @return
     */
    public List<ReadDetailOverallStat> getReadOverallStat(ReadDetailOverallStatSH sf) {
        try {
            PageRequest pr = sf.buildQuery();
            pr.setNeedWhere(false);
            DateGroupType dateGroupType = sf.getDateGroupType();
            String statSql = """
                    select indexValue,sum(duration) as totalDuration,cate_id as cateId from (
                    select rd.cate_id,rrd.duration,{date_group_field} as indexValue
                    from read_detail rrd,book rd
                    where rrd.book_id=rd.book_id
                    {query_para}
                    ) as res group by cate_id,indexValue order by indexValue
                    """;
            statSql = statSql.replace("{date_group_field}",MysqlUtil.dateTypeMethod("rrd.read_time", dateGroupType))
                    .replace("{query_para}",pr.getParameterString());
            List<ReadDetailOverallStat> list = this.getEntityListSI(statSql, NO_PAGE,NO_PAGE_SIZE, ReadDetailOverallStat.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取按图书类型阅读明细的统计异常", e);
        }
    }

    /**
     * 获取时间列表
     * @param sf
     * @return
     */
    public List<Date> getReadDateList(ReadDetailSH sf) {
        try {
            String hql = """
                    select readTime from ReadDetail
                    {query_para}
                     order by readTime
                    """;
            PageRequest pr = sf.buildQuery();
            hql = hql.replace("{query_para}",pr.getParameterString());
            List<Date> list = this.getEntityListHI(hql,NO_PAGE,NO_PAGE_SIZE,Date.class, pr.getParameterValue());
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取时间列表异常", e);
        }
    }

}
