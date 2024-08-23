package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import greencity.dto.event.EventForSendEmailDto;
import greencity.dto.user.EventAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static greencity.constant.AppConstant.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final RestClient restClient;
    private final HttpServletRequest httpServletRequest;

    @Override
    public AddEventDtoResponse saveEvent(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, String email) {
        Event eventToSave = genericSave(addEventDtoRequest, images, email);

        AddEventDtoResponse addEventDtoResponse = modelMapper.map(eventToSave, AddEventDtoResponse.class);
//        sendEmailDto(addEventDtoResponse, eventToSave.getAuthor());

        return addEventDtoResponse;
    }

    private Event genericSave(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, String email) {
        Event eventToSave = modelMapper.map(addEventDtoRequest, Event.class);
        UserVO byEmail = restClient.findByEmail(email);
        User user = modelMapper.map(byEmail, User.class);
        eventToSave.setAuthor(user);

        List<String> imagePaths = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imagePath = fileService.upload(image);
                imagePaths.add(imagePath);
            }
            eventToSave.setImagePaths(imagePaths);
        }

        try {
            eventRepository.save(eventToSave);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENT_NOT_SAVED);
        }
        return eventToSave;
    }

    public void sendEmailDto(AddEventDtoResponse addEventDtoResponse,
                             User user) {
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        EventAuthorDto eventAuthorDto = modelMapper.map(user, EventAuthorDto.class);
        EventForSendEmailDto dto = EventForSendEmailDto.builder()
                .author(eventAuthorDto)
                .title(addEventDtoResponse.getTitle())
                .description(addEventDtoResponse.getDescription())
                .unsubscribeToken(accessToken)
                .imagePaths(addEventDtoResponse.getImagePaths())
                .datesLocations(addEventDtoResponse.getDatesLocations())
                .build();
            restClient.addEvent(dto);
    }
}
