package greencity.dto.search;

import greencity.dto.user.EcoNewsAuthorDto;
import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SearchNewsDto {
    @Min(0)
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private EcoNewsAuthorDto author;
    @NotEmpty
    private ZonedDateTime creationDate;
    @NotEmpty
    private List<String> tags;
}
