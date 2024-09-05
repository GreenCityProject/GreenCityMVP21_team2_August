package greencity.repository;

import greencity.dto.newssubscription.NewsSubscriptionDto;
import greencity.entity.NewsSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NewsSubscriptionRepository extends JpaRepository<NewsSubscription, Long> {
    @Query("select new greencity.dto.newssubscription.NewsSubscriptionDto(n.email, n.token) from NewsSubscription n")
    List<NewsSubscriptionDto> findAllDto();
    boolean existsByEmail(String email);
    Optional<NewsSubscription> findByToken(String token);
}
