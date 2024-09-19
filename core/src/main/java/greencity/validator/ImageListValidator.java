package greencity.validator;

import greencity.annotations.ImageListValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class ImageListValidator implements ConstraintValidator<ImageListValidation, List<MultipartFile>> {

    private final List<String> validType = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    @Override
    public void initialize(ImageListValidation constraintAnnotation) {
        // Initializes the validator in preparation for #isValid calls
    }

    @Override
    public boolean isValid(List<MultipartFile> images, ConstraintValidatorContext constraintValidatorContext) {
        if (images == null || images.isEmpty()) {
            return true;
        }
        for (MultipartFile image : images) {
            if (image != null && !validType.contains(image.getContentType())) {
                return false;
            }
        }
        return true;
    }
}
