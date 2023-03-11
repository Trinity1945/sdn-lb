package com.example.faslbloadbalancer.common.util.valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  22:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArgumentInvalidResult {

    private String field;
    private Object rejectedValue;
    private String reason;

}
