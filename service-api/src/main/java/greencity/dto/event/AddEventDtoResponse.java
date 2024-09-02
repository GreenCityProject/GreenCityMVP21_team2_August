package greencity.dto.event;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AddEventDtoResponse{
    private Long id;

    private String title;

    private String description;

    private boolean open;

    private List<String> imagePaths;

    private List<DatesLocationsDto> datesLocations;

    private List<String> tags;

}
