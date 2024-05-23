package org.jeecg.config.mybatis.isolation;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * <p>
 * 数据隔离拦截器
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/8/24
 */
@Slf4j
@Component
//@Intercepts({
//        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
//        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
//})
//@Intercepts({
//        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
//        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
//})
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
//@Intercepts({
//        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
//        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
//})
public class DataIsolationInterceptor implements Interceptor {

    private static final String LINE_COLUMN = "line_id";
    private static final String WORKSHOP_COLUMN = "workshop_id";
    private static final String COMPANY_COLUMN = "company_id";
    private static final String LINE_FIELD = "lineId";
    private static final String WORKSHOP_FIELD = "workshopId";
    private static final String COMPANY_FIELD = "companyId";

    @Value("${tiros.dataIsolation.dialect}")
    private String dialect;
    @Value("${tiros.dataIsolation.tableNames}")
    private String tableNames;
    @Value("${tiros.dataIsolation.isolationColumns}")
    private String isolationColumns;
    @Value("${tiros.dataIsolation.ignoreDbNullData}")
    private String ignoreDbNullData;

    private List<String> isolationTableNameList;
    private List<String> isolationColumnList;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 由用户获取隔离字段值
        LoginUser sysUser = this.getLoginUser();
        String lineId = null;
        String workshopId = null;
        String companyId = null;
        if (null != sysUser) {
            lineId = sysUser.getLineId();
            workshopId = sysUser.getWorkshopId();
            companyId = sysUser.getCompanyId();
        }

        // 初始化数据隔离表和字段
        initIsolationTableColumn();

        String methodName = invocation.getMethod().getName();
        if ("update".equals(methodName)) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            String sqlId = mappedStatement.getId();
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            // 插入的才处理
            if (!SqlCommandType.INSERT.equals(sqlCommandType)) {
                return invocation.proceed();
            }
            // 获取传入sql语句的参数对象
            Object parameterObject = invocation.getArgs()[1];
            if (parameterObject == null) {
                return invocation.proceed();
            }
            BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
            // 获取到拥有占位符的sql语句
            String sql = boundSql.getSql();
            boolean needIsolation = needIsolationByInsertSql(sql);
            if (!needIsolation) {
                return invocation.proceed();
            }

            String interceptInfo = String.format("数据隔离拦截器--插入sql(sqlId=%s，sqlCommandType=%s)：", sqlId, sqlCommandType);
            log.debug("{}拦截前sql：{}", interceptInfo, sql);
            try {
                Field[] fields = oConvertUtils.getAllFields(parameterObject);
                for (Field field : fields) {
                    field.setAccessible(true);

                    if (LINE_FIELD.equals(field.getName()) && StrUtil.isNotBlank(lineId)) {
                        String lineIdValue = (String) field.get(parameterObject);
                        if (StringUtils.isBlank(lineIdValue)) {
                            field.set(parameterObject, lineId);
                            log.debug("{}添加设置lineId={}", interceptInfo, lineId);
                        }
                    }
                    if (WORKSHOP_FIELD.equals(field.getName()) && StrUtil.isNotBlank(workshopId)) {
                        String workshopIdValue = (String) field.get(parameterObject);
                        if (StringUtils.isBlank(workshopIdValue)) {
                            field.set(parameterObject, workshopId);
                            log.debug("{}添加设置workshopId={}", interceptInfo, workshopId);
                        }
                    }
                    if (COMPANY_FIELD.equals(field.getName()) && StrUtil.isNotBlank(companyId)) {
                        String companyIdValue = (String) field.get(parameterObject);
                        if (StringUtils.isBlank(companyIdValue)) {
                            field.set(parameterObject, companyId);
                            log.debug("{}添加设置companyId={}", interceptInfo, companyId);
                        }
                    }

                    field.setAccessible(false);
                }
            } catch (Exception ignored) {
            }
        }
