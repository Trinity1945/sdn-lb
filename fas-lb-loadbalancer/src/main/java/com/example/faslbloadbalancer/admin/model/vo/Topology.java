package com.example.faslbloadbalancer.admin.model.vo;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/9  10:29
 */
@Data
public class Topology {
    public String switchDPID;

    public Set<Links> links=new HashSet<>();

    @Data
    public static class Links {
        public String srcSwitch;
        public Integer srcPort;
        public String dstSwitch;
        public Integer dstPort;
        public Integer latency;
        private Double rate;
    }
}
