package app.xlui.seckill.distributed.zookeeper;

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

	public static boolean acquire(long time, TimeUnit unit) {
		try {
			return getMutex().acquire(time, unit);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void release() {
		try {
			getMutex().release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
