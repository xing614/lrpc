package ljx.lrpc.remote.netty4.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ljx.lrpc.registry.ServerRegisterDiscovery;
import ljx.lrpc.registry.ZookeeperService;
import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc服务端
 * @author liang
 *
 */
public class LrpcServer {

    private static final Logger log = LoggerFactory.getLogger(LrpcServer.class);

    private LrpcServerOption lrpcServerOption = new LrpcServerOption();

    public LrpcServer(int port) throws InterruptedException {
        this(port, null, null);
    }
    //服务端的端口号 和 zookeeper地址端口
    public LrpcServer(int port,String zkAddress) throws InterruptedException {
        this(port, zkAddress,null);
    }

    public LrpcServer(int port, String zkAddress,LrpcServerOption option) throws InterruptedException {

        // 判断用户是否设置自定义服务端相关配置
        if (option != null) {
            BeanCopier copier = BeanCopier.create(LrpcServerOption.class, LrpcServerOption.class, false);
            copier.copy(option, lrpcServerOption, null);
        }

        EventLoopGroup boss;//负责接收线程
        EventLoopGroup work;//负责处理线程
        ServerBootstrap bootstrap = new ServerBootstrap();//netty服务端引导类
        // 选择IO模型
        if (Epoll.isAvailable()) {
            boss = new EpollEventLoopGroup(lrpcServerOption.getNettyBossThreadNum());
            work = new EpollEventLoopGroup(lrpcServerOption.getNettyWorkThreadNum());
            bootstrap.channel(EpollServerSocketChannel.class);
            bootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);//设置相关参数
            bootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
            log.info("use epoll edge trigger model.");
        } else {
            boss = new NioEventLoopGroup(lrpcServerOption.getNettyBossThreadNum());
            work = new NioEventLoopGroup(lrpcServerOption.getNettyWorkThreadNum());
            bootstrap.channel(NioServerSocketChannel.class);
            log.info("use normal model.");
        }
        bootstrap.group(boss, work);
        // 配置TPC相关参数
        bootstrap.option(ChannelOption.SO_BACKLOG, lrpcServerOption.getBacklog());
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, lrpcServerOption.isKeepAlive());
        bootstrap.childOption(ChannelOption.TCP_NODELAY, lrpcServerOption.isTcpNoDelay());
        bootstrap.childOption(ChannelOption.SO_LINGER, lrpcServerOption.getLinger());
        bootstrap.childOption(ChannelOption.SO_SNDBUF, lrpcServerOption.getSendBufferSize());
        bootstrap.childOption(ChannelOption.SO_RCVBUF, lrpcServerOption.getReceiveBufferSize());
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 绑定服务端处理器
                ch.pipeline().addLast(new LrpcServerHandler());
            }
        });
        // 绑定端口
        bootstrap.bind(port).sync().addListener(future -> {
            if (future.isSuccess()) {
                log.info("server bind port is success");
                // 端口绑定成功后，判断用户是否要把服务注册到zookeeper
                if (zkAddress != null) {
                    ServerRegisterDiscovery serverRegisterDiscovery = new ZookeeperService(zkAddress);
                    serverRegisterDiscovery.register(port);
                    System.out.println("注册成功");
                }
            }
        });

    }
}
