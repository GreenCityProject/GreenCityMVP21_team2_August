package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.ShoppingListItemService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ShoppingListItemControllerTest {
    private static final String USER_SHOPPING_LIST_ITEMS_URL = "/user/shopping-list-items";

    private MockMvc mockMvc;

    @Mock
    private ShoppingListItemService shoppingListItemService;

    @InjectMocks
    private ShoppingListItemController shoppingListItemController;

    @Mock
    private Validator mockValidator;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    private ObjectMapper objectMapper;

    private final Principal principal = getPrincipal();

    private final UserVO userVO = getUserVO();

    private final Locale locale = Locale.of("en");


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(shoppingListItemController)
                .setValidator(mockValidator)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void bulkDeleteUserShoppingListItemTest() throws Exception {
        String ids = "1,2";
        mockMvc.perform(delete(STR."\{USER_SHOPPING_LIST_ITEMS_URL}/user-shopping-list-items")
                        .param("ids", ids)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteTest() throws Exception {
        long habitId = 1L;
        long shoppingListItemId = 1L;

        when(userService.findByEmail(anyString())).thenReturn(userVO);


        mockMvc.perform(delete(USER_SHOPPING_LIST_ITEMS_URL)
                        .param("habitId", Long.toString(habitId))
                        .param("shoppingListItemId", Long.toString(shoppingListItemId))
                        .principal(principal))

                .andExpect(status().isOk());

    }

    @Test
    public void findAllByUserTest() throws Exception {
        Long userId = 1L;

        ShoppingListItemDto dummyItem = new ShoppingListItemDto();
        dummyItem.setId(1L);
        dummyItem.setStatus("PROGRESS");

        mockMvc.perform(get(STR."\{USER_SHOPPING_LIST_ITEMS_URL}/{userId}/get-all-inprogress", userId)
                        .param("lang", String.valueOf(locale))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserShoppingListItemWithoutLanguageParamTest() throws Exception {
        Long habitId = 1L;
        when(userService.findByEmail(anyString())).thenReturn(getUserVO());
        mockMvc.perform(get(STR."\{USER_SHOPPING_LIST_ITEMS_URL}/habits/{habitId}/shopping-list", habitId)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "USER")
    public void saveUserShoppingListItemsTest() throws Exception {
        String languageCode = String.valueOf(locale);

        ShoppingListItemRequestDto requestDto = ShoppingListItemRequestDto.builder()
                .id(1L)
                .build();

        List<ShoppingListItemRequestDto> requestDtoList = List.of(requestDto);

        UserShoppingListItemResponseDto responseDto = new UserShoppingListItemResponseDto();
        responseDto.setId(1L);

        List<UserShoppingListItemResponseDto> responseDtoList = List.of(responseDto);


        when(userService.findByEmail(anyString())).thenReturn(getUserVO());
        when(shoppingListItemService.saveUserShoppingListItems(anyLong(), anyLong(), anyList(), anyString()))
                .thenReturn(responseDtoList);

        mockMvc.perform(post(USER_SHOPPING_LIST_ITEMS_URL)
                        .content(objectMapper.writeValueAsString(requestDtoList))
                        .param("habitId", "1")
                        .param("lang", languageCode)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void getUserShoppingListItemsWithLanguageParamTest() throws Exception {
        Long habitId = 1L;
        UserVO user = new UserVO();
        user.setId(1L);


        UserShoppingListItemResponseDto item1 = new UserShoppingListItemResponseDto();
        item1.setId(1L);

        UserShoppingListItemResponseDto item2 = new UserShoppingListItemResponseDto();
        item2.setId(2L);


        List<UserShoppingListItemResponseDto> mockedList = Arrays.asList(item1, item2);

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(shoppingListItemService.getUserShoppingList(eq(user.getId()), eq(habitId), eq(locale.getLanguage())))
                .thenReturn(mockedList);


        mockMvc.perform(get(STR."\{USER_SHOPPING_LIST_ITEMS_URL}/habits/{habitId}/shopping-list", habitId)
                        .locale(locale)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void updateUserShoppingListItemStatusWithLanguageParamTest() throws Exception {
        Long userShoppingListItemId = 1L;
        String status = "COMPLETED";


        when(userService.findByEmail(anyString())).thenReturn(userVO);
        mockMvc.perform(patch(STR."\{USER_SHOPPING_LIST_ITEMS_URL}/{userShoppingListItemId}/status/{status}", userShoppingListItemId, status)
                        .param("lang", String.valueOf(locale))
                        .principal(principal))
                .andExpect(status().isOk());

    }

    @Test
    public void updateUserShoppingListItemStatus() throws Exception {
        Long userShoppingListItemId = 1L;
        String status = "COMPLETED";

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(STR."\{USER_SHOPPING_LIST_ITEMS_URL}/{userShoppingListItemId}/status/{status}", userShoppingListItemId, status)
                        .param("lang", String.valueOf(locale))
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserShoppingListItemStatusWithoutLanguageParamTest() throws Exception {
        Long userShoppingListItemId = 1L;
        String status = "COMPLETED";

        when(userService.findByEmail(anyString())).thenReturn(userVO);


        mockMvc.perform(patch(STR."\{USER_SHOPPING_LIST_ITEMS_URL}/{userShoppingListItemId}/status/{status}", userShoppingListItemId, status)
                        .principal(principal))
                .andExpect(status().isOk());

    }


}
