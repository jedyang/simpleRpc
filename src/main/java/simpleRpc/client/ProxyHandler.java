package simpleRpc.client;

import simpleRpc.protocol.RpcRequest;
import simpleRpc.protocol.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ProxyHandler implements InvocationHandler {


    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public ProxyHandler(String serverAddress, ServiceDiscovery serviceDiscovery) {
        this.serverAddress = serverAddress;
        this.serviceDiscovery = serviceDiscovery;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 组装请求
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        // 发现服务
        String serverAddress = serviceDiscovery.discovery();

        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        // 发送请求
        RpcClient rpcClient = new RpcClient(host, port);

        RpcResponse response = rpcClient.send(request);

        if (response.isError()) {
            return response.getError();
        } else {
            return response.getResult();
        }


    }
}
