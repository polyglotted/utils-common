package io.polyglotted.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ThreadUtil {

    public static boolean notInterrupted() {
        return !Thread.interrupted();
    }

    public static void stopThreadPool(ExecutorService threadPool) {
        stopThreadPool(threadPool, 10, TimeUnit.SECONDS);
    }

    public static void stopThreadPool(ExecutorService threadPool, long time, TimeUnit unit) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(time, unit)) {
                threadPool.shutdownNow();
                threadPool.awaitTermination(time, unit);
            }
        } catch (InterruptedException ie) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void safeSleep(long duration, TimeUnit unit) {
        safeSleep(unit.toMillis(duration));
    }

    public static void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.debug("sleep interrupted");
        }
    }

    public static void safeAwait(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted waiting for latch");
        }
    }

    public static <T> T safeTake(BlockingQueue<T> queue) {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted taking from queue");
        }
    }
}