package io.polyglotted.common.test;

import io.polyglotted.common.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;

public class ThreadUtilTest extends ThreadUtil {

    @Test
    public void testNotInterrupted() { assertTrue(notInterrupted()); }

    @Test @SuppressWarnings("StatementWithEmptyBody")
    public void testInterrupted() {
        Thread thread = new Thread(() -> {
            while (notInterrupted()) ;
        });
        thread.start();
        thread.interrupt();
    }

    @Test(expected = ExecutionException.class)
    public void testSafeAwait() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        execWithShutdown(() -> {
            safeAwait(latch);
            return null;
        });
    }

    @Test(expected = ExecutionException.class)
    public void testSafeTake() throws Exception {
        final BlockingQueue<String> queue = new SynchronousQueue<>();
        execWithShutdown(() -> {
            safeTake(queue);
            return null;
        });
    }

    private static void execWithShutdown(Callable<Object> task) throws InterruptedException, ExecutionException {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Object> submit = executorService.submit(task);
        new Thread(() -> {
            safeSleep(10, TimeUnit.MILLISECONDS);
            executorService.shutdownNow();
        }).start();
        submit.get();
    }

    @Test
    public void testShutdownNoTerminate() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        waitedFuture(executorService);
        stopThreadPool(executorService, 50, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testShutdownWithInterrupt() {
        Thread thread = new Thread(() -> {
            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            waitedFuture(executorService);
            stopThreadPool(executorService);
        });
        thread.start();
        safeSleep(100);
        thread.interrupt();
    }

    private static void waitedFuture(ExecutorService executorService) { executorService.submit(() -> safeAwait(new CountDownLatch(1))); }
}