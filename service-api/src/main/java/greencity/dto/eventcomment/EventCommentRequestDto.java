package greencity.dto.eventcomment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventCommentRequestDto {
    @NotBlank(message = "Comment cannot be blank")
    @Size(min = 1, max = 8000)
    @Pattern(regexp = "^[\\\\w\\\\s!\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~]*$\n", message = "Comment text contains invalid characters")
    private String text;

    private Long parentCommentId;
}
