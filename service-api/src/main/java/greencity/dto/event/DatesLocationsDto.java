package greencity.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatesLocationsDto {
    @NotNull(message = "Start date is mandatory")
    @FutureOrPresent(message = "Start date cannot be in the past.")
    private ZonedDateTime startDate;

    @NotNull(message = "Finish date is mandatory")
    @Future(message = "Finish date must be in the future.")
    private ZonedDateTime finishDate;


    private CoordinatesDto coordinates;

    @Pattern(regexp = "^(https?://).*", message = "Please add a link to the event. The link must start with http(s)://")
    private String onlineLink;
}
