package app.xlui.seckill.aop.resolver;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * The smaller order will be invoked first.
 * <code>@Order</code> annotation indicate that, if <code>@ServiceLock</code> was placed
 * with <code>@Transactional</code>, {@link ServiceLockResolver} will be invoked first
  */
@Component
@Aspect
@Order(1)
public class ServiceLockResolver {
	// Fair Reentrant Lock
	// This lock is singleton, because this class is singleton, this is guaranteed by spring.
	private static final ReentrantLock lock = new ReentrantLock(true);

	@Pointcut("@annotation(app.xlui.seckill.aop.ServiceLock)")
	public void aspect() {
	}

	@Around("aspect()")
	public Object around(ProceedingJoinPoint joinPoint) {
		final ReentrantLock lock = ServiceLockResolver.lock;
		Object object = null;
		lock.lock();
		try {
			object = joinPoint.proceed();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		} finally {
			lock.unlock();
		}
		return object;
	}
}
