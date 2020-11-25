package com.yaofu.basesdk.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auth:long3.yang
 * Date:2020/10/27
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)//运行时注解,可以通过反射来获取
@Target(ElementType.TYPE)//方法的声明
public @interface UKHost {
    String hostname();
}
