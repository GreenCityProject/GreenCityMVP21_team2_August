package greencity.dto.eventcomment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventCommentResponseDto {
    private Long id;

    private String text;

    private LocalDateTime createdDate;

    private EventCommentAuthorDto author;

    private Long eventId;
}