package simpleRpc.client;

import java.lang.reflect.Proxy;

public class RpcServiceProxy {

    private ServiceDiscovery serviceDiscovery;

    public RpcServiceProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(Class interfaceClass){
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new ProxyHandler(serviceDiscovery)
        );
    }
}
