package ljx.lrpc.remote.netty4.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

import ljx.lrpc.remote.netty4.exchange.LrpcInvocation;
import ljx.lrpc.remote.netty4.exchange.LrpcRequest;
import ljx.lrpc.remote.netty4.exchange.LrpcResponse;

/**
 * Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果。必要时可以通过get方法获取执行结果，该方法会阻塞直到任务返回结果
 * 重写 判断任务是否取消，是否完成，get获得结果
 * 
 * 就是callable的结果的获取
 * @author liang
 *
 */
public class LrpcFuture implements Future<Object> {

	/**
	 * 自定义AQS，当状态state为1时可以获取锁，释放锁时将state设置为1
	 * @author liang
	 *
	 */
    private static final class Sync extends AbstractQueuedSynchronizer {
        /**
         * future status
         */
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {//独占模式尝试获取，acquire会先调用该方法，如果状态值为1则获取成功
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {//独占模式尝试释放  release方法会先调用该方法
            if (getState() == pending) {
                return compareAndSetState(pending, done);//如果状态值为0，则设置state值为1
            } else {
                return true;
            }
        }
        //状态值为1时返回true
        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }

    private final Sync sync;
    private LrpcRequest request;
    private LrpcResponse response;

    private List<RpcCallback> pendingCallbacks = new ArrayList<>();//挂起的回调
    private ReentrantLock lock = new ReentrantLock();
    
    public LrpcFuture(LrpcRequest request) {
        this.sync = new Sync();
        this.request = request;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);//独占模式下线程获取共享资源，如果获取到资源，线程直接返回，否则进入等待队列，直到获取到资源为止
        if (this.response != null) {
            return this.response.getmResult();
        }
        return null;
    }

    //请求的一发一收会有延迟出现空指针，导致测试直接空指针
    //在返回数据时做延时，不然会有空指针问题
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));//如果当前线程没有在指定时间内获取同步状态，则会返回false，否则返回true。-1数据同acquire
        if (success) {
            if (this.response != null) {
                return this.response.getmResult();
            }
            return null;
        }
        throw new RuntimeException("Timeout exception.Request id: " + this.request.getmId()
                + ". Request class name : " + ((LrpcInvocation)this.request.getmData()).getClass()
                + ". Request method : " + ((LrpcInvocation)this.request.getmData()).getMethodName());
    }


    //根据服务返回数据进行 成功/失败回调
    private void runCallback(final RpcCallback callback) {
        final LrpcResponse response = this.response;
        ClientWorkTask.submit(() -> {
//            if (!response.isError()) {
//                callback.success(response.getmResult());
//            } else {
//                callback.fail(new RuntimeException("FrpcResponse error", new Throwable(response.getError())));
//            }
        });
    }

    private void callback() {
        lock.lock();
        try {
            for (final RpcCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    //将服务的响应结果封装到线程中
    public void done(LrpcResponse response) {
        this.response = response;
        sync.release(1);//释放
        callback();//回调
    }

}
