package net.floodlightcontroller.com.zhangyh.core.domain;

import lombok.Data;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TransportPort;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/14  20:22
 */
@Data
public class Client {

    IPv4Address ipAddress;
    IpProtocol nw_proto;
    TransportPort srcPort;
    TransportPort targetPort;
}
