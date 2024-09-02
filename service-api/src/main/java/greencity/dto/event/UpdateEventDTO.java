package greencity.dto.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventDTO {
    @NotNull
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 70, message = "Title cannot exceed 70 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 20, max = 63206, message = "Description cannot exceed 63,206 characters")
    private String description;

    @Valid
    private List<DatesLocationsDto> datesLocations;

    @NotNull
    @Size(min = 1, max = 5, message = "You can upload up to 5 images")
    private List<String> additionalImages;

    private List<String> imagesToDelete;

    @NotNull(message = "Tags are mandatory")
    @Size(min = 1, message = "At least one tag is required")
    private List<String> tags;

    @NotNull
    private Boolean isOpen;
}
