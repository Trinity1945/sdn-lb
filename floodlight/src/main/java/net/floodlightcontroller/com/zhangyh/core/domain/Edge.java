package net.floodlightcontroller.com.zhangyh.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/21  20:58
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Edge {
    public String dstSwitch;
    public Integer srcPort;
    public Integer dstPort;
    public Long latency;
    public Double rate;
}

