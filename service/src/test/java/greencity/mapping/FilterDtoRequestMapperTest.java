package greencity.mapping;

import greencity.dto.user.UserFilterDtoRequest;
import greencity.entity.Filter;
import greencity.enums.FilterType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static greencity.ModelUtils.getUserFilterDtoRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class FilterDtoRequestMapperTest {

    @InjectMocks
    private FilterDtoRequestMapper mapper;

    @Test
    void convert() {
        UserFilterDtoRequest filterUserDto = getUserFilterDtoRequest();

        Filter result = mapper.convert(filterUserDto);

        assertNotNull(result);
        assertEquals(filterUserDto.getName(), result.getName());
        assertEquals(FilterType.USERS.toString(), result.getType());

        String expectedValues = filterUserDto.getSearchCriteria() + ";" +
                filterUserDto.getUserRole() + ";" +
                filterUserDto.getUserStatus();
        assertEquals(expectedValues, result.getValues());
    }

}