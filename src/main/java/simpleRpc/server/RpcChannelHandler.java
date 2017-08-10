package simpleRpc.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import simpleRpc.protocol.RpcRequest;
import simpleRpc.protocol.RpcResponse;

import java.util.Map;

public class RpcChannelHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcChannelHandler.class);

    private Map<String, Object> handlerMap;

    public RpcChannelHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 收到消息时该方法会被触发
     * 注意：netty5这个方法会改名
     *
     * @param ctx
     * @param request
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        LOGGER.info("======server received request : {}", request);
        RpcResponse response = new RpcResponse();
        String requestId = request.getRequestId();
        response.setRequestId(requestId);
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Exception e) {
            response.setError(e.getMessage());
        }

        // 写入RPC响应，并自动关闭连接
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 通过反射调用服务
     *
     * @param request
     * @return
     */
    private Object handle(RpcRequest request) throws Exception {
        String serviceName = request.getClassName() + ":" + request.getVersion();
        LOGGER.info("serviceName : {}", serviceName);
        LOGGER.info("handlerMap : {}", handlerMap);
        Object obj = handlerMap.get(serviceName);
        LOGGER.info("obj : {}", obj);

        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        Class<?>[] parameterTypes = request.getParameterTypes();

        Class<?> objClass = obj.getClass();

        // 使用java原生反射，性能较差
//        Method method = objClass.getMethod(methodName, parameterTypes);
//        method.setAccessible(true);
//        Object result = method.invoke(obj, parameters);

        // 使用CGlib的 反射api
        FastClass fastClass = FastClass.create(objClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);
        return fastMethod.invoke(obj, parameters);
    }


}
