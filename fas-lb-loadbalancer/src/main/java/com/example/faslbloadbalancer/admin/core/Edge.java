package com.example.faslbloadbalancer.admin.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/9  21:52
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Edge {
    public String dstSwitch;
    public Integer srcPort;
    public Integer dstPort;
    public Integer latency;
    public Double rate;
}
