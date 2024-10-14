package com.shoppingbackend.admin.category.customAnnotation;

import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueName {
    String message() default "Email is already in use! Try the different email.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
