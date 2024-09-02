package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCoordinates {
    @Column
    private Double latitude;

    @Column
    private Double longitude;
}
