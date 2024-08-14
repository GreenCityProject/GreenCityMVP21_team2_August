package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import org.junit.jupiter.api.Test;
import greencity.entity.Category;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryDtoResponseMapperTest {
    private final CategoryDtoResponseMapper mapper = new CategoryDtoResponseMapper();
    @Test
    public void testConvert(){
        Category category = Category.builder()
                .id(1L)
                .name("Fruits")
                .build();
        CategoryDtoResponse dto = mapper.convert(category);


        assertNotNull(dto, "The converted CategoryDtoResponse should not be null");
        assertEquals(1L, dto.getId(), "The ID should match the ID of the Category");
        assertEquals("Fruits", dto.getName(), "The name should match the name of the Category");
    }
}
