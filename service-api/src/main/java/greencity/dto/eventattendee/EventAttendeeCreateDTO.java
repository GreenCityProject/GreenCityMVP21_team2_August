package greencity.dto.eventattendee;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EventAttendeeCreateDTO {
    @NotNull(message = "Event id is mandatory")
    @Positive(message = "Event id must be positive")
    private Long eventId;
    @NotNull(message = "User id is mandatory")
    @Positive(message = "User id must be positive")
    private Long userId;
}
