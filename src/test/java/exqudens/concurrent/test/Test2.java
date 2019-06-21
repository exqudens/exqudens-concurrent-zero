package exqudens.concurrent.test;

import exqudens.concurrent.util.ExecutorServiceUtils;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Test2 {

    @Test
    public void testCompleteExceptionally() throws Throwable {
        ExecutorService executorService = ExecutorServiceUtils.createExecutorService(10, "test");

        CompletableFuture<String> head = new CompletableFuture<>();

        CompletableFuture<String> tail = head
                .handleAsync((string, throwable) -> {
                    if (string != null) {
                        return string;
                    } else {
                        System.out.println("0: " + throwable.getClass().getName());
                        return "_";
                    }
                })
                .thenApplyAsync(this::func1, executorService)
                .handleAsync((string, throwable) -> {
                    if (string != null) {
                        return string;
                    } else {
                        System.out.println("1: " + throwable.getClass().getName());
                        return "a";
                    }
                })
                .thenApplyAsync(this::func2, executorService)
                .handleAsync((string, throwable) -> {
                    if (string != null) {
                        return string;
                    } else {
                        System.out.println("2: " + throwable.getClass().getName());
                        return "b";
                    }
                })
                .thenApplyAsync(this::func3, executorService)
                .handleAsync((string, throwable) -> {
                    if (string != null) {
                        return string;
                    } else {
                        System.out.println("3: " + throwable.getClass().getName());
                        return "c";
                    }
                });

        head.completeExceptionally(new IllegalStateException());

        System.out.println("---");
        System.out.println(tail.get());
        System.out.println("---");

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void testCancel() throws Throwable {
        ExecutorService executorService = ExecutorServiceUtils.createExecutorService(10, "test");

        CompletableFuture<String> head = new CompletableFuture<>();

        CompletableFuture<String> tail = head
                .handleAsync((string, throwable) -> {
                    if (string != null) {
                        return string;
                    } else {
                        System.out.println("0: " + throwable.getClass().getName());
                        return "_";
                    }
                })
                .thenApplyAsync(this::func1, executorService)
                .handleAsync((string, throwable) -> {
                    if (string != null) {
                        return string;
                    } else {
                        System.out.println("1: " + throwable.getClass().getName());
                        return "a";
                    }
                })
                .thenApplyAsync(this::func2, executorService)
                .handleAsync((string, throwable) -> {
                    if (string != null) {
                        return string;
                    } else {
                        System.out.println("2: " + throwable.getClass().getName());
                        return "b";
                    }
                })
                .thenApplyAsync(this::func3, executorService)
                .handleAsync((string, throwable) -> {
                    if (string != null) {
                        return string;
                    } else {
                        System.out.println("3: " + throwable.getClass().getName());
                        return "c";
                    }
                });

        head.cancel(true);

        System.out.println("---");
        System.out.println(tail.get());
        System.out.println("---");

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void testFunc2Exceptionally() throws Throwable {
        ExecutorService executorService = ExecutorServiceUtils.createExecutorService(10, "test");

        CompletableFuture<String> head = new CompletableFuture<>();

        CompletableFuture<String> tail = head
                .thenApplyAsync(this::func1, executorService)
                .exceptionally(s -> s + " a")
                .thenApplyAsync(this::func2Exceptionally, executorService)
                .exceptionally(s -> s + " b")
                .thenApplyAsync(this::func3, executorService)
                .exceptionally(s -> s + " c");

        head.complete("0");

        System.out.println("---");
        System.out.println(tail.get());
        System.out.println("---");

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    private String func1(String s) {
        try {
            System.out.println(new Throwable().getStackTrace()[0].getMethodName());
            Thread.sleep(2000L);
            return s + " 1";
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private String func2(String s) {
        try {
            System.out.println(new Throwable().getStackTrace()[0].getMethodName());
            Thread.sleep(2000L);
            return s + " 2";
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private String func2Exceptionally(String s) {
        throw new IllegalStateException();
    }

    private String func3(String s) {
        try {
            System.out.println(new Throwable().getStackTrace()[0].getMethodName());
            Thread.sleep(2000L);
            return s + " 3";
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
