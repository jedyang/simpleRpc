package SimpleRpc;

public interface HelloService {
    String sayHello(String msg);

    String sayHello(Person person);
}