//        else if ("prepare".equals(methodName)) {
//            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
//            BoundSql boundSql = statementHandler.getBoundSql();
//            String originalSql = boundSql.getSql();
//
//            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
//            if (CollectionUtils.isEmpty(parameterMappingList)) {
//                return invocation.proceed();
//            }
//            String property = parameterMappingList.get(0).getProperty();
//            if (parameterMappingList.size() == 1 && StringUtils.isNotBlank(property) && property.toLowerCase().contains("id")) {
//                return invocation.proceed();
//            }
//
//            // 数据隔离
//            String originalSql2 = originalSql;
//            if (CollectionUtils.isNotEmpty(isolationColumnList)) {
//                for (String isolationColumn : isolationColumnList) {
//                    if (LINE_COLUMN.equals(isolationColumn) && StrUtil.isNotBlank(lineId)) {
//                        originalSql2 = addIsolationCondition(originalSql2, isolationColumn, lineId);
//                    }
//                    if (WORKSHOP_COLUMN.equals(isolationColumn) && StrUtil.isNotBlank(workshopId)) {
//                        originalSql2 = addIsolationCondition(originalSql2, isolationColumn, workshopId);
//                    }
//                    if (COMPANY_COLUMN.equals(isolationColumn) && StrUtil.isNotBlank(companyId)) {
//                        originalSql2 = addIsolationCondition(originalSql2, isolationColumn, companyId);
//                    }
//                }
//            }
//
//            if (!originalSql2.equals(originalSql)) {
//                log.debug("数据隔离拦截器--查询sql：拦截前sql：{}", originalSql);
//                log.debug("数据隔离拦截器--查询sql：拦截后sql：{}", originalSql2);
//            }
//
//            ReflectUtil.setFieldValue(boundSql, "sql", originalSql2);
//        }
        else if ("query".equals(methodName)) {
            Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];

            if (null != mappedStatement) {
                String mappedStatementId = mappedStatement.getId();
                if (StringUtils.isNotBlank(mappedStatementId) && mappedStatementId.contains(".selectById")) {
                    // 根据id查询的不处理
                    return invocation.proceed();
                }
            }

            Object parameter = args[1];
            RowBounds rowBounds = (RowBounds) args[2];
            ResultHandler resultHandler = (ResultHandler) args[3];
            Executor executor = (Executor) invocation.getTarget();
            CacheKey cacheKey;
            BoundSql boundSql;
            // 由于逻辑关系，只会进入一次
            if (args.length == 4) {
                // 4个参数时
                boundSql = mappedStatement.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
            } else {
                // 6个参数时
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }

            // 数据隔离
            String sql = boundSql.getSql();
            String changedSql = sql;
            if (CollectionUtils.isNotEmpty(isolationColumnList)) {
                for (String isolationColumn : isolationColumnList) {
                    if (LINE_COLUMN.equals(isolationColumn) && StrUtil.isNotBlank(lineId)) {
                        changedSql = addIsolationCondition(changedSql, isolationColumn, lineId);
                    }
                    if (WORKSHOP_COLUMN.equals(isolationColumn) && StrUtil.isNotBlank(workshopId)) {
                        changedSql = addIsolationCondition(changedSql, isolationColumn, workshopId);
                    }
                    if (COMPANY_COLUMN.equals(isolationColumn) && StrUtil.isNotBlank(companyId)) {
                        changedSql = addIsolationCondition(changedSql, isolationColumn, companyId);
                    }
                }
            }

            if (!changedSql.equals(sql)) {
                log.debug("数据隔离拦截器--查询sql：拦截前sql：{}", sql);
                log.debug("数据隔离拦截器--查询sql：拦截后sql：{}", changedSql);
            }

            ReflectUtil.setFieldValue(boundSql, "sql", changedSql);

            // 下面的方法可以根据自己的逻辑调用多次，在分页插件中，count 和 page 各调用了一次
            return executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);

