package ljx.llpc;

import ljx.lrpc.remote.netty4.server.LrpcServer;

/**
 * rpc服务端
 * @author liang
 *
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
    	System.out.println("运行rpc服务端");
    	//生成ServerBootstrap 和处理线程组，判断使用epoll还是Nio，设置bootstrap参数，绑定处理器FrpcServerHandler，绑定10027端口。
    	//进行监听，进行zookeeper注册
        LrpcServer server = new LrpcServer(10027, "localhost:2181");
    }

}
