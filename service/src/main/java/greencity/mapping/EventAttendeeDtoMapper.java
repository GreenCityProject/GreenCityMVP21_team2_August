package greencity.mapping;

import greencity.dto.eventattendee.EventAttendeeDto;
import greencity.entity.EventAttendee;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventAttendeeDtoMapper extends AbstractConverter<EventAttendee, EventAttendeeDto> {
    @Override
    protected EventAttendeeDto convert(final EventAttendee source) {
        return EventAttendeeDto.builder()
                .id(source.getId())
                .eventId(source.getEvent().getId())
                .eventTitle(source.getEvent().getTitle())
                .userId(source.getUser().getId())
                .userName(source.getUser().getName())
                .userProfilePicturePath(source.getUser().getProfilePicturePath())
                .status(source.getStatus())
                .mark(source.getMark())
                .build();
    }
}
