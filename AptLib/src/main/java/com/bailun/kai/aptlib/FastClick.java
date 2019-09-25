package com.bailun.kai.aptlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义编译时注解
 * 作用于方法(点击事件)
 * @author : kai.mao
 * @date :  2019/9/17
 */


@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface FastClick {

    int value();

}
