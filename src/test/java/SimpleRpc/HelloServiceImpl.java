package SimpleRpc;

import simpleRpc.server.RpcService;

@RpcService(value = HelloService.class)
public class HelloServiceImpl implements  HelloService {
    public String sayHello(String msg) {
        return "hi " + msg;
    }

    public String sayHello(Person person) {
        return "hello " + person.getName();
    }
}
