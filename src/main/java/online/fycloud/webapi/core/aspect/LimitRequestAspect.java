package online.fycloud.webapi.core.aspect;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import online.fycloud.webapi.core.annotation.LimitRequest;
import online.fycloud.webapi.core.common.R;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author VarleyT
 */
@Aspect
@Component
public class LimitRequestAspect {

    private static ConcurrentHashMap<String, ExpiringMap<String, Integer>> book = new ConcurrentHashMap<>();

    @Pointcut("@annotation(limitRequest)")
    public void limitService(LimitRequest limitRequest) {
    }

    @Around("limitService(limitRequest)")
    public Object around(ProceedingJoinPoint pjp, LimitRequest limitRequest) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        ExpiringMap<String, Integer> uc = book.getOrDefault(request.getRequestURI(),
                ExpiringMap.builder().variableExpiration().build());
        Integer uCount = uc.getOrDefault(request.getRemoteAddr(), 0);
        if (uCount >= limitRequest.count()) {
            return R.error("请求频繁！");
        } else if (uCount == 0) {
            uc.put(request.getRemoteAddr(),
                    uCount + 1,
                    ExpirationPolicy.CREATED,
                    limitRequest.time(),
                    TimeUnit.MILLISECONDS);
        } else {
            uc.put(request.getRemoteAddr(), uCount + 1);
        }
        book.put(request.getRequestURI(), uc);
        return pjp.proceed();
    }

}
