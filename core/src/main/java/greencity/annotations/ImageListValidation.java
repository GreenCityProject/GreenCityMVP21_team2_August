package greencity.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = greencity.validator.ImageListValidator.class)
public @interface ImageListValidation {
    String message() default "Invalid image type in the list";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
