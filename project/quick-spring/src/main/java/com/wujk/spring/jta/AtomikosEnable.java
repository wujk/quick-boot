package com.wujk.spring.jta;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AtomikosEnable {

    String value() default "";

    String transactionManagerName() default "atomikosJta";

}
