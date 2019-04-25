package ljx.lrpc.config.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.alibaba.fastjson.JSON;

import ljx.lrpc.remote.netty4.client.LrpcClient;
import ljx.lrpc.remote.netty4.client.LrpcClientHandler;
import ljx.lrpc.remote.netty4.client.LrpcFuture;
import ljx.lrpc.remote.netty4.client.pool.LrpcPooledChannel;
import ljx.lrpc.remote.netty4.exchange.LrpcInvocation;
import ljx.lrpc.remote.netty4.exchange.LrpcRequest;
import ljx.lrpc.remote.netty4.exchange.LrpcResult;

public class LrpcJdkProxyFactory implements InvocationHandler{

	private Class interfaceClass;
	
	public LrpcJdkProxyFactory(Class inter) {
		this.interfaceClass = inter;
	}
			
	@SuppressWarnings("unchecked")
	public <T> T getProxy() {
		System.out.println("获得代理类");
		return (T)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {interfaceClass}, this);
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("代理运行"+method);
		return method.invoke(proxy, args);
	}


}
