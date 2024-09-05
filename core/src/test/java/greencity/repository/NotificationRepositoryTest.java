package greencity.repository;

import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;
import greencity.ModelUtils;
import greencity.entity.Notification;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = GreenCityApplication.class)
public class NotificationRepositoryTest extends IntegrationTestBase {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private NotificationRepository notificationRepository;
    private User user;

    @BeforeEach
    void setUp() {
        user = ModelUtils.getUser();
        user.setRefreshTokenKey("refresh-token");
        userRepo.save(user);
    }

    @Test
    void countByUserIdAndViewedFalse_AddedTwoDifferentNotifications_ReturnOne() {
        final Notification notificationWithViewedTrue = ModelUtils.getNotification();
        notificationWithViewedTrue.setViewed(true);
        notificationWithViewedTrue.setUser(user);
        final Notification notificationWithViewedFalse = ModelUtils.getNotification();
        notificationWithViewedFalse.setUser(user);
        notificationRepository.save(notificationWithViewedTrue);
        notificationRepository.save(notificationWithViewedFalse);

        final long actual = notificationRepository.countByUserIdAndViewedFalse(user.getId());

        assertEquals(1, actual);
    }

    @Test
    void countByUserIdAndViewedFalse_AddedNothing_ReturnZero() {
        final long actual = notificationRepository.countByUserIdAndViewedFalse(user.getId());

        assertEquals(0, actual);
    }

    @Test
    void findByIdAndUserId_FoundNotification_ReturnNotification() {
        final Notification notification = ModelUtils.getNotification();
        notification.setUser(user);
        notificationRepository.save(notification);

        final Optional<Notification> actual = notificationRepository.findByIdAndUserId(notification.getId(), user.getId());

        assertThat(actual).isPresent();
    }

    @Test
    void findByIdAndUserId_NotFoundNotification_ReturnsEmptyOptional() {
        final Optional<Notification> actual = notificationRepository.findByIdAndUserId(1, user.getId());

        assertThat(actual).isEmpty();
    }

    @Test
    void findAllByUserIdOrderByCreatedDateDesc_AddedTwoNotifications_ReturnListOrderedByCreationDate() {
        final Notification notification1 = ModelUtils.getNotification();
        notification1.setUser(user);
        notificationRepository.save(notification1);

        final Notification notification2 = ModelUtils.getNotification();
        notification2.setUser(user);
        notificationRepository.save(notification2);

        final Pageable pageable = PageRequest.of(0, 20);

        final Page<Notification> actual = notificationRepository.findAllByUserIdOrderByCreatedDateDesc(pageable, user.getId());
        final Notification firstElement = actual.get().findFirst()
                .orElse(null);

        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());
        assertEquals(notification2, firstElement);
    }

    @Test
    void findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc_AddedTwoNotifications_ReturnListOrderedByCreationDate() {
        final Notification notification1 = ModelUtils.getNotification();
        notification1.setUser(user);
        notificationRepository.save(notification1);

        final Notification notification2 = ModelUtils.getNotification();
        notification2.setUser(user);
        notificationRepository.save(notification2);

        final List<Notification> actual = notificationRepository.findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc(user.getId());

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(notification2, actual.getFirst());
    }
}