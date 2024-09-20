package greencity.mapping;

import greencity.dto.eventattendee.EventAttendeeCreateDTO;
import greencity.entity.Event;
import greencity.entity.EventAttendee;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

/**
 * Mapper for {@link EventAttendeeCreateDTO} and {@link EventAttendee}.
 */
@Component
public class EventAttendeeCreateDtoMapper extends AbstractConverter<EventAttendeeCreateDTO, EventAttendee> {
    @Override
    protected EventAttendee convert(final EventAttendeeCreateDTO source) {
        return EventAttendee.builder()
                .event(Event.builder().id(source.getEventId()).build())
                .user(User.builder().id(source.getUserId()).build())
                .build();
    }
}
