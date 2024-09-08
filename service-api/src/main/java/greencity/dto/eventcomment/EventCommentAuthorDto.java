package greencity.dto.eventcomment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventCommentAuthorDto {
    private Long id;
    private String name;
    private String userProfilePicturePath;
}