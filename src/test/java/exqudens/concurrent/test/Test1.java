package exqudens.concurrent.test;

import exqudens.concurrent.util.ExecutorServiceUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test1 {

    /*@Test
    public void test0() throws Throwable {
        ExecutorService executorService = ExecutorServiceUtils.createExecutorService(10, "test");

        Future<String> future1 = executorService.submit(() -> {
            String s = "--- AAA ---";
            System.out.println(s);
            return s;
        });


        Future<String> future2 = executorService.submit(() -> {
            String s = future1.get().replace("AAA", "BBB");
            System.out.println(s);
            return s;
        });

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }*/

    @Test
    public void test1() throws Throwable {
        Future<String> future = calculateAsync();
        String s = future.get();
        Assert.assertEquals("Hello", s);
    }

    @Test
    public void test2() throws Throwable {
        CompletableFuture<String> future = CompletableFuture.completedFuture("Hello");
        String s = future.get();
        Assert.assertEquals("Hello", s);
    }

    @Test
    public void test3() throws Throwable {
        Future<String> future = calculateAsyncWithCancellation();
        try {
            String s = future.get();
        } catch (Throwable t) {
            Assert.assertTrue(t instanceof CancellationException);
        }
    }

    @Test
    public void test4() throws Throwable {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
        String s = future.get();
        Assert.assertEquals("Hello", s);
    }

    @Test
    public void test5() throws Throwable {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future = completableFuture.thenApply(s -> s + " World");
        String s = future.get();
        Assert.assertEquals("Hello World", s);
    }

    @Test
    public void test6() throws Throwable {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            if (true) {
                throw new IllegalArgumentException("qqq");
            }
            return "Hello";
        });
        CompletableFuture<String> future = completableFuture.thenApply(s -> s + " World");
        try {
            String s = future.get();
        } catch (Throwable t) {
            Assert.assertTrue(t instanceof ExecutionException);
            Assert.assertTrue(t.getCause() instanceof IllegalArgumentException);
            Assert.assertEquals("qqq", t.getCause().getMessage());
        }
    }

    @Test
    public void test7() throws Throwable {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<Void> future = completableFuture.thenAccept(s -> System.out.println("Computation returned: " + s));
        future.get();
    }

    @Test
    public void test8() throws Throwable {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<Void> future = completableFuture.thenRun(() -> System.out.println("Computation finished."));
        future.get();
    }

    @Test
    public void test9() throws Throwable {
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> "Hello")
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"))
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + "!!!"));
        String s = completableFuture.get();
        Assert.assertEquals("Hello World!!!", s);
    }

    @Test
    public void test10() throws Throwable {
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> "Hello")
                .thenCombine(CompletableFuture.supplyAsync(() -> " World"), (String s1, String s2) -> s1 + s2)
                .thenCombine(CompletableFuture.supplyAsync(() -> 111), (String s1, Integer s2) -> s1 + s2);
        String s = completableFuture.get();
        Assert.assertEquals("Hello World111", s);
    }

    @Test
    public void test11() throws Throwable {
        CompletableFuture<Void> completableFuture = CompletableFuture
                .supplyAsync(() -> "Hello")
                .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"), (s1, s2) -> System.out.println(s1 + s2));
        completableFuture.get();
    }

    @Test
    public void test12() throws Throwable {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Beautiful");
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3);

        combinedFuture.get();

        Assert.assertTrue(future1.isDone());
        Assert.assertTrue(future2.isDone());
        Assert.assertTrue(future3.isDone());
    }

    @Test
    public void test13() throws Throwable {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Beautiful");
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "World");

        String combined = Stream.of(future1, future2, future3)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));

        Assert.assertEquals("Hello Beautiful World", combined);
    }

    private Future<String> calculateAsync() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(500);
            completableFuture.complete("Hello");
            return null;
        });

        return completableFuture;
    }

    private Future<String> calculateAsyncWithCancellation() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(500);
            completableFuture.cancel(false);
            return null;
        });

        return completableFuture;
    }

}
