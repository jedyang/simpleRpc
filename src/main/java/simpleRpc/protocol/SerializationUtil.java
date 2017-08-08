package simpleRpc.protocol;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用protostuff代替java的序列化
 * 下一步改成配置项配置序列化方法
 *
 * @author yunsheng
 */
public class SerializationUtil {

    private static Map<Class, Schema> schemaMap = new HashMap<Class, Schema>();

    // objenesis是一个小型Java类库用来实例化一个特定class的对象。
    private static Objenesis objenesis = new ObjenesisStd(true);

    // 存储模式对象映射
    private static Schema getSchema(Class cls) {
        Schema schema = schemaMap.get(cls);
        if (null == schema) {
            schema = RuntimeSchema.createFrom(cls);
            if (null != schema) {
                schemaMap.put(cls, schema);
            }
        }
        return schema;
    }

    // 序列化
    public static <T> byte[] serilize(T obj) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        Class cls = obj.getClass();

        try {
            Schema schema = getSchema(cls);
            byte[] bytes = ProtobufIOUtil.toByteArray(obj, schema, buffer);
            return bytes;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            buffer.clear();
        }
    }

    // 反序列化
    public static <T> T deserilize(byte[] bytes, Class<T> cls) {
        try {
            T instance = objenesis.newInstance(cls);
            Schema schema = getSchema(cls);
            ProtobufIOUtil.mergeFrom(bytes, instance, schema);
            return instance;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
