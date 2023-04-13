package com.zhangyh.FasLB.sync.sql.table.mysql.index;

import com.zhangyh.FasLB.sync.sql.table.Index;

import java.text.MessageFormat;

/**
 * @author zhangyh
 * @Date 2023/4/10 11:28
 * @desc 主键索引
 */
public class PRI implements Index {

    private static final String CREATE_TEMPLATE="PRIMARY KEY (`{0}`) USING BTREE";
    private static final String ALTER_TEMPLATE="ALTER TABLE `{0}` ADD PRIMARY KEY ( `{1}` ) ";
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
        return MessageFormat.format(DROP_TEMPLATE, field + "_" + PRI.class.getName().substring(PRI.class.getName().lastIndexOf(".")+1), tableName);
    }
}
