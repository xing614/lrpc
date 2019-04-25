package ljx.lrpc.remote.netty4.exchange;

public interface ResponseCallback {

	void done(Object response);//成功返回后回调
	
	void caught(Throwable exception);//失败后回调
}