//            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
//            Object obj = invocation.getArgs()[1];
//            RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
//            ResultHandler resultHandler = (ResultHandler) invocation.getArgs()[3];
//            if (invocation.getArgs().length > 4) {
//                CacheKey cacheKey = (CacheKey) invocation.getArgs()[4];
//                BoundSql boundSql1 = (BoundSql) invocation.getArgs()[5];
//            }
//            String sqlId = mappedStatement.getId();
//            // 根据id查询的不处理
//            if (sqlId.contains(".selectById")) {
//                return invocation.proceed();
//            }
//            // 获取传入sql语句的参数对象
//            Object parameterObject = invocation.getArgs()[1];
//            BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
//            // 获取sql语句
//            String sql = boundSql.getSql();
//
//            // 数据隔离
//            String changedSql = sql;
//            if (CollectionUtils.isNotEmpty(isolationColumnList)) {
//                for (String isolationColumn : isolationColumnList) {
//                    if (LINE_COLUMN.equals(isolationColumn) && StrUtil.isNotBlank(lineId)) {
//                        changedSql = addIsolationCondition(changedSql, isolationColumn, lineId);
//                    }
//                    if (WORKSHOP_COLUMN.equals(isolationColumn) && StrUtil.isNotBlank(workshopId)) {
//                        changedSql = addIsolationCondition(changedSql, isolationColumn, workshopId);
//                    }
//                    if (COMPANY_COLUMN.equals(isolationColumn) && StrUtil.isNotBlank(companyId)) {
//                        changedSql = addIsolationCondition(changedSql, isolationColumn, companyId);
//                    }
//                }
//            }
//
//            if (!changedSql.equals(sql)) {
//                log.debug("数据隔离拦截器--查询sql：拦截前sql：{}", sql);
//                log.debug("数据隔离拦截器--查询sql：拦截后sql：{}", changedSql);
//            }
//
//            ReflectUtil.setFieldValue(boundSql, "sql", changedSql);
//            System.out.println(1111);
        }

        return invocation.proceed();
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private LoginUser getLoginUser() {
        LoginUser sysUser;
        try {
            sysUser = SecurityUtils.getSubject().getPrincipal() != null ? (LoginUser) SecurityUtils.getSubject().getPrincipal() : null;
        } catch (Exception ex) {
            // ex.printStackTrace();
            sysUser = null;
        }
        return sysUser;
    }

    private void initIsolationTableColumn() {
        isolationTableNameList = new ArrayList<>(Arrays.asList(tableNames.split(",")));
        isolationColumnList = new ArrayList<>(Arrays.asList(isolationColumns.split(",")));
    }

    private String addIsolationCondition(String sql, String columnName, String columnValue) {
        if (StringUtils.isBlank(sql) || StringUtils.isBlank(columnName) || StringUtils.isBlank(columnValue)) {
            return sql;
        }
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dialect);

        if (statementList == null || statementList.size() == 0) {
            return sql;
        }

        SQLStatement sqlStatement = statementList.get(0);
        if (sqlStatement instanceof SQLSelectStatement) {
            SQLSelectQuery sqlSelectQuery = ((SQLSelectStatement) sqlStatement).getSelect().getQuery();
            if (sqlSelectQuery instanceof SQLUnionQuery) {
                SQLUnionQuery queryObject = (SQLUnionQuery) sqlSelectQuery;
                SQLSelectQueryBlock firstQueryBlock = queryObject.getFirstQueryBlock();
                addSelectStatementCondition(firstQueryBlock, firstQueryBlock.getFrom(), columnName, columnValue);
            } else {
                SQLSelectQueryBlock queryObject = (SQLSelectQueryBlock) sqlSelectQuery;
                addSelectStatementCondition(queryObject, queryObject.getFrom(), columnName, columnValue);
            }
        }

        return SQLUtils.toSQLString(statementList, dialect);
    }

    private void addSelectStatementCondition(SQLSelectQueryBlock queryObject, SQLTableSource from, String columnName, String columnValue) {
        if (StringUtils.isBlank(columnName) || from == null || queryObject == null) {
            return;
        }

        SQLExpr originCondition = queryObject.getWhere();
        if (from instanceof SQLExprTableSource) {
            String tableName = ((SQLIdentifierExpr) ((SQLExprTableSource) from).getExpr()).getName();
            String alias = from.getAlias();
            SQLExpr newCondition = newColumnCondition(tableName, alias, columnName, columnValue, originCondition);
            queryObject.setWhere(newCondition);
        } else if (from instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinObject = (SQLJoinTableSource) from;
            SQLTableSource left = joinObject.getLeft();
            SQLTableSource right = joinObject.getRight();

            addSelectStatementCondition(queryObject, left, columnName, columnValue);
            addRightJoinSelectStatementCondition(joinObject, right, columnName, columnValue);
        } else if (from instanceof SQLSubqueryTableSource) {
            SQLSelect subSelectObject = ((SQLSubqueryTableSource) from).getSelect();
            if (!subSelectObject.getQuery().getClass().equals(SQLUnionQuery.class)) {
                SQLSelectQueryBlock subQueryObject = (SQLSelectQueryBlock) subSelectObject.getQuery();
                addSelectStatementCondition(subQueryObject, subQueryObject.getFrom(), columnName, columnValue);
            }
        } else if (from instanceof SQLUnionQueryTableSource) {
            SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLUnionQueryTableSource) from).getUnion().getFirstQueryBlock();
            addSelectStatementCondition(sqlSelectQueryBlock, sqlSelectQueryBlock.getFrom(), columnName, columnValue);
        } else {
            throw new NotImplementedException("数据隔离拦截器：未处理异常");
        }
    }

    private void addRightJoinSelectStatementCondition(SQLJoinTableSource joinObject, SQLTableSource right, String columnName, String columnValue) {
        SQLExpr condition = joinObject.getCondition();

        // 特殊查询不处理
        if (right instanceof SQLSubqueryTableSource){
            return;
        }

        String tableName = ((SQLIdentifierExpr) ((SQLExprTableSource) right).getExpr()).getName();

        String alias = right.getAlias();

        SQLExpr newCondition = newColumnCondition(tableName, alias, columnName, columnValue, condition);

        joinObject.setCondition(newCondition);
    }

    private SQLExpr newColumnCondition(String tableName, String tableAlias, String columnName, String columnValue, SQLExpr originCondition) {
        boolean needIsolation = needIsolationByTableColumn(tableName, columnName, columnValue);
        if (!needIsolation) {
            return originCondition;
        }

//        if (LINE_COLUMN.equals(columnName)) {
//            return newMultipleEqualityCondition(tableName, tableAlias, columnName, columnValue, originCondition);
//        } else {
//            return newEqualityCondition(tableName, tableAlias, columnName, columnValue, originCondition);
//        }
        // 如果数据库中数据存在“CJ1,CJ2”这种，需要用like方式
        if (LINE_COLUMN.equals(columnName)) {
            return newMultipleLikeCondition(tableName, tableAlias, columnName, columnValue, originCondition);
        } else {
            return newLikeCondition(tableName, tableAlias, columnName, columnValue, originCondition);
        }
    }

    private SQLExpr newEqualityCondition(String tableName, String tableAlias, String columnName, String columnValue, SQLExpr originCondition) {
        String filedName = StringUtils.isBlank(tableAlias) ? columnName : tableAlias + "." + columnName;
        SQLExpr condition = new SQLBinaryOpExpr(new SQLIdentifierExpr(filedName), new SQLCharExpr(columnValue), SQLBinaryOperator.Equality);

        if ("true".equals(ignoreDbNullData)) {
            condition = newNullIsCondition(filedName, condition);
        }

        return SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, condition, false, originCondition);
    }

    private SQLExpr newMultipleEqualityCondition(String tableName, String tableAlias, String columnName, String columnValue, SQLExpr originCondition) {
        String filedName = StringUtils.isBlank(tableAlias) ? columnName : tableAlias + "." + columnName;
        SQLExpr condition = new SQLBinaryOpExpr(new SQLIdentifierExpr(filedName), new SQLCharExpr(columnValue), SQLBinaryOperator.Equality);

        if ("true".equals(ignoreDbNullData)) {
            condition = newNullIsCondition(filedName, condition);
        }

        if (columnValue.contains(",")) {
            for (String columnValueItem : columnValue.split(",")) {
                SQLExpr itemCondition = new SQLBinaryOpExpr(new SQLIdentifierExpr(filedName), new SQLCharExpr(columnValueItem), SQLBinaryOperator.Equality);
                condition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanOr, itemCondition, false, condition);
            }
        }

        return SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, condition, false, originCondition);
    }

    private SQLExpr newNullIsCondition(String filedName, SQLExpr condition) {
        SQLExpr nullCondition = new SQLBinaryOpExpr(new SQLIdentifierExpr(filedName), new SQLNullExpr(), SQLBinaryOperator.Is);
        condition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanOr, nullCondition, false, condition);

        return condition;
    }

    private SQLExpr newLikeCondition(String tableName, String tableAlias, String columnName, String columnValue, SQLExpr originCondition) {
        String filedName = StringUtils.isBlank(tableAlias) ? columnName : tableAlias + "." + columnName;
        SQLExpr condition = new SQLBinaryOpExpr(new SQLIdentifierExpr(filedName), new SQLCharExpr("%" + columnValue + "%"), SQLBinaryOperator.Like);

        if ("true".equals(ignoreDbNullData)) {
            condition = newNullIsCondition(filedName, condition);
        }

        return SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, condition, false, originCondition);
    }

    private SQLExpr newMultipleLikeCondition(String tableName, String tableAlias, String columnName, String columnValue, SQLExpr originCondition) {
        String filedName = StringUtils.isBlank(tableAlias) ? columnName : tableAlias + "." + columnName;
        SQLExpr condition = new SQLBinaryOpExpr(new SQLIdentifierExpr(filedName), new SQLCharExpr("%" + columnValue + "%"), SQLBinaryOperator.Like);

        if ("true".equals(ignoreDbNullData)) {
            condition = newNullIsCondition(filedName, condition);
        }

        if (columnValue.contains(",")) {
            for (String columnValueItem : columnValue.split(",")) {
                SQLExpr conditionItem = new SQLBinaryOpExpr(new SQLIdentifierExpr(filedName), new SQLCharExpr("%" + columnValueItem + "%"), SQLBinaryOperator.Like);
                condition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanOr, conditionItem, false, condition);
            }
        }
        return SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, condition, false, originCondition);
    }

    private boolean needIsolationByTableColumn(String tableName, String columnName, String columnValue) {
        // 不是需要数据隔离的表
        if (!isolationTableNameList.contains(tableName)) {
            return false;
        }
        // 不是需要数据隔离的字段
        if (!isolationColumnList.contains(columnName)) {
            return false;
        }
        // 隔离字段的值为空
        if (columnValue == null) {
            return false;
        }
        return true;
    }

    private boolean needIsolationByInsertSql(String sql) {
        String tableNameFromSql = getTableNameFromInsertSql(sql);
        for (String tableName : isolationTableNameList) {
            if (tableNameFromSql.equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    private String getTableNameFromInsertSql(String sql) {
        String table = sql;
        table = table.toLowerCase();
        table = table.substring(0, table.indexOf("("));
        if (table.contains("insert into")) {
            table = table.replaceAll("insert into", "");
        } else {
            table = table.replaceAll("insert ", "");
        }
        table = table.trim();
        return table;
    }

}
