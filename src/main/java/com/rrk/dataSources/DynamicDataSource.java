package com.rrk.dataSources;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * AbstractRoutingDataSource 这个类可以实现多数源的切换
 */
public class DynamicDataSource extends AbstractRoutingDataSource {





    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 配置DataSourceAnnonation, defaultTargetDataSource为主数据库
     */

    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object,Object> targetDataSources){

        /**
         * todo: targetDataSources 保存了key和数据库连接的映射关系
         * todo:defaultTargetDataSource标识默认的连接
         * todo: resolvedDataSources这个数据结构是通过targetDataSources构建而来，
         * 存储结构也是数据库标识和数据源的映射关系
         *
         */


        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }



    public static void setDataSource(String dataSource) {
        threadLocal.set(dataSource);
    }

    public static String getDataSource() {
        return threadLocal.get();
    }

    public static void clearDataSource() {
        threadLocal.remove();
    }
}
