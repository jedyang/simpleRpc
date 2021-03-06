package simpleRpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static simpleRpc.protocol.RpcDecoder.LOGGER;

/**
 * @author yunsheng
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class targetClass;

    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        LOGGER.info("======encode one time======:{}", msg);
        if (targetClass.isInstance(msg)){
            byte[] bytes = SerializationUtil.serilize(msg);

            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }

    public RpcEncoder(Class targetClass) {
        this.targetClass = targetClass;
    }
}
