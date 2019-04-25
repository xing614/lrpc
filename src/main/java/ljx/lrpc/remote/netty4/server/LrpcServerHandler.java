package ljx.lrpc.remote.netty4.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ljx.lrpc.common.JsonSerializer;
import ljx.lrpc.common.Serialization;
import ljx.lrpc.remote.netty4.exchange.LrpcInvocation;
import ljx.lrpc.remote.netty4.exchange.LrpcRequest;
import ljx.lrpc.remote.netty4.exchange.LrpcResponse;
import ljx.lrpc.remote.netty4.exchange.LrpcResult;

import java.lang.reflect.Method;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 自定义处理通道数据的类,用来处理客户端发送的数据
 * @author liang
 *
 */
public class LrpcServerHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	System.out.println("服务端收到数据");
    	//读取数据
        ByteBuf buf = (ByteBuf) msg;
        int len = buf.readableBytes();
        System.out.println("len=="+len+"=="+buf.toString());
        if (len > 0) {
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            System.out.println("bytes=="+bytes);
            // 反序列化
            Serialization json = new JsonSerializer();
            LrpcRequest request = json.deserialize(bytes, LrpcRequest.class);

            System.out.println("请求信息"+request.toString());
            // 反射执行请求中要调用的接口
            JSONObject object = JSON.parseObject(request.getmData().toString());
            LrpcInvocation parseObject = JSON.parseObject(request.getmData().toString(), LrpcInvocation.class);
            String className =parseObject.getClassName();
            String methodName = parseObject.getMethodName();
            Class<?>[] parameterTypes = parseObject.getParameterTypes();
            Object[] arguments = parseObject.getArguments();
            Object clazz = Class.forName(className).newInstance();
            Method method = clazz.getClass().getMethod(methodName, parameterTypes);
            Object result = method.invoke(clazz, arguments);
            System.out.println("result=="+result);
            System.out.println("result=="+result.toString());
            // 把结果组装发送到客户端
            LrpcResponse response = new LrpcResponse();
            response.setmId(request.getmId());
            LrpcResult lr = new LrpcResult(result);
            response.setmResult(lr);
            System.out.println("服务端要发送的数据"+result.toString());
            byte[] responseByte = json.serialize(response);
            ByteBuf out = ctx.alloc().ioBuffer();
            out.writeBytes(responseByte);
            ctx.channel().writeAndFlush(out);

        }
    }
}
