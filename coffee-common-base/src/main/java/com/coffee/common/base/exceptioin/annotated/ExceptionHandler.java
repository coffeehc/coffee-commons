package com.coffee.common.base.exceptioin.annotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.coffee.common.base.exceptioin.IExceptionHandler;


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ExceptionHandler {

	Class<? extends IExceptionHandler> value();
}
