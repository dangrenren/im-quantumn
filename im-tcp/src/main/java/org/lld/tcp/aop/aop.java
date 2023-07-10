package org.lld.tcp.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class aop{
    //new a aop to study aop
    //应该会打印出来两个方法的前置通知,返回通知,后置通知
    //注意point可以 具体指向某一个方法,也可以指向某一个类下的所有方法.但是不可以指向某一个包,而是要指向包下的某一个类
    //对于

    //前置通知
    @Before("execution(* org.lld.tcp.server.LimServer.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Before method: " + methodName);
    }
    //返回后通知
    @AfterReturning(pointcut = "execution(* org.lld.tcp.server.LimServer.*(..))", returning = "result")
    public void afterMethodReturnig(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("After method: " + methodName);
        System.out.println("Result: " + result);
    }
    //@AfterReturning：在目标方法成功返回后执行。它只在目标方法成功返回时才触发，不会在目标方法抛出异常或出现其他错误时触发。
    //@After：无论目标方法是成功返回还是抛出异常，@After注解都会在目标方法执行后触发。
    //@AfterReturning：可以通过returning属性指定一个参数名，用于接收目标方法的返回值。在通知方法中可以访问该参数。
    //@After：通知方法中没有默认提供接收目标方法返回值的参数。
    //对于@AfterReturing可以使用@AfterReturning(pointcut = "execution(* com.example.demo.MyService.doSomething(..))") 不接受返回值，用于没有返回值的方法

    //后置通知
    @After("execution(* org.lld.tcp.server.LimServer.*(..))")
    public void afterMethod(JoinPoint joinPoint)
    {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("After method: " + methodName);
    }
    //抛出异常后通知
    @AfterThrowing(pointcut = "execution(* org.lld.tcp.server.LimServer.*(..))", throwing = "ex")
    public  void afterThrowing(JoinPoint joinPoint, Exception ex)
    {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("After method: " + methodName);
        System.out.println("Exception: " + ex.getMessage());
    }

    //环绕通知
    @Around("execution(* org.lld.tcp.server.LimServer.*(..))")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // 在方法执行之前的逻辑
        System.out.println("Before method execution");

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 在方法执行之后的逻辑
        System.out.println("After method execution");
        System.out. println("Result: " + result+" 第二次");
        return result+"第二次";
    }

}
