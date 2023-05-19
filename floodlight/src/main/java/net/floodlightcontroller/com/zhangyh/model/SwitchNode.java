package net.floodlightcontroller.com.zhangyh.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/9  10:13
 */
@Data
public class SwitchNode {
    public String switchDPID;
    private List<Links> links=new ArrayList<>();
    @Data
    public static class Links{
        public String srcSwitch;
        public Integer srcPort;
        public String dstSwitch;
        public Integer dstPort;
        /**
         * 延迟
         */
        public Long latency;
        /**
         * 带宽利用率
         */
        public String rate;
    }
}
