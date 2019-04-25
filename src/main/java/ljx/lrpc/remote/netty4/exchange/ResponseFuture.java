package ljx.lrpc.remote.netty4.exchange;

public interface ResponseFuture {
	
	Object get();
	
	Object get(int timeoutInMillis);
	
	void setCallback(ResponseCallback callback);
	
	boolean isDone();
}
