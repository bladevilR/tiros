package org.jeecg.modules.third.dynamic;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * mybatis-plus配置
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-30
 */
@Configuration
//@MapperScan(value = {"org.jeecg.modules.third.**.mapper*"})
@MapperScan(value = {"org.jeecg.modules.**.mapper*","org.jeecg.common.**.mapper*"})
// org.jeecg.modules.**.mapper*
public class MybatisPlusConfiguration {

    @Value("${spring.datasource.dynamic.datasource.jdxdb.url}")
    private String jdxurl;

    @Value("${spring.datasource.dynamic.datasource.jdxdb.username}")
    private String jdxuser;

    @Value("${spring.datasource.dynamic.datasource.jdxdb.password}")
    private String jdxpassword;

    @Value("${spring.datasource.dynamic.datasource.jdxdb.driver-class-name}")
    private String jdxdriverClass;

    @Value("${spring.datasource.dynamic.datasource.maximodb.url}")
    private String maximourl;

    @Value("${spring.datasource.dynamic.datasource.maximodb.username}")
    private String maximouser;

    @Value("${spring.datasource.dynamic.datasource.maximodb.password}")
    private String maximopassword;

    @Value("${spring.datasource.dynamic.datasource.maximodb.driver-class-name}")
    private String maximodriverClass;

    @Value("${spring.datasource.dynamic.datasource.workshopdb.url}")
    private String workshopdburl;

    @Value("${spring.datasource.dynamic.datasource.workshopdb.username}")
    private String workshopdbuser;

    @Value("${spring.datasource.dynamic.datasource.workshopdb.password}")
    private String workshopdbpassword;

    @Value("${spring.datasource.dynamic.datasource.workshopdb.driver-class-name}")
    private String workshopdbdriverClass;

    @Value("${mybatis-plus.mapper-locations}")
    private String mapperLocations;


    @Bean(name = "jdxdb")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.jdxdb")
    public DataSource jdxdb() {
        return DataSourceBuilder.create()
                .driverClassName(jdxdriverClass)
                .url(jdxurl)
                .username(jdxuser)
                .password(jdxpassword)
                .build();
    }

    @Bean(name = "maximodb")
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.maximodb")
    public DataSource maximodb() {
        return DataSourceBuilder.create()
                .driverClassName(maximodriverClass)
                .url(maximourl)
                .username(maximouser)
                .password(maximopassword)
                .build();
    }


    @Bean(name = "workshopdb")
    @ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.workshopdb")
    public DataSource workshopdb() {
        return DataSourceBuilder.create()
                .driverClassName(workshopdbdriverClass)
                .url(workshopdburl)
                .username(workshopdbuser)
                .password(workshopdbpassword)
                .build();
    }

    /**
     * 动态数据源配置
     *
     * @return 动态数据源
     */
    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap  = new HashMap<>(8);
        dataSourceMap .put(DataSourceEnum.jdxdb.getValue(), jdxdb());
        dataSourceMap .put(DataSourceEnum.maximodb.getValue(), maximodb());
        dataSourceMap .put(DataSourceEnum.workshopdb.getValue(), workshopdb());
        // 设置默认数据源
        dynamicDataSource.setDefaultDataSource(jdxdb());
        // 添加数据源
        dynamicDataSource.setDataSources(dataSourceMap);

        return dynamicDataSource;
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        paginationInterceptor.setOverflow(false);
        paginationInterceptor.setLimit(-1L);
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));

        return paginationInterceptor;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dynamicDataSource());
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);

        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setPlugins(new Interceptor[]{paginationInterceptor()});

        return sqlSessionFactory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        // 配置事务管理, 使用事务时在方法头部添加@Transactional注解即可
        return new DataSourceTransactionManager(dynamicDataSource());
    }

}
