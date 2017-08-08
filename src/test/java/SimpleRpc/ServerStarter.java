package SimpleRpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务端启动
 */
public class ServerStarter {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-context-server.xml");
    }
}
