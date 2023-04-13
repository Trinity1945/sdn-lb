package com.zhangyh.FasLB.sync.core;


import cn.hutool.core.util.ModifierUtil;
import com.zhangyh.FasLB.sync.annotation.IgnoreField;
import com.zhangyh.FasLB.sync.annotation.TableSync;
import com.zhangyh.FasLB.sync.sql.script.SqlBuilder;
import com.zhangyh.FasLB.sync.sql.table.Index;
import com.zhangyh.FasLB.sync.sql.table.TypeConvert;
import com.zhangyh.FasLB.sync.sql.table.IndexHelper;
import com.zhangyh.FasLB.sync.sql.table.TableSchema;
import com.zhangyh.FasLB.sync.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangyh
 * @Date 2023/4/10 10:37
 * @desc 将被@TableSync注解的类注入Spring后进行处理
 */
@Component
@RequiredArgsConstructor
public class TableSyncBeanPostProcessor implements BeanPostProcessor, InitializingBean {

    private final JdbcTemplate jdbcTemplate;

    private final DataSource dataSource;

    private static String DB_NAME;

    public static final Logger LOGGER = LoggerFactory.getLogger(SyncScannerRegistry.class);

    private static final Map<String, TableSchema> DB_TABLE_SCHEMAS = new ConcurrentHashMap<>();

