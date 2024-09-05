package greencity.mapping;

import greencity.dto.notification.NotificationReadDto;
import greencity.entity.Notification;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationReadDtoMapper extends AbstractConverter<Notification, NotificationReadDto> {
    @Override
    protected NotificationReadDto convert(Notification source) {
        return NotificationReadDto.builder()
                .id(source.getId())
                .title(source.getTitle())
                .message(source.getMessage())
                .createdDate(source.getCreatedDate())
                .type(source.getType())
                .projectName(source.getProjectName())
                .userId(source.getUser().getId())
                .build();
    }
}
