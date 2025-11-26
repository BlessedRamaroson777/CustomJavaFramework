package itu.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyURL {
    String value() default "";
    // Optional HTTP methods to accept for this route. Empty = accept any method.
    String[] methods() default {};
}
