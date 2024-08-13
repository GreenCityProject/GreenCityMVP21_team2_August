package greencity.controller;

import greencity.dto.habitstatistic.*;
import greencity.enums.HabitRate;
import greencity.service.HabitStatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitStatisticControllerTest {

    private static final String HABIT_STATISTIC_URL = "/habit/statistic";

    private MockMvc mockMvc;

    @Mock
    private HabitStatisticService habitStatisticService;

    @InjectMocks
    private HabitStatisticController habitStatisticController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAllByHabitIdShouldReturnHabitStatisticsWhenFound() throws Exception {
        Long habitId = 1L;
        ZonedDateTime now = ZonedDateTime.now();

        List<HabitStatisticDto> habitStatistics = Arrays.asList(
                HabitStatisticDto.builder().id(1L).habitRate(HabitRate.GOOD).createDate(now).amountOfItems(5).habitAssignId(1L).build(),
                HabitStatisticDto.builder().id(2L).habitRate(HabitRate.NORMAL).createDate(now).amountOfItems(3).habitAssignId(1L).build()
        );

        GetHabitStatisticDto getHabitStatisticDto = GetHabitStatisticDto.builder()
                .amountOfUsersAcquired(10L)
                .habitStatisticDtoList(habitStatistics)
                .build();

        when(habitStatisticService.findAllStatsByHabitId(habitId)).thenReturn(getHabitStatisticDto);

        mockMvc.perform(get(HABIT_STATISTIC_URL + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitStatisticService).findAllStatsByHabitId(habitId);
    }

    @Test
    void findAllStatsByHabitAssignIdShouldReturnHabitStatisticsListWhenFound() throws Exception {
        Long habitAssignId = 1L;
        ZonedDateTime now = ZonedDateTime.now();

        List<HabitStatisticDto> habitStatistics = Arrays.asList(
                HabitStatisticDto.builder().id(1L).habitRate(HabitRate.GOOD).createDate(now).amountOfItems(5).habitAssignId(1L).build(),
                HabitStatisticDto.builder().id(2L).habitRate(HabitRate.NORMAL).createDate(now).amountOfItems(3).habitAssignId(1L).build()
        );

        when(habitStatisticService.findAllStatsByHabitAssignId(habitAssignId)).thenReturn(habitStatistics);

        mockMvc.perform(get(HABIT_STATISTIC_URL + "/assign/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitStatisticService).findAllStatsByHabitAssignId(habitAssignId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateStatisticShouldReturnStatusOK() throws Exception {
        Long habitStatisticId = 1L;
        Long userId = 1L;
        UpdateHabitStatisticDto updateHabitStatisticDto = UpdateHabitStatisticDto.builder()
                .amountOfItems(5)
                .habitRate(HabitRate.GOOD)
                .build();

        when(habitStatisticService.update(habitStatisticId, userId, updateHabitStatisticDto))
                .thenReturn(updateHabitStatisticDto);

        mockMvc.perform(put(HABIT_STATISTIC_URL + "/{id}", habitStatisticId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateHabitStatisticDto)))
                .andExpect(status().isOk());

        verify(habitStatisticService).update(habitStatisticId, userId, updateHabitStatisticDto);
    }

    @Test
    void getTodayStatisticsForAllHabitItemsShouldReturnHabitStatisticsListAmountAllHabits() throws Exception {
        List<HabitItemsAmountStatisticDto> habitItemsAmountStatisticDtoList = Arrays.asList(
                HabitItemsAmountStatisticDto.builder().habitItem("habitItem1").notTakenItems(1L).build(),
                HabitItemsAmountStatisticDto.builder().habitItem("habitItem2").notTakenItems(2L).build()
        );

        when(habitStatisticService.getTodayStatisticsForAllHabitItems(any())).thenReturn(habitItemsAmountStatisticDtoList);

        mockMvc.perform(get(HABIT_STATISTIC_URL + "/todayStatisticsForAllHabitItems")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitStatisticService).getTodayStatisticsForAllHabitItems(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void findAmountOfAcquiredHabitsShouldReturnOk() throws Exception {
        mockMvc.perform(get(HABIT_STATISTIC_URL + "/acquired/count")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void findAmountOfHabitsInProgressShouldReturnOk() throws Exception {
        mockMvc.perform(get(HABIT_STATISTIC_URL + "/in-progress/count")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}