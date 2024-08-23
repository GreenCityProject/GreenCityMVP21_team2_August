package greencity.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatesLocationsDto {
    @NotNull(message = "Start date is mandatory")
    private ZonedDateTime startDate;

    @NotNull(message = "Finish date is mandatory")
    private ZonedDateTime finishDate;

    @NotNull(message = "Coordinates are mandatory")
    private CoordinatesDto coordinates;

    private String onlineLink;
}
