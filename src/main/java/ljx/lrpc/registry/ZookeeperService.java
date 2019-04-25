package ljx.lrpc.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ljx.lrpc.common.NetUtils;

public class ZookeeperService implements ServerRegisterDiscovery{

	private static final Logger log = LoggerFactory.getLogger(ZookeeperService.class);
	
	private CuratorFramework zkClient;
	
	public ZookeeperService(String registerAddress) {
		//重连策略
		ExponentialBackoffRetry retryPolicy =
                new ExponentialBackoffRetry
                        (
                                Constants.DEFAULT_ZK_BASE_SLEEP_TIME_MS,
                                Constants.DEFAULT_ZK_MAX_RETRIES
                        );
        zkClient =
                CuratorFrameworkFactory.builder()
                        .connectString(registerAddress)
                        .retryPolicy(retryPolicy)
                        .sessionTimeoutMs(Constants.DEFAULT_ZK_SESSION_TIMEOUT_MS)
                        .build();
        zkClient.start();//启动会话		
	}
	//从zookeeper获得服务地址
	public String discovery() {
		try {
        	//获取节点数据  地址端口
            byte[] serverAddressByte = zkClient.getData().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH);
            return new String(serverAddressByte);
        } catch (Exception e) {
        	
        }
        return null;
	}

	public void register(int port) {
		// TODO Auto-generated method stub
		try {
        	System.out.println("--"+zkClient.checkExists().forPath(Constants.DEFAULT_ZK_ROOT_PATH));
            if (zkClient.checkExists().forPath(Constants.DEFAULT_ZK_ROOT_PATH) == null) {
                zkClient.create().forPath(Constants.DEFAULT_ZK_ROOT_PATH);
                createNode(zkClient, NetUtils.getHostAddress() + ":" + port);//在客户端上创建一个某端口的节点
            }
        } catch (Exception e) {
        	
        }
	}

	private void createNode(CuratorFramework zkClient, String serverAddress) {
        try {
            if (zkClient.checkExists().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH) == null) {
            	//创建节点
                zkClient.create().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH, serverAddress.getBytes());
                System.out.println("节点组："+zkClient.getData().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH));
            }
        } catch (Exception e) {
        	
        }
    }
}
