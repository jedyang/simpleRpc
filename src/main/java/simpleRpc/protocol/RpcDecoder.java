package simpleRpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleRpc.Const;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    public static final Logger LOGGER = LoggerFactory.getLogger(RpcDecoder.class);

    private Class targetClass;

    public RpcDecoder(Class targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        LOGGER.info("======decode one time======:");

        if (in.readableBytes() < Const.headerLen) {
            LOGGER.error("the input message too short:{}", in.readableBytes());
            return;
        }

        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            LOGGER.error("dataLemgth < 0");
            channelHandlerContext.close();
        }

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = SerializationUtil.deserilize(data, targetClass);
        out.add(obj);
    }


    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }
}
