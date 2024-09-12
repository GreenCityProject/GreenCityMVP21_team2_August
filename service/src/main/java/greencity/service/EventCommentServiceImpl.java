package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.enums.CommentStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private final EventRepository eventRepository;
    private final EventCommentRepo eventCommentRepo;
    private final UserServiceImpl userServiceImpl;
    private final UserService userService;
    private ModelMapper modelMapper;

    @Override
    public EventCommentResponseDto save(Long eventId, EventCommentRequestDto requestDto, UserVO user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found."));
        EventComment eventComment = modelMapper.map(requestDto, EventComment.class);
        eventComment.setEvent(event);
        eventComment.setUser(modelMapper.map(user, User.class));
        eventComment = eventCommentRepo.save(eventComment);
        return modelMapper.map(eventComment, EventCommentResponseDto.class);
    }

    @Override
    public int countOfComments(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found."));
        return eventCommentRepo.countByEvent(event);
    }

    @Override
    public PageableDto<EventCommentResponseDto> getAllEventComments(Pageable pageable, Long eventId, UserVO userVO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found."));
        Page<EventComment> pages = eventCommentRepo.findAllByEventOrderByCreatedDateDesc(event, pageable);
        List<EventCommentResponseDto> eventComments = pages.stream()
                .map(eventComment -> modelMapper.map(eventComment, EventCommentResponseDto.class))
                .toList();
        return new PageableDto<>(
                eventComments,
                pages.getTotalElements(),
                pages.getPageable().getPageNumber(),
                pages.getTotalPages());
    }

    @Transactional
    @Override
    public void update(Long commentId, String commentText, String email) {
        EventComment comment = eventCommentRepo.findByIdAndCommentStatus(commentId, CommentStatus.DELETED)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_Id + commentId));

        UserVO userVO = userService.findByEmail(email);

        if (!userVO.getId().equals(comment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        comment.setText(commentText);
        comment.setCommentStatus(CommentStatus.EDITED);
        eventCommentRepo.save(comment);

    }

}