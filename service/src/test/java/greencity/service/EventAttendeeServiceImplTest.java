package greencity.service;

import greencity.dto.eventattendee.EventAttendeeCreateDTO;
import greencity.dto.eventattendee.EventAttendeeDto;
import greencity.dto.eventattendee.EventAttendeeUpdateDto;
import greencity.entity.Event;
import greencity.entity.EventAttendee;
import greencity.entity.User;
import greencity.enums.EventAttendanceStatus;
import greencity.enums.EventAttendeeMark;
import greencity.exception.exceptions.EventStatusCannotBeUpdated;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyAttached;
import greencity.repository.EventAttendeeRepository;
import greencity.repository.EventRepository;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventAttendeeServiceImplTest {
    @InjectMocks
    private EventAttendeeServiceImpl eventAttendeeService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private EventAttendeeRepository eventAttendeeRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void createEventAttendee_CorrectInput_Success() {
        final long eventId = 1L;
        final long userId = 1L;
        final EventAttendeeCreateDTO eventAttendeeCreateDTO = new EventAttendeeCreateDTO(eventId, userId);
        final EventAttendee eventAttendee = new EventAttendee();
        final Event event = new Event();
        final User user = new User();
        event.setOpen(true);

        when(eventAttendeeRepository.existsByEventIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.of(event));
        when(userRepo.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(modelMapper.map(eventAttendeeCreateDTO, EventAttendee.class))
                .thenReturn(eventAttendee);

        eventAttendeeService.createEventAttendee(eventAttendeeCreateDTO);

        Assertions.assertNotNull(eventAttendee.getEvent());
        Assertions.assertNotNull(eventAttendee.getUser());

        verify(eventAttendeeRepository).existsByEventIdAndUserId(anyLong(), anyLong());
        verify(eventRepository).findById(anyLong());
        verify(userRepo).findById(anyLong());
        verify(eventAttendeeRepository).save(any());
        verify(eventAttendeeRepository).existsByEventIdAndUserId(anyLong(), anyLong());
        verify(eventAttendeeRepository).save(any());
    }

    @Test
    void createEventAttendee_InvalidEventId_ThrowsException() {
        final EventAttendeeCreateDTO eventAttendeeCreateDTO = EventAttendeeCreateDTO.builder()
                .eventId(1L)
                .userId(1L)
                .build();
        when(userRepo.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> eventAttendeeService.createEventAttendee(eventAttendeeCreateDTO));
        verify(eventRepository).findById(anyLong());
    }

    @Test
    void createEventAttendee_InvalidUserId_ThrowsException() {
        final EventAttendeeCreateDTO eventAttendeeCreateDTO = EventAttendeeCreateDTO.builder()
                .eventId(1L)
                .userId(1L)
                .build();

        when(userRepo.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> eventAttendeeService.createEventAttendee(eventAttendeeCreateDTO));
        verify(userRepo).findById(anyLong());
    }

    @Test
    void createEventAttendee_ExistsInDatabase_Success() {
        final EventAttendeeCreateDTO eventAttendeeCreateDTO = EventAttendeeCreateDTO.builder()
                .eventId(1L)
                .userId(1L)
                .build();
        when(eventAttendeeRepository.existsByEventIdAndUserId(anyLong(), anyLong()))
                .thenReturn(true);

        assertThrows(UserAlreadyAttached.class, () -> eventAttendeeService.createEventAttendee(eventAttendeeCreateDTO));
        verify(eventAttendeeRepository).existsByEventIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void getEventAttendeesByUserId_FilledList_Success() {
        when(eventAttendeeRepository.findAllByUser_Id(anyLong())).thenReturn(List.of(EventAttendee.builder()
                .id(1L)
                .status(EventAttendanceStatus.ATTENDED)
                .build())
        );

        List<EventAttendeeDto> result = eventAttendeeService.getEventAttendeesByUserId(1L);

        verify(eventAttendeeRepository).findAllByUser_Id(anyLong());
        assertEquals(1, result.size());
    }

    @Test
    void getEventAttendeesByEventId_FilledList_Success() {
        when(eventAttendeeRepository.findAllByEvent_Id(anyLong())).thenReturn(List.of(EventAttendee.builder()
                .id(1L)
                .status(EventAttendanceStatus.ATTENDED)
                .build())
        );

        List<EventAttendeeDto> result = eventAttendeeService.getEventAttendeesByEventId(1L);

        verify(eventAttendeeRepository).findAllByEvent_Id(anyLong());
        assertEquals(1, result.size());
    }

    @Test
    void getEventAttendeesByEventId_EmptyList_Success() {
        when(eventAttendeeRepository.findAllByEvent_Id(anyLong())).thenReturn(List.of());

        List<EventAttendeeDto> result = eventAttendeeService.getEventAttendeesByEventId(1L);

        verify(eventAttendeeRepository).findAllByEvent_Id(anyLong());
        assertEquals(0, result.size());
    }

    @Test
    void updateEventAttendee_FurtherStep_Success() {
        final EventAttendee eventAttendee = EventAttendee.builder()
                .status(EventAttendanceStatus.PLANNED)
                .build();
        final EventAttendeeUpdateDto eventAttendeeUpdateDto = EventAttendeeUpdateDto.builder()
                .status(EventAttendanceStatus.ATTENDED)
                .mark(EventAttendeeMark.HIGH)
                .build();

        when(eventAttendeeRepository.findById(anyLong()))
                .thenReturn(Optional.of(eventAttendee));

        eventAttendeeService.update(1L, eventAttendeeUpdateDto);
        verify(eventAttendeeRepository).findById(anyLong());
        Assertions.assertEquals(EventAttendanceStatus.ATTENDED, eventAttendee.getStatus());
    }

    @Test
    void updateEventAttendee_NotGoodChangeOfStates_ThrowsException() {
        final EventAttendee eventAttendee = new EventAttendee();
        eventAttendee.setStatus(EventAttendanceStatus.ATTENDED);
        when(eventAttendeeRepository.findById(anyLong()))
                .thenReturn(Optional.of(eventAttendee));
        final EventAttendeeUpdateDto eventAttendeeUpdateDto = EventAttendeeUpdateDto.builder()
                .status(EventAttendanceStatus.PLANNED)
                .build();

        assertThrows(EventStatusCannotBeUpdated.class, () -> eventAttendeeService.update(1L, eventAttendeeUpdateDto));
        verify(eventAttendeeRepository).findById(anyLong());
    }

    @Test
    void updateEventAttendee_NotFoundInDatabase_ThrowsException() {
        when(eventAttendeeRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        try {
            eventAttendeeService.update(1L, EventAttendeeUpdateDto.builder().build());
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertEquals(NotFoundException.class, e.getClass());
        }
        verify(eventAttendeeRepository).findById(anyLong());
    }

    @Test
    void deleteEventAttendee_Success() {
        eventAttendeeService.deleteEventAttendee(1L);
        verify(eventAttendeeRepository).deleteById(anyLong());
    }

    @Test
    void deleteEventAttendeesByEventId_Success() {
        eventAttendeeService.deleteEventAttendeesByEventId(1L);
        verify(eventAttendeeRepository).deleteAllByEvent_Id(anyLong());
    }
}