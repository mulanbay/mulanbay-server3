package cn.mulanbay.persistent.service;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 基础service，封装常用的操作方法
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
@Transactional
@Service
public class BaseService extends BaseHibernateDao {

	/**
	 * 删除对象
	 *
	 * @param object
	 *            object
	 */
	public void deleteObject(Object object) {
		try {
			this.removeEntity(object);
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,"删除对象失败！",e);
		}
	}

	/**
	 * 增加对象
	 *
	 * @param object
	 *            object
	 */
	public void saveObject(Object object) {
		try {
			this.saveEntity(object);
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,"增加对象失败！",e);
		}
	}

	/**
	 * 增加对象
	 *
	 * @param objects
	 *            object
	 */
	@SuppressWarnings("rawtypes")
	public void saveObjects(List objects) {
		try {
			this.saveEntities(objects.toArray());
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,"增加多个对象失败！",e);
		}
	}

	/**
	 * 更新对象
	 *
	 * @param object
	 *            object
	 */
	public void updateObject(Object object) {
		try {
			this.updateEntity(object);
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,"更新对象失败！",e);
		}
	}

	/**
	 * 更新对象
	 *
	 * @param object
	 *            object
	 */
	public void updateByMergeObject(Object object) {
		try {
			this.mergeEntity(object);
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,"更新对象失败！",e);
		}
	}

	/**
	 * 获取对象
	 *
	 * @param c
	 *            c
	 * @param id
	 *            id
	 * @return Object Object
	 */
	public <T> T getObject(Class<T> c, Serializable id) {
		try {
			return (T) this.getEntityById(c, id);
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,"获取对象["+c.getSimpleName()+"],id["+id+"]失败！",e);
		}
	}

	/**
	 * 获取对象
	 * @param clazz
	 * @param id
	 * @param idField
	 * @return
	 * @param <T>
	 */
	public <T> T getObject(Class<T> clazz, Serializable id,String idField) {
		try {
			String beanName = clazz.getSimpleName();
			String hql="from "+beanName+" where "+idField+"=?1 ";
			return this.getEntity(hql,clazz,id);
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,"获取对象["+clazz+"],id["+id+"]失败！",e);
		}
	}

	/**
	 * 获取对象
	 *
	 * @param id
	 *            id
	 * @param userId
	 *
	 * @return Object Object
	 */
	public <T> T getObjectWithUser(Class<T> c, Serializable id,Long userId) {
		try {
			String hql="from "+c.getSimpleName()+" where id=?1 and userId=?2 ";
			return this.getEntity(hql,c,id,userId);
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,"获取对象["+c.getSimpleName()+"],id["+id+"],userId["+userId+"]失败！",e);
		}
	}


	/**
	 * 删除对象
	 *
	 * @param c
	 *            c
	 * @param fieldName
	 *            fieldName
	 * @param id
	 *            id
	 */
	@SuppressWarnings("rawtypes")
	public void deleteObject(Class c, String fieldName, Serializable id) {
		try {
			String hql = "delete from " + c.getSimpleName() + " t where t."
					+ fieldName + "=?1 ";
			this.updateEntities(hql, id);
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,"删除对象失败！",e);
		}
	}


	/**
	 * 删除对象
	 *
	 * @param c
	 *            c
	 * @param id
	 *            id
	 */
	@SuppressWarnings("rawtypes")
	public void deleteObject(Class c, Serializable id) {
		try {
			this.removeEntity(c, id);
		} catch (Exception e) {
			throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,"删除对象失败！",e);
		}
	}

	/**
	 * 批量删除对象
	 *
	 * @param c
	 *            c
	 * @param ids
	 *            ids
	 */
	@SuppressWarnings("rawtypes")
	public void deleteObjects(Class c, Serializable[] ids) {
		try {
			for(Serializable id:ids){
				this.removeEntity(c, id);
			}
		} catch (Exception e) {
			throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,"删除对象失败！",e);
		}
	}

	/**
	 * 删除对象
	 * @param c
	 * @param ids
	 * @param userId
	 */
	public void deleteObjectsWithUser(Class c, Serializable[] ids,Long userId) {
		try {
			for(Serializable id:ids){
				String hql="delete from "+c.getSimpleName()+" where id=?1 and userId=?2 ";
				this.updateEntities(hql,id,userId);
			}
		} catch (Exception e) {
			throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,"删除对象失败！",e);
		}
	}

	/**
	 * 批量删除对象
	 *
	 * @param c
	 *            c
	 * @param ids
	 *            ids
	 */
	public void deleteObjects(Class c, Collection<Serializable> ids) {
		try {
			for(Serializable id:ids){
				this.removeEntity(c, id);
			}
		} catch (Exception e) {
			throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,"删除对象失败！",e);
		}
	}

	/**
	 * 保存或更新对象
	 *
	 * @param object
	 */
	public void saveOrUpdateObject(Object object) {
		try {
			this.mergeEntity(object);
		} catch (BaseException e) {
			e.printStackTrace();
			throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,"增加或更新对象失败！",e);
		}

	}

	/**
	 * 获取列表数据
	 * @param pr
	 * @param <T>
	 * @return
	 */
	public <T> PageResult<T> getBeanResult(PageRequest pr) {
		try {
			PageResult<T> qb = new PageResult<T>();
			String hql = "from " + pr.getBeanClass().getName();
			hql += pr.getParameterString();
			Object[] values = pr.getParameterValue();
			if (pr.getPage() > NO_PAGE&&pr.isNeedTotal()) {
				long maxRow = this.getCount("select count(0) " + hql,pr.getBeanClass(),values);
				qb.setMaxRow(maxRow);
			}
			hql+=pr.getSortString();
			List<T> list = this.getEntityListHI(hql.toString(),
					pr.getPage(), pr.getPageSize(), pr.getBeanClass(),values);
			qb.setBeanList(list);
			qb.setPage(pr.getPage());
			return qb;
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,"获取列表数据异常", e);
		}
	}

	/**
	 * 获取列表数据
	 * @param c
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @return
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	public <T> PageResult<T> getBeanResult(Class<T> c, int page, int pageSize, Sort sort) {
		try {
			PageResult<T> qb = new PageResult<T>();
			StringBuffer hql = new StringBuffer();
			hql.append("from " + c.getName());
			if (page >= NO_PAGE) {
				long maxRow = this.getCount("select count(*) " + hql.toString(),c);
				qb.setMaxRow(maxRow);
			}
			if (sort != null) {
				hql.append(" order by " + sort.getSortString());
			}
			List<T> list = this.getEntityListHI(hql.toString(), page,
					pageSize,c);
			qb.setBeanList(list);
			return qb;
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,"获取列表异常", e);
		}
	}

	/**
	 * 获取列表数据
	 * @param c
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @return
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getBeanList(Class<T> c, int page, int pageSize, Sort sort) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from " + c.getName());
			if (sort != null) {
				hql.append(" order by " + sort.getSortString());
			}
			List<T> list = this.getEntityListHI(hql.toString(), page,pageSize,c);
			return list;
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,"获取列表异常", e);
		}
	}

	/**
	 * 获取列表数据(不分页，全部)
	 * @param c
	 * @param sort
	 * @return
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getBeanList(Class<T> c, Sort sort) {
		return this.getBeanList(c,NO_PAGE,NO_PAGE_SIZE,sort);
	}

	/**
	 * 获取列表数据
	 * @param pr
	 * @param <T>
	 * @return
	 */
	public <T> List<T> getBeanList(PageRequest pr) {
		try {
			String hql = "from " + pr.getBeanClass().getName();
			hql += pr.getParameterString();
			Object[] values = pr.getParameterValue();
			hql+=pr.getSortString();
			List<T> list = this.getEntityListHI(hql,
					pr.getPage(), pr.getPageSize(),pr.getBeanClass(), values);
			return list;
		} catch (BaseException e) {
			throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,"获取列表异常", e);
		}
	}

	/**
	 * 执行通用的JOB存储过程异常(非查询类)
	 *
	 * @param procedureName
	 * @param objects
	 */
	public void updateJobProcedure(String procedureName, Object... objects) {
		try {
			String sql = "";
			if (objects == null || objects.length == 0) {
				sql = "{call " + procedureName + "()}";
			} else {
				String para = "";
				int index=0;
				for (Object o : objects) {
					para += o.toString()+",";
				}
				para = para.substring(0, para.length() - 1);
				sql = "{call " + procedureName + "(" + para + ")}";
			}
			//todo 使用参数绑定暂时有问题
			this.executeVoidProcedure(sql);
		} catch (BaseException e) {
			throw new PersistentException("执行通用的JOB存储过程异常", e);
		}
	}
}
