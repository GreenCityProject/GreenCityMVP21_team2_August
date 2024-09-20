package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.eventattendee.EventAttendeeCreateDTO;
import greencity.dto.eventattendee.EventAttendeeUpdateDto;
import greencity.exception.exceptions.EventStatusCannotBeUpdated;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyAttached;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventAttendeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventAttendeeControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private EventAttendeeController eventAttendeeController;
    @Mock
    private EventAttendeeService eventAttendeeService;
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String eventAttendeeLink = "/event-attendees";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventAttendeeController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void createEventAttendee_CorrectBody_ReturnsCreated() throws Exception {
        final String content = "{\"eventId\": 1, \"userId\": 1}";
        mockMvc.perform(post(eventAttendeeLink)
                        .content(content)
                        .contentType("application/json"))
                .andExpect(status().isCreated());
        verify(eventAttendeeService).createEventAttendee(any(EventAttendeeCreateDTO.class));
    }

    @Test
    void createEventAttendee_IncorrectBody_ReturnsBadRequest() throws Exception {
        final String content = "{\"eventId\": 1, \"userId\": 0}";
        mockMvc.perform(post(eventAttendeeLink)
                        .content(content)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(eventAttendeeService, never()).createEventAttendee(any(EventAttendeeCreateDTO.class));
    }

    @Test
    void createEventAttendee_UserAlreadyAttachedException_ReturnsBadRequest() throws Exception {
        final String content = "{\"eventId\": 1, \"userId\": 1}";
        doThrow(new UserAlreadyAttached("User already attached")).when(eventAttendeeService)
                .createEventAttendee(any(EventAttendeeCreateDTO.class));
        mockMvc.perform(post(eventAttendeeLink)
                        .content(content)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(eventAttendeeService).createEventAttendee(any(EventAttendeeCreateDTO.class));
    }

    @Test
    void createEventAttendee_EventOrUserNotFound_ReturnsNotFound() throws Exception {
        final String content = "{\"eventId\": 1, \"userId\": 1}";
        doThrow(new NotFoundException("Event or user not found")).when(eventAttendeeService)
                .createEventAttendee(any(EventAttendeeCreateDTO.class));
        mockMvc.perform(post(eventAttendeeLink)
                        .content(content)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
        verify(eventAttendeeService).createEventAttendee(any(EventAttendeeCreateDTO.class));
    }

    @Test
    void getEventAttendeesByEventId_CorrectRequest_ReturnsOk() throws Exception {
        when(eventAttendeeService.getEventAttendeesByEventId(anyLong()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get(eventAttendeeLink + "/by-event/1"))
                .andExpect(status().isOk());

        verify(eventAttendeeService).getEventAttendeesByEventId(anyLong());
    }

    @Test
    void getEventAttendeesByUserId_CorrectRequest_ReturnsOk() throws Exception {
        when(eventAttendeeService.getEventAttendeesByUserId(anyLong()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get(eventAttendeeLink + "/by-user/1"))
                .andExpect(status().isOk());

        verify(eventAttendeeService).getEventAttendeesByUserId(anyLong());
    }

    @Test
    void updateEventAttendee_IncorrectId_ConstraintViolation() throws Exception {
        final String content = "{\"status\": \"PLANNED\"}";
        mockMvc.perform(patch(eventAttendeeLink + "/0")
                        .content(content)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEventAttendee_CorrectRequest_ReturnsOk() throws Exception {
        final String content = "{\"status\": \"PLANNED\"}";
        mockMvc.perform(patch(eventAttendeeLink + "/1")
                        .content(content)
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(eventAttendeeService).update(anyLong(), any(EventAttendeeUpdateDto.class));
    }

    @Test
    void updateEventAttendee_EventStatusCannotBeUpdated_ConstraintViolation() throws Exception {
        when(eventAttendeeService.update(anyLong(), any(EventAttendeeUpdateDto.class)))
                .thenThrow(new EventStatusCannotBeUpdated("Event status cannot be updated"));
        final String content = "{\"status\": \"PLANNED\"}";
        mockMvc.perform(patch(eventAttendeeLink + "/1")
                        .content(content)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEventAttendee_NotFound_ReturnsNotFound() throws Exception {
        when(eventAttendeeService.update(anyLong(), any(EventAttendeeUpdateDto.class)))
                .thenThrow(new NotFoundException("Event not found"));

        final String content = "{\"status\": \"PLANNED\"}";
        mockMvc.perform(patch(eventAttendeeLink + "/1")
                        .content(content)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEventAttendeesByUserId_CorrectRequest_Success() throws Exception {
        mockMvc.perform(delete(eventAttendeeLink + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEventAttendeesByUserId_IncorrectId_ConstraintViolation() throws Exception {
        mockMvc.perform(delete(eventAttendeeLink + "/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteEventAttendee_CorrectRequest_Success() throws Exception {
        mockMvc.perform(delete(eventAttendeeLink + "/by-event/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEventAttendee_IncorrectId_ConstraintViolation() throws Exception {
        mockMvc.perform(delete(eventAttendeeLink + "/by-event/0"))
                .andExpect(status().isBadRequest());
    }
}
