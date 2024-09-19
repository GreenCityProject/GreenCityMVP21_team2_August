package greencity.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = greencity.validator.DatesValidator.class)
@Documented
public @interface ValidDates {
    String message() default "Finish date must not be earlier than start date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
