package exqudens.concurrent.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceUtils {

    public static ExecutorService createExecutorService(int size, String name) {
        ExecutorService executorService = new ThreadPoolExecutor(
                size,
                size,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(size, true),
                new NamedThreadFactory(name),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return executorService;
    }

}
