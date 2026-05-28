package org.example.gateway.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Administrator
 */
@Component
public class AppGatewayHelper {

    public InetAddress getLocalHost() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address;
    }

    public void bindNetInfo(ServerWebExchange exchange){
        InetAddress address = this.getLocalHost();
        String hostAddress = address.getHostAddress();
        String hostName = address.getHostName();
        exchange.getRequest().mutate().header("gateway_host_address", hostAddress).build();
        exchange.getRequest().mutate().header("gateway_host_name", hostName).build();
    }
}