    private final SqlBuilder sqlBuilder = new SqlBuilder();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(TableSync.class)) {
            TableSchema tableSchema = buildTableSchema(bean);
            TableSchema dbTableSchema = DB_TABLE_SCHEMAS.get(tableSchema.getTableName());
            if (dbTableSchema == null) {
                //直接构建新表
                createTable(tableSchema);
            } else if (!tableSchema.equals(dbTableSchema)) {
                //修改差异
                processTableSync(tableSchema, dbTableSchema);
            }
        }
        return bean;
    }

    /**
     * 将TableSchema转为SQL语句并执行SQL语句建表
     *
     * @param tableSchema /
     */
    private void createTable(TableSchema tableSchema) {
        String createScript = sqlBuilder.buildCreateTableSql(tableSchema);
        if(StringUtils.isNotBlank(createScript)){
            LOGGER.info("execute sql :\r\n{}", createScript);
        }
        jdbcTemplate.execute((ConnectionCallback<? extends Object>) con -> {
            Statement statement = con.createStatement();
            try {
                String[] sqlArr = createScript.split(";");
                for (String s : sqlArr) {
                    if (StringUtils.isBlank(s)) {
                        continue;
                    }
                    statement.addBatch(s); // 添加到批处理中
                }
                statement.executeBatch(); // 执行批处理
                return null;
            } finally {
                statement.close(); // 关闭 Statement 对象
                con.close(); // 关闭连接
            }
        });
    }

    /**
     * 处理结构不同的情况
     *
     * @param modelTable
     * @param dbTableSchema
     */
    private void processTableSync(TableSchema modelTable, TableSchema dbTableSchema) {
        String alterTableSql = sqlBuilder.buildAlterTableSql(modelTable, dbTableSchema);
        if(StringUtils.isNotBlank(alterTableSql)){
            LOGGER.info("execute sql :\r\n{}", alterTableSql);
        }
        jdbcTemplate.execute((ConnectionCallback<? extends Object>) con -> {
            Statement statement = con.createStatement();
            try {
                String[] sqlArr = alterTableSql.split(";");
                for (String s : sqlArr) {
                    if (StringUtils.isBlank(s)) {
                        continue;
                    }
                    statement.addBatch(s); // 添加到批处理中
                }
                statement.executeBatch(); // 执行批处理
                return null;
            } finally {
                statement.close(); // 关闭 Statement 对象
                con.close(); // 关闭连接
            }
        });
    }

    /**
     * 获取数据库中的表名
     */
    private List<String> getDbTables() {
        return jdbcTemplate.execute((ConnectionCallback<List<String>>) con -> {
            List<String> tables = new ArrayList<>();
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");
            while (resultSet.next()) {
                String table = resultSet.getString(1);
                tables.add(table);
            }
            return tables;
        });
    }

    /**
     * 获取数据库中的表结构，构造成tableSchema
     *
     * @param tableName 表名
     * @return 抽象table结构
     */
    private TableSchema getDbTableSchema(String tableName) {
        return jdbcTemplate.execute((ConnectionCallback<TableSchema>) con -> {
            TableSchema tableSchema = new TableSchema();
            tableSchema.setTableName(tableName);
            tableSchema.setDbName(DB_NAME);
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("show full fields from " + tableName);
            while (resultSet.next()) {
                // 获取字段名称
                String columnName = resultSet.getString("Field");
                // 获取字段类型
                String columnType = resultSet.getString("Type");
                String[] typeAndLength = columnType.split("\\(");
                // 获取字段是否允许为空
                boolean isNullable = "YES".equals(resultSet.getString("Null"));
                // 获取字段默认值
                String defaultValue = resultSet.getString("Default");
                // 获取字段注释
                String comment = resultSet.getString("Comment");
                String columnKey = resultSet.getString("Key");
                String columnExtra = resultSet.getString("Extra");

                // 输出结果
//                System.out.printf("%-20s%-20s%-10s%-20s%-20s%-20s%s\n", columnName, columnType, isNullable, defaultValue, comment,columnKey,columnExtra);
                TableSchema.Field field = TableSchema.Field.builder()
                        .name(columnName)
                        .type(typeAndLength[0].toUpperCase())
                        .allowNull(isNullable)
                        .defaultValue(defaultValue)
                        .comment(comment)
                        .indexType(StringUtils.isNotBlank(columnKey) ? (Index) IndexHelper.getIndex(columnKey) : null)
                        .autoIncrement(StringUtils.isNotBlank(columnExtra) && columnExtra.equals("auto_increment"))
                        .length(typeAndLength.length == 2 && !TypeConvert.ignoreLength(typeAndLength[0].toUpperCase()) && !isDecimal(typeAndLength[0].toUpperCase()) ? Integer.valueOf(typeAndLength[1].replace(")", "")) : isDecimal(typeAndLength[0].toUpperCase()) ? Integer.valueOf(typeAndLength[1].substring(0, typeAndLength[1].indexOf(","))) : null)
                        .decimalPoint(isDecimal(typeAndLength[0].toUpperCase()) ? Integer.valueOf(typeAndLength[1].replace(")", "").substring(typeAndLength[1].indexOf(",") + 1)) : null)
                        .build();

                tableSchema.addFiled(field);
            }
            Statement conStatement = con.createStatement();
            ResultSet res = conStatement.executeQuery("show table status from `" + DB_NAME + "` like '" + tableName + "'");
            if (res.next()) {
                String comment = res.getString("Comment");
                tableSchema.setTableComment(comment);
            }
            return tableSchema;
        });
    }

    /**
     * 构建Table抽象对象
     */
    private TableSchema buildTableSchema(Object c) {
        TableSync annotation = c.getClass().getAnnotation(TableSync.class);
        String tableName= annotation.tableName();
        if(StringUtils.isBlank(tableName)){
            tableName= annotation.value();
        }
        TableSchema table = TableSchema.builder()
                .tableName(StringUtils.isBlank(tableName) ?
                        StringUtil.toUnderScoreCase(c.getClass().getName().substring(c.getClass().getName().lastIndexOf(".") + 1)) :
                        tableName)
                .fields(new ArrayList<>())
                .tableComment(annotation.tableComment())
                .delOldField(annotation.delOldField())
                .build();
        Field[] declaredFields = c.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            IgnoreField ignoreField = declaredField.getAnnotation(IgnoreField.class);
            if (ignoreField != null|| ModifierUtil.hasModifier(declaredField, ModifierUtil.ModifierType.STATIC)) {
                continue;
            }
           com.zhangyh.FasLB.sync.annotation.Field field = declaredField.getAnnotation(com.zhangyh.FasLB.sync.annotation.Field.class);
            if (field != null) {
                String fieldName = field.value();
                if (StringUtils.isBlank(fieldName)) {
                    fieldName = field.field();
                }
                boolean autoIncrement = field.autoIncrement();
                boolean allowNull = field.allowNull();
                String defaultValue = field.defaultValue();
                int point = field.decimalPoint();
                String comment = field.comment();
                Class<? extends Index> index = field.index();
                int length = field.length();
                JDBCType jdbcType = field.jdbcType();
                TableSchema.Field buildField = TableSchema.Field.builder()
                        .name(StringUtils.isNotBlank(fieldName) ? fieldName : StringUtil.toUnderScoreCase(declaredField.getName()))
                        .type(jdbcType.equals(JDBCType.NULL) ? TypeConvert.getJdbcType(declaredField.getType(), length) : jdbcType.getName())
                        .indexType(index.isInterface() ? null : (Index) IndexHelper.getIndex(index.getName().substring(index.getName().lastIndexOf(".") + 1)))
                        .comment(StringUtils.isNotBlank(comment) ? comment : null)
                        .allowNull(allowNull)
                        .autoIncrement(autoIncrement)
                        .defaultValue(StringUtils.isNotBlank(defaultValue) ? defaultValue : null)
                        .build();
                //设置数据长度
                if (jdbcType.equals(JDBCType.NULL)) {
                    String type = TypeConvert.getJdbcType(declaredField.getType(), length);
                    if (!TypeConvert.ignoreLength(type)) {
                        buildField.setLength(length);
                        if (isDecimal(type)) {
                            buildField.setDecimalPoint(point);
                        }
                    }
                } else if (!TypeConvert.ignoreLength(jdbcType.getName())) {
                    buildField.setLength(length);
                    if (isDecimal(jdbcType.getName())) {
                        buildField.setDecimalPoint(point);
                    }
                }
                table.addFiled(buildField);
            } else {
                //无注解则直接自动生成
                TableSchema.Field buildField = TableSchema.Field.builder()
                        .name(StringUtil.toUnderScoreCase(declaredField.getName()))
                        .type(TypeConvert.getJdbcType(declaredField.getType(), 255))
                        .autoIncrement(declaredField.getName().equals("id"))
                        .allowNull(!declaredField.getName().equals("id"))
                        .comment(null)
                        .decimalPoint(isDecimal(TypeConvert.getJdbcType(declaredField.getType(), 255).toLowerCase()) ? 4 : null)
                        .indexType(declaredField.getName().equals("id") ? (Index) IndexHelper.getIndex("PRI") : null)
                        .defaultValue(null)
                        .length(TypeConvert.ignoreLength(TypeConvert.getJdbcType(declaredField.getType(), 255)) ? null : 255)
                        .build();
                table.addFiled(buildField);
            }
        }
        return table;
    }

    private boolean isDecimal(String type) {
        return type.equals("DECIMAL") || type.equals("DOUBLE") || type.equals("FLOAT");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        DB_NAME = dataSource.getConnection().getCatalog();
        List<String> dbTables = getDbTables();
        dbTables.forEach(tableName -> {
            TableSchema dbTableSchema = getDbTableSchema(tableName);
            DB_TABLE_SCHEMAS.put(tableName, dbTableSchema);
        });
    }
}
