package ljx.lrpc.remote.netty4.client;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import ljx.lrpc.remote.netty4.client.pool.LrpcPooledChannel;
import ljx.lrpc.remote.netty4.exchange.LrpcInvocation;
import ljx.lrpc.remote.netty4.exchange.LrpcRequest;
import ljx.lrpc.remote.netty4.exchange.LrpcResult;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

/**
 * rpc代理，使用cglib。使用动态代理是为了将要调用的类和方法合并成一个request类
 * 拦截器。在调用目标方法时，CGLib会回调MethodInterceptor接口方法拦截,使用intercept方法
 * @author liang
 *
 */
@SuppressWarnings("unchecked")
public class LrpcProxy implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LrpcProxy.class);

    private LrpcPooledChannel lrpcPooledChannel;

    public LrpcProxy(LrpcClient lrpcClient) {
        // 用客户端拿到连接池对象
        this.lrpcPooledChannel = lrpcClient.getLrpcPooledChannel();
    }

    public static <T> T getProxy(Class clazz,LrpcClient lrpcClient) {
        Enhancer enhancer = new Enhancer();//Enhancer类是CGLib中的一个字节码增强器，它方便对要处理的类进行扩展
        enhancer.setSuperclass(clazz);//将被代理类clazz设置成父类
        enhancer.setCallback(new LrpcProxy(lrpcClient));//设置拦截器FrpcProxy
        return (T) enhancer.create();//执行enhancer.create()动态生成一个代理类，并从Object强制转型成父类型T
    }

    //组装RpcRequest，
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        // 请求消息组装
        //String requestId = UUID.randomUUID().toString();
        //方法类名称，方法名称，方法参数类型
        //LrpcRequest request = new LrpcRequest(requestId, method.getDeclaringClass().getName(),
        //        method.getName(), method.getParameterTypes(), args);
        
        //输出类型
        //Arrays.stream(request.getParams()).forEach(param -> log.debug("param is : {}",param));
        //Arrays.stream(request.getParamTypes()).forEach(paramType -> log.debug("param type is : {}",paramType));

    	LrpcInvocation li = new LrpcInvocation(method.getDeclaringClass().getName(), 
    			method.getName(), method.getParameterTypes(),args);
    	LrpcRequest request = new LrpcRequest();
    	request.setmData(li);
        LrpcClientHandler handler = new LrpcClientHandler();
        // 客户端请求服务端
        LrpcFuture frpcFuture = handler.send(request,lrpcPooledChannel);
        
        //这里get会acquire(-1)获取资源，成功获取就得到响应的result数据，  资源会在channelRead0方法匹配对应请求id时release
        Object object = frpcFuture.get();
        LrpcResult lr = JSON.parseObject(object.toString(),LrpcResult.class);
        return lr.getResult();
    }
}
