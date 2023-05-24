package net.floodlightcontroller.com.zhangyh.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.floodlightcontroller.core.IOFSwitch;
import org.projectfloodlight.openflow.protocol.OFPacketIn;

import java.util.Objects;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/24  12:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadBalanceClient {

    private Client client;

    private IOFSwitch sw;

    OFPacketIn pi;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadBalanceClient that = (LoadBalanceClient) o;
        return Objects.equals(client, that.client) && Objects.equals(sw, that.sw) && Objects.equals(pi, that.pi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, sw, pi);
    }
}
