package com.zhangyh.FasLB.sync.sql.script;


import com.zhangyh.FasLB.sync.sql.table.Index;
import com.zhangyh.FasLB.sync.sql.table.mysql.index.MUL;
import com.zhangyh.FasLB.sync.sql.table.mysql.index.UNI;
import com.zhangyh.FasLB.sync.sql.table.IndexHelper;
import com.zhangyh.FasLB.sync.sql.table.TableSchema;
import com.zhangyh.FasLB.sync.sql.table.mysql.index.PRI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zhangyh
 * @Date 2023/4/10 16:25
 * @desc
 */
@Slf4j
public class SqlBuilder {

    private static final String CREATE_TEMPLATE = "CREATE TABLE IF NOT EXISTS `{0}` (\n" +
            "{1}\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT={2};";

    private static final String ADD_TEMPLATE = "alter table `{0}` add column {1}";
    private static final String MODIFY_TEMPLATE = "alter table `{0}` modify column {1}";
    private static final String DROP_TEMPLATE = "alter table `{0}` drop column `{1}`";
    private static final String ALTER_AUTO_INCREMENT = "alter table `{0}` modify {1}  auto_increment";
    private static final String DROP_TABLE = "drop table `{0}`";
    private static final String CHANG_TABLE = "ALTER TABLE `{0}` change `{0}`";
    private static final String ALTER_TABLE_COMMENT = "alter table `{0}` comment = {1}";
    private static final String TABLE_COMMENT = "show table status from {0} like `{1}`";

    /**
     * 构建创建表的语句
     *
     * @param schema 表结构
     * @return SQL
     */
    public String buildCreateTableSql(TableSchema schema) {
        if (schema == null) {
            throw new RuntimeException("缺失表信息");
        }
        String tableName = schema.getTableName();
        StringBuilder fieldStrBuilder = new StringBuilder();
        List<TableSchema.Field> fields = schema.getFields();

        //建表后添加索引语句
        StringBuilder index = new StringBuilder();

        for (int i = 0; i < fields.size(); i++) {
            TableSchema.Field field = fields.get(i);
            //构建索引语句
            if (field.getIndexType() instanceof PRI) {
                Index pri = (Index) IndexHelper.getIndex("PRI");
                String sql = pri.alterScript(tableName, field.getName());
                index.append(sql).append(";").append("\r\n");
                if (field.getAutoIncrement()) {
                    String autoIncrement = MessageFormat.format(ALTER_AUTO_INCREMENT, tableName, buildFieldSql(field));
                    index.append(autoIncrement).append(";").append("\r\n");
                }
            } else if (field.getIndexType() instanceof UNI) {
                Index pri = (Index) IndexHelper.getIndex("UNI");
                String sql = pri.alterScript(tableName, field.getName());
                index.append(sql).append(";").append("\r\n");
            } else if (field.getIndexType() instanceof MUL) {
                Index pri = (Index) IndexHelper.getIndex("MUL");
                String sql = pri.alterScript(tableName, field.getName());
                index.append(sql).append(";").append("\r\n");
            }
            String fieldSql = buildFieldSql(field);
            fieldStrBuilder.append(fieldSql);
            // 最后一个字段后没有逗号和换行
            if (i != fields.size() - 1) {
                fieldStrBuilder.append(",");
                fieldStrBuilder.append("\n");
            }
        }
        String fieldInfo = fieldStrBuilder.toString();
        //填充模板
        String table = MessageFormat.format(CREATE_TEMPLATE, tableName, fieldInfo, String.format("'%s'", schema.getTableComment()));
        return table + "\r\n" + index;
    }

    /**
     * 构建字段
     *
     * @param filed 字段
     * @return 字段
     */
    public String buildFieldSql(TableSchema.Field filed) {
        if (filed == null) {
            throw new RuntimeException("缺少字段信息");
        }
        String name = filed.getName().toLowerCase();
        Integer length = filed.getLength();
        Integer decimalPoint = filed.getDecimalPoint();
        String type = filed.getType();
        Boolean allowNull = filed.getAllowNull();
        String defaultValue = filed.getDefaultValue();
        String comment = filed.getComment();
        StringBuilder fieldStringBuilder = new StringBuilder();
        //字段模板  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键'
        //字段名
        fieldStringBuilder.append("`").append(name).append("`");
        //字段类型
        fieldStringBuilder.append(" ").append(type);
        //字段长度 有小数点设置小数点 money(10,2)
        if (decimalPoint != null) {
            fieldStringBuilder.append(String.format("(%d,%d)", length, decimalPoint));
        }
        if (decimalPoint == null && length != null) {
            fieldStringBuilder.append(String.format("(%d)", length));
        }
        //默认值
        if (StringUtils.isNotBlank(defaultValue)) {
            fieldStringBuilder.append(" ").append(String.format("default '%s'", defaultValue));
        }
        //是否为空
        fieldStringBuilder.append(" ").append(allowNull ? "null" : "not null");
        //注释
        if (StringUtils.isNotBlank(comment)) {
            fieldStringBuilder.append(" ").append(String.format("comment '%s'", comment));
        }
        return fieldStringBuilder.toString();
    }

