package greencity.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.lang.annotation.*;

@NotNull
@Positive
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Documented
public @interface RequiredPositive {
    String message() default "Value must be not null and positive";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
