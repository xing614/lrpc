package ljx.llpc;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ljx.llpc.service.SayHelloService;
import ljx.llpc.service.impl.SayHelloServiceImpl;
import ljx.lrpc.remote.netty4.client.LrpcClient;
import ljx.lrpc.remote.netty4.client.LrpcClientOption;
import ljx.lrpc.remote.netty4.client.LrpcProxy;
import ljx.lrpc.remote.netty4.server.LrpcServer;

import java.net.InetSocketAddress;

@RunWith(JUnit4.class)
public class LrpcTest {

    @Before
    public void startServer() throws InterruptedException {
        LrpcServer server = new LrpcServer(10027);
    }

    @Test
    public void startClient() {
        LrpcClientOption option = new LrpcClientOption();
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 10027);
        LrpcClient client = new LrpcClient(serverAddress, option);
        SayHelloService sayHelloService = LrpcProxy.getProxy(SayHelloServiceImpl.class, client);
        System.out.println(sayHelloService.sayHello("lll"));
    }
}