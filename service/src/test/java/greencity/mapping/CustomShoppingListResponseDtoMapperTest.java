package greencity.mapping;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class CustomShoppingListResponseDtoMapperTest {
    private final CustomShoppingListResponseDtoMapper mapper = new CustomShoppingListResponseDtoMapper();

    CustomShoppingListItem item = CustomShoppingListItem.builder()
            .id(1L)
            .text("Buy apples")
            .status(ShoppingListItemStatus.ACTIVE)
            .build();

    @Test
    public void testConvert() {
        CustomShoppingListItemResponseDto dto = mapper.convert(item);

        assertNotNull(dto, "The converted CustomShoppingListItemResponseDto should not be null");
        assertEquals(1L, dto.getId(), "The ID should match the ID of the CustomShoppingListItem");
        assertEquals("Buy apples", dto.getText(), "The text should match the text of the CustomShoppingListItem");
        assertEquals(ShoppingListItemStatus.ACTIVE, dto.getStatus(), "The status should match the status of the CustomShoppingListItem");
    }

    @Test
    public void testMapAllToList() {
        CustomShoppingListItem item2 = CustomShoppingListItem.builder()
                .id(2L)
                .text("Buy oranges")
                .status(ShoppingListItemStatus.INPROGRESS)
                .build();


        List<CustomShoppingListItem> itemList = Arrays.asList(item, item2);
        List<CustomShoppingListItemResponseDto> dtoList = mapper.mapAllToList(itemList);


        assertNotNull(dtoList, "The result list should not be null");
        assertEquals(2, dtoList.size(), "The size of the result list should match the input list size");

        CustomShoppingListItemResponseDto dto1 = dtoList.get(0);
        assertEquals(1L, dto1.getId(), "The ID of the first DTO should match the ID of the first item");
        assertEquals("Buy apples", dto1.getText(), "The text of the first DTO should match the text of the first item");
        assertEquals(ShoppingListItemStatus.ACTIVE, dto1.getStatus(), "The status of the first DTO should match the status of the first item");

        CustomShoppingListItemResponseDto dto2 = dtoList.get(1);
        assertEquals(2L, dto2.getId(), "The ID of the second DTO should match the ID of the second item");
        assertEquals("Buy oranges", dto2.getText(), "The text of the second DTO should match the text of the second item");
        assertEquals(ShoppingListItemStatus.INPROGRESS, dto2.getStatus(), "The status of the second DTO should match the status of the second item");
    }


}
