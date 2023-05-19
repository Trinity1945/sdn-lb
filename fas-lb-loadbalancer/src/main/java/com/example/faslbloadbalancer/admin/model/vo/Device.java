package com.example.faslbloadbalancer.admin.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/9  13:03
 */
@Data
public class Device {
    private List<Host> devices;

    @Data
    public static class Host {
        private List<String> mac;
        private List<String> ipv4;
        private List<String> ipv6;
        private List<String> vlan;
        private List<AttachmentPoint> attachmentPoint;
        private Long lastSeen;

        @Data
        public static class AttachmentPoint {

            @JsonProperty("switch")
            @JSONField(name = "switch")
            private String switchDPID;

            private Integer port;
        }
    }
}
