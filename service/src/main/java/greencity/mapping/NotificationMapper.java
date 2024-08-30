package greencity.mapping;

import greencity.dto.notification.NotificationCreateDto;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationType;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper extends AbstractConverter<NotificationCreateDto, Notification> {
    @Override
    public Notification convert(NotificationCreateDto source) {
        return Notification.builder()
                .message(source.getMessage())
                .type(source.getType())
                .projectName(source.getProjectName())
                .user(mapToUser(source.getUserId()))
                .build();
    }

    private User mapToUser(long userId) {
        return User.builder()
                .id(userId)
                .build();
    }
}
