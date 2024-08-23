package greencity.service;

import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {
    /**
     * Method for creating {@link Event} instance.
     *
     * @param addEventDtoRequest - DTO with event details.
     * @return {@link AddEventDtoResponse} instance.
     */
    AddEventDtoResponse saveEvent(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, String email);
}
