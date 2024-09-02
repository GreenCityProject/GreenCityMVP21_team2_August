package greencity.dto.event;

import greencity.dto.user.UserVO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EventVO {
    private Long id;
    private String title;
    private String description;
    private boolean open;
    private List<DatesLocationsDto> datesLocations;
    private List<String> tags;
    private UserVO author;
    private List<String> imagePaths;
}
