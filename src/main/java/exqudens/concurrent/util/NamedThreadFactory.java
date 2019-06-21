package exqudens.concurrent.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger increment;
    private final String name;

    public NamedThreadFactory(String name) {
        super();
        this.increment = new AtomicInteger(1);
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(name + "-" + increment.getAndIncrement());
        t.setUncaughtExceptionHandler(
            (thread, throwable) -> System.err.println("Exception in thread '" + thread.getName() + "'")
        );
        return t;
    }

}
