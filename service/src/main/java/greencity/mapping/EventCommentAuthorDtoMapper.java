package greencity.mapping;

import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;

public class EventCommentAuthorDtoMapper extends AbstractConverter<User, EventCommentAuthorDto> {
    @Override
    protected EventCommentAuthorDto convert(User user) {
        return EventCommentAuthorDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userProfilePicturePath(user.getProfilePicturePath())
                .build();
    }
}