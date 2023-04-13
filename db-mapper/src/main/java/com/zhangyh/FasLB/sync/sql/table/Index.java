package com.zhangyh.FasLB.sync.sql.table;

/**
 * @author zhangyh
 * @Date 2023/4/10 11:21
 * @desc
 */
public interface Index {

    /**
     * 创建索引
     *
     * @param field 索引的字段
     * @return 创建索引的SQL语句
     */
    String createScript(String field);

    /**
     * 修改索引
     *
     * @param tableName 表名
     * @param field     索引字段
     * @return 修改索引的SQL语句
     */
    String alterScript(String tableName, String field);

    /**
     * 删除索引
     * @param tableName 　表明
     * @param field     　索引的字段
     * @return 删除索引的SQL语句
     */
    String dropScript( String field,String tableName);

}
