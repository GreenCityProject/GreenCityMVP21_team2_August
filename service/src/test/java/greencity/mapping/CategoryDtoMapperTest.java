package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryDtoMapperTest {
    private final CategoryDtoMapper mapper = new CategoryDtoMapper();

    @Test
    public void testConvert() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("Vegetables")
                .build();

        Category category = mapper.convert(categoryDto);

        assertNotNull(category, "The converted Category should not be null");
        assertEquals("Vegetables", category.getName(), "The name should match the name in CategoryDto");
    }
}
