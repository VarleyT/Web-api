package online.fycloud.webapi.core.aspect;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.core.common.R;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author VarleyT
 */
@Aspect
@Slf4j
@Component
public class CheckLoginAspect {

    @Around("@annotation(online.fycloud.webapi.core.annotation.CheckLogin)")
    public Object round(ProceedingJoinPoint point) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        HttpServletResponse response = sra.getResponse();
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setContentType("text/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(R.error("未登录")));
            return null;
        } else {
            Object user = session.getAttribute("loginUser");
            if (user == null) {
                response.setContentType("text/json;charset=utf-8");
                response.getWriter().write(JSON.toJSONString(R.error("未登录")));
                return null;
            }
        }
        return point.proceed();
    }
}
