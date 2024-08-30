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

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private NotificationRepository notificationRepository;
    private ModelMapper modelMapper;

    @Override
    public NotificationReadDto createNotification(final NotificationCreateDto notificationCreateDto) {
        final Notification notification = modelMapper.map(notificationCreateDto, Notification.class);
        return modelMapper.map(notificationRepository.save(notification), NotificationReadDto.class);
    }

    @Override
    public long countUnreadNotifications(final long userId) {
        return notificationRepository.countByUserIdAndViewedFalse(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationReadDto> getFiveUnreadNotifications(final long userId) {
        return notificationRepository.findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc(userId).stream()
                .map(o -> modelMapper.map(o, NotificationReadDto.class))
                .toList();
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

    @Transactional(readOnly = true)
    @Override
    public Page<NotificationReadDto> getAllNotifications(final Pageable pageable, final long userId) {
        final Page<Notification> page = notificationRepository.findAllByUserIdOrderByCreatedDateDesc(pageable, userId);
        return convert(page);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<NotificationReadDto> getByFilter(final long userId, final Pageable pageable, final List<SearchCriteria> criteria) {
        final Specification<Notification> specification = getSpecification(userId, criteria);
        final Page<Notification> page = notificationRepository.findAll(specification, pageable);
        return convert(page);
    }

    @Transactional(readOnly = true)
    @Override
    public NotificationReadDto findById(final long id) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
        return modelMapper.map(notification, NotificationReadDto.class);
    }

    private Page<NotificationReadDto> convert(final Page<Notification> page) {
        return page.map(notification -> modelMapper.map(notification, NotificationReadDto.class));
    }

    private Specification<Notification> getSpecification(final long userId, final List<SearchCriteria> searchCriteria) {
        final String user_id = Notification_.USER + "_" + User_.ID;
        searchCriteria.removeIf(o -> o.getKey().equals(user_id));
        final SearchCriteria idCriterion = SearchCriteria.builder()
                .key(user_id)
                .type(CriteriaOperations.EQUAL)
                .value(userId)
                .build();
        searchCriteria.add(idCriterion);
        return new NotificationSpecification(searchCriteria);
    }
}
