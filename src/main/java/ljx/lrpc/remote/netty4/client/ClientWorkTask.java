package ljx.lrpc.remote.netty4.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 客户端 进行线程通过线程池运行
 * @author liang
 *
 */
class ClientWorkTask {
	//线程工厂，负责生产线程，设置线程池默认名称
    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("client-future-pool-%d").build();
    //线程池
    private static ExecutorService threadPoolExecutor =
            new ThreadPoolExecutor(//最佳线程数目 = （（线程等待时间+线程CPU时间）/线程CPU时间 ）* CPU数目
                    Runtime.getRuntime().availableProcessors() * 2,//核心线程池大小，返回可用处理器的Java虚拟机的数量*2
                    Runtime.getRuntime().availableProcessors() * 2,
                    100L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(1024),
                    namedThreadFactory,
                    new ThreadPoolExecutor.AbortPolicy());//拒绝策略，默认的拒绝策略就是AbortPolicy。直接抛出异常
    //线程池执行该线程
    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

}
