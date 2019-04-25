package ljx.lrpc.remote.netty4.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ljx.lrpc.common.JsonSerializer;
import ljx.lrpc.common.Serialization;
import ljx.lrpc.remote.netty4.client.pool.LrpcPooledChannel;
import ljx.lrpc.remote.netty4.exchange.LrpcRequest;
import ljx.lrpc.remote.netty4.exchange.LrpcResponse;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义处理通道数据的类
 * 
 * ChannelHandler用于处理Channel对应的事件
 * 当客户端连接到服务器时，Netty新建一个ChannelPipeline处理其中的事件，而一个ChannelPipeline中含有若干ChannelHandler。如果每个客户端连接都新建一个ChannelHandler实例，当有大量客户端时，服务器将保存大量的ChannelHandler实例。
 * @author liang
 *
 */
public class LrpcClientHandler extends SimpleChannelInboundHandler<Object> {
	private static ConcurrentHashMap<Long, LrpcFuture> pendingRpc = new ConcurrentHashMap<>();//存储客户端请求
    //解析通道读取的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	System.out.println("客户端读入数据");
        // 客户端解析响应
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        Serialization jsonSerializer = new JsonSerializer();
        LrpcResponse response = jsonSerializer.deserialize(bytes, LrpcResponse.class);//将服务端传来的数据转成响应格式

        // 拿出请求id,查询ConcurrentHashMap中是否存在这条请求
        long requestId = response.getmId();
        //LrpcFuture frpcFuture = pendingRpc.remove(requestId);
        LrpcFuture frpcFuture = pendingRpc.get(requestId);
        if (frpcFuture != null) {
            pendingRpc.remove(requestId);
            // 把响应装进异步请求
            frpcFuture.done(response);//在这里释放资源release，这样get方法acquice就能得到数据了，表示请求已返回响应数据
        }



    }

    //客户端发送数据
    public LrpcFuture send(LrpcRequest request,LrpcPooledChannel frpcPooledChannel) throws Exception {
    	System.out.println("客户端发送数据"+request.toString());
        // 组装异步请求
        LrpcFuture frpcFuture = new LrpcFuture(request);

        // 把异步请求装进ConcurrentHashMap
        pendingRpc.put(request.getmId(), frpcFuture);

        // 请求序列化
        Serialization json = new JsonSerializer();
        byte[] bytes = json.serialize(request);

        // 连接池获取Channel
        Channel channel = frpcPooledChannel.getChannel();

        // 初始化ByteBuf并写进请求数据发送到服务端
        ByteBuf buf = channel.alloc().ioBuffer();
        buf.writeBytes(bytes);
        channel.writeAndFlush(buf);

        // 返回Channel到连接池
        frpcPooledChannel.returnChannel(channel);

        // 返回异步请求, 服务端返回之前一直阻塞等待结果
        return frpcFuture;
    }

}
