package greencity.entity;

import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@EntityListeners(NotificationListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String message;

    @CreationTimestamp
    private LocalDateTime createdDate;

    //The additional logic is implemented in EntityListener above
    private LocalDateTime viewedDate;

    private boolean viewed;

    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    @Enumerated(value = EnumType.STRING)
    private ProjectName projectName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
