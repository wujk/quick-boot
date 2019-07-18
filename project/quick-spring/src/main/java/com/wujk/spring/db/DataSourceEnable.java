package com.wujk.spring.db;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Target({ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceEnable {

    String value() default "";

    String transactionManagerName() default "transactionManager";

}
