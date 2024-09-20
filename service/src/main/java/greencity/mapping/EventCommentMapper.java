package greencity.mapping;

import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.entity.EventComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventCommentMapper extends AbstractConverter<EventCommentRequestDto, EventComment> {
    @Override
    protected EventComment convert(EventCommentRequestDto requestDto) {
        return EventComment.builder()
                .text(requestDto.getText())
                .parentComment(EventComment.builder()
                        .id(requestDto.getParentCommentId())
                        .build())
                .build();
    }
}