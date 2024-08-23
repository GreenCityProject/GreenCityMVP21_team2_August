package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AddEventDtoRequest {
    @NotBlank(message = "Title is mandatory")
    @Size(max = 70, message = "Title cannot exceed 70 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 20, max = 63206, message = "Description cannot exceed 63,206 characters")
    private String description;

    private boolean open;

    @NotNull(message = "Dates and locations are mandatory")
    private List<DatesLocationsDto> datesLocations;

    @NotNull(message = "Tags are mandatory")
    @Size(min = 1, message = "At least one tag is required")
    private List<String> tags;

    @Size(max = 5, message = "You can upload up to 5 images")
    private List<String> imagePaths;
}
