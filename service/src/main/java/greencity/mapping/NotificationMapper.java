package greencity.mapping;

import greencity.dto.notification.NotificationCreateDto;
import greencity.entity.Notification;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for mapping between {@link NotificationCreateDto} and {@link Notification} entities.
 * It is used by {@link ModelMapper}
 */
@Component
public class NotificationMapper extends AbstractConverter<NotificationCreateDto, Notification> {
    /**
     * Converts a NotificationCreateDto to a Notification entity.
     *
     * @param source the {@link NotificationCreateDto} to be converted
     * @return the converted Notification entity
     */
    @Override
    public Notification convert(NotificationCreateDto source) {
        return Notification.builder()
                .type(source.getType())
                .projectName(source.getProjectName())
                .user(mapToUser(source.getUserId()))
                .build();
    }

    /**
     * Maps a user ID to a {@link User} entity.
     *
     * @param userId the ID of the user
     * @return the User entity
     */
    private User mapToUser(long userId) {
        return User.builder()
                .id(userId)
                .build();
    }
}
