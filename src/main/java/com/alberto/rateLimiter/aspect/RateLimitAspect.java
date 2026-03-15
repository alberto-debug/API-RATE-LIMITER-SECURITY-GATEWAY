package com.alberto.rateLimiter.aspect;


import com.alberto.rateLimiter.annotation.RateLimit;
import com.alberto.rateLimiter.business.RateLimiterService;
import com.alberto.rateLimiter.exception.RateLimitExceededException;
import com.alberto.rateLimiter.util.ClientIdentifier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

    private final RateLimiterService rateLimiterService;
    private final ClientIdentifier clientIdentifier;

    @Around("@annotation(rateLimit)")
    public Object enforce(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable{
        HttpServletRequest request = getRequest();

        String clientId = clientIdentifier.getSafeClientIdentifier(request);

        if (!rateLimiterService.isAllowed(clientId, rateLimit.requestsPerMinute(), rateLimit.windowSizeSeconds())){
            log.warn("Rate limit exceeded for client: {}", clientId);
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        return joinPoint.proceed();
    }

    private HttpServletRequest getRequest(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes ==null){
            throw new IllegalStateException("No request context found");
        }

        return attributes.getRequest();
    }


}
