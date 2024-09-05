package greencity.repository;


import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;
import greencity.entity.NewsSubscription;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static greencity.ModelUtils.getNewsSubscription;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GreenCityApplication.class)
class NewsSubscriptionRepositoryTest extends IntegrationTestBase {
    @Autowired
    private NewsSubscriptionRepository notificationRepository;
    private final NewsSubscription newsSubscription = getNewsSubscription();

    @BeforeEach
    void setUp() {
        notificationRepository.save(newsSubscription);
    }

    @Test
    void existsByEmail_NotExistsInDatabase_ReturnsTrue() {
        final String email = newsSubscription.getEmail();
        assertTrue(notificationRepository.existsByEmail(email));
    }

    @Test
    void existsByEmail_ExistsInDatabase_ReturnsFalse() {
        final String nonExistingEmail = "non_existing_email@gmail.com";
        assertFalse(notificationRepository.existsByEmail(nonExistingEmail));
    }

    @Test
    void findByToken_NonExistentToken_ReturnsEmpty() {
        final String token = "non_existing_token";

        Optional<NewsSubscription> result = notificationRepository.findByToken(token);
        assertFalse(result.isPresent());
    }

    @Test
    void findByToken_ExistentToken_ReturnsTrue() {
        final String token = newsSubscription.getToken();

        final Optional<NewsSubscription> result = notificationRepository.findByToken(token);
        assertTrue(result.isPresent());
    }

    @Test
    void save_SavingEntityWithEmptyEmailAndToken_ThrowsConstraintViolationException() {
        final NewsSubscription emptyNewsSubscription = new NewsSubscription();
        assertThrows(ConstraintViolationException.class, () -> notificationRepository.save(emptyNewsSubscription));
    }
}