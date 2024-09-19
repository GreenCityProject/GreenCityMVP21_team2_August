package greencity.mapping;

import greencity.dto.habit.HabitAssignManagementDto;
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
public class HabitAssignManagementDtoMapperTest {

    @InjectMocks
    private HabitAssignManagementDtoMapper habitAssignManagementDtoMapper;

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

        HabitAssignManagementDto actual = HabitAssignManagementDto.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .createDateTime(ZonedDateTime.of(2022, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC")))
                .userId(1L)
                .duration(1)
                .habitStreak(1)
                .workingDays(1)
                .lastEnrollment(ZonedDateTime.of(2022, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC")))
                .build();

        HabitAssignManagementDto expected = habitAssignManagementDtoMapper.convert(habitAssign);

        assertEquals(expected, actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getCreateDateTime(), actual.getCreateDateTime());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getHabitStreak(), actual.getHabitStreak());
        assertEquals(expected.getWorkingDays(), actual.getWorkingDays());
        assertEquals(expected.getLastEnrollment(), actual.getLastEnrollment());
    }

}