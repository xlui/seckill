package app.xlui.seckill.aop.resolver;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ServiceLimitResolver {
	// 5 token per second
	private static RateLimiter rateLimiter = RateLimiter.create(5);

	@Pointcut("@annotation(app.xlui.seckill.aop.ServiceLimit)")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) {
		boolean acquire = rateLimiter.tryAcquire();
		Object object = null;
		try {
			if (acquire) {
				object = joinPoint.proceed();
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return object;
	}
}
