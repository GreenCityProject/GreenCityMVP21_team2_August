package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CustomShoppingListItemService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static greencity.enums.ShoppingListItemStatus.ACTIVE;
import static greencity.enums.ShoppingListItemStatus.INPROGRESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class CustomShoppingListItemControllerTest {

    private static final String Custom_SHOPPING_LIST_ITEMS_URL = "/custom/shopping-list-items";

    private MockMvc mockMvc;

    @Mock
    private CustomShoppingListItemService customShoppingListItemService;

    @InjectMocks
    private CustomShoppingListItemController customShoppingListItemController;

    @Mock
    private Validator mockValidator;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    private ObjectMapper objectMapper;

    private final Long userId = 1L;

    private ShoppingListItemStatus[] statuses = ShoppingListItemStatus.values();

    private Long itemId = 2L;

    private Long habitAssignId = 2L;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController).setValidator(mockValidator).setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void bulkDeleteCustomShoppingListItemsTest() throws Exception {
        String ids = "1,2,3";
        List<Long> deletedIds = Arrays.asList(1L, 2L, 3L);

        when(customShoppingListItemService.bulkDelete(ids)).thenReturn(deletedIds);

        mockMvc.perform(delete(Custom_SHOPPING_LIST_ITEMS_URL + "/{userId}/custom-shopping-list-items", userId)
                        .param("ids", ids))
                .andExpect(status().isOk());

        verify(customShoppingListItemService).bulkDelete(ids);
    }

    @Test
    public void saveUserCustomShoppingListItemsTest() throws Exception {
        BulkSaveCustomShoppingListItemDto dto = new BulkSaveCustomShoppingListItemDto();
        List<CustomShoppingListItemResponseDto> responseDtoList = List.of(new CustomShoppingListItemResponseDto(1L, "Apples", ACTIVE));
        var json = objectMapper.writeValueAsString(dto);

        when(customShoppingListItemService.save(any(BulkSaveCustomShoppingListItemDto.class), any(Long.class), any(Long.class))).thenReturn(responseDtoList);


        mockMvc.perform(post(Custom_SHOPPING_LIST_ITEMS_URL + "/{userId}/{habitAssignId}/custom-shopping-list-items", userId, habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isString());

        verify(customShoppingListItemService).save(dto, userId, habitAssignId);
    }

    @Test
    public void getAllAvailableCustomShoppingListItemsTest() throws Exception {
        CustomShoppingListItemResponseDto item1 = new CustomShoppingListItemResponseDto(1L, "Apples", ACTIVE);
        CustomShoppingListItemResponseDto item2 = new CustomShoppingListItemResponseDto(2L, "Oranges", INPROGRESS);
        List<CustomShoppingListItemResponseDto> items = Arrays.asList(item1, item2);

        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(userId, habitAssignId)).thenReturn(items);
        mockMvc.perform(get(Custom_SHOPPING_LIST_ITEMS_URL + "/{userId}/{habitAssignId}", userId, habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(customShoppingListItemService, times(1))
                .findAllAvailableCustomShoppingListItems(userId, habitAssignId);

    }


    @Test
    public void updateItemStatusTest() throws Exception {
        var itemStatus = statuses[1].name();
        CustomShoppingListItemResponseDto updatedItem = new CustomShoppingListItemResponseDto(1L, "Apples", ACTIVE);

        when(customShoppingListItemService.updateItemStatus(userId, itemId, itemStatus)).thenReturn(updatedItem);

        mockMvc.perform(patch(Custom_SHOPPING_LIST_ITEMS_URL + "/{userId}/custom-shopping-list-items", userId)
                        .param("itemId", itemId.toString())
                        .param("status", itemStatus)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(customShoppingListItemService).updateItemStatus(userId, itemId, itemStatus);
    }

    @Test
    public void updateItemStatusToDoneTest() throws Exception {
        mockMvc.perform(patch(Custom_SHOPPING_LIST_ITEMS_URL + "/{userId}/done", userId)
                        .param("itemId", itemId.toString()))
                .andExpect(status().isOk());

        verify(customShoppingListItemService, times(1))
                .updateItemStatusToDone(userId, itemId);
    }

    @Test
    public void updateItemStatusToDoneIncorectItemIdTest() throws Exception {
        String idItem = "a";

        mockMvc.perform(patch(Custom_SHOPPING_LIST_ITEMS_URL + "/{userId}/done", userId)
                        .param("itemId", idItem))
                .andExpect(status().isBadRequest());


    }

    @Test
    public void getAllCustomShoppingItemsByStatusWithStatusTest() throws Exception {
        ShoppingListItemStatus[] statuses = ShoppingListItemStatus.values();
        var status = statuses[0].name();
        List<CustomShoppingListItemResponseDto> listItemResponseDtos = Arrays.asList(new CustomShoppingListItemResponseDto());

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, status)).thenReturn(listItemResponseDtos);

        mockMvc.perform(get(Custom_SHOPPING_LIST_ITEMS_URL + "/{userId}/custom-shopping-list-items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", status))
                .andExpect(status().isOk());

        verify(customShoppingListItemService).findAllUsersCustomShoppingListItemsByStatus(userId, status);
    }

    @Test
    public void getAllCustomShoppingItemsByStatusWithoutStatusTest() throws Exception {
        String status = null;

        List<CustomShoppingListItemResponseDto> listItemResponseDtos = Arrays.asList(new CustomShoppingListItemResponseDto());

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, status)).thenReturn(listItemResponseDtos);
        mockMvc.perform(get(Custom_SHOPPING_LIST_ITEMS_URL + "/{userId}/custom-shopping-list-items", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(customShoppingListItemService).findAllUsersCustomShoppingListItemsByStatus(userId, status);

    }
}