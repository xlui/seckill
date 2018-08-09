package app.xlui.seckill.cloud.service.aop.resolver;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ServiceLimitResolver {
	private static RateLimiter rateLimiter = RateLimiter.create(20);

	@Pointcut("@annotation(app.xlui.seckill.cloud.service.aop.ServiceLimit)")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) {
		rateLimiter.acquire();
		Object object = null;
		try {
			object = joinPoint.proceed();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return object;
	}
}
