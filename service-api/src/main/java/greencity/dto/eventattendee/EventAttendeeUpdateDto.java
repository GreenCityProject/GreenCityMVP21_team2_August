package greencity.dto.eventattendee;

import greencity.enums.EventAttendanceStatus;
import greencity.enums.EventAttendeeMark;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EventAttendeeUpdateDto {
    @NotNull
    private EventAttendanceStatus status;
    private EventAttendeeMark mark;
}
