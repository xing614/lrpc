package ljx.lrpc.remote.netty4.exchange;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.plaf.basic.BasicComboPopup.InvocationKeyHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.handler.timeout.TimeoutException;
import ljx.lrpc.registry.Constants;

public class DefaultFuture implements ResponseFuture{
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultFuture.class);
	//id与对应的通道
    private static final Map<Long, Channel> CHANNELS = new ConcurrentHashMap<Long, Channel>();
    //id与对应的返回值
    private static final Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<Long, DefaultFuture>();
    //设置守护线程，对Future进行过期检测
    static {
    	Thread th = new Thread(new RemotingTimeoutScan(),"lrpcDefaultRepsonseTimeoutScan");
    	th.setDaemon(true);
    	th.start();
    }
    
    private final long id;
    private final Channel channel;
    private final LrpcRequest request;
    private int timeout;
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private final long start = System.currentTimeMillis();
    private volatile long sent;
    private volatile LrpcResponse response;
    private volatile ResponseCallback callback;
    
    public DefaultFuture(Channel channel, LrpcRequest request, int timeout) {
        this.channel = channel;
        this.request = request;
        this.id = request.getmId();
        this.timeout = timeout > 0 ? timeout : Constants.DEFAULT_TIMEOUT;
        // put into waiting map.
        FUTURES.put(id, this);
        CHANNELS.put(id, channel);
    }
	@Override
	public Object get() {
		// TODO Auto-generated method stub
		return get(timeout);
	}

	@Override
	public Object get(int timeoutInMillis) {
		// TODO Auto-generated method stub
		if (timeout <= 0) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }
		if(!isDone()) {
			long start = System.currentTimeMillis();
			lock.lock();
			try {
				while(!isDone()) {
					done.await(timeout,TimeUnit.MILLISECONDS);
					if(isDone() || System.currentTimeMillis() - start>timeout) {
						break;
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
				throw new RuntimeException(e);
			}finally {
				lock.unlock();
			}
			if(!isDone()) {
				throw new RuntimeException();
			}
		}
		return returnFromResponse();
	}

	private Object returnFromResponse() {
		// TODO Auto-generated method stub
		LrpcResponse res = response;
        if (res == null) {
            throw new IllegalStateException("response cannot be null");
        }
        if (res.getmStatus() == LrpcResponse.OK) {
            return res.getmResult();
        }
        if (res.getmStatus() == LrpcResponse.CLIENT_TIMEOUT || res.getmStatus() == LrpcResponse.SERVER_TIMEOUT) {
            //throw new TimeoutException(res.getmStatus() == LrpcResponse.SERVER_TIMEOUT, channel, res.getmErrorMsg());
        }
        //throw new RemotingException(channel, res.getmErrorMsg());
		return null;
	}
	@Override
	public void setCallback(ResponseCallback callback) {
		// TODO Auto-generated method stub
        if (isDone()) {
            invokeCallback(callback);
        } else {
            boolean isdone = false;
            lock.lock();
            try {
                if (!isDone()) {
                    this.callback = callback;
                } else {
                    isdone = true;
                }
            } finally {
                lock.unlock();
            }
            if (isdone) {
                invokeCallback(callback);
            }
        }
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return response!=null;
	}

    public void cancel() {
        LrpcResponse errorResult = new LrpcResponse(id);
        errorResult.setmErrorMsg("request future has been canceled.");
        response = errorResult;
        FUTURES.remove(id);
        CHANNELS.remove(id);
    }
    
    public static DefaultFuture getFuture(long id) {
        return FUTURES.get(id);
    }
    
    public static boolean hasFuture(Channel channel) {
        return CHANNELS.containsValue(channel);
    }
    
    public static void sent(Channel channel,LrpcRequest request) {
    	DefaultFuture future = FUTURES.get(request.getmId());
    	if(future!=null) {
    		future.doSent();
    	}
    }
    
	private void doSent() {
		// TODO Auto-generated method stub
		sent = System.currentTimeMillis();
	}
	
	public static void received(Channel channel, LrpcResponse response) {
		try {
            DefaultFuture future = FUTURES.remove(response.getmId());
            if (future != null) {
                future.doReceived(response);
            } else {
                logger.warn("The timeout response finally returned at "
                        + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()))
                        + ", response " + response
                        );
            }
        } finally {
            CHANNELS.remove(response.getmId());
        }
	}

	private void doReceived(LrpcResponse res) {
		// TODO Auto-generated method stub
		lock.lock();
		try {
			response = res;
			if(done!=null) {
				done.signal();
			}
		}catch (Exception e) {
			// TODO: handle exception
		}finally {
			lock.unlock();
		}
		if(callback!=null) {
			invokeCallback(callback);
		}
	}

	private void invokeCallback(ResponseCallback callback2) {
		// TODO Auto-generated method stub
		
	}

    private long getId() {
        return id;
    }

    private Channel getChannel() {
        return channel;
    }

    private boolean isSent() {
        return sent > 0;
    }

    public LrpcRequest getRequest() {
        return request;
    }

    private int getTimeout() {
        return timeout;
    }

    private long getStartTimestamp() {
        return start;
    }
	
    private String getTimeoutMessage(boolean scan) {
        long nowTimestamp = System.currentTimeMillis();
        return (sent > 0 ? "Waiting server-side response timeout" : "Sending request timeout in client-side")
                + (scan ? " by scan timer" : "") + ". start time: "
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(start))) + ", end time: "
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())) + ","
                + (sent > 0 ? " client elapsed: " + (sent - start)
                + " ms, server elapsed: " + (nowTimestamp - sent)
                : " elapsed: " + (nowTimestamp - start)) + " ms, timeout: "
                //+ timeout + " ms, request: " + request + ", channel: " + channel.getLocalAddress()
                //+ " -> " + channel.getRemoteAddress();
                ;
    }
    
    //守护线程循环判断是否超时
	private static class RemotingTimeoutScan implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				try {
					for(DefaultFuture df:FUTURES.values()) {
						if(df == null||df.isDone()) {
							continue;
						}
						if(System.currentTimeMillis() - df.getStartTimestamp() > df.getTimeout()) {
                            LrpcResponse timeoutResponse = new LrpcResponse(df.getId());
                            timeoutResponse.setmStatus(df.isSent() ? LrpcResponse.SERVER_TIMEOUT : LrpcResponse.CLIENT_TIMEOUT);
                            timeoutResponse.setmErrorMsg(df.getTimeoutMessage(true));
                            DefaultFuture.received(df.getChannel(), timeoutResponse);
						}
					}
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		
	}
}
