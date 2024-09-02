package greencity.dto.user;

import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventAuthorDto implements Serializable {
    private Long id;
    private String email;
    private String name;
}
