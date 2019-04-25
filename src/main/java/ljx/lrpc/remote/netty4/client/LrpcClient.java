package ljx.lrpc.remote.netty4.client;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import ljx.lrpc.remote.netty4.client.pool.LrpcPooledChannel;
import net.sf.cglib.beans.BeanCopier;

public class LrpcClient {
	private static final Logger log = LoggerFactory.getLogger(LrpcClient.class);

    private LrpcClientOption lrpcClientOption = new LrpcClientOption();
    private LrpcPooledChannel lrpcPooledChannel;//对象池，存储通道
    private Bootstrap bootstrap;//netty客户端引导

    public LrpcClient(InetSocketAddress serverAddress) {
        this(serverAddress, null, null);
    }

    public LrpcClient(InetSocketAddress serverAddress, LrpcClientOption option) {
        this(serverAddress, option, null);
    }

    public LrpcClient(String zkAddress) {
        this(null, null, zkAddress);
    }

    public LrpcClient(LrpcClientOption option, String zkAddress) {
        this(null, option, zkAddress);
    }


    public LrpcClient(InetSocketAddress serverAddress,LrpcClientOption option,String zkAddress) {

        // 判断直接使用服务端地址，还是从zookeeper中取
        if (serverAddress != null) {
            log.info("serverAddress is :" + serverAddress);
            lrpcPooledChannel = new LrpcPooledChannel(serverAddress, this);
        } else {
            lrpcPooledChannel = new LrpcPooledChannel(zkAddress, this);//zkAddress 是zook中的地址
        }

        // 判断用户是否设置自定义客户端相关配置
        if (option != null) {
        	//bean类复制工具类
            BeanCopier copier = BeanCopier.create(LrpcClientOption.class, LrpcClientOption.class, false);
            copier.copy(option, lrpcClientOption, null);
        }

        EventLoopGroup work;
        bootstrap = new Bootstrap();
      //NioEventLoop：采用的是jdk Selector接口（使用PollSelectorImpl的poll方式）来实现对Channel的事件检测
    	//EpollEventLoop：没有采用jdk Selector的接口实现EPollSelectorImpl，而是Netty自己实现的epoll方式来实现对Channel的事件检测，所以在EpollEventLoop中就不存在jdk的Selector
        // 选择IO模型
        if (Epoll.isAvailable()) {//是否可以用epoll模式
            work = new EpollEventLoopGroup(lrpcClientOption.getNettyWorkThreadNum());//设置线程数
            bootstrap.channel(EpollSocketChannel.class);
            log.info("use epoll edge trigger mode");
        } else {
            work = new NioEventLoopGroup(lrpcClientOption.getNettyWorkThreadNum());
            bootstrap.channel(NioSocketChannel.class);
            log.info("use normal mode");
        }
        bootstrap.group(work);
        // 配置TPC相关参数
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, lrpcClientOption.getConnectTimeOutMillis());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, lrpcClientOption.isKeepAlive());
        bootstrap.option(ChannelOption.TCP_NODELAY, lrpcClientOption.isNoDelay());
        //添加handler监听服务端的IO动作
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
            	//将一个自定义的ChannelHandler加入到了ChannelPipeline
                // 绑定客户端处理器
                ch.pipeline().addLast(new LrpcClientHandler());//添加读取数据动作
            }
        });

    }

    public Channel getConnect(InetSocketAddress serverAddress) throws InterruptedException {
        return getConnect(null, null, serverAddress);
    }

    public Channel getConnect(String zkAddress, Integer port) throws InterruptedException {
        return getConnect(zkAddress, port, null);
    }
    public Channel getConnect(String zkAddress, Integer port,InetSocketAddress serverAddress) throws InterruptedException {
        if (serverAddress != null) {
            ChannelFuture future = bootstrap.connect(serverAddress).sync();//连接服务器
            return future.channel();
        }
        ChannelFuture future = bootstrap.connect(zkAddress, port).sync();
        return future.channel();
    }
    //获取对象池
    public LrpcPooledChannel getLrpcPooledChannel() {
        return lrpcPooledChannel;
    }

    public LrpcClientOption getLrpcClientOption() {
        return lrpcClientOption;
    }
}
