package com.kxdkcf.utils.pool;

import java.util.concurrent.*;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.utils.pool
 * Author:              wenhao
 * CreateTime:          2025-03-01  13:17
 * Description:         TODO
 * Version:             1.0
 */
public class ThreadPool {

    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            10,
            20,
            1,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(5),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static ExecutorService getThreadPool() {

        int i = pool.prestartAllCoreThreads();
        System.out.println("核心线程数 = " + i);

        return pool;

    }

}
