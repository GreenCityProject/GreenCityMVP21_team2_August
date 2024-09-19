package greencity.dto.event;

import greencity.dto.user.EventAuthorDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class EventForSendEmailDto {

    private String title;
    private String description;
    private String unsubscribeToken;
    private List<String> imagePaths;
    private List<DatesLocationsDto> datesLocations;
    private EventAuthorDto author;
}
