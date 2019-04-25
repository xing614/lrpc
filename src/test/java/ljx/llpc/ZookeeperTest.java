package ljx.llpc;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import ljx.lrpc.common.NetUtils;
import ljx.lrpc.registry.Constants;


public class ZookeeperTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CuratorFramework zkClient;
		ExponentialBackoffRetry retryPolicy =
                new ExponentialBackoffRetry
                        (
                                Constants.DEFAULT_ZK_BASE_SLEEP_TIME_MS,
                                Constants.DEFAULT_ZK_MAX_RETRIES
                        );
        zkClient =
                CuratorFrameworkFactory.builder()
                        .connectString("localhost:2181")
                        .retryPolicy(retryPolicy)
                        .sessionTimeoutMs(Constants.DEFAULT_ZK_SESSION_TIMEOUT_MS)
                        .connectionTimeoutMs(Constants.DEFAULT_ZK_CONNECTION_TIMEOUT_MS)
                        .build();
        zkClient.start();//启动会话
        //Thread.currentThread().sleep(10000);
        System.out.println(zkClient.checkExists().forPath(Constants.DEFAULT_ZK_ROOT_PATH));
        if (zkClient.checkExists().forPath(Constants.DEFAULT_ZK_ROOT_PATH) == null) {
            zkClient.create().forPath(Constants.DEFAULT_ZK_ROOT_PATH);
            //createNode(zkClient, NetUtils.getHostAddress() + ":" + 10027);//在客户端上创建一个某端口的节点
            if (zkClient.checkExists().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH) == null) {
            	//创建节点
                zkClient.create().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH, (NetUtils.getHostAddress() + ":" + "10027").getBytes());
                System.out.println("节点组："+zkClient.getData().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH));
            }
        }
        byte[] bs = zkClient.getData().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH);  
        System.out.println("新建的节点，data为:" + new String(bs));
	}

}
