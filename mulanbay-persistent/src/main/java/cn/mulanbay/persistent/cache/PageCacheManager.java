package cn.mulanbay.persistent.cache;

import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.DigestUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 分页查询的缓存处理
 * @Author: fenghong
 * @Create : 2020/10/9 22:22
 */
@Component
public class PageCacheManager extends BaseHibernateDao {

    private static final Logger logger = LoggerFactory.getLogger(PageCacheManager.class);

    /**
     * 前缀
     */
    @Value("${mulanbay.persistent.page.cache.keyPrefix:pageSearch}")
    private String keyPrefix;

    /**
     * 列表数据数据是否缓存
     */
    @Value("${mulanbay.persistent.page.cache.list:false}")
    private boolean listCache;

    /**
     * 总页数是否缓存
     */
    @Value("${mulanbay.persistent.page.cache.total:false}")
    private boolean totalCache;

    /**
     * 需要缓存的bean
     */
    @Value("${mulanbay.persistent.page.cache.beans:}")
    private String cacheBeans;

    /**
     * 缓存失效时间
     */
    @Value("${mulanbay.persistent.page.cache.expireSeconds:300}")
    private int expireSeconds;

    @Autowired(required = false)
    CacheProcessor cacheProcessor;

    /**
     * 是否启用缓存
     * @return
     */
    public boolean enableCache(){
        return listCache || totalCache;
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
            String paraString =  pr.getParameterString();
            Object[] values = pr.getParameterValue();
            String sortString = pr.getSortString();
            if (pr.getPage() > 0&&pr.isNeedTotal()) {
                long maxRow = this.getTotal(paraString,values,sortString,pr.getBeanClass());
                qb.setMaxRow(maxRow);
            }
            List<T> list = this.getList(paraString,values,sortString,pr.getBeanClass(),
                    pr.getPage(), pr.getPageSize());
            qb.setBeanList(list);
            qb.setPage(pr.getPage());
            return qb;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,"获取列表数据异常", e);
        }
    }

    /**
     * 获取列表
     * @param paraString
     * @param values
     * @param sortString
     * @param beanClass
     * @return
     */
    private <T> List<T> getList(String paraString,Object[] values,String sortString,Class beanClass,int page,int pageSize) throws BaseException{
        if(!this.isListCache(beanClass)) {
            return this.getBeanList(paraString,values,sortString,beanClass,page,pageSize);
        }
        String key = createCacheKey(paraString,values,sortString,beanClass)+":"+page+":"+pageSize;
        List list = cacheProcessor.get(key,List.class);
        if(list == null){
            list = this.getBeanList(paraString,values,sortString,beanClass,page,pageSize);
            cacheProcessor.set(key,list,expireSeconds);
        }else{
            logger.debug("获取列表缓存,beanClass:{},key:{}",beanClass,key);
        }
        return list;
    }

    /**
     * 从数据库获取列表
     * @param paraString
     * @param values
     * @param sortString
     * @param beanClass
     * @param page
     * @param pageSize
     * @param <T>
     * @return
     * @throws BaseException
     */
    private <T> List<T> getBeanList(String paraString,Object[] values,String sortString,Class beanClass,int page,int pageSize) throws BaseException {
        String hql = "from " + beanClass.getName()+paraString+sortString;
        List<T> list = this.getEntityListHI(hql,page, pageSize,beanClass, values);
        return list;
    }

    /**
     * 创建缓存KEY
     * @param paraString
     * @param values
     * @param sortString
     * @param beanClass
     * @return
     */
    private String createCacheKey(String paraString,Object[] values,String sortString,Class beanClass){
        String beanName = this.getBeanClassName(beanClass);
        return keyPrefix +":" +beanName+":"+this.getParaKey(paraString,values,sortString);
    }

    /**
     * 获取总记录值
     * @param paraString
     * @param values
     * @param sortString
     * @param beanClass
     * @return
     */
    private long getTotal(String paraString,Object[] values,String sortString,Class beanClass) throws BaseException{
        if(!this.isTotalCache(beanClass)) {
            return this.getMaxRow(paraString,beanClass,values);
        }
        String key = createCacheKey(paraString,values,sortString,beanClass)+":t";
        Long total = cacheProcessor.get(key,Long.class);
        if(total == null){
            total = this.getMaxRow(paraString,beanClass,values);
            cacheProcessor.set(key,total,expireSeconds);
        }else{
            logger.debug("获取总数缓存,beanClass:{},key:{}",beanClass,key);
        }
        return total.longValue();
    }

    /**
     * 从数据库获取总记录值
     * @param beanClass
     * @param values
     * @return
     * @throws BaseException
     */
    private long getMaxRow(String paraString,Class beanClass,Object[] values) throws BaseException {
        String hql = "select count(*) from " + beanClass.getName()+paraString;
        long maxRow = this.getCount(hql, values);
        return maxRow;
    }

    /**
     * 删除缓存
     * @param beanClass
     */
    public void removeCache(Class beanClass){
        String beanName = this.getBeanClassName(beanClass);
        String key = keyPrefix +beanName+":*";
        cacheProcessor.delete(key);
    }

    /**
     * 判断列表数据是否要缓存
     * @param beanClass
     * @return
     */
    private boolean isListCache(Class beanClass){
        if(!listCache) {
            return false;
        }
        return this.isBeanCache(beanClass);
    }

    /**
     * 判断总记录值是否要缓存
     * @param beanClass
     * @return
     */
    private boolean isTotalCache(Class beanClass){
        if(!totalCache) {
            return false;
        }
        return this.isBeanCache(beanClass);
    }

    /**
     * 判断bean是否要缓存
     * @param beanClass
     * @return
     */
    private boolean isBeanCache(Class beanClass){
        if(StringUtil.isEmpty(cacheBeans)) {
            return false;
        }
        String beanName = this.getBeanClassName(beanClass);
        if("*".equals(cacheBeans)) {
            return true;
        }
        String[] ss = cacheBeans.split(",");
        for(String s : ss){
            if(s.equals(beanName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取bean名称
     * @param beanClass
     * @return
     */
    private String getBeanClassName(Class beanClass){
        return beanClass.getSimpleName();
    }

    /**
     * 获取参数key,会产生hash碰撞
     * @param paraString
     * @param values
     * @param sortString
     * @return
     */
    private String getParaKey(String paraString,Object[] values,String sortString){
        StringBuffer sb = new StringBuffer();
        sb.append(paraString);
        for(Object o : values){
            if(o==null){
                sb.append(",null");
            }else{
                sb.append(","+o.toString());
            }
        }
        sb.append(","+sortString);
        return DigestUtil.sha1(sb.toString()).toLowerCase();
    }
}
