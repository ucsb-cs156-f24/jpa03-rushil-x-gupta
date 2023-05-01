package edu.ucsb.cs156.example.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


/**
 * This class is an Aspect that logs all invocations of controller methods that are annotated
 * with @RequestMapping, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping,
 * or @PatchMapping.
 * 
 * For more information on Aspect Oriented Programming (AOP)
 * and AspectJ, including what a JoinPoint is, 
 * refer to <a href="https://www.baeldung.com/aspectj">https://www.baeldung.com/aspectj</a> 
 */

@Slf4j
@Aspect
@Component
public class LoggingAspect {
  // language=PointcutExpression
  private static final String pointcut = """
      @annotation(org.springframework.web.bind.annotation.RequestMapping) ||
      @annotation(org.springframework.web.bind.annotation.GetMapping) ||
      @annotation(org.springframework.web.bind.annotation.PostMapping) ||
      @annotation(org.springframework.web.bind.annotation.PutMapping) ||
      @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
      @annotation(org.springframework.web.bind.annotation.PatchMapping)
      """;

  private ArrayList<String> stoplist = new ArrayList<String>(Arrays.asList(
      "edu.ucsb.cs156.example.controllers.FrontendProxyController"));

  /**
   * This method is called before any controller method that is annotated with
   * @RequestMapping, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping,
   * or @PatchMapping.
   * @param joinPoint
   */

  @Before(pointcut)
  public void logControllers(JoinPoint joinPoint) {
    getCurrentHttpRequest().ifPresent(
        request -> {
          String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
          if (!stoplist.contains(declaringTypeName)) {
            log.info("===== %s %s handled by %s in %s".formatted(request.getMethod(), request.getRequestURI(),
                joinPoint.getSignature().getName(), declaringTypeName));
          }
        });
  }

  private static Optional<HttpServletRequest> getCurrentHttpRequest() {
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast)
        .map(ServletRequestAttributes::getRequest);
  }
}
