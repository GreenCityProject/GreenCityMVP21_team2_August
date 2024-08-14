package greencity.mapping;

import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.entity.Habit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomHabitMapperTest {
    @Test
    public void testConvert() {
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = AddCustomHabitDtoRequest.builder()
                .image("image.png")
                .complexity(1)
                .defaultDuration(21).build();

        Habit habit = new CustomHabitMapper().convert(addCustomHabitDtoRequest);
        assertNotNull(habit, "The converted Habit should not be null");
        assertEquals("image.png", habit.getImage(), "The image should match the one in the DTO");
        assertEquals(1, habit.getComplexity(), "The complexity should match the one in the DTO");
        assertEquals(21, habit.getDefaultDuration(), "The default duration should match the one in the DTO");
        assertTrue(habit.getIsCustomHabit(), "The isCustomHabit field should be true");
    }
}

