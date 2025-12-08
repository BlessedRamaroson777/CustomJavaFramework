package itu.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Json {
    /**
     * HTTP status code to return (default 200)
     */
    int status() default 200;
    
    /**
     * Custom status message (default "success")
     */
    String message() default "success";
}
