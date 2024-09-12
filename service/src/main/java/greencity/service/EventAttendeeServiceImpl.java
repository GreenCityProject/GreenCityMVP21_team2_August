package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.eventattendee.EventAttendeeCreateDTO;
import greencity.dto.eventattendee.EventAttendeeDto;
import greencity.dto.eventattendee.EventAttendeeUpdateDto;
import greencity.entity.Event;
import greencity.entity.EventAttendee;
import greencity.entity.User;
import greencity.enums.EventAttendanceStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.EventStatusCannotBeUpdated;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyAttached;
import greencity.repository.EventAttendeeRepository;
import greencity.repository.EventRepository;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class EventAttendeeServiceImpl implements EventAttendeeService {
    private UserRepo userRepo;
    private EventAttendeeRepository eventAttendeeRepository;
    private EventRepository eventRepository;
    private ModelMapper modelMapper;

    @Override
    public EventAttendeeDto createEventAttendee(final EventAttendeeCreateDTO eventAttendeeCreateDTO) {
        if (eventAttendeeRepository.existsByEventIdAndUserId(eventAttendeeCreateDTO.getEventId(), eventAttendeeCreateDTO.getUserId())) {
            throw new UserAlreadyAttached("You have already signed up to attend this event");
        }
        final User user = userRepo.findById(eventAttendeeCreateDTO.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + eventAttendeeCreateDTO.getUserId()));
        final Event event = eventRepository.findById(eventAttendeeCreateDTO.getEventId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND + eventAttendeeCreateDTO.getEventId()));
        if (!event.isOpen()) {
            throw new BadRequestException(ErrorMessage.EVENT_CLOSE);
        }
        final EventAttendee eventAttendee = modelMapper.map(eventAttendeeCreateDTO, EventAttendee.class);
        eventAttendee.setEvent(event);
        eventAttendee.setUser(user);
        eventAttendee.setStatus(EventAttendanceStatus.PLANNED);
        return modelMapper.map(eventAttendeeRepository.save(eventAttendee), EventAttendeeDto.class);
    }

    @Override
    public List<EventAttendeeDto> getEventAttendeesByEventId(final long eventId) {
        return eventAttendeeRepository.findAllByEvent_Id(eventId).stream()
                .map(eventAttendee -> modelMapper.map(eventAttendee, EventAttendeeDto.class))
                .toList();
    }

    @Override
    public List<EventAttendeeDto> getEventAttendeesByUserId(final long userId) {
        return eventAttendeeRepository.findAllByUser_Id(userId).stream()
                .map((element) -> modelMapper.map(element, EventAttendeeDto.class))
                .toList();
    }

    @Override
    public EventAttendeeDto update(final long id, final EventAttendeeUpdateDto eventAttendeeUpdateDto) {
        final EventAttendee eventAttendee = eventAttendeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND + id));
        if (eventAttendee.getStatus().getNumberOfSequence() > eventAttendeeUpdateDto.getStatus().getNumberOfSequence()) {
            throw new EventStatusCannotBeUpdated("Status cannot be updated, because it is lower than the current one");
        }
        eventAttendee.setStatus(eventAttendeeUpdateDto.getStatus());
        if (eventAttendee.getStatus() == EventAttendanceStatus.ATTENDED) {
            eventAttendee.setMark(eventAttendeeUpdateDto.getMark());
        }
        return modelMapper.map(eventAttendee, EventAttendeeDto.class);
    }

    @Override
    public void deleteEventAttendee(final long id) {
        eventAttendeeRepository.deleteById(id);
    }

    @Override
    public void deleteEventAttendeesByEventId(final long eventId) {
        eventAttendeeRepository.deleteAllByEvent_Id(eventId);
    }
}