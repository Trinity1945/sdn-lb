package com.zhangyh.FasLB.sync.sql.table;

import com.zhangyh.FasLB.sync.sql.table.mysql.index.MUL;
import com.zhangyh.FasLB.sync.sql.table.mysql.index.PRI;
import com.zhangyh.FasLB.sync.sql.table.mysql.index.UNI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangyh
 * @Date 2023/4/10 15:40
 * @desc
 */
public class IndexHelper {

    private static final Map<String, Object> INDEX = new ConcurrentHashMap<>();

    private static void registryIndex(Class<? extends Index> c) {
        try {
            INDEX.putIfAbsent(c.getName().substring(c.getName().lastIndexOf(".")+1), c.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        registryIndex(PRI.class);
        registryIndex(UNI.class);
        registryIndex(MUL.class);
    }

    public static Object getIndex(String name) {
        return INDEX.get(name);
    }

    public static void main(String[] args) {
        System.out.println(PRI.class.getName().substring(PRI.class.getName().lastIndexOf(".")+1));
    }

}
