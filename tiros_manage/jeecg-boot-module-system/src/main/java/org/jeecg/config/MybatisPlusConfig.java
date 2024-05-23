package org.jeecg.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.jeecg.config.mybatis.MybatisInterceptor;
import org.jeecg.config.mybatis.isolation.DataIsolationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 单数据源配置（jeecg.datasource.open = false时生效）
 *
 * @Author zhoujf
 */
@Slf4j
@Configuration
@MapperScan(value = {"org.jeecg.modules.**.mapper*", "org.jeecg.common.**.mapper*"})
public class MybatisPlusConfig {

    //    @Autowired
//    private List<SqlSessionFactory> sqlSessionFactoryList;
//    @Resource
//    private MybatisSqlSessionFactoryBean sqlSessionFactory;


    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        // 设置sql的limit为无限制，默认是500
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        paginationInterceptor.setOverflow(true);
        paginationInterceptor.setLimit(-1);
        return paginationInterceptor;
    }

    @PostConstruct
    // 只会执行一次：
    // 顺序：Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的方法)
    public void addMysqlInterceptor() {
//        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
//        sqlSessionFactory.setPlugins(new Interceptor[]{paginationInterceptor(), new MybatisInterceptor(), new DataIsolationInterceptor()});

        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        PaginationInterceptor paginationInterceptor = paginationInterceptor();
        MybatisInterceptor mybatisInterceptor = new MybatisInterceptor();
        DataIsolationInterceptor dataIsolationInterceptor = new DataIsolationInterceptor();
        sqlSessionFactory.setPlugins(new Interceptor[]{paginationInterceptor, mybatisInterceptor, dataIsolationInterceptor});

//        // 创建自定义mybatis拦截器，添加到chain的最后面
//        PaginationInterceptor paginationInterceptor = paginationInterceptor();
//        MybatisInterceptor mybatisInterceptor = new MybatisInterceptor();
//        DataIsolationInterceptor dataIsolationInterceptor = new DataIsolationInterceptor();
//        for (SqlSessionFactory factory : sqlSessionFactoryList) {
//            org.apache.ibatis.session.Configuration configuration = factory.getConfiguration();
//            // 自己添加
//            configuration.addInterceptor(paginationInterceptor);
//            configuration.addInterceptor(mybatisInterceptor);
//            configuration.addInterceptor(dataIsolationInterceptor);
//        }
        log.info("执行了MybatisPlusConfig的@PostConstruct方法，添加了自定义拦截器和分页拦截器");
    }

//    /**
//     * mybatis-plus SQL执行效率插件【生产环境可以关闭】
//     */
//    @Bean
//    public PerformanceInterceptor performanceInterceptor() {
//        return new PerformanceInterceptor();
//    }
}
