package itu.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Session {
    // Optional key name to retrieve from session
    // If empty, injects the whole session (HttpSession or Map<String,Object>)
    String value() default "";
}
