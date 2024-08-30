package greencity.repository;

import greencity.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    long countByUserIdAndViewedFalse(Long userId);

    List<Notification> findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc(Long userId);

    Page<Notification> findAllByUserIdOrderByCreatedDateDesc(Pageable pageable, Long userId);

    Optional<Notification> findByIdAndUserId(long id, long userId);

}
