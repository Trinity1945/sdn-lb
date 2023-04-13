package com.zhangyh.FasLB.sync.sql.table.mysql.index;

import com.zhangyh.FasLB.sync.sql.table.Index;
import lombok.ToString;

import java.text.MessageFormat;

/**
 * @author zhangyh
 * @Date 2023/4/10 11:37
 * @desc 唯一索引
 */
@ToString
public class UNI implements Index {

    private static final String CREATE_TEMPLATE="UNIQUE KEY `{0}_uni` (`{0}`) USING BTREE";
    private static final String ALTER_TEMPLATE="ALTER TABLE `{0}` ADD UNIQUE `{1}_uni` (`{1}`)";
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
        return MessageFormat.format(DROP_TEMPLATE, field + "_" + UNI.class.getName().substring(UNI.class.getName().lastIndexOf(".")+1), tableName);
    }
}
