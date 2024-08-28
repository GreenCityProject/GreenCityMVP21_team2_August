package greencity.validator;

import greencity.annotations.ValidAddEventDtoRequest;
import greencity.constant.ErrorMessage;
import greencity.constant.ValidationConstants;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.DatesLocationsDto;
import greencity.exception.exceptions.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class AddEventDtoRequestValidator implements ConstraintValidator<ValidAddEventDtoRequest, AddEventDtoRequest> {
    @Override
    public void initialize(ValidAddEventDtoRequest constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(AddEventDtoRequest addEventDtoRequest, ConstraintValidatorContext constraintValidatorContext) {
        String title = addEventDtoRequest.getTitle();
        if (title == null || title.isBlank()) {
            throw new InvalidTitleException(ErrorMessage.EVENT_TITLE_MANDATORY_EXCEPTION);
        }
        if (title.length() > 70) {
            throw new InvalidTitleException(ErrorMessage.EVENT_TITLE_LENGTH_EXCEPTION);
        }

        String description = addEventDtoRequest.getDescription();
        if (description == null || description.isBlank()) {
            throw new InvalidDescriptionException(ErrorMessage.EVENT_DESCRIPTION_MANDATORY_EXCEPTION);
        }
        if (description.length() < 20 || description.length() > 63206) {
            throw new InvalidDescriptionException(ErrorMessage.EVENT_DESCRIPTION_LENGTH_EXCEPTION);
        }

        List<String> tags = addEventDtoRequest.getTags();
        if (tags == null || tags.isEmpty()) {
            throw new WrongCountOfTagsException(ErrorMessage.WRONG_COUNT_OF_EVENT_TAGS_EXCEPTION);
        }

        List<DatesLocationsDto> datesLocations = addEventDtoRequest.getDatesLocations();
        if (datesLocations == null || datesLocations.isEmpty()) {
            throw new InvalidDatesLocationsException(ErrorMessage.EVENT_DATES_LOCATIONS_EMPTY_EXCEPTION);
        }

        List<String> imagePaths = addEventDtoRequest.getImagePaths();
        if (imagePaths == null || imagePaths.isEmpty() || imagePaths.size() > ValidationConstants.MAX_AMOUNT_OF_IMAGES) {
            throw new InvalidImagePathsException("You can upload up to " + ValidationConstants.MAX_AMOUNT_OF_IMAGES + " images");
        }

        if (datesLocations != null) {
            for (DatesLocationsDto datesLocation : datesLocations) {
                if (datesLocation.getOnlineLink() != null && !datesLocation.getOnlineLink().isEmpty()) {
                    if (!isUrlValid(datesLocation.getOnlineLink())) {
                        throw new InvalidOnlineLinkException(ErrorMessage.INVALID_EVENT_LINK_EXCEPTION);
                    }
                }
            }
        }

        return true;
    }

    private boolean isUrlValid(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
