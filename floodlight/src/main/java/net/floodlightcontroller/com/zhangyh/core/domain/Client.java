package net.floodlightcontroller.com.zhangyh.core.domain;

import lombok.Data;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TransportPort;

import java.util.Objects;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/14  20:22
 */
@Data
public class Client {

    IPv4Address ipAddress;
    IPv4Address targetIpAddress;
    IpProtocol nw_proto;
    TransportPort srcPort;
    TransportPort targetPort;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(ipAddress, client.ipAddress) && Objects.equals(targetIpAddress, client.targetIpAddress) && Objects.equals(nw_proto, client.nw_proto) && Objects.equals(srcPort, client.srcPort) && Objects.equals(targetPort, client.targetPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress, targetIpAddress, nw_proto, srcPort, targetPort);
    }
}
