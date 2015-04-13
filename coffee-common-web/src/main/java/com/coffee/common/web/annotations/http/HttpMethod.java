/**
 *
 */
package com.coffee.common.web.annotations.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月9日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HttpMethod {

	RequestMethodEunm value() default RequestMethodEunm.GET;

}
