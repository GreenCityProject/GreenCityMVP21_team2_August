package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.*;
import greencity.dto.user.EventAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
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
import java.util.stream.Collectors;

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

    @Override
    public AddEventDtoResponse updateEvent(UpdateEventDTO updateEventDTO, List<MultipartFile> images, UserVO user) {
        Event toUpdate = modelMapper.map(eventRepository.findById(updateEventDTO.getId()), Event.class);
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(toUpdate.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        enhanceWithNewData(toUpdate, updateEventDTO, images);
        Event updatedEvent = eventRepository.save(toUpdate);

        return modelMapper.map(updatedEvent, AddEventDtoResponse.class);
    }

    @Override
    public void delete(Long id, UserVO user) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found by id: " + id));

        EventVO eventVO = modelMapper.map(event, EventVO.class);

        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(eventVO.getAuthor().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        eventRepository.deleteById(eventVO.getId());
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

    private void enhanceWithNewData(Event toUpdate, UpdateEventDTO updateEventDTO,
                                    List<MultipartFile> images) {
        toUpdate.setTitle(updateEventDTO.getTitle());
        toUpdate.setTags(updateEventDTO.getTags());
        toUpdate.setDescription(updateEventDTO.getDescription());
        toUpdate.setOpen(updateEventDTO.getIsOpen());
        toUpdate.setDatesLocations(updateEventDTO.getDatesLocations().stream()
                .map(dateLocationDTO -> modelMapper.map(dateLocationDTO, DatesLocations.class))
                .collect(Collectors.toList()));

        List<String> imagePaths = new ArrayList<>(updateEventDTO.getAdditionalImages());

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imagePath = fileService.upload(image);
                imagePaths.add(imagePath);
            }
        }

        if (updateEventDTO.getImagesToDelete() != null) {
            imagePaths.removeAll(updateEventDTO.getImagesToDelete());
        }

        toUpdate.setImagePaths(imagePaths);
    }
}
