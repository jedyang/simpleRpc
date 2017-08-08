package simpleRpc.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 用来注解要发布的服务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    // 用来指定实现的接口
    Class<?> value();
}
