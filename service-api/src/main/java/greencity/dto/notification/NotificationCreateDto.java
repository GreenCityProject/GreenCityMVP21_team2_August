package greencity.dto.notification;

import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class NotificationCreateDto {
    private String[] titleParams;
    private String[] messageParams;
    @NotNull(message = "Type is mandatory")
    private NotificationType type;
    @NotNull(message = "Project name is mandatory")
    private ProjectName projectName;
    @Positive(message = "User id must be positive")
    @NotNull(message = "User id is mandatory")
    private long userId;
}
