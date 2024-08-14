package greencity.mapping;

import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcoNewsDtoMapperTest {

    @Mock
    private User user;

    @Mock
    private Tag tag;

    private EcoNewsDtoMapper ecoNewsDtoMapper;

    @BeforeEach
    void setUp() {
        ecoNewsDtoMapper = new EcoNewsDtoMapper();
    }

    @Test
    void convertShouldMapEcoNewsToEcoNewsDtoWithCorrectFieldsAndTags() {
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Test Name");

        Language languageEn = mock(Language.class);
        when(languageEn.getCode()).thenReturn(AppConstant.DEFAULT_LANGUAGE_CODE);

        Language languageUa = mock(Language.class);
        when(languageUa.getCode()).thenReturn("ua");

        TagTranslation tagTranslationEn = mock(TagTranslation.class);
        when(tagTranslationEn.getName()).thenReturn("Environment");
        when(tagTranslationEn.getLanguage()).thenReturn(languageEn);

        TagTranslation tagTranslationUa = mock(TagTranslation.class);
        when(tagTranslationUa.getName()).thenReturn("Екологія");
        when(tagTranslationUa.getLanguage()).thenReturn(languageUa);

        when(tag.getTagTranslations()).thenReturn(Arrays.asList(tagTranslationEn, tagTranslationUa));

        EcoNews ecoNews = mock(EcoNews.class);
        when(ecoNews.getAuthor()).thenReturn(user);
        when(ecoNews.getId()).thenReturn(1L);
        when(ecoNews.getText()).thenReturn("Some content");
        when(ecoNews.getCreationDate()).thenReturn(ZonedDateTime.now());
        when(ecoNews.getImagePath()).thenReturn("path/to/image");
        when(ecoNews.getUsersLikedNews()).thenReturn(Collections.emptySet());
        when(ecoNews.getShortInfo()).thenReturn("short info");
        when(ecoNews.getTags()).thenReturn(List.of(tag));
        when(ecoNews.getUsersLikedNews()).thenReturn(Collections.emptySet());
        when(ecoNews.getUsersDislikedNews()).thenReturn(Collections.emptySet());
        when(ecoNews.getTitle()).thenReturn("Tittle");
        when(ecoNews.getEcoNewsComments()).thenReturn(Collections.emptyList());

        EcoNewsDto ecoNewsDto = ecoNewsDtoMapper.convert(ecoNews);

        assertEquals(1L, ecoNewsDto.getId());
        assertEquals("Some content", ecoNewsDto.getContent());
        assertEquals("path/to/image", ecoNewsDto.getImagePath());
        assertEquals("short info", ecoNewsDto.getShortInfo());
        assertEquals("Tittle", ecoNewsDto.getTitle());
        assertEquals("Environment", ecoNewsDto.getTags().getFirst());
        assertEquals("Екологія", ecoNewsDto.getTagsUa().getFirst());
    }
}