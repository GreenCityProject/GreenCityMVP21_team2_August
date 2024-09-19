package greencity.service;

import com.fasterxml.jackson.databind.cfg.MapperConfig;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.entity.Event;
import greencity.enums.CommentStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.dto.eventcomment.EventCommentMessageInfoDto;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepository;
import greencity.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private final EventRepository eventRepository;
    private final EventCommentRepo eventCommentRepo;
    private final UserService userService;
    private final RestClient restClient;
    private final UserRepo userRepo;
    private ModelMapper modelMapper;
    private final ThreadPoolExecutor emailThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    @Override
    public EventCommentResponseDto save(Long eventId, EventCommentRequestDto requestDto, UserVO user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        EventComment eventComment = modelMapper.map(requestDto, EventComment.class);
        eventComment.setEvent(event);
        eventComment.setUser(modelMapper.map(user, User.class));
        Set<User> mentionedUsers = mentionedUsers(requestDto.getText());
        setParentComment(eventId, eventComment, requestDto);
        eventComment.setMentionedUsers(mentionedUsers);
        eventComment.setStatus(CommentStatus.ORIGINAL);
        eventComment = eventCommentRepo.save(eventComment);
        sendNotifications(eventComment, event, eventComment.getUser());
        return modelMapper.map(eventComment, EventCommentResponseDto.class);
    }

    private void sendNotifications(EventComment comment, Event event, User commentAuthor) {
        EventCommentMessageInfoDto message = getMessageDto(comment, event, commentAuthor);
        sendEmailNotificationToEventAuthor(message);
        comment.getMentionedUsers().forEach(
                mentionedUser -> sendEmailNotificationToMentionedUser
                        (getMessageDto(comment, event, mentionedUser))
        );
    }

    private EventCommentMessageInfoDto getMessageDto(EventComment comment, Event event, User receiver) {
        return EventCommentMessageInfoDto.builder()
                .receiverName(receiver.getName())
                .eventId(event.getId())
                .eventName(event.getTitle())
                .commentAuthorName(comment.getUser().getName())
                .commentCreatedDateTime(comment.getCreatedDate())
                .commentText(comment.getText())
                .commentId(comment.getId())
                .emailReceiver(receiver.getEmail())
                .build();
    }

    private void setParentComment(Long eventId, EventComment eventComment, EventCommentRequestDto requestDto) {
        if (requestDto.getParentCommentId() != null && requestDto.getParentCommentId() > 0) {
            Long parentCommentId = requestDto.getParentCommentId();
            EventComment parentEventComment = eventCommentRepo
                    .findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED)
                    .orElseThrow(() ->
                            new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId));

            if (!parentEventComment.getEvent().getId().equals(eventId)) {
                throw new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + parentCommentId
                        + " in event with id: " + eventId);
            }

            if (parentEventComment.getParentComment() != null) {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }

            eventComment.setParentComment(parentEventComment);
        } else if (requestDto.getParentCommentId() == null) {
            eventComment.setParentComment(null);
        }
    }

    private void sendEmailNotificationToEventAuthor(EventCommentMessageInfoDto eventCommentMessageInfoDto) {
        RequestAttributes originalRequestAttributes = RequestContextHolder.getRequestAttributes();
        emailThreadPool.submit(() -> {
            try {
                RequestContextHolder.setRequestAttributes(originalRequestAttributes);
                restClient.sendEventCommentNotification(eventCommentMessageInfoDto);
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    private void sendEmailNotificationToMentionedUser(EventCommentMessageInfoDto eventCommentMessageInfoDto) {
        RequestAttributes originalRequestAttributes = RequestContextHolder.getRequestAttributes();
        emailThreadPool.submit(() -> {
            try {
                RequestContextHolder.setRequestAttributes(originalRequestAttributes);
                restClient.sendMentionedInEventCommentNotification(eventCommentMessageInfoDto);
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    @Override
    public int countOfComments(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return eventCommentRepo.countByEvent(event);
    }

    @Override
    public PageableDto<EventCommentResponseDto> getAllEventComments(Pageable pageable, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
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

    @Override
    public EventCommentResponseDto getByEventCommentId(Long eventId, Long commentId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        EventComment eventComment = eventCommentRepo.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + commentId));
        if (!eventComment.getEvent().getId().equals(eventId)) {
            throw new BadRequestException("Comment with id " + commentId + " not found in comments of event with id " + eventId);
        }
        return modelMapper.map(eventComment, EventCommentResponseDto.class);
    }

    private Set<User> mentionedUsers(String commentText) {
        Set<User> mentionedUsers = new HashSet<>();
        if (commentText.contains("@") || commentText.contains("#")) {
            String[] textByWord = commentText.split(" ");
            Pattern p1 = Pattern.compile("@\\w+");
            Pattern p2 = Pattern.compile("#\\w+");
            List<String> usernames = Arrays.stream(textByWord)
                    .filter(word -> {
                        Matcher m1 = p1.matcher(word);
                        Matcher m2 = p2.matcher(word);
                        return m1.matches() || m2.matches();
                    })
                    .map(username -> username.substring(1))
                    .toList();
            mentionedUsers = usernames.stream().map(userRepo::findByName)
                    .filter(Optional::isPresent)
                    .map(Optional::get).collect(Collectors.toSet());
        }
        return mentionedUsers;
    }

    @Transactional
    @Override
    public String delete(Long eventCommentId, String email) {
        EventComment eventComment = eventCommentRepo.findByIdAndStatusNot(eventCommentId, CommentStatus.DELETED)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_COMMENT_NOT_FOUND_BY_ID + eventCommentId));

        UserVO currentUser = restClient.findByEmail(email);

        if (!currentUser.getId().equals(eventComment.getUser().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        eventComment.setStatus(CommentStatus.DELETED);
        if (eventComment.getComments() != null) {
            eventComment.getComments()
                    .forEach(comment -> comment.setStatus(CommentStatus.DELETED));
        }

        eventCommentRepo.save(eventComment);
        return "Comment deleted successfully";
    }
}