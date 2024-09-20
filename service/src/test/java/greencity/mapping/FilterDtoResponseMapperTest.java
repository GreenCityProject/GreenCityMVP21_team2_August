package greencity.mapping;

import greencity.dto.user.UserFilterDtoResponse;
import greencity.entity.Filter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static greencity.ModelUtils.getFilter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class FilterDtoResponseMapperTest {

    @InjectMocks
    private FilterDtoResponseMapper mapper;

    @Test
    void convert() {
        Filter filter = getFilter();

        UserFilterDtoResponse result = mapper.convert(filter);

        assertNotNull(result);
        assertEquals(filter.getId(), result.getId());
        assertEquals(filter.getName(), result.getName());

        String[] criteria = filter.getValues().split(";");
        assertEquals(criteria[0], result.getSearchCriteria());
        assertEquals(criteria[1], result.getUserRole());
        assertEquals(criteria[2], result.getUserStatus());
    }

}