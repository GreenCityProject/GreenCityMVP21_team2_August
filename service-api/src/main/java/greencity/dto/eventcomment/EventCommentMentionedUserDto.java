package greencity.dto.eventcomment;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventCommentMentionedUserDto {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;
}