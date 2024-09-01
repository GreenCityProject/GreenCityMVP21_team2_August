package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.notification.NotificationCreateDto;
import greencity.dto.notification.NotificationReadDto;
import greencity.entity.Notification;
import greencity.entity.Notification_;
import greencity.entity.User_;
import greencity.exception.exceptions.NotFoundException;
import greencity.constant.CriteriaOperations;
import greencity.filters.NotificationSpecification;
import greencity.filters.SearchCriteria;
import greencity.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private NotificationRepository notificationRepository;
    private ModelMapper modelMapper;
    private final String notificationBundle = "notifications";
    private final Locale locale = Locale.forLanguageTag("en");
    private final String titleInBundle = "_TITLE";
    private final String messageInBundle = "_MESSAGE";

    @Override
    public NotificationReadDto createNotification(final NotificationCreateDto notificationCreateDto, final String language) {
        final Notification notification = modelMapper.map(notificationCreateDto, Notification.class);
        final ResourceBundle bundle = ResourceBundle.getBundle(notificationBundle, Locale.forLanguageTag(language));
        final String[] titleParams = notificationCreateDto.getTitleParams();
        final String[] messageParams = notificationCreateDto.getMessageParams();

        final String title = bundle.getString(notification.getType().name() + titleInBundle);
        if (!Objects.isNull(titleParams) && titleParams.length != 0) {
            notification.setTitle(title.formatted((Object[]) titleParams));
        } else {
            notification.setTitle(title);
        }
        final String message = bundle.getString(notification.getType().name() + messageInBundle);
        if (!Objects.isNull(messageParams) && messageParams.length != 0) {
            notification.setMessage(message.formatted((Object[]) messageParams));
        } else {
            notification.setMessage(message);
        }

        return convertAccordingToLanguage(modelMapper.map(notificationRepository.save(notification), NotificationReadDto.class), language);
    }


    @Override
    public long countUnreadNotifications(final long userId) {
        return notificationRepository.countByUserIdAndViewedFalse(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationReadDto> getFiveUnreadNotifications(final long userId, final String language) {
        return notificationRepository.findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc(userId).stream()
                .map(o -> convertAccordingToLanguage(modelMapper.map(o, NotificationReadDto.class), language))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<NotificationReadDto> getAllNotifications(final Pageable pageable, final long userId, final String language) {
        final Page<Notification> page = notificationRepository.findAllByUserIdOrderByCreatedDateDesc(pageable, userId);
        return convertToPageDto(page, language);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<NotificationReadDto> getByFilter(final long userId, final Pageable pageable,
                                                 final List<SearchCriteria> criteria, final String language) {
        final Specification<Notification> specification = getSpecification(userId, criteria);
        final Page<Notification> page = notificationRepository.findAll(specification, pageable);
        return convertToPageDto(page, language);
    }

    @Transactional(readOnly = true)
    @Override
    public NotificationReadDto getById(final long id, final String language) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
        return convertAccordingToLanguage(modelMapper.map(notification, NotificationReadDto.class), language);
    }

    @Override
    public void markAsViewed(final long id, long userId) {
        final Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
        notification.setViewed(true);
    }

    @Override
    public void markAsUnviewed(final long id, long userId) {
        final Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
        notification.setViewed(false);
    }

    private String[] parse(final String stringWithRegExp, final String formattedString) {
        final String[] parts = stringWithRegExp.split("%s");
        final String regExp = createRegExp(parts);
        return Arrays.stream(formattedString.split(regExp))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    private static String createRegExp(final String[] parts) {
        return Arrays.stream(parts)
                .map(s -> "(" + s + ")")
                .collect(Collectors.joining("|"));
    }

    private NotificationReadDto convertAccordingToLanguage(final NotificationReadDto notificationReadDto, final String language) {
        final ResourceBundle resourceBundleForSpecificLanguage = ResourceBundle.getBundle(notificationBundle, Locale.forLanguageTag(language),
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
        final ResourceBundle resourceBundle = ResourceBundle.getBundle(notificationBundle, locale);

        if (locale.getLanguage().equals(language)) {
            final String title = resourceBundle.getString(notificationReadDto.getType().name() + titleInBundle);
            final String message = resourceBundle.getString(notificationReadDto.getType().name() + messageInBundle);
            final String[] titleParams = parse(title, notificationReadDto.getTitle());
            final String[] messageParams = parse(message, notificationReadDto.getMessage());
            final String titleInLanguage = resourceBundleForSpecificLanguage.getString(notificationReadDto.getType().name() + titleInBundle)
                    .formatted((Object[]) titleParams);
            final String messageInLanguage = resourceBundleForSpecificLanguage.getString(notificationReadDto.getType().name() + messageInBundle)
                    .formatted((Object[]) messageParams);
            notificationReadDto.setTitle(titleInLanguage);
            notificationReadDto.setMessage(messageInLanguage);
        }
        return notificationReadDto;
    }

    private Page<NotificationReadDto> convertToPageDto(final Page<Notification> page, final String language) {
        return page.map(notification -> convertAccordingToLanguage(modelMapper.map(notification, NotificationReadDto.class), language));
    }

    private Specification<Notification> getSpecification(final long userId, final List<SearchCriteria> searchCriteria) {
        final String userIdColumn = Notification_.USER + "_" + User_.ID;
        searchCriteria.removeIf(o -> o.getKey().equals(userIdColumn));
        final SearchCriteria idCriterion = SearchCriteria.builder()
                .key(userIdColumn)
                .type(CriteriaOperations.EQUAL)
                .value(userId)
                .build();
        searchCriteria.add(idCriterion);
        return new NotificationSpecification(searchCriteria);
    }
}