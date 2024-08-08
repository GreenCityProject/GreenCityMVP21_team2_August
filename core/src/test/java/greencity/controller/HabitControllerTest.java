package greencity.controller;


import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitService;
import greencity.service.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitControllerTest {

    private static final String HABIT_URL = "/habit";

    private MockMvc mockMvc;

    @Mock
    private HabitService habitService;

    @Mock
    private TagsService tagsService;

    @InjectMocks
    private HabitController habitController;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getHabitByIdShouldReturnHabitWhenFoundById() throws Exception {
        HabitDto habitDto = mock(HabitDto.class);

        when(habitService.getByIdAndLanguageCode(anyLong(), anyString())).thenReturn(habitDto);

        mockMvc.perform(get(HABIT_URL + "/{id}", 1)
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitService).getByIdAndLanguageCode(1L, "en");
    }

    @Test
    void getHabitByIdShouldReturnBadRequestWhenIdIsInvalid() throws Exception {
        mockMvc.perform(get(HABIT_URL + "/{id}", "abc")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllShouldReturnAllHabit() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);

        HabitDto habitDto1 = mock(HabitDto.class);
        HabitDto habitDto2 = mock(HabitDto.class);

        List<HabitDto> habits = Arrays.asList(habitDto1, habitDto2);

        PageableDto<HabitDto> pageableDto = new PageableDto<>(habits, pageable.getPageNumber(), pageable.getPageSize(), habits.size());

        when(habitService.getAllHabitsByLanguageCode(any(), any(), anyString())).thenReturn(pageableDto);

        mockMvc.perform(get(HABIT_URL)
                        .header("Accept-Language", "en")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(() -> "user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitService).getAllHabitsByLanguageCode(any(), any(), anyString());
    }


    @Test
    void getShoppingListItemsShouldReturnShoppingListItemDto() throws Exception {
        ShoppingListItemDto shoppingListItemDto1 = mock(ShoppingListItemDto.class);
        ShoppingListItemDto shoppingListItemDto2 = mock(ShoppingListItemDto.class);
        List<ShoppingListItemDto> shoppingList = Arrays.asList(shoppingListItemDto1, shoppingListItemDto2);

        when(habitService.getShoppingListForHabit(anyLong(), anyString())).thenReturn(shoppingList);

        mockMvc.perform(get(HABIT_URL + "/{id}/shopping-list", 1)
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitService, times(1)).getShoppingListForHabit(1L, "en");
        verify(habitService).getShoppingListForHabit(argThat(id -> id == 1L), eq("en"));
    }

    @Test
    void getAllByTagsAndLanguageCodeShouldReturnPageableDtoHabit() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        HabitDto habitDto1 = mock(HabitDto.class);
        HabitDto habitDto2 = mock(HabitDto.class);

        List<HabitDto> habits = Arrays.asList(habitDto1, habitDto2);

        PageableDto<HabitDto> pageableDto = new PageableDto<>(habits, pageable.getPageNumber(), pageable.getPageSize(), habits.size());

        when(habitService.getAllByTagsAndLanguageCode(any(), any(), anyString())).thenReturn(pageableDto);

        mockMvc.perform(get(HABIT_URL + "/tags/search")
                        .header("Accept-Language", "en")
                        .param("tags", "tag1", "tag2")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitService).getAllByTagsAndLanguageCode(any(), any(), anyString());
    }

    @Test
    void getAllByDifferentParametersShouldReturnPageableDtoHabit() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<String> tags = Arrays.asList("tag1", "tag2");

        HabitDto habitDto1 = mock(HabitDto.class);
        HabitDto habitDto2 = mock(HabitDto.class);

        List<HabitDto> habits = Arrays.asList(habitDto1, habitDto2);

        PageableDto<HabitDto> pageableDto = new PageableDto<>(habits, pageable.getPageNumber(), pageable.getPageSize(), habits.size());

        when(habitService.getAllByDifferentParameters(any(), any(), any(), any(), any(), anyString())).thenReturn(pageableDto);

        mockMvc.perform(get(HABIT_URL + "/search")
                        .header("Accept-Language", "en")
                        .param("tags", "tag1", "tag2")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(() -> "user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitService).getAllByDifferentParameters(any(), any(), any(), any(), any(), anyString());
        verify(habitService).getAllByDifferentParameters(
                any(UserVO.class),
                eq(pageable),
                eq(Optional.of(tags)),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq("en")
        );
    }

    @Test
    void findAllHabitsTagsShouldReturnAllHabits() throws Exception {
        List<String> habits = List.of("habit1", "habit2", "habit3", "habit4");
        when(tagsService.findAllHabitsTags(anyString())).thenReturn(habits);

        mockMvc.perform(get(HABIT_URL + "/tags")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(tagsService).findAllHabitsTags(anyString());
    }
}