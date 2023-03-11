package com.zhangyh.logging.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/11  9:18
 */
@Data
@Component
public class ElAdminProperties {

    public static Boolean ipLocal;

    @Value("${ip.local-parsing:true}")
    public void setIpLocal(Boolean ipLocal) {
        ElAdminProperties.ipLocal = ipLocal;
    }
}
