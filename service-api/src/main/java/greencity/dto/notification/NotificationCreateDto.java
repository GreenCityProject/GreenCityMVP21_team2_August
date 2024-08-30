package greencity.dto.notification;

import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NotificationCreateDto {
    @NotBlank
    private String message;
    @NotNull
    private NotificationType type;
    @NotNull
    private ProjectName projectName;
    @Positive
    @NotNull
    private long userId;
}
