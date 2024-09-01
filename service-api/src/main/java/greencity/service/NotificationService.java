package greencity.service;


import greencity.dto.notification.NotificationCreateDto;
import greencity.dto.notification.NotificationReadDto;
import greencity.filters.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    NotificationReadDto createNotification(NotificationCreateDto notificationCreateDto, String language);

    long countUnreadNotifications(long userId);

    List<NotificationReadDto> getFiveUnreadNotifications(long userId, String language);

    Page<NotificationReadDto> getAllNotifications(Pageable pageable, long userId, String language);

    Page<NotificationReadDto> getByFilter(long userId, Pageable pageable, List<SearchCriteria> criteria, String language);

    NotificationReadDto getById(long id, String language);

    void markAsViewed(long id, long userId);

    void markAsUnviewed(long id, long userId);
}