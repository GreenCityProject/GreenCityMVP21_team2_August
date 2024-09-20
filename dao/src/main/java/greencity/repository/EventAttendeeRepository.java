package greencity.repository;

import greencity.entity.EventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {
    void deleteAllByEvent_Id(long id);
    List<EventAttendee> findAllByEvent_Id(long id);
    List<EventAttendee> findAllByUser_Id(long id);
    boolean existsByEventIdAndUserId(long eventId, long userId);
}
