package greencity.entity;

import greencity.enums.EventAttendanceStatus;
import greencity.enums.EventAttendeeMark;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_attendees")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EventAttendee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(value = EnumType.STRING)
    private EventAttendanceStatus status;

    @Enumerated(value = EnumType.STRING)
    private EventAttendeeMark mark;
}
