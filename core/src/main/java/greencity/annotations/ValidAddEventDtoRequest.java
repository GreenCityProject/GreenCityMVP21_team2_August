package greencity.annotations;

import greencity.validator.AddEventDtoRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AddEventDtoRequestValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ValidAddEventDtoRequest {
    String message() default "Invalid event data";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
