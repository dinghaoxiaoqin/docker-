package com.rrk.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.rrk.config.properties.MultiDataSourceTransactionFactory;
import com.rrk.dataSources.DBTypeEnum;
import com.rrk.dataSources.DynamicDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@MapperScan("com.rrk.dao*")
public class MyBatisPlusConfig {


    /**
     * 创建 DataSource Bean
     * */

    @Bean(name = "dataSourceDb1")
    @ConfigurationProperties("spring.datasource.db1")
    public DataSource dataSourceDb1(){
        DataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }

    @Bean(name = "dataSourceDb2")
    @ConfigurationProperties("spring.datasource.db2")
    public DataSource dataSourceDb2(){
        DataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }

    /**
     * 将数据源信息载入targetDataSources
     * */

    @Bean
    @Primary
    public DynamicDataSource dataSource(@Qualifier("dataSourceDb1") DataSource dataSourceDb1,@Qualifier("dataSourceDb2") DataSource dataSourceDb2) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put(DBTypeEnum.db1.getValue(), dataSourceDb1);
        targetDataSources.put(DBTypeEnum.db2.getValue(), dataSourceDb2);
        // 如果还有其他数据源,可以按照数据源one和two这种方法去进行配置，然后在targetDataSources中继续添加
        System.out.println("加载的数据源DataSources:" + targetDataSources);

        //DynamicDataSource（默认数据源,所有数据源） 第一个指定默认数据库

        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(dataSourceDb1);
        return dynamicDataSource;
    }


    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource(dataSourceDb1(), dataSourceDb2()));
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/com/rrk/mapper/*.xml"));
        //添加事物配置（重点）
        sqlSessionFactory.setTransactionFactory(new MultiDataSourceTransactionFactory());
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        //添加分页插件
        sqlSessionFactory.setPlugins(new Interceptor[]{paginationInterceptor()});
        return sqlSessionFactory.getObject();
    }

    /**
     * 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 开启 PageHelper 的支持
        return paginationInterceptor;
    }




    /**
     * SQL执行效率插件
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setMaxTime(100000);
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }
}
