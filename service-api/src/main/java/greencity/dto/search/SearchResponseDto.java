package greencity.dto.search;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
public class SearchResponseDto {
    List<SearchNewsDto> ecoNews;

    Long countOfResults;

    public SearchResponseDto() {
    }

    public SearchResponseDto(List<SearchNewsDto> mockEcoNews, long l) {
    }
}


