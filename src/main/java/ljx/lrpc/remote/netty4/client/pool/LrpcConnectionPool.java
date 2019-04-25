package ljx.lrpc.remote.netty4.client.pool;

import io.netty.channel.Channel;

/**
 * 连接池
 * @author liang
 *
 */
public interface LrpcConnectionPool {
	
    Channel getChannel() throws Exception;

    void returnChannel(Channel channel);

}
