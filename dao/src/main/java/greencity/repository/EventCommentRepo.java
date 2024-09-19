package greencity.repository;

import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {

    int countByEvent(Event event);

    Page<EventComment> findAllByEventOrderByCreatedDateDesc(Event event, Pageable pageable);

    Optional<EventComment> findByIdAndStatusNot(Long id, CommentStatus status);

    @Query("SELECT COUNT(ec) FROM EventComment ec WHERE ec.parentComment.id = :parentCommentId")
    int countReplies(@Param("parentCommentId")Long parentCommentId);

    Optional<EventComment> findByIdAndEventId(Long commentId, Long eventId);

    Page<EventComment> findAllByParentComment(EventComment parentComment, Pageable pageable);


}