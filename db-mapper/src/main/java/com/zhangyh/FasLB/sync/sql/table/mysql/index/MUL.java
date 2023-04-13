package com.zhangyh.FasLB.sync.sql.table.mysql.index;

import com.zhangyh.FasLB.sync.sql.table.Index;
import lombok.ToString;

import java.text.MessageFormat;

/**
 * @author zhangyh
 * @Date 2023/4/10 11:37
 * @desc 普通索引
 */
@ToString
public class MUL implements Index {

    private static final String CREATE_TEMPLATE="KEY `{0}_mul` (`{0}`) USING BTREE";
    private static final String ALTER_TEMPLATE="ALTER TABLE `{0}` ADD INDEX `{1}_mul` (`{1}`)";
    private static final String DROP_TEMPLATE="drop index `{0}` on `{1}`";

    @Override
    public String createScript( String field) {
        return   MessageFormat.format(CREATE_TEMPLATE,field);
    }

    @Override
    public String alterScript( String tableName, String field) {
        return MessageFormat.format(ALTER_TEMPLATE,tableName,field);
    }

    @Override
    public String dropScript(  String field,String tableName) {
        return MessageFormat.format(DROP_TEMPLATE, field + "_" + MUL.class.getName().substring(MUL.class.getName().lastIndexOf(".")+1), tableName);
    }
}
