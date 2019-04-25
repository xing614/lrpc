package ljx.llpc;

import ljx.llpc.service.SayHelloService;
import ljx.llpc.service.impl.SayHelloServiceImpl;
import ljx.lrpc.remote.netty4.client.LrpcClient;
import ljx.lrpc.remote.netty4.client.LrpcClientOption;
import ljx.lrpc.remote.netty4.client.LrpcProxy;


public class Client {

	//效率spend-total-time:32s,re/s=3125.0
    public static void main(String[] args) {
    	System.out.println("运行rpc客户端");
        LrpcClientOption option = new LrpcClientOption();
        LrpcClient client = new LrpcClient(option,"localhost:2181");//设置zookeeper地址，包含生成对象池(通道)，生成服务注册中心，获取服务方地址，设置地址信息到通道池工厂
        long startTime = System.currentTimeMillis();
        int requestNum = 100000;
        for (int i = 0; i < requestNum; i++) {
            SayHelloService sayHelloService = LrpcProxy.getProxy(SayHelloServiceImpl.class, client);// 用客户端拿到连接池对象，动态代理SayHelloServiceImpl
            System.out.println(sayHelloService.sayHello("lll"));//动态代理方法，封装request请求格式，利用对象池里的通道channel连接服务端端口，使用FrpcClientHandler处理器发送数据
            //send发送数据过程：封装异步请求FrpcFuture，把请求id和future放入map 返回结果时使用。请求对象转二进制，刷入缓冲区写入通道，将通道返还对象池，返回异步请求, 服务端返回之前一直阻塞等待结果
        }
        long spendTime = (System.currentTimeMillis() - startTime)/1000;
        //时间差
        System.out.println(String.format("spend-total-time:%ss,req/s=%s",
                spendTime,(
                        (double)requestNum/spendTime
                )
                )
        );
        
        
    }

}
