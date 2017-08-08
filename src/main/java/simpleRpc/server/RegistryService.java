package simpleRpc.server;

import SimpleRpc.Const;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 服务注册
 *
 * @author yunsheng
 */

public class RegistryService {
    private Logger logger = LoggerFactory.getLogger(RegistryService.class);
    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;

    /**
     * 注册服务
     *
     * @param data
     */
    public void register(String data) {
        if (data != null) {
            ZooKeeper zk = connectZK();
            if (zk != null) {
                createDataNode(zk, data);
            } else {
                logger.error("connectZK failed!!");
            }
        } else {
            logger.error("RegistryService data is null!!");
        }
    }

    private ZooKeeper connectZK() {
        ZooKeeper zk = null;
        try {
            // 连接zookeeper
            zk = new ZooKeeper(Const.registryAddress, Const.ZK_SESSION_TIMEOUT, new Watcher() {
                // 事件通知处理器
                public void process(WatchedEvent watchedEvent) {
                    // 连接状态
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException e) {
            logger.error("", e);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
        return zk;
    }

    private void AddRootNode(ZooKeeper zk) {
        // 根节点要创建为持久化节点
        try {
            zk.create(Const.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            logger.error("", e);
        } catch (InterruptedException e) {
            logger.error("", e);
        }

    }

    /**
     * 创建节点
     *
     * @param zk
     * @param data
     */
    private void createDataNode(ZooKeeper zk, String data) {
        try {
            // 检查节点是否存在，false表示不需要监听这个node
            // 返回的是节点状态，null表示不存在
            Stat s = zk.exists(Const.ZK_REGISTRY_PATH, false);
            if (s == null) {
                AddRootNode(zk);
            }

            byte[] bytes = data.getBytes();
            // 数据节点建的是瞬态顺序节点
            String path = zk.create(Const.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException e) {
            logger.error("", e);
        } catch (InterruptedException ex) {
            logger.error("", ex);
        }
    }


    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }
}
