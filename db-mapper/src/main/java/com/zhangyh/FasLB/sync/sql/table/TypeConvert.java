package com.zhangyh.FasLB.sync.sql.table;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.sql.JDBCType.*;

/**
 * @author zhangyh
 * @Date 2023/4/10 13:21
 * @desc 默认数据类型对应
 */
public class TypeConvert {
    //java类型映射jdbc类型 一对多
    private static final MultiValueMap<Class<?>, String> JAVA_2_JDBC=new LinkedMultiValueMap<>();


    //jdbc类型映射java类型 一对一
    private static final Map<String,Class<?>> JDBC_2_JAVA=new ConcurrentHashMap<>();

    private static final List<String> IGNORE_LENGTH=new ArrayList<>();

    private static void registry(String jdbcType,Class<?> javaType){
        JAVA_2_JDBC.add(javaType,jdbcType);
        JDBC_2_JAVA.put(jdbcType,javaType);
    }

    public static String getJdbcType(Class<?> javaType,Integer length){
        if(String.class.equals(javaType)&&length==1024){
            return "LONGTEXT";
        }
        return JAVA_2_JDBC.get(javaType).get(0);
    }

    public static Boolean ignoreLength(String jdbcType) {
        return IGNORE_LENGTH.contains(jdbcType);
    }

    static {
        registry(BOOLEAN.name(), Boolean.class);
        registry(BIT.name(), Boolean.class);

        registry(TINYINT.name(), Byte.class);

        registry(SMALLINT.name(), Short.class);

        registry("INT", Integer.class);
        registry(INTEGER.name(), Integer.class);

        registry(INTEGER.name(), int.class);
        registry("INT", int.class);

        registry(FLOAT.name(), Float.class);

        registry(DOUBLE.name(), Double.class);

        registry(VARCHAR.name(), String.class);
        registry(LONGVARCHAR.name(), String.class);
        registry(CHAR.name(), String.class);
        registry("LONGTEXT", String.class);
        registry("TEXT", String.class);

        registry(BIGINT.name(), Long.class);

        registry(DECIMAL.name(), BigDecimal.class);

        registry(BLOB.name(), Byte[].class);
        registry(BLOB.name(), byte[].class);
        registry(TIMESTAMP.name(), LocalDateTime.class);

        registry("DATETIME", Date.class);
        registry(TIMESTAMP.name(), Date.class);

        IGNORE_LENGTH.add("LONGTEXT");
        IGNORE_LENGTH.add("TEXT");
        IGNORE_LENGTH.add(INTEGER.name());
        IGNORE_LENGTH.add(BOOLEAN.name());
        IGNORE_LENGTH.add(BIGINT.name());
        IGNORE_LENGTH.add("INT");
        IGNORE_LENGTH.add(TINYINT.name());
        IGNORE_LENGTH.add(SMALLINT.name());
        IGNORE_LENGTH.add(TIMESTAMP.name());
        IGNORE_LENGTH.add("DATETIME");
        IGNORE_LENGTH.add(BLOB.name());
    }
}
