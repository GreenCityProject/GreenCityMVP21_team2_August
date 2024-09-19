package greencity.mapping;

import greencity.dto.habit.HabitAssignUserDurationDto;
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
public class HabitAssignUserDurationDtoMapperTest {

    @InjectMocks
    private HabitAssignUserDurationDtoMapper habitAssignUserDurationDtoMapper;

    @Test
    void convertTest() {
        HabitAssign habitAssign = HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .createDate(ZonedDateTime.of(2022, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC")))
                .user(User.builder().id(1L).build())
                .duration(1)
                .habitStreak(1)
                .workingDays(1)
                .lastEnrollmentDate(ZonedDateTime.of(2022, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC")))
                .progressNotificationHasDisplayed(null)
                .userShoppingListItems(null)
                .habit(Habit.builder().build())
                .habitStatusCalendars(new ArrayList<>())
                .build();

        HabitAssignUserDurationDto actual = HabitAssignUserDurationDto.builder()
                .habitAssignId(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .userId(1L)
                .duration(1)
                .workingDays(1)
                .build();

        HabitAssignUserDurationDto expected = habitAssignUserDurationDtoMapper.convert(habitAssign);

        assertEquals(expected, actual);
        assertEquals(expected.getHabitAssignId(), actual.getHabitAssignId());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getWorkingDays(), actual.getWorkingDays());
    }
}