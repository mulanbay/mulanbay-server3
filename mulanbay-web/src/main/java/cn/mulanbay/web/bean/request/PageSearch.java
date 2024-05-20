package cn.mulanbay.web.bean.request;

import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.QueryBuilder;

/**
 * 查询列表通用基类
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class PageSearch extends QueryBuilder {

	/**
	 * 页码
	 */
	protected Integer page = 0;

	/**
	 * 单页显示条数
	 */
	protected Integer pageSize = BaseHibernateDao.DEFAULT_PAGE_SIZE;

	/**
	 * 是否需要总页数，移动端可以不需要这个
	 */
	protected boolean needTotal = true;

	/**
	 * 默认是第一页
	 *
	 * @return
	 */
	public Integer getPage() {
		if (page == null) {
			return 1;
		}
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isNeedTotal() {
		return needTotal;
	}

	public void setNeedTotal(boolean needTotal) {
		this.needTotal = needTotal;
	}

	/**
	 * 支持分页
	 * @return
	 */
	@Override
	public PageRequest buildQuery() {
		PageRequest pg = super.buildQuery();
		pg.setPage(this.getPage());
		pg.setPageSize(this.getPageSize());
		pg.setNeedTotal(needTotal);
		return pg;
	}
}
