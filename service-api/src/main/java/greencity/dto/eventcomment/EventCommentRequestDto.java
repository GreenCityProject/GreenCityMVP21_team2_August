package greencity.dto.eventcomment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventCommentRequestDto {
    @NotBlank
    @Size(min = 1, max = 8000)
    String text;
}