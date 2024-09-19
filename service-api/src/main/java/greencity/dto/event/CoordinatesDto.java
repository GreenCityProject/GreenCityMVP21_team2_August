package greencity.dto.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CoordinatesDto {
    private Double latitude;
    private Double longitude;
}
