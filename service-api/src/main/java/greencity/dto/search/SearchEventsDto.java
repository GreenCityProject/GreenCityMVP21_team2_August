package greencity.dto.search;

import greencity.dto.event.DatesLocationsDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SearchEventsDto {
    private Long id;
    private String title;
    private String description;
    private List<DatesLocationsDto> datesLocations;
    private List<String> tags;
    private String author;
    private List<String> imagePaths;
}
