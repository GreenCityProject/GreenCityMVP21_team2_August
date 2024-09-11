package greencity.mapping;

import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.entity.EventComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventCommentResponseDtoMapper extends AbstractConverter<EventComment, EventCommentResponseDto> {
    private final EventCommentAuthorDtoMapper authorMapper = new EventCommentAuthorDtoMapper();
    private final EventCommentMentionedUserMapper mentionedUserMapper = new EventCommentMentionedUserMapper();
    @Override
    protected EventCommentResponseDto convert(EventComment eventComment) {
        return EventCommentResponseDto.builder()
                .id(eventComment.getId())
                .text(eventComment.getText())
                .createdDate(eventComment.getCreatedDate())
                .author(authorMapper.convert(eventComment.getUser()))
                .eventId(eventComment.getEvent().getId())
                .mentionedUsers(eventComment.getMentionedUsers() != null &&
                        !eventComment.getMentionedUsers().isEmpty() ?
                        mentionedUserMapper.mapAllToList(eventComment.getMentionedUsers()) : new ArrayList<>())
                .build();
    }

    public List<EventCommentResponseDto> mapAllToList(List<EventComment> comments) {
        return comments.stream().map(this::convert).toList();
    }
}