package com.zhang.faslbadmin.common.constant;

import lombok.Getter;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/16  22:40
 */
@Getter
public enum RedisConstants {
    VERIFY_CODE("fas_admin")
    ;

    private String key;

    RedisConstants(String key) {
        this.key = key;
    }
}
