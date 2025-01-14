package greencity.controller;

import greencity.constant.ErrorMessage;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.exception.exceptions.InvalidStatusException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitAssignService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Locale;

import static greencity.ModelUtils.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitAssignControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private HabitAssignService habitAssignService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HabitAssignController habitAssignController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final UserVO userVO = getUserVO();

    private final Principal principal = getPrincipal();

    private final HabitAssignCustomPropertiesDto habitAssignCustomPropertiesDto = getHabitAssignCustomPropertiesDto();

    private final UserShoppingAndCustomShoppingListsDto userShoppingAndCustomShoppingListsDto = getUserShoppingAndCustomShoppingListsDto();

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    private final String habitAssignLink = "/habit/assign";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void assignDefault_CorrectRequest_ResponseCreated() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        final long habitId = 1;
        mockMvc.perform(post(habitAssignLink + "/{habitId}", 1)
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(userService, times(1)).findByEmail("test@gmail.com");
        verify(habitAssignService).assignDefaultHabitForUser(habitId, userVO);
    }

    @Test
    void assignCustom_CorrectData_ResponseCreated() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitId = 1;

        final String content = """
                {
                    "friendsIdsList": [2, 3, 4],
                    "habitAssignPropertiesDto": {
                        "duration": 8,
                        "defaultShoppingListItems": [1, 2, 3]
                    }
                 }""";

        mockMvc.perform(post(habitAssignLink + "/{habitId}/custom", habitId)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        final ObjectMapper mapper = new ObjectMapper();
        final HabitAssignCustomPropertiesDto propertiesDto = mapper.readValue(content, HabitAssignCustomPropertiesDto.class);
        verify(habitAssignService, times(1)).assignCustomHabitForUser(habitId, userVO, propertiesDto);
    }

    @Test
    void assignCustom_HabitIdForNotExistingHabit_NotFoundException() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitId = 100;
        Mockito.doThrow(new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId))
                .when(habitAssignService).assignCustomHabitForUser(habitId, userVO, habitAssignCustomPropertiesDto);

        final ObjectMapper mapper = new ObjectMapper();
        final String content = mapper.writeValueAsString(habitAssignCustomPropertiesDto);

        mockMvc.perform(post(habitAssignLink + "/{habitId}/custom", habitId)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNotFound());

        verify(habitAssignService, times(1)).assignCustomHabitForUser(habitId, userVO, habitAssignCustomPropertiesDto);
    }

    @Test
    void updateHabitAssignDuration_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitAssignId = 1;
        final int duration = 9;

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/update-habit-duration?duration=" + duration, habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).updateUserHabitInfoDuration(habitAssignId, userVO.getId(), duration);
    }

    @Test
    void updateHabitAssignDuration_HabitAssignIdForNotExistingHabit_NotFoundException() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitAssignId = 100;
        final int duration = 9;

        Mockito.doThrow(new InvalidStatusException(ErrorMessage.HABIT_ASSIGN_STATUS_IS_NOT_INPROGRESS_OR_USER_HAS_NOT_ANY_ASSIGNED_HABITS))
                .when(habitAssignService).updateUserHabitInfoDuration(habitAssignId, userVO.getId(), duration);

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/update-habit-duration?duration=" + duration, habitAssignId)
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(habitAssignService, times(1)).updateUserHabitInfoDuration(habitAssignId, userVO.getId(), duration);
    }

    @Test
    void getHabitAssign_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitAssignId = 30;
        Locale locale = Locale.ENGLISH;

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}", habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService)
                .getByHabitAssignIdAndUserId(habitAssignId, userVO.getId(), locale.getLanguage());
    }

    @Test
    void getHabitAssign_DifferentUsers_UserHasNoPermissionToAccessException() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        doThrow(new UserHasNoPermissionToAccessException(ErrorMessage.HABIT_ASSIGN_STATUS_IS_NOT_INPROGRESS_OR_USER_HAS_NOT_ANY_ASSIGNED_HABITS))
                .when(habitAssignService).getByHabitAssignIdAndUserId(any(), any(), any());
        final long habitAssignId = 100;
        Locale locale = Locale.ENGLISH;
        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}", habitAssignId)
                        .principal(principal))
                .andExpect(status().isForbidden());

        verify(habitAssignService, times(1)).getByHabitAssignIdAndUserId(habitAssignId, userVO.getId(), locale.getLanguage());
    }

    @Test
    void allForCurrentUser_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        mockMvc.perform(get(habitAssignLink + "/allForCurrentUser")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(userVO.getId(), Locale.ENGLISH.getLanguage());
    }

    @Test
    void getUserShoppingAndCustomShoppingLists_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitAssignId = 1;
        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(userVO.getId(), habitAssignId, Locale.ENGLISH.getLanguage());
    }

    @Test
    void updateUserAndCustomShoppingLists_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitAssignId = 1;

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userShoppingAndCustomShoppingListsDto)))
                .andExpect(status().isOk());

        verify(habitAssignService)
                .fullUpdateUserAndCustomShoppingLists(userVO.getId(), habitAssignId, userShoppingAndCustomShoppingListsDto, Locale.ENGLISH.getLanguage());
    }

    @Test
    void getListOfUserAndCustomShoppingListsInprogress_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitAssignLink + "/allUserAndCustomShoppingListsInprogress")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).getListOfUserAndCustomShoppingListsWithStatusInprogress(userVO.getId(), Locale.ENGLISH.getLanguage());
    }

    @Test
    void getAllHabitAssignsByHabitIdAndAcquired_CorrectData_ResponseOk() throws Exception {
        final long habitId = 1;

        mockMvc.perform(get(habitAssignLink + "/{habitId}/all", habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, Locale.ENGLISH.getLanguage());
    }

    @Test
    void getHabitAssignByHabitId_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitId = 1;

        mockMvc.perform(get(habitAssignLink + "/{habitId}/active", habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, Locale.ENGLISH.getLanguage());
    }

    @Test
    void getUsersHabitByHabitAssignId_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitId = 4;

        mockMvc.perform(get(habitAssignLink + "/{habitId}/more", habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitByUserIdAndHabitAssignId(userVO.getId(), habitId, Locale.ENGLISH.getLanguage());
    }

    @Test
    void updateAssignByHabitId_CorrectData_ResponseOk() throws Exception {
        final long habitAssignId = 4;

        HabitAssignStatDto habitAssignStatDto = new HabitAssignStatDto();
        habitAssignStatDto.setStatus(HabitAssignStatus.ACQUIRED);

        mockMvc.perform(patch(habitAssignLink + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitAssignStatDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateStatusByHabitAssignId(habitAssignId, habitAssignStatDto);
    }

    @Test
    void updateAssignByHabitId_NotCorrectBody_ConstraintViolations() throws Exception {
        final long habitAssignId = 4;

        mockMvc.perform(patch(habitAssignLink + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(400));

        verify(habitAssignService, never()).updateStatusByHabitAssignId(anyLong(), any());
    }

    @Test
    void enrollHabit_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitAssignId = 4;
        final LocalDate date = LocalDate.now();

        mockMvc.perform(post(habitAssignLink + "/{habitAssignId}/enroll/{date}", habitAssignId, date)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).enrollHabit(habitAssignId, userVO.getId(), date, Locale.ENGLISH.getLanguage());
    }

    @Test
    void unenrollHabit_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitAssignId = 4;
        final LocalDate date = LocalDate.now();

        mockMvc.perform(post(habitAssignLink + "/{habitAssignId}/unenroll/{date}", habitAssignId, date)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).unenrollHabit(habitAssignId, userVO.getId(), date);
    }

    @Test
    void getInprogressHabitAssignOnDate_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final LocalDate date = LocalDate.now();

        mockMvc.perform(get(habitAssignLink + "/active/{date}", date)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).findInprogressHabitAssignsOnDate(userVO.getId(), date, Locale.ENGLISH.getLanguage());
    }

    @Test
    void getHabitAssignBetweenDates_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = LocalDate.now();

        mockMvc.perform(get(habitAssignLink + "/activity/{from}/to/{to}", startDate, endDate)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignsBetweenDates(userVO.getId(), startDate, endDate, Locale.ENGLISH.getLanguage());
    }

    @Test
    void cancelHabitAssign_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitId = 4;

        mockMvc.perform(patch(habitAssignLink + "/cancel/{habitId}", habitId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).cancelHabitAssign(habitId, userVO.getId());
    }

    @Test
    void deleteHabitAssign_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitAssignId = 4;

        mockMvc.perform(delete(habitAssignLink + "/delete/{habitId}", habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).deleteHabitAssign(habitAssignId, userVO.getId());
    }

    @Test
    void deleteHabitAssignByHabitId_InvalidData_NotFoundException() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        doThrow(new NotFoundException("Habit not found"))
                .when(habitAssignService)
                .deleteHabitAssign(anyLong(), anyLong());

        mockMvc.perform(delete(habitAssignLink + "/delete/{habitId}", 1)
                .principal(principal))
                .andExpect(status().isNotFound());

        verify(habitAssignService).deleteHabitAssign(anyLong(), anyLong());
    }

    @Test
    void updateShoppingListStatus_CorrectData_ResponseOk() throws Exception {
        final long habitAssignId = 4;
        UpdateUserShoppingListDto updateUserShoppingListDto = getUpdateUserShoppingListDto();

        mockMvc.perform(put(habitAssignLink + "/saveShoppingListForHabitAssign", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserShoppingListDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserShoppingListItem(updateUserShoppingListDto);
    }

    @Test
    void updateProgressNotificationHasDisplayed_CorrectData_ResponseOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        final long habitAssignId = 4;

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/updateProgressNotificationHasDisplayed", habitAssignId)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitAssignService).updateProgressNotificationHasDisplayed(habitAssignId, userVO.getId());
    }
}
