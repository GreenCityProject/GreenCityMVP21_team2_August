package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.Validator;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @Mock
    private Validator validator;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setValidator(validator)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void searchEverythingTest() throws Exception {
        String searchQuery = "Title";
        Locale locale = Locale.ENGLISH;
        SearchResponseDto searchResponseDto = new SearchResponseDto();
        when(searchService.search(searchQuery, locale.getLanguage())).thenReturn(searchResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                        .param("searchQuery", searchQuery)
                        .header("Accept-Language", locale.toLanguageTag())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(searchService).search(eq(searchQuery), eq(locale.getLanguage()));
    }

    @Test
    void searchEcoNewsTest() throws Exception {
        String searchQuery = "Eco news title";

        EcoNewsAuthorDto authorDto = EcoNewsAuthorDto.builder()
                .id(1L)
                .name("Author Name")
                .build();

        SearchNewsDto searchNewsDto = SearchNewsDto.builder()
                .id(1L)
                .title("Eco Title")
                .author(authorDto)
                .creationDate(ZonedDateTime.now())
                .tags(List.of("tag1", "tag2"))
                .build();

        PageableDto<SearchNewsDto> mockPageableDto = new PageableDto<>(
                List.of(searchNewsDto),
                1L,
                0,
                1
        );

        when(searchService.searchAllNews(any(Pageable.class), eq(searchQuery), anyString()))
                .thenReturn(mockPageableDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/search/econews")
                        .param("searchQuery", searchQuery)
                        .param("locale", Locale.ENGLISH.toLanguageTag())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "title,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(searchService).searchAllNews(
                argThat(pageable -> pageable.getPageNumber() == 0 &&
                        pageable.getPageSize() == 10 &&
                        pageable.getSort().toString().equals("title: ASC")),
                eq(searchQuery),
                eq(Locale.ENGLISH.getLanguage())
        );
    }
}

