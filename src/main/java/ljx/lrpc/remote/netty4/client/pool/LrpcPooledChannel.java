package ljx.lrpc.remote.netty4.client.pool;


import io.netty.channel.Channel;
import ljx.lrpc.common.NetUtils;
import ljx.lrpc.registry.RegisterInfo;
import ljx.lrpc.registry.ServerRegisterDiscovery;
import ljx.lrpc.registry.ZookeeperService;
import ljx.lrpc.remote.netty4.client.LrpcClient;
import ljx.lrpc.remote.netty4.client.LrpcClientOption;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 对象池，存储通道
 * @author liang
 *
 */
public class LrpcPooledChannel implements LrpcConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(LrpcPooledChannel.class);

    private GenericObjectPool<Channel> channelGenericObjectPool;

    public LrpcPooledChannel(InetSocketAddress serverAddress, LrpcClient client) {
        this(serverAddress, null, client);
    }

    public LrpcPooledChannel(String zkAddress, LrpcClient client) {
        this(null, zkAddress, client);
    }

    //zkAddress时zookeeper地址
    public LrpcPooledChannel(InetSocketAddress serverAddress,String zkAddress, LrpcClient client) {

        LrpcClientOption LrpcClientOption = client.getLrpcClientOption();
        GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();//连接池配置数据
        config.setMaxWaitMillis(LrpcClientOption.getConnectTimeOutMillis());
        config.setMaxTotal(LrpcClientOption.getMaxTotalConnections());
        config.setMaxIdle(LrpcClientOption.getMaxTotalConnections());
        config.setMinIdle(LrpcClientOption.getMinIdleConnections());
        config.setTestWhileIdle(true);
        config.setTimeBetweenEvictionRunsMillis(LrpcClientOption.getTimeBetweenEvictionRunsMillis());

        if (zkAddress != null) {//如果地址不为空，就根据地址找到host和port
            ServerRegisterDiscovery serverRegisterDiscovery = new ZookeeperService(zkAddress);//注册中心
            String serverAddressAndPort = serverRegisterDiscovery.discovery();
            RegisterInfo registerInfo = NetUtils.getRegisterInfo(serverAddressAndPort);//包括host和port

            channelGenericObjectPool = new GenericObjectPool<>(
                    new ChannelPoolFactory(client, registerInfo.getHost(), registerInfo.getPort()), config);
        } else {

            channelGenericObjectPool = new GenericObjectPool<>(
                    new ChannelPoolFactory(client, serverAddress), config
            );
        }

        try {
            channelGenericObjectPool.preparePool();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Channel getChannel() throws Exception {
        return channelGenericObjectPool.borrowObject();
    }

    //归还池中对象
    @Override
    public void returnChannel(Channel channel) {
        channelGenericObjectPool.returnObject(channel);
    }
}
