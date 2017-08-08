package simpleRpc.client;

import java.lang.reflect.Proxy;

public class RpcServiceProxy {

    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcServiceProxy(String serverAddress, ServiceDiscovery serviceDiscovery) {
        this.serverAddress = serverAddress;
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(Class interfaceClass){
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new ProxyHandler(serverAddress, serviceDiscovery)
        );
    }
}
