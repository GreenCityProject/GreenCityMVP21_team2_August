package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HabitFactControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HabitFactService habitFactService;

    @InjectMocks
    private HabitFactController habitFactController;

    @Mock
    private Validator validator;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ObjectMapper objectMapper;

    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    private static final String habitFactLink = "/facts";

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(habitFactController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .setValidator(validator)
                .build();
    }

    @Test
    void getRandomFactByHabitId_ReturnsOk() throws Exception {
        mockMvc.perform(get(habitFactLink + "/random/{habitId}", 1L))
                .andExpect(status().isOk());

        verify(habitFactService, times(1)).getRandomHabitFactByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void getHabitFactOfTheDay_ReturnsOk() throws Exception {
        mockMvc.perform(get(habitFactLink + "/dayFact/{languageId}", 1L))
                .andExpect(status().isOk());

        verify(habitFactService, times(1)).getHabitFactOfTheDay(anyLong());
        verify(habitFactService, times(1)).getHabitFactOfTheDay(1L);
    }

    @Test
    void getAll_ReturnsOk() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(habitFactLink))
                .andExpect(status().isOk());

        verify(habitFactService, times(1)).getAllHabitFacts(pageable, "en");
    }

    @Test
    void delete_ReturnsOk() throws Exception {
        mockMvc.perform(delete(habitFactLink + "/{id}", 1L))
                .andExpect(status().isOk());

        verify(habitFactService).delete(1L);
    }

    @Test
    void saveHabitFact_ReturnsOk() throws Exception {
        LanguageDTO languageDTO = LanguageDTO.builder().id(1L).code("en").build();
        LanguageTranslationDTO languageTranslationDTO = LanguageTranslationDTO
                .builder()
                .language(languageDTO)
                .content("New habit fact")
                .build();
        HabitIdRequestDto habitIdRequestDto = HabitIdRequestDto.builder().id(1L).build();
        HabitFactPostDto habitFactPostDto = HabitFactPostDto.builder()
                .translations(Collections.singletonList(languageTranslationDTO))
                .habit(habitIdRequestDto)
                .build();

        mockMvc.perform(post(habitFactLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"translations\": [\n" +
                                "    {\n" +
                                "      \"language\": {\n" +
                                "        \"id\": 1,\n" +
                                "        \"code\": \"en\"\n" +
                                "      },\n" +
                                "      \"content\": \"New habit fact\"\n" +
                                "    }\n" +
                                "  ],\n" +
                                "  \"habit\": {\n" +
                                "    \"id\": 1\n" +
                                "  }\n" +
                                "}"))
                .andExpect(status().isCreated());

        verify(habitFactService, times(1)).save(habitFactPostDto);
    }

    @Test
    void update_ReturnsOk() throws Exception {
        mockMvc.perform(put(habitFactLink + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"habit\": {\n" +
                                "    \"id\": 1\n" +
                                "  },\n" +
                                "  \"translations\": [\n" +
                                "    {\n" +
                                "      \"factOfDayStatus\": \"POTENTIAL\",\n" +
                                "      \"language\": {\n" +
                                "        \"id\": 1,\n" +
                                "        \"code\": \"en\"\n" +
                                "      },\n" +
                                "      \"content\": \"Updated habit fact\"\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}"))
                .andExpect(status().isOk());

        verify(habitFactService, times(1)).update(any(HabitFactUpdateDto.class), eq(1L));
    }

    @Test
    void delete_ReturnsBadRequest() throws Exception {
        doThrow(new BadRequestException(HttpStatus.BAD_REQUEST.toString())).when(habitFactService).delete(1L);

        mockMvc.perform(delete(habitFactLink + "/{id}", 1L))
                .andExpect(status().isBadRequest());
    }
}