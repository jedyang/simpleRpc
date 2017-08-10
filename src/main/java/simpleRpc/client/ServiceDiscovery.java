package simpleRpc.client;

import simpleRpc.Const;
import io.netty.util.internal.ThreadLocalRandom;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger("rpc");

    private String registryAddress;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private volatile List<String> dataList = new ArrayList<String>();

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;

        ZooKeeper zooKeeper = connectServer();
        if (null != zooKeeper) {
            watchNode(zooKeeper);
        } else {
            LOGGER.error("connect server err!!");
        }
    }

    public String discovery() {
        String data = null;
        int size = dataList.size();
        LOGGER.info("service discover : {}", dataList);
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                LOGGER.info("use the only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.info("use the random data: {}", data);
            }
        }

        return data;
    }

    private ZooKeeper connectServer() {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(registryAddress, Const.ZK_SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
        } catch (IOException e) {
            LOGGER.error("connect zk err: ", e);
        } catch (InterruptedException e) {
            LOGGER.error("connect zk err: ", e);
        }

        return zooKeeper;
    }

    // 监听根目录下所有node
    // 遍历node，获取其中的数据，即服务提供方的地址
    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> zkChildren = zk.getChildren(Const.ZK_REGISTRY_PATH, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    if (Event.EventType.NodeDataChanged == watchedEvent.getType()) {
                        watchNode(zk);
                    }
                }
            });

            List<String> dataList = new ArrayList<String>();
            for (String node : zkChildren) {
                byte[] bytes = zk.getData(Const.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            LOGGER.info("node data: {}", dataList);
            this.dataList = dataList;
        } catch (KeeperException e) {
            LOGGER.error("watch node err:", e);
        } catch (InterruptedException e) {
            LOGGER.error("watch node err:", e);
        }

    }

}
