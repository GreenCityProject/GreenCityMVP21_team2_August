package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.notification.NotificationCreateDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.NotificationService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private NotificationController notificationController;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;

    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final UserVO userVO = getUserVO();
    private final Principal principal = getPrincipal();
    private final String notificationLink = "/notifications";
    private final Locale englishLocale = Locale.forLanguageTag("en");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper),
                        new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void create_EmptyBody_ReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(notificationLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(notificationService, times(0))
                .createNotification(any(NotificationCreateDto.class), anyString());
    }

    @Test
    void create_NotValidBody_ReturnsOk() throws Exception {
        final String content = """
                {
                    "title": "title"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post(notificationLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .principal(principal))
                .andExpect(status().isBadRequest());
        verify(notificationService, times(0))
                .createNotification(any(NotificationCreateDto.class), anyString());
    }

    @Test
    void create_ValidBody_ReturnsOk() throws Exception {
        final NotificationCreateDto notificationCreateDto = NotificationCreateDto.builder()
                .type(NotificationType.EVENT_CREATED)
                .projectName(ProjectName.GREEN_CITY)
                .userId(1L)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(notificationLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationCreateDto))
                        .principal(principal))
                .andExpect(status().isCreated());
        verify(notificationService, times(1))
                .createNotification(any(NotificationCreateDto.class), anyString());
    }

    @Test
    void getById_CorrectRequest_ResponseOkay() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(notificationLink + "/1")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void getFiveUnreadNotifications_CorrectRequest_ResponseOkay() throws Exception {
        when(userService.findByEmail(anyString()))
                .thenReturn(userVO);
        mockMvc.perform(MockMvcRequestBuilders.get(notificationLink + "/unread/latest")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void getAllNotifications_CorrectRequest_ResponseOkay() throws Exception {
        when(userService.findByEmail(anyString()))
                .thenReturn(userVO);
        mockMvc.perform(MockMvcRequestBuilders.get(notificationLink + "/all")
                        .principal(principal))
                .andExpect(status().isOk());
        verify(notificationService).getAllNotifications(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    void countUnread_CorrectRequest_ResponseOkay() throws Exception {
        when(userService.findByEmail(anyString()))
                .thenReturn(userVO);
        mockMvc.perform(MockMvcRequestBuilders.get(notificationLink + "/countUnread")
                        .principal(principal))
                .andExpect(status().isOk());
        verify(notificationService, times(1))
                .countUnreadNotifications(anyLong());
    }


    @Test
    void markAsUnviewed_CorrectRequest_ReturnsOk() throws Exception {
        when(userService.findByEmail(anyString()))
                .thenReturn(userVO);
        mockMvc.perform(MockMvcRequestBuilders.patch(notificationLink + "/unview/1")
                        .principal(principal))
                .andExpect(status().isOk());
        verify(notificationService).markAsUnviewed(anyLong(), anyLong());

    }

    @Test
    void markAsUnviewed_NoNotificationIdInDatabase_ReturnsBadRequest() throws Exception {
        when(userService.findByEmail(anyString()))
                .thenReturn(userVO);
        doThrow(new NotFoundException("Notification not found"))
                .when(notificationService).markAsUnviewed(anyLong(), anyLong());
        mockMvc.perform(MockMvcRequestBuilders.patch(notificationLink + "/unview/1")
                        .principal(principal))
                .andExpect(status().isNotFound());
        verify(notificationService).markAsUnviewed(anyLong(), anyLong());
    }

    @Test
    void markAsViewed_CorrectRequest_ReturnsOk() throws Exception {
        when(userService.findByEmail(anyString()))
                .thenReturn(userVO);
        mockMvc.perform(MockMvcRequestBuilders.patch(notificationLink + "/view/1")
                        .principal(principal))
                .andExpect(status().isOk());
        verify(notificationService).markAsViewed(anyLong(), anyLong());
    }

    @Test
    void markAsViewed_NoNotificationIdInDatabase_ReturnsBadRequest() throws Exception {
        when(userService.findByEmail(anyString()))
                .thenReturn(userVO);
        doThrow(new NotFoundException("Notification not found"))
                .when(notificationService).markAsViewed(anyLong(), anyLong());
        mockMvc.perform(MockMvcRequestBuilders.patch(notificationLink + "/view/1")
                        .principal(principal))
                .andExpect(status().isNotFound());
        verify(notificationService).markAsViewed(anyLong(), anyLong());
    }
}