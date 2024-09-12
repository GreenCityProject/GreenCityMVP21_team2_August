package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

public interface EventCommentService {

    EventCommentResponseDto save(Long eventId, EventCommentRequestDto requestDto, UserVO user);

    int countOfComments(Long ecoNewsId);

    EventCommentResponseDto getByEventCommentId(Long eventId, Long commentId);

    PageableDto<EventCommentResponseDto> getAllEventComments(Pageable pageable, Long eventId, UserVO userVO);

    void update(Long  commentId, String commentText, String email);
}