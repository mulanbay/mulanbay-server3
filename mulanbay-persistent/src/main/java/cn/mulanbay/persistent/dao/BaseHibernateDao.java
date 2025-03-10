package cn.mulanbay.persistent.dao;

import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.persistent.cache.PageCacheProcessor;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.common.OPUtil;
import jakarta.persistence.NoResultException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.SessionFactoryUtils;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据库基本操作:Hibernate基类
 * @link <a href="https://docs.jboss.org/hibernate/orm/6.4/introduction/html_single/Hibernate_Introduction.html">参考</a>
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class BaseHibernateDao {

	/**
	 * 默认每页数
	 */
	public static int DEFAULT_PAGE_SIZE = 20;

	/**
	 * 不分页的页码
	 */
	public static final int NO_PAGE = 0;

	public static final int NO_PAGE_SIZE = 0;

	/**
	 * Hibernate的变量绑定开始序号,6.x需要从1开始
	 * @link <a href="https://docs.jboss.org/hibernate/orm/6.4/querylanguage/html_single/Hibernate_Query_Language.html">the-label</a>
	 */
	public static final int START_OPL = 1;

	private static final Logger logger = LoggerFactory.getLogger(BaseHibernateDao.class);

	/**
	 * 默认的空结果集，避免调用者空指针判断
	 */
	private static final List EMPTY_LIST = new ArrayList(0);

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private PageCacheProcessor pageCacheProcessor;

	@Autowired
	public void setPageCacheProcessor(PageCacheProcessor pageCacheProcessor) {
		this.pageCacheProcessor = pageCacheProcessor;
	}

	/**
	 * 根据pk删除对象
	 *
	 * @param c
	 *            对象类型
	 * @param id
	 *            主键
	 * @throws BaseException
	 */
	@SuppressWarnings("rawtypes")
	protected void removeEntity(Class c, Serializable id) throws BaseException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("removeEntity c:" + c + ",id=" + id);
			}
			Object obj = this.getSession().load(c, id);
			this.getSession().remove(obj);
		} catch (HibernateException e) {
			throw OPUtil.handleException(e);
		}
	}

	/**
	 *
	 * @return
	 */
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	protected void flushSession(){
		this.getSession().flush();
	}

	protected void refreshObject(Object o){
		this.getSession().refresh(o);
	}

	/**
	 * 删除对象
	 *
	 * @param o
	 * @throws BaseException
	 */
	protected void removeEntity(Object o) throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("removeEntity o:" + JsonUtil.beanToJson(o));
		}
		try {
			this.getSession().remove(o);
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 根据HQL删除对象集合（类似 from Entity as e where e.pk=*）
	 *
	 * @param hql
	 * @throws BaseException
	 * @return 删除的条数
	 */
	protected int removeEntities(String hql) throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("removeEntities HQL:" + hql);
		}
		try {
			return this.getSession().createQuery(hql,Integer.class).executeUpdate();
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 删除对象集合
	 *
	 * @param objs
	 * @throws BaseException
	 */
	@SuppressWarnings("rawtypes")
	protected void removeEntities(Object... objs) throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("removeEntities objs:" + JsonUtil.beanToJson(objs));
		}
		for (Object o : objs) {
			removeEntity(o);
		}
	}

	/**
	 * 根据pk找回对象,没找到赋予null
	 *
	 * @param c
	 *            对象类型
	 * @param id
	 *            主键
	 * @return 实体对象
	 * @throws BaseException
	 */
	protected <T> T getEntityById(Class<T> c, Serializable id)
			throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("getEntityById c:" + c + ",id=" + id);
		}
		T obj = null;
		try {
			obj = this.getSession().get(c, id);
		} catch (Exception he) {
			throw OPUtil.handleException(he);
		}
		return obj;
	}

	/**
	 * 保存对象集合
	 *
	 * @param obj
	 *            对象
	 * @throws BaseException
	 */
	protected void saveEntity(Object obj) throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("saveEntity obj:" + JsonUtil.beanToJson(obj));
		}
		try {
			this.getSession().persist(obj);
		} catch (Exception e) {
			logger.error("保持对象失败，Object="+ JsonUtil.beanToJson(obj));
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 保存对象数据
	 *
	 * @param objs
	 *            对象数据
	 * @throws BaseException
	 */
	protected void saveEntities(Object... objs) throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("saveEntities objs:" + JsonUtil.beanToJson(objs));
		}
		for (Object element : objs) {
			saveEntity(element);
		}
	}

	/**
	 * 更新对象
	 *
	 * @param obj
	 * @throws BaseException
	 */
	protected void updateEntity(Object obj) throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("updateEntity obj:" + JsonUtil.beanToJson(obj));
		}
		try {
			this.getSession().merge(obj);
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 更新对象
	 *
	 * @param obj
	 * @throws BaseException
	 */
	protected void mergeEntity(Object obj) throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("mergeEntity obj:" + JsonUtil.beanToJson(obj));
		}
		try {
			this.getSession().merge(obj);
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 更新对象集合
	 *
	 * @param objs
	 * @throws BaseException
	 */
	protected void updateEntities(Object... objs) throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("updateEntities objs:" + JsonUtil.beanToJson(objs));
		}
		for (Object element : objs) {
			this.updateEntity(element);
		}
	}

	/**
	 * 利用hql更新
	 *
	 * @param hql
	 *            HQL语句
	 * @param objects
	 *            变量绑定参数
	 * @throws BaseException
	 */
	protected int updateEntities(String hql, Object... objects)
			throws BaseException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("updateObjs hql:" + hql);
				logger.debug("请求参数："+JsonUtil.beanToJson(objects));
			}
			MutationQuery query = getSession().createMutationQuery(hql);
			int i = START_OPL;
			for (Object object : objects) {
				query.setParameter(i++, object);
			}
			return query.executeUpdate();
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 利用预编译SQL查询，如果查询不到，返回新的ArrayList，不返回NullPoint。
	 *
	 * @param hql
	 *            HQL语句
	 * @param clazz
	 *            指定类名
	 * @param iObjects
	 *            以索引变量绑定参数
	 * @return Query 返回Query
	 */
	@SuppressWarnings("rawtypes")
	public Long getCount(String hql, Class clazz,Object... iObjects) throws BaseException {
		Long result;
		if(pageCacheProcessor.isTotalCache(clazz)){
			String key = pageCacheProcessor.createTotalCacheKey(hql,clazz,iObjects);
			result = pageCacheProcessor.getCacheTotal(key);
			if(result==null){
				result = this.getCount_DT(hql,iObjects);
				pageCacheProcessor.cacheTotal(key,result);
			}else {
				logger.debug("得到总数HQL缓存，hql:{},key:{}",hql,key);
			}
		}else{
			result = this.getCount_DT(hql,iObjects);
		}
		return result;
	}

	/**
	 * 利用预编译SQL查询，如果查询不到，返回新的ArrayList，不返回NullPoint。
	 *
	 * @param hql
	 *            HQL语句
	 * @param iObjects
	 *            以索引变量绑定参数
	 * @return Query 返回Query
	 */
	@SuppressWarnings("rawtypes")
	public Long getCount(String hql,Object... iObjects) throws BaseException {
		return this.getCount_DT(hql,iObjects);
	}

	/**
	 * 利用预编译HQL查询记录总数
	 *
	 * @param hql
	 *            HQL语句
	 * @param objects
	 *            变量绑定参数
	 * @return Long 返回记录条数
	 */
	@SuppressWarnings("unchecked")
	protected Long getCount_DT(String hql, Object... objects) throws BaseException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("getCount hql:" + hql);
				logger.debug("getCount 变量=" + JsonUtil.beanToJson(objects));
			}
			Query query = getSession().createQuery(hql,Long.class);
			int i = START_OPL;
			for (Object object : objects) {
				query.setParameter(i++, object);
			}
			List<Object> result = query.list();
			if (result == null||result.isEmpty()) {
				return 0L;
			} else {
				Object o = result.get(0);
				return Long.valueOf(o.toString()) ;
			}
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 利用预编译HQL查询，如果查询不到，返回新的ArrayList，不返回NullPoint。
	 *
	 * @param hql
	 *            SQL语句
	 * @param page
	 * @param pageSize
	 * @param clazz
	 *            指定类名
	 * @param objects
	 *            以索引变量绑定参数
	 * @return Query 返回Query
	 */
	@SuppressWarnings("rawtypes")
	protected <T> List<T>  getEntityListHI(String hql, int page,int pageSize, Class<T> clazz, Object... objects) throws BaseException {
		List<T> result;
		if(pageCacheProcessor.isListCache(clazz)){
			String key = pageCacheProcessor.createListCacheKey(hql,page,pageSize,clazz,objects);
			result = pageCacheProcessor.getCacheList(key,clazz);
			if(result==null){
				result = this.getEntityListHQL(hql,page,pageSize,clazz,objects);
				pageCacheProcessor.cacheList(key,result);
			} else {
				logger.debug("得到列表HQL缓存，hql:{},key:{}",hql,key);
			}
		}else{
			result = this.getEntityListHQL(hql,page,pageSize,clazz,objects);
		}
		return result;
	}

	/**
	 * 利用预编译HQL查询，如果查询不到，返回新的ArrayList，不返回NullPoint。
	 *
	 * @param hql
	 *            SQL语句
	 * @param page
	 * @param pageSize
	 * @param clazz
	 *            指定类名
	 * @param iObjects
	 *            以索引变量绑定参数
	 * @return Query 返回Query
	 */
	@SuppressWarnings("rawtypes")
	private <T> List<T>  getEntityListHQL(String hql, int page,int pageSize, Class<T> clazz,Object... iObjects) throws BaseException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("retrieveSQLObjs hql:" + hql);
				logger.debug("参数：page=" + page + ",pageSize=" + pageSize);
				logger.debug("iObjects:" + JsonUtil.beanToJson(iObjects));
			}
			Query query = getSession().createQuery(hql,clazz);
			int i = START_OPL;
			for (Object object : iObjects) {
				query.setParameter(i++, object);
			}
			if(page>NO_PAGE){
				query.setFirstResult(pageSize * (page - 1));
				query.setMaxResults(pageSize);
			}
			query.setCacheable(true);
			query.setCacheRegion("frontpages");
			List<T> result = query.list();
			if (result == null) {
				return EMPTY_LIST;
			} else {
				return result;
			}
		} catch (Exception e) {
			logger.error("执行sql异常,HQL:\n"+hql);
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 利用预编译SQL查询，不分页
	 *
	 * @param sql
	 *            SQL语句
	 * @param clazz
	 *            指定类名
	 * @param iObjects
	 *            以索引变量绑定参数
	 * @return Query 返回Query
	 */
	@SuppressWarnings("rawtypes")
	protected <T> List<T>  getEntityListSINP(String sql, Class<T> clazz, Object... iObjects) throws BaseException {
		return this.getEntityListSI(sql,NO_PAGE,NO_PAGE_SIZE,clazz,iObjects);
	}

	/**
	 * 利用预编译SQL查询，如果查询不到，返回新的ArrayList，不返回NullPoint。
	 *
	 * @param sql
	 *            SQL语句
	 * @param page
	 * @param pageSize
	 * @param clazz
	 *            指定类名
	 * @param iObjects
	 *            以索引变量绑定参数
	 * @return Query 返回Query
	 */
	@SuppressWarnings("rawtypes")
	protected <T> List<T>  getEntityListSI(String sql, int page,int pageSize, Class<T> clazz, Object... iObjects) throws BaseException {
		List<T> result;
		if(pageCacheProcessor.isListCache(clazz)){
			String key = pageCacheProcessor.createListCacheKey(sql,page,pageSize,clazz,iObjects);
			result = pageCacheProcessor.getCacheList(key,clazz);
			if(result==null){
				result = this.getEntityListSQL(sql,page,pageSize,clazz,iObjects);
				pageCacheProcessor.cacheList(key,result);
			} else {
				logger.debug("得到列表SQL缓存，sql:{},key:{}",sql,key);
			}
		}else{
			result = this.getEntityListSQL(sql,page,pageSize,clazz,iObjects);
		}
		return result;
	}

	/**
	 * 利用预编译SQL查询，如果查询不到，返回新的ArrayList，不返回NullPoint。
	 *
	 * @param sql
	 *            SQL语句
	 * @param page
	 * @param pageSize
	 * @param clazz
	 *            指定类名
	 * @param iObjects
	 *            以索引变量绑定参数
	 * @return Query 返回Query
	 */
	private <T> List<T> getEntityListSQL(String sql, int page,int pageSize, Class<T> clazz, Object... iObjects) throws BaseException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("retrieveSQLObjs sql:" + sql);
				logger.debug("参数：page=" + page + ",pageSize=" + pageSize);
				logger.debug("iObjects:" + JsonUtil.beanToJson(iObjects));
			}
			NativeQuery<T> query =  getSession().createNativeQuery(sql,clazz);
			int i = START_OPL;
			for (Object object : iObjects) {
				query.setParameter(i++, object);
			}
			if (page > NO_PAGE) {
				query.setFirstResult(pageSize * (page - 1));
				query.setMaxResults(pageSize);
			}
			query.setCacheable(true);
			query.setCacheRegion("frontpages");
			List<T> result = query.getResultList();
			if (result == null) {
				return EMPTY_LIST;
			} else {
				return result;
			}
		} catch (Exception e) {
			logger.error("执行sql异常,SQL:\n"+sql);
			throw OPUtil.handleException(e);
		}

	}

	/**
	 * 利用预编译sql查询记录总数
	 *
	 * @param sql
	 *            sql语句
	 * @param objects
	 *            变量绑定参数
	 * @return Long 返回记录条数
	 */
	@SuppressWarnings("unchecked")
	protected Long getCountSQL(String sql, Object... objects) throws BaseException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("getCount sql:" + sql);
				logger.debug("getCount 变量=" + JsonUtil.beanToJson(objects));
			}
			NativeQuery query = getSession().createNativeQuery(sql,Long.class);
			int i = START_OPL;
			for (Object object : objects) {
				query.setParameter(i++, object);
			}
			List<Object> result = query.list();
			if (result == null||result.isEmpty()) {
				return 0L;
			} else {
				Object o = result.get(0);
				return Long.valueOf(o.toString()) ;
			}
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 根据sql获取列表
	 *
	 * @param sql
	 *            sql
	 * @param objects
	 *            变量绑定参数
	 * @return list Object[]对象列表
	 * @throws BaseException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List execSqlQuery(String sql, Object... objects)
			throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("execSqlQuery sql:" + sql);
			logger.debug("请求参数:"+JsonUtil.beanToJson(objects));
		}
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		List list = new ArrayList();
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			int i = START_OPL;
			for (Object object : objects) {
				setObject(pstmt, i++, object);
			}
			rs = pstmt.executeQuery();
			ResultSetMetaData resultMetaData = rs.getMetaData();
			int fieldCount = resultMetaData.getColumnCount();
			while (rs.next()) {
				if (fieldCount == 1) {
					list.add(getObject(rs, 1));
				} else {
					List<Object> l = new ArrayList<Object>();
					for (int j = 1; j <= fieldCount; j++) {
						l.add(getObject(rs, j));
					}
					list.add(l.toArray());
					l = null;
				}
			}
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				throw OPUtil.handleException(e);
			}
		}
		return list;
	}

	private void setObject(PreparedStatement pstmt, int paramIndex,
						   Object paramObject) throws SQLException {
		if (paramObject != null && paramObject instanceof Date) {
			paramObject = new Timestamp(((Date) paramObject).getTime());
		}
		pstmt.setObject(paramIndex, paramObject);
	}

	/**
	 * @param rs
	 * @param indexColumn
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private Object getObject(ResultSet rs, int indexColumn)
			throws SQLException, IOException {
		if (rs != null) {
			Object obj = rs.getObject(indexColumn);
			if (obj != null && obj instanceof Blob) {
				obj = toByteArray(((Blob) obj).getBinaryStream());
			}
			return obj;
		} else {
			return null;
		}
	}

	private byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	private int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	private long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024 * 4];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * 利用预编译HQL查询，如果查询不到，返回null，不返回NullPoint。
	 *
	 * @param hql
	 *            HQL语句
	 * @param objects
	 *            变量绑定参数
	 * @return Object 返回对象实体
	 */
	@SuppressWarnings("rawtypes")
	protected  <T> T getEntity(String hql,Class<T> clazz, Object... objects)
			throws BaseException {
		try {
			List<T> list = this.getEntityListHI(hql,1,1,clazz,objects);
			if(list==null||list.isEmpty()){
				return null;
			}else{
				return list.get(0);
			}
		} catch (NoResultException e) {
			logger.error("未找到数据",e);
			return null;
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		}

	}


	/**
	 * 利用预编译HQL查询，如果查询不到，返回null，不返回NullPoint。
	 *
	 * @param sql
	 *            HQL语句
	 * @param objects
	 *            变量绑定参数
	 * @return Object 返回对象实体
	 */
	@SuppressWarnings("rawtypes")
	protected  <T> T getEntitySQL(String sql,Class<T> clazz, Object... objects)
			throws BaseException {
		List list = this.getEntityListSI(sql,1, 1,clazz, objects);
		if (list.isEmpty()) {
			return null;
		} else {
			return (T) list.get(0);
		}
	}

	public Connection getConnection() {
		try {
			return SessionFactoryUtils.getDataSource(getSessionFactory())
					.getConnection();
		} catch (SQLException e) {
			throw new PersistentException("获取链接失败。", e);
		}
	}

	/**
	 * 根据sql获取列表
	 *
	 * @param sql
	 *            sql
	 * @param objects
	 *            变量绑定参数
	 * @return list Object[]对象列表
	 * @throws BaseException
	 */
	protected int execSqlUpdate(String sql, Object... objects)
			throws BaseException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("execSqlUpdate sql:" + sql);
				logger.debug("请求参数：" + JsonUtil.beanToJson(objects));
			}
			NativeQuery query = getSession().createNativeQuery(sql,Long.class);
			int i = START_OPL;
			for (Object object : objects) {
				query.setParameter(i++, object);
			}
			return query.executeUpdate();
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		}
	}

	/**
	 * 采用JDBC模式更新
	 * 占位符绑定时无需序号
	 *
	 * @param sql
	 *            sql
	 * @param objects
	 *            变量绑定参数
	 * @return list Object[]对象列表
	 * @throws BaseException
	 */
	protected int execSqlUpdateJDBC(String sql, Object... objects)
			throws BaseException {
		if(logger.isDebugEnabled()){
			logger.debug("execSqlUpdate sql:" + sql);
			logger.debug("请求参数：" + JsonUtil.beanToJson(objects));
		}
		Connection conn = null;
		int rs = 0;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			int i = START_OPL;
			for (Object object : objects) {
				setObject(pstmt, i++, object);
			}
			rs = pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error("execSqlUpdate error:"+sql);
			throw OPUtil.handleException(e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error("execSqlUpdate关闭资源异常", e);
			}
		}
		return rs;
	}

	/**
	 * 通过SQL执行无返回结果的存储过程(仅限于存储过程)
	 *
	 * @param procedureName
	 * @param objects
	 */
	public void executeVoidProcedure(String procedureName, Object... objects) throws BaseException{
		if(logger.isDebugEnabled()){
			logger.debug("executeVoidProcedure procedureName:" + procedureName);
			logger.debug("请求参数：" + JsonUtil.beanToJson(objects));
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			CallableStatement call = conn.prepareCall(procedureName);
			if (null != objects) {
				for (int i = 0; i < objects.length; i++) {
					call.setObject((i+1), objects[i]);
				}
			}
			call.executeQuery();
		} catch (Exception e) {
			throw OPUtil.handleException(e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error("executeVoidProcedure关闭资源异常", e);
			}
		}
	}
}