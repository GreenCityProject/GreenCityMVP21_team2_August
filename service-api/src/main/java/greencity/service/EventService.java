package greencity.service;

import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import greencity.dto.event.EventVO;
import greencity.dto.event.UpdateEventDTO;
import greencity.dto.user.UserVO;
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

    /**
     * Method for updating {@link Event} instance.
     *
     * @param updateEventDTO - DTO with event details.
     * @return {@link AddEventDtoResponse} instance.
     */
    AddEventDtoResponse updateEvent(UpdateEventDTO updateEventDTO, List<MultipartFile> images, UserVO user);

    /**
     * Method for deleting the {@link EventVO} instance by its id.
     *
     * @param id   - {@link EventVO} instance id which will be deleted.
     * @param user current {@link UserVO} that wants to delete.
     */
    void delete(Long id, UserVO user);
}
