package app.xlui.seckill.aop;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceLock {
}
