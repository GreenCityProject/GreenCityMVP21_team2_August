package greencity.repository;

import greencity.entity.EcoNewsComment;
import greencity.entity.EventComment;
import greencity.entity.Event;
import greencity.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {

    int countByEvent(Event event);

    Page<EventComment> findAllByEventOrderByCreatedDateDesc(Event event, Pageable pageable);

    Optional<EventComment> findByIdAndStatusNot(Long id, CommentStatus status);

    Optional<EventComment> findByIdAndCommentStatus(Long id, CommentStatus commentStatus);

}