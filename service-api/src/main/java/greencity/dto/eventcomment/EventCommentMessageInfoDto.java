package greencity.dto.eventcomment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class EventCommentMessageInfoDto {
    String receiverName;
    Long eventId;
    String eventName;
    String commentAuthorName;
    LocalDateTime commentCreatedDateTime;
    String commentText;
    Long commentId;
    String emailReceiver;
}