    /**
     * 构建Model与数据库对应的修改sql语句
     *
     * @param modelTable    /
     * @param dbTableSchema /
     * @return sql语句
     */
    public String buildAlterTableSql(TableSchema modelTable, TableSchema dbTableSchema) {
        String tableName = modelTable.getTableName();
        String modelTableTableComment = modelTable.getTableComment();
        String dbTableSchemaTableComment = dbTableSchema.getTableComment();
        StringBuilder allDrop = null;
        //索引修改与创建语句
        StringBuilder indexCreate = new StringBuilder();
        StringBuilder scriptBuilder = new StringBuilder();
        //表注释修改语句
        if (!modelTableTableComment.equals(dbTableSchemaTableComment)) {
            String dbComment = MessageFormat.format(ALTER_TABLE_COMMENT, tableName, String.format("'%s'", modelTableTableComment));
            scriptBuilder.append(dbComment).append(";").append("\r\n");
        }
        //转为 字段名->字段对象
        Map<String, TableSchema.Field> modelField = modelTable.getFields().stream().collect(Collectors.toMap(TableSchema.Field::getName, e -> e));
        Map<String, TableSchema.Field> dbField = dbTableSchema.getFields().stream().collect(Collectors.toMap(TableSchema.Field::getName, e -> e));
        //删除多余的字段
        if (modelTable.getDelOldField()) {
            AtomicInteger atomicInteger = new AtomicInteger();
            dbField.forEach((fk, fv) -> {
                TableSchema.Field field = modelField.get(fk);
                if (field == null) {
                    atomicInteger.getAndIncrement();
                    String dropColum = MessageFormat.format(DROP_TEMPLATE, tableName, fk);
                    scriptBuilder.append(dropColum).append(";").append("\r\n");
                }
            });
            //如果字段同时删除则留非主键到最后删
            if (atomicInteger.get() == dbField.size()) {
                allDrop = new StringBuilder(scriptBuilder);
                scriptBuilder.setLength(0);
            }
        }
        modelField.forEach((k, v) -> {
            TableSchema.Field field = dbField.get(k);
            if (field == null) {
                //数据库中不存在字段则添加字段
                String fieldSql = buildFieldSql(v);
                String addColumSql = MessageFormat.format(ADD_TEMPLATE, tableName, fieldSql);
                scriptBuilder.append(addColumSql).append(";").append("\r\n");
                if (v.getIndexType() != null) {
                    Index index = (Index) IndexHelper.getIndex(v.getIndexType().getClass().getName().substring(v.getIndexType().getClass().getName().lastIndexOf(".") + 1));
                    String indexSql = index.alterScript(tableName, v.getName());
                    indexCreate.append(indexSql).append(";").append("\r\n");
                    //主键且自增且为INT则修改自增语句
                    if (v.getIndexType() instanceof PRI && v.getAutoIncrement() && v.getType().equals("INT")) {
                        String auto_increment = MessageFormat.format(ALTER_AUTO_INCREMENT, tableName, buildFieldSql(v));
                        indexCreate.append(auto_increment).append(";").append("\r\n");
                    }
                }
                return;
            }
            if (v.equals(field)) {
                return;
            }
            //直接修改字段
            String fieldSql = buildFieldSql(v);
            String modifyColumSql = MessageFormat.format(MODIFY_TEMPLATE, tableName, fieldSql);
            scriptBuilder.append(modifyColumSql).append(";").append("\r\n");

            if (v.getIndexType() == null && field.getIndexType() != null) {
                //实体删除了索引但数据库还有索引，删除索引
                Index index = (Index) IndexHelper.getIndex(field.getIndexType().getClass().getName().substring(field.getIndexType().getClass().getName().lastIndexOf(".") + 1));
                String dropScript = index.dropScript(v.getName(), tableName);
                indexCreate.append(dropScript).append(";").append("\r\n");
            }
            if (v.getIndexType() != null && field.getIndexType() != null && !field.getIndexType().getClass().getName().equals(v.getIndexType().getClass().getName())) {
                //都有索引，但索引类型不一致
                Index index = (Index) IndexHelper.getIndex(field.getIndexType().getClass().getName().substring(field.getIndexType().getClass().getName().lastIndexOf(".") + 1));
                String dropScript = index.dropScript(v.getName(), tableName);
                indexCreate.append(dropScript).append(";").append("\r\n");
                Index createIndex = (Index) IndexHelper.getIndex(v.getIndexType().getClass().getName().substring(v.getIndexType().getClass().getName().lastIndexOf(".") + 1));
                String createScript = createIndex.alterScript(tableName, v.getName());
                indexCreate.append(createScript).append(";").append("\r\n");
                if(v.getAutoIncrement()&&!field.getAutoIncrement()&&v.getIndexType() instanceof PRI){
                    String autoIncrement = MessageFormat.format(ALTER_AUTO_INCREMENT, tableName, buildFieldSql(field));
                    scriptBuilder.append(autoIncrement).append(";").append("\r\n");
                }
            }
            if (v.getIndexType() != null && field.getIndexType() == null) {
                //实体存在索引数据库不存在索引
                Index index = (Index) IndexHelper.getIndex(v.getIndexType().getClass().getName().substring(v.getIndexType().getClass().getName().lastIndexOf(".") + 1));
                String createScript = index.alterScript(tableName, v.getName());
                indexCreate.append(createScript).append(";").append("\r\n");
                if(v.getAutoIncrement()&&!field.getAutoIncrement()&&v.getIndexType() instanceof PRI){
                    String autoIncrement = MessageFormat.format(ALTER_AUTO_INCREMENT, tableName, buildFieldSql(field));
                    scriptBuilder.append(autoIncrement).append(";").append("\r\n");
                }
            }
        });

        if (allDrop != null) {
            scriptBuilder.append(allDrop);
        }
        if (indexCreate.length() > 0) {
            scriptBuilder.append(indexCreate);
        }
        return scriptBuilder.toString();
    }

    /**
     * 删除表语句
     *
     * @param tableName
     * @return
     */
    public String buildDropTableSql(String tableName) {
        return MessageFormat.format(DROP_TABLE, tableName);
    }
}
