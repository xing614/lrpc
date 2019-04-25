package ljx.lrpc.remote.netty4.client;
/**
 * rpc回调 成功/失败
 * @author liang
 *
 */
public interface RpcCallback {

    void success(Object result);

    void fail(Throwable e);

}
