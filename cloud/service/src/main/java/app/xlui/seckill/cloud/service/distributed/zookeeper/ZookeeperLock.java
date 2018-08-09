package app.xlui.seckill.cloud.service.distributed.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class ZookeeperLock {
    private static String address = "127.0.0.1:2181";
    private static CuratorFramework curator;

    static {
        // Retry Policy
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curator = CuratorFrameworkFactory.newClient(address, retryPolicy);
        curator.start();
    }

    private static final class Singleton {
        static InterProcessMutex mutex = new InterProcessMutex(curator, "/curator/lock");
    }

    public static InterProcessMutex getMutex() {
        return Singleton.mutex;
    }

    /**
     * Try to acquire a zk lock, until success
     *
     * @return true if mutex is acquired, else if exception
     */
    public static boolean acquire() {
        try {
            getMutex().acquire();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Acquire a distributed lock from zookeeper, block until the lock is available
     * or the given time expires. Note that the same thread can call acquire reentrantly
     *
     * @param time time to wait
     * @param unit time unit
     * @return true if mutex is acquired, false if not
     */
    public static boolean acquire(long time, TimeUnit unit) {
        try {
            return getMutex().acquire(time, unit);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * If current thread own the lock, release the mutex. If current thread has
     * multi calls to acquire, the mutex will still be held when this method returns.
     */
    public static void release() {
        try {
            getMutex().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
