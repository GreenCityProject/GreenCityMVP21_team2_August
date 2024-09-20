package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitAssignMapperTest {

    @InjectMocks
    private HabitAssignMapper habitAssignMapper;

    @Test
    void convertTest() {
        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .createDateTime(ZonedDateTime.of(2022, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC")))
                .userId(1L)
                .duration(1)
                .habitStreak(1)
                .workingDays(1)
                .lastEnrollmentDate(ZonedDateTime.of(2022, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC")))
                .habitStatusCalendarDtoList(new ArrayList<>())
                .userShoppingListItems(new ArrayList<>())
                .habit(HabitDto.builder()
                        .id(1L)
                        .complexity(2)
                        .defaultDuration(1)
                        .build())
                .progressNotificationHasDisplayed(null)
                .build();

        HabitAssign expected = HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .createDate(ZonedDateTime.of(2022, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC")))
                .user(User.builder().id(1L).build())
                .duration(1)
                .habitStreak(1)
                .workingDays(1)
                .lastEnrollmentDate(ZonedDateTime.of(2022, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC")))
                .progressNotificationHasDisplayed(null)
                .userShoppingListItems(new ArrayList<>())
                .habit(Habit.builder()
                        .id(1L)
                        .complexity(2)
                        .defaultDuration(1)
                        .build())
                .user(null)
                .habitStatusCalendars(null)
                .build();

        HabitAssign actual = habitAssignMapper.convert(habitAssignDto);

        assertEquals(expected, actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getCreateDate(), actual.getCreateDate());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getHabitStreak(), actual.getHabitStreak());
        assertEquals(expected.getWorkingDays(), actual.getWorkingDays());
        assertEquals(expected.getLastEnrollmentDate(), actual.getLastEnrollmentDate());
        assertEquals(expected.getProgressNotificationHasDisplayed(), actual.getProgressNotificationHasDisplayed());
        assertEquals(expected.getUserShoppingListItems(), actual.getUserShoppingListItems());
        assertEquals(expected.getHabit(), actual.getHabit());
        assertEquals(expected.getHabitStatusCalendars(), actual.getHabitStatusCalendars());
    }

}