package greencity.mapping;

import greencity.dto.notification.NotificationCreateDto;
import greencity.dto.notification.NotificationReadDto;
import greencity.entity.Notification;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationDtoMapperTest {
    private final NotificationMapper notificationDtoMapper = new NotificationMapper();

    @Test
    void test() {
        final NotificationCreateDto notificationCreateDto = NotificationCreateDto.builder()
                .type(NotificationType.EVENT_CREATED)
                .projectName(ProjectName.GREEN_CITY)
                .userId(1L)
                .build();

        final Notification notification = notificationDtoMapper.convert(notificationCreateDto);
        assertEquals(NotificationType.EVENT_CREATED, notification.getType());
        assertEquals(ProjectName.GREEN_CITY, notification.getProjectName());
        assertEquals(1L, notification.getUser().getId());
    }
}
