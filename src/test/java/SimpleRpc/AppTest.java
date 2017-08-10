package SimpleRpc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import simpleRpc.client.RpcServiceProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-context.xml")
public class AppTest {

    @Autowired
    private RpcServiceProxy rpcServiceProxy;

    @Test
    public void helloTest() {
        HelloService helloService = rpcServiceProxy.create(HelloService.class, "yunsheng");
        String result = helloService.sayHello("World");
        Assert.assertEquals("hi World", result);
    }
}
