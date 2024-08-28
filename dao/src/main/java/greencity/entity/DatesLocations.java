package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatesLocations {
    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column(nullable = false)
    private ZonedDateTime finishDate;

    @Embedded
    private EventCoordinates coordinates;

    private String onlineLink;
}
