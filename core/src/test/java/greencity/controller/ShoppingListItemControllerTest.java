package greencity.controller;

import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.LanguageService;
import greencity.service.ShoppingListItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ShoppingListItemControllerTest {
    private static final String USER_SHOPPING_LIST_ITEMS_URL = "/user/shopping-list-items";


    private MockMvc mockMvc;

    @Mock
    private ShoppingListItemService shoppingListItemService;

    @InjectMocks
    private ShoppingListItemController shoppingListItemController;

    @MockBean
    private LanguageService languageService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(shoppingListItemController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(new MethodValidationPostProcessor())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "USER")
    public void saveUserShoppingListItemsTest() throws Exception {
        String languageCode = "en";

        ShoppingListItemRequestDto requestDto = ShoppingListItemRequestDto.builder()
                .id(1L)
                .build();

        List<ShoppingListItemRequestDto> requestDtoList = List.of(requestDto);

        UserShoppingListItemResponseDto responseDto = new UserShoppingListItemResponseDto();
        responseDto.setId(1L);

        List<UserShoppingListItemResponseDto> responseDtoList = List.of(responseDto);

        when(shoppingListItemService.saveUserShoppingListItems(anyLong(), anyLong(), anyList(), anyString()))
                .thenReturn(responseDtoList);

        mockMvc.perform(post(USER_SHOPPING_LIST_ITEMS_URL)
                        .content(objectMapper.writeValueAsString(requestDtoList))
                        .param("habitId", "1")
                        .param("lang", languageCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void bulkDeleteUserShoppingListItemTest() throws Exception {
        String ids = "1,2";
        mockMvc.perform(delete(USER_SHOPPING_LIST_ITEMS_URL + "/user-shopping-list-items")
                        .param("ids", ids)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void deleteTest() throws Exception {
        long habitId = 1L;
        long shoppingListItemId = 2L;
        mockMvc.perform(delete(USER_SHOPPING_LIST_ITEMS_URL)
                        .param("habitId", Long.toString(habitId))
                        .param("shoppingListItemId", Long.toString(shoppingListItemId)))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    public void findAllByUserTest() throws Exception {
        Long userId = 1L;
        String languageCode = "en";

        ShoppingListItemDto dummyItem = new ShoppingListItemDto();
        dummyItem.setId(1L);
        dummyItem.setStatus("INPROGRESS");

        mockMvc.perform(get(USER_SHOPPING_LIST_ITEMS_URL + "/{userId}/get-all-inprogress", userId)
                        .param("lang", languageCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }

    @Test
    public void getUserShoppingListItemsWithLanguageParamTest() throws Exception {
        // Arrange
        Long habitId = 1L;
        Locale locale = Locale.of("en");
        UserVO user = new UserVO();
        user.setId(1L);

        // Create mock data
        UserShoppingListItemResponseDto item1 = new UserShoppingListItemResponseDto();
        item1.setId(1L);

        UserShoppingListItemResponseDto item2 = new UserShoppingListItemResponseDto();
        item2.setId(2L);


        List<UserShoppingListItemResponseDto> mockedList = Arrays.asList(item1, item2);

        // Mock the service layer
        when(shoppingListItemService.getUserShoppingList(eq(user.getId()), eq(habitId), eq(locale.getLanguage())))
                .thenReturn(mockedList);


        mockMvc.perform(get(USER_SHOPPING_LIST_ITEMS_URL+"/habits/{habitId}/shopping-list", habitId)
                        .locale(locale)  // Setting the locale for the request
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
//
//    @Test
//    public void getUserShoppingListItemWithoutLanguageParamTest() throws Exception {
//    }
//
//    @Test
//    public void updateUserShoppingListItemStatusWithLanguageParamTest() throws Exception {
//    }
//
//    @Test
//    public void updateUserShoppingListItemStatus() throws Exception {
//    }
//
//    @Test
//    public void updateUserShoppingListItemStatusWithoutLanguageParamTest() throws Exception {
//    }


}
