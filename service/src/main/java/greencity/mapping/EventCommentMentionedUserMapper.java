package greencity.mapping;

import greencity.dto.eventcomment.EventCommentMentionedUserDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventCommentMentionedUserMapper extends AbstractConverter<User, EventCommentMentionedUserDto> {
    @Override
    protected EventCommentMentionedUserDto convert(User value) {
        return EventCommentMentionedUserDto.builder()
                .id(value.getId())
                .name(value.getName())
                .build();
    }

    public List<EventCommentMentionedUserDto> mapAllToList(
            Set<User> usersSet) {
        return usersSet.stream().map(this::convert).collect(Collectors.toList());
    }
}