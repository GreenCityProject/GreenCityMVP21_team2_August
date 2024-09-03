package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.newssubscription.NewsSubscriptionDto;
import greencity.exception.exceptions.AlreadySubscribedException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.NewsSubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NewsSubscriptionControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private NewsSubscriptionController newsSubscriptionController;
    @Mock
    private NewsSubscriptionService newsSubscriptionService;
    private final String notificationPath = "/newsSubscriptions";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(newsSubscriptionController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void findAll_CorrectRequest_StatusOk() throws Exception {
        mockMvc.perform(get(notificationPath))
                .andExpect(status().isOk());
        verify(newsSubscriptionService, times(1)).findAll();
    }

    @Test
    void subscribe_CorrectRequest_StatusCreated() throws Exception {
        final NewsSubscriptionDto dto = getNewsSubscriptionDtoForRequest();
        final String content = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post(notificationPath + "/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        verify(newsSubscriptionService, times(1)).subscribe(any());
    }

    @Test
    void subscribe_NoBody_StatusBadRequest() throws Exception {
        mockMvc.perform(post(notificationPath + "/subscribe"))
                .andExpect(status().isBadRequest());
        verify(newsSubscriptionService, never()).subscribe(any());
    }

    @Test
    void subscribe_IncorrectEmailInBody_StatusBadRequest() throws Exception {
        final String content = """
                {
                    "email": "email@mail"
                }
                """;
        mockMvc.perform(post(notificationPath + "/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
        verify(newsSubscriptionService, never()).subscribe(any());
    }

    @Test
    void subscribe_EmailAlreadySubscribed_StatusBadRequest() throws Exception {
        when(newsSubscriptionService.subscribe(any()))
                .thenThrow(AlreadySubscribedException.class);
        mockMvc.perform(post(notificationPath + "/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getNewsSubscriptionDtoForRequest())))
                .andExpect(status().isBadRequest());
        verify(newsSubscriptionService, times(1)).subscribe(any());
    }

    @Test
    void isSubscribed_CorrectEmailAsParam_StatusOk() throws Exception {
        final String email = "email@mail.com";
        mockMvc.perform(get(notificationPath + "/isSubscribed")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(newsSubscriptionService, times(1)).isSubscribed(email);
    }

    @Test
    void isSubscribed_IncorrectEmailAsParam_StatusBadRequest() throws Exception {
        final String email = "email@mail";
        mockMvc.perform(get(notificationPath + "/isSubscribed")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(newsSubscriptionService, never()).isSubscribed(any());
    }
}
