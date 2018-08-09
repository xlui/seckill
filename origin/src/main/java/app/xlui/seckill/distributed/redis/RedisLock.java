package app.xlui.seckill.distributed.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class RedisLock {
	private static RedissonClient redissonClient;

	public void setRedissonClient(RedissonClient redissonClient) {
		RedisLock.redissonClient = redissonClient;
	}

	/**
	 * ReentrantLock for distributed usage
	 *
	 * @param key lock instance name
	 * @return lock object
	 */
	public static RLock lock(String key) {
		RLock rLock = redissonClient.getLock(key);
		rLock.lock();
		return rLock;
	}

	/**
	 * Distributed lock with timeout. If the lock is not available then the
	 * thread will be blocked until acquire the lock. The lock will be hold
	 * until <code>unlock</code> is invoked
	 * or until <code>timeout</code> seconds have passed.
	 *
	 * @param key     lock instance name
	 * @param timeout the maximum time to hold the lock after granting it
	 * @return lock object
	 */
	public static RLock lock(String key, int timeout) {
		RLock rLock = redissonClient.getLock(key);
		rLock.lock(timeout, TimeUnit.SECONDS);
		return rLock;
	}

	/**
	 * @param key      lock instance name
	 * @param timeout  the maximum time to hold the lock after granting it
	 * @param timeUnit the time unit of timeout
	 * @return lock object
	 * @see RedisLock#lock(String key, int timeout)
	 */
	public static RLock lock(String key, int timeout, TimeUnit timeUnit) {
		RLock rLock = redissonClient.getLock(key);
		rLock.lock(timeout, timeUnit);
		return rLock;
	}

	/**
	 * Returns <code>true</code> as soon as the lock is acquired.
	 * If the lock is obtained by another thread of another process in distributed
	 * system, will wait for <code>waitTime</code> seconds. And then give up
	 * with returns <code>false</code>.
	 * The lock is hold until <code>unlock</code> is invoked
	 * or until <code>leaseTime</code> has passed.
	 *
	 * @param key       lock instance name
	 * @param waitTime  the maximum time to acquire the lock
	 * @param leaseTime lease time
	 * @return <code>true</code> if lock has been successfully acquired
	 */
	public static boolean tryLock(String key, int waitTime, int leaseTime) {
		RLock rLock = redissonClient.getLock(key);
		try {
			return rLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * @param key       lock instance name
	 * @param waitTime  the maximum time to acquire the lock
	 * @param leaseTime lease time
	 * @param timeUnit  time unit
	 * @return <code>true</code> if lock has been successfully acquired
	 * @see RedisLock#tryLock(String key, int waitTime, int leaseTime)
	 */
	public static boolean tryLock(String key, int waitTime, int leaseTime, TimeUnit timeUnit) {
		RLock rLock = redissonClient.getLock(key);
		try {
			return rLock.tryLock(waitTime, leaseTime, timeUnit);
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * unlock distributed lock
	 *
	 * @param key lock instance name
	 */
	public static void unlock(String key) {
		RLock rLock = redissonClient.getLock(key);
		rLock.unlock();
	}

	/**
	 * unlock distributed lock
	 *
	 * @param rLock lock object
	 */
	public static void unlock(RLock rLock) {
		rLock.unlock();
	}
}
