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

/**
 * Service implementation for managing notifications.
 * This service provides methods for creating, retrieving, and updating notifications.
 */
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

    /**
     * Creates a new notification and saves it to the database.
     * <p>
     * The method involves mapping the provided {@link NotificationCreateDto} to a {@link Notification} entity,
     * fetching the appropriate localized messages from a resource bundle, formatting the title and message
     * with any provided parameters, and saving the notification to the database.
     *
     * @param notificationCreateDto the DTO containing notification details to be created.
     * @param language              the language code for localizing the notification.
     * @return a DTO representing the created notification.
     */
    @Override
    public NotificationReadDto createNotification(final NotificationCreateDto notificationCreateDto, final String language) {
        final Notification notification = modelMapper.map(notificationCreateDto, Notification.class);
        final ResourceBundle bundle = ResourceBundle.getBundle(notificationBundle, locale);
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

    /**
     * Counts the number of unread notifications for a specific user.
     *
     * @param userId the ID of the user whose unread notifications are to be counted.
     * @return the count of unread notifications for the specified user.
     */
    @Override
    public long countUnreadNotifications(final long userId) {
        return notificationRepository.countByUserIdAndViewedFalse(userId);
    }

    /**
     * Retrieves the five most recent unread notifications for a specific user.
     * <p>
     * This method fetches the top five unread notifications, orders them by creation date in descending order,
     * converts them to DTOs, and localizes them according to the provided language.
     *
     * @param userId   the ID of the user whose unread notifications are to be retrieved.
     * @param language the language code for localizing the notifications.
     * @return a list of the top five unread notifications, localized and ready for display.
     */
    @Transactional(readOnly = true)
    @Override
    public List<NotificationReadDto> getFiveUnreadNotifications(final long userId, final String language) {
        return notificationRepository.findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc(userId).stream()
                .map(o -> convertAccordingToLanguage(modelMapper.map(o, NotificationReadDto.class), language))
                .toList();
    }

    /**
     * Retrieves all notifications for a specific user, with pagination.
     * <p>
     * This method fetches all notifications for the given user, orders them by creation date in descending order,
     * converts them to DTOs, and localizes them according to the provided language.
     *
     * @param pageable pagination information.
     * @param userId   the ID of the user whose notifications are to be retrieved.
     * @param language the language code for localizing the notifications.
     * @return a paginated list of all notifications for the user, localized and ready for display.
     */
    @Transactional(readOnly = true)
    @Override
    public Page<NotificationReadDto> getAllNotifications(final Pageable pageable, final long userId, final String language) {
        final Page<Notification> page = notificationRepository.findAllByUserIdOrderByCreatedDateDesc(pageable, userId);
        return convertToPageDto(page, language);
    }

    /**
     * Retrieves filtered notifications for a specific user, with pagination.
     * <p>
     * This method applies filters to the notifications of the given user based on specified criteria,
     * converts the filtered results to DTOs, and localizes them according to the provided language.
     *
     * @param userId   the ID of the user whose notifications are to be filtered and retrieved.
     * @param pageable pagination information.
     * @param criteria the list of search criteria to filter the notifications.
     * @param language the language code for localizing the notifications.
     * @return a paginated list of filtered notifications, localized and ready for display.
     */
    @Transactional(readOnly = true)
    @Override
    public Page<NotificationReadDto> getByFilter(final long userId, final Pageable pageable,
                                                 final List<SearchCriteria> criteria, final String language) {
        final Specification<Notification> specification = getSpecification(userId, criteria);
        final Page<Notification> page = notificationRepository.findAll(specification, pageable);
        return convertToPageDto(page, language);
    }

    /**
     * Retrieves a notification by its ID.
     * <p>
     * This method fetches a notification by its ID, ensuring it exists, converts it to a DTO,
     * and localizes it according to the provided language.
     *
     * @param id       the ID of the notification to be retrieved.
     * @param language the language code for localizing the notification.
     * @return a DTO representing the retrieved notification, localized and ready for display.
     * @throws NotFoundException if the notification with the specified ID is not found.
     */
    @Transactional(readOnly = true)
    @Override
    public NotificationReadDto getById(final long id, final String language) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
        return convertAccordingToLanguage(modelMapper.map(notification, NotificationReadDto.class), language);
    }

    /**
     * Marks a notification as viewed.
     * <p>
     * This method fetches a notification by its ID and user ID, ensures it exists, and marks it as viewed.
     *
     * @param id     the ID of the notification to be marked as viewed.
     * @param userId the ID of the user who owns the notification.
     * @throws NotFoundException if the notification with the specified ID and user ID is not found.
     */
    @Override
    public void markAsViewed(final long id, long userId) {
        final Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
        notification.setViewed(true);
    }

    /**
     * Marks a notification as unviewed.
     * <p>
     * This method fetches a notification by its ID and user ID, ensures it exists, and marks it as unviewed.
     *
     * @param id     the ID of the notification to be marked as unviewed.
     * @param userId the ID of the user who owns the notification.
     * @throws NotFoundException if the notification with the specified ID and user ID is not found.
     */
    @Override
    public void markAsUnviewed(final long id, long userId) {
        final Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
        notification.setViewed(false);
    }

    /**
     * Parses a formatted string into its component parts based on a regular expression.
     * <p>
     * This method splits the input string by a regular expression derived from a template string,
     * returning the parts that match the placeholders in the template.
     *
     * @param stringWithRegExp the template string containing placeholders (e.g., "%s").
     * @param formattedString  the string to be parsed.
     * @return an array of strings representing the parts of the formatted string that match the placeholders.
     */
    private String[] parse(final String stringWithRegExp, final String formattedString) {
        final String[] parts = stringWithRegExp.split("%s");
        final String regExp = createRegExp(parts);
        return Arrays.stream(formattedString.split(regExp))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    /**
     * Creates a regular expression pattern based on the provided array of string parts.
     * Each part is wrapped in parentheses to form a capturing group, and the parts are
     * joined together with a "|" character to form an "or" pattern in the regular expression.
     *
     * @param parts an array of strings, each representing a segment of the pattern.
     * @return a regular expression string that matches any of the provided parts.
     */
    private static String createRegExp(final String[] parts) {
        return Arrays.stream(parts)
                .map(s -> "(" + s + ")")
                .collect(Collectors.joining("|"));
    }

    /**
     * Converts the given {@link NotificationReadDto} into the specified language.
     * If the requested language matches the default language (set by {@link #locale}),
     * the method retrieves the title and message from the resource bundle, extracts the parameters
     * used in these strings, and formats the localized title and message accordingly.
     *
     * @param notificationReadDto the notification data transfer object to be localized.
     * @param language the language code used to localize the notification's title and message.
     * @return a {@link NotificationReadDto} with the title and message localized to the specified language.
     */
    private NotificationReadDto convertAccordingToLanguage(final NotificationReadDto notificationReadDto, final String language) {
        final ResourceBundle resourceBundleForSpecificLanguage = ResourceBundle.getBundle(notificationBundle, Locale.forLanguageTag(language),
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
        final ResourceBundle resourceBundle = ResourceBundle.getBundle(notificationBundle, locale);

        if (!locale.getLanguage().equals(language)) {
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

    /**
     * Converts a {@link Page} of {@link Notification} entities into a {@link Page} of {@link NotificationReadDto} objects,
     * with each DTO localized to the specified language.
     *
     * @param page the {@link Page} of {@link Notification} entities to be converted.
     * @param language the language code used to localize the notifications.
     * @return a {@link Page} of {@link NotificationReadDto} objects with localized titles and messages.
     */
    private Page<NotificationReadDto> convertToPageDto(final Page<Notification> page, final String language) {
        return page.map(notification -> convertAccordingToLanguage(modelMapper.map(notification, NotificationReadDto.class), language));
    }

    /**
     * Constructs a {@link Specification} object based on the provided user ID and a list of search criteria.
     * This method ensures that the search criteria include a criterion for matching the user ID,
     * which is added if not already present.
     *
     * @param userId the ID of the user whose notifications are to be filtered.
     * @param searchCriteria the list of criteria to apply for filtering notifications.
     * @return a {@link Specification} object representing the combined filter criteria.
     */
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