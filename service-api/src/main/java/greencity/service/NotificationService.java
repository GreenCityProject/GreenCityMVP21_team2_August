package greencity.service;


import greencity.dto.notification.NotificationCreateDto;
import greencity.dto.notification.NotificationReadDto;
import greencity.filters.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    NotificationReadDto createNotification(NotificationCreateDto notificationCreateDto);

    long countUnreadNotifications(long userId);

    List<NotificationReadDto> getFiveUnreadNotifications(long userId);

    Page<NotificationReadDto> getAllNotifications(Pageable pageable, long userId);

    void markAsViewed(long id, long userId);

    void markAsUnviewed(long id, long userId);

    Page<NotificationReadDto> getByFilter(long userId, Pageable pageable, List<SearchCriteria> criteria);

    NotificationReadDto findById(long id);
}