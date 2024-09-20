package greencity.validator;

import greencity.annotations.ValidDates;
import greencity.dto.event.DatesLocationsDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DatesValidator implements ConstraintValidator<ValidDates, DatesLocationsDto> {
    @Override
    public void initialize(ValidDates constraintAnnotation) {
    }
    @Override
    public boolean isValid(DatesLocationsDto dto, ConstraintValidatorContext context) {
        if (dto.getStartDate() == null || dto.getFinishDate() == null) {
            return true;
        }
        return !dto.getFinishDate().isBefore(dto.getStartDate());
    }
}
