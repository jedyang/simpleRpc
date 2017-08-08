package simpleRpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author yunsheng
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class targetClass;

    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
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
