package greencity.dto.notification;

import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class NotificationReadDto {
    private long id;
    private String message;
    private LocalDateTime createdDate;
    private NotificationType type;
    private ProjectName projectName;
    private long userId;
}
