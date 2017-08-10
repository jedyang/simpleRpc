package simpleRpc;

/**
 * 常量
 * @author yunsheng
 */

public interface Const {
    String registryAddress = "127.0.0.1:2181";

    int ZK_SESSION_TIMEOUT = 5000;
    String ZK_REGISTRY_PATH = "/registry";

    // 消息头部长度
    int headerLen = 4;
}
