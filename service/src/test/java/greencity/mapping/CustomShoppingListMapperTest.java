package greencity.mapping;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CustomShoppingListMapperTest {

    private final CustomShoppingListMapper mapper = new CustomShoppingListMapper();

    @Test
    public void testConvert() {

        CustomShoppingListItemResponseDto dto = CustomShoppingListItemResponseDto.builder()
                .id(1L)
                .text("Buy milk")
                .status(ShoppingListItemStatus.ACTIVE)
                .build();


        CustomShoppingListItem item = mapper.convert(dto);


        assertNotNull(item, "The converted CustomShoppingListItem should not be null");
        assertEquals(1L, item.getId(), "The ID should match the ID in CustomShoppingListItemResponseDto");
        assertEquals("Buy milk", item.getText(), "The text should match the text in CustomShoppingListItemResponseDto");
        assertEquals(ShoppingListItemStatus.ACTIVE, item.getStatus(), "The status should match the status in CustomShoppingListItemResponseDto");
    }
    @Test
    public void testMapAllToList() {
        CustomShoppingListItemResponseDto dto1 = CustomShoppingListItemResponseDto.builder()
                .id(1L)
                .text("Buy milk")
                .status(ShoppingListItemStatus.ACTIVE)
                .build();

        CustomShoppingListItemResponseDto dto2 = CustomShoppingListItemResponseDto.builder()
                .id(2L)
                .text("Buy bread")
                .status(ShoppingListItemStatus.INPROGRESS)
                .build();

        List<CustomShoppingListItemResponseDto> dtoList = Arrays.asList(dto1, dto2);
        List<CustomShoppingListItem> itemList = mapper.mapAllToList(dtoList);

        assertNotNull(itemList, "The result list should not be null");
        assertEquals(2, itemList.size(), "The size of the result list should match the input list size");

        CustomShoppingListItem item1 = itemList.get(0);
        assertEquals(1L, item1.getId(), "The ID of the first item should match the ID of the first DTO");
        assertEquals("Buy milk", item1.getText(), "The text of the first item should match the text of the first DTO");
        assertEquals(ShoppingListItemStatus.ACTIVE, item1.getStatus(), "The status of the first item should match the status of the first DTO");

        CustomShoppingListItem item2 = itemList.get(1);
        assertEquals(2L, item2.getId(), "The ID of the second item should match the ID of the second DTO");
        assertEquals("Buy bread", item2.getText(), "The text of the second item should match the text of the second DTO");
        assertEquals(ShoppingListItemStatus.INPROGRESS, item2.getStatus(), "The status of the second item should match the status of the second DTO");
    }
}
