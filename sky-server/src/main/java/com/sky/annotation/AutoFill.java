package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// AutoFill自定义注解，加在mapper里的方法上用来告诉切面需要自动填充公共字段

@Target(ElementType.METHOD)// 自定义注解，指定注解的位置只能加在方法上
@Retention(RetentionPolicy.RUNTIME)//修饰注解的注解，决定该AutoFill注解类运行时依然存在，可以通过反射读取
public @interface AutoFill {
    // 数据库操作类型，更新、插入
    OperationType value();//operationType 是枚举类（里面有更新和插入操作），参数名为value

}
