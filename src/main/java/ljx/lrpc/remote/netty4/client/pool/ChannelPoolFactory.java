package ljx.lrpc.remote.netty4.client.pool;

import io.netty.channel.Channel;
import ljx.lrpc.remote.netty4.client.LrpcClient;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.net.InetSocketAddress;

/**
 * 通道池工厂
 * @author liang
 *
 */
public class ChannelPoolFactory extends BasePooledObjectFactory<Channel> {

    private LrpcClient lrpcClient;
    private String zkAddress;
    private Integer port;
    private InetSocketAddress serverAddress;

    public ChannelPoolFactory(LrpcClient lrpcClient, InetSocketAddress serverAddress) {
        this(lrpcClient, serverAddress, null, null);
    }

    public ChannelPoolFactory(LrpcClient lrpcClient, String zkAddress, Integer port) {
        this(lrpcClient, null, zkAddress, port);
    }

    public ChannelPoolFactory(LrpcClient lrpcClient,InetSocketAddress serverAddress, String zkAddress,Integer port) {
        this.lrpcClient = lrpcClient;
        this.serverAddress = serverAddress;
        this.zkAddress = zkAddress;
        this.port = port;
    }

    //创建通道的时候使用客户端连接地址
    @Override
    public Channel create() throws Exception {
        if (serverAddress != null) {
            return lrpcClient.getConnect(serverAddress);
        }
        return lrpcClient.getConnect(zkAddress,port);
    }

    @Override
    public PooledObject<Channel> wrap(Channel obj) {
        return new DefaultPooledObject<>(obj);
    }


    @Override
    public void destroyObject(PooledObject<Channel> p) throws Exception {
        Channel channel = p.getObject();
        if (channel != null && channel.isOpen() && channel.isActive()) {
            channel.close();
        }
    }

    @Override
    public boolean validateObject(PooledObject<Channel> p) {
        Channel channel = p.getObject();
        return channel != null && channel.isActive() && channel.isOpen();
    }

}
