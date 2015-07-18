package derson.com.httpsender.AsyncHttpClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chengli on 15/7/12.
 */
public class SingleHolder {
        private static final int CORE_POOL_SIZE = 3;
        private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE + 1;
        private static final int KEEP_ALIVE = 1;

        private static final BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue<Runnable>();
        private static final ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "BBSHttp#" + mCount.getAndIncrement());
            }
        };

        public static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, sWorkQueue, sThreadFactory,
                new ThreadPoolExecutor.DiscardOldestPolicy());
}
