package greencity.mapping;

import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsVO;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcoNewsVOMapperTest {

    private EcoNewsVOMapper ecoNewsVOMapper;

    @Mock
    private User user;

    @Mock
    private Tag tag;

    @BeforeEach
    void setUp() {
        ecoNewsVOMapper = new EcoNewsVOMapper();
    }

    @Test
    void convertShouldMapFieldsCorrectly() {
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Test Name");
        when(user.getUserStatus()).thenReturn(UserStatus.ACTIVATED);
        when(user.getRole()).thenReturn(Role.ROLE_ADMIN);

        Language languageEn = mock(Language.class);
        when(languageEn.getCode()).thenReturn(AppConstant.DEFAULT_LANGUAGE_CODE);

        TagTranslation tagTranslationEn = mock(TagTranslation.class);
        when(tagTranslationEn.getName()).thenReturn("Environment");
        when(tagTranslationEn.getLanguage()).thenReturn(languageEn);

        when(tag.getTagTranslations()).thenReturn(List.of(tagTranslationEn));

        EcoNews ecoNews = mock(EcoNews.class);
        when(ecoNews.getId()).thenReturn(1L);
        when(ecoNews.getAuthor()).thenReturn(user);
        when(ecoNews.getCreationDate()).thenReturn(ZonedDateTime.now());
        when(ecoNews.getImagePath()).thenReturn("path/to/image");
        when(ecoNews.getSource()).thenReturn("path/to/source");
        when(ecoNews.getText()).thenReturn("Some content");
        when(ecoNews.getTitle()).thenReturn("Tittle");
        when(ecoNews.getTags()).thenReturn(List.of(tag));
        when(ecoNews.getUsersLikedNews()).thenReturn(Collections.emptySet());
        when(ecoNews.getUsersDislikedNews()).thenReturn(Collections.emptySet());
        when(ecoNews.getEcoNewsComments()).thenReturn(Collections.emptyList());

        EcoNewsVO ecoNewsVO = ecoNewsVOMapper.convert(ecoNews);

        assertEquals(1L, ecoNewsVO.getId());
        assertEquals("path/to/image", ecoNewsVO.getImagePath());
        assertEquals("path/to/source", ecoNewsVO.getSource());
        assertEquals("Some content", ecoNewsVO.getText());
        assertEquals("Tittle", ecoNewsVO.getTitle());

        assertEquals(1, ecoNewsVO.getTags().size());
        assertEquals("Environment", ecoNewsVO.getTags().getFirst().getTagTranslations().getFirst().getName());

        assertEquals(1L, ecoNewsVO.getAuthor().getId());
        assertEquals("Test Name", ecoNewsVO.getAuthor().getName());
        assertEquals(UserStatus.ACTIVATED, ecoNewsVO.getAuthor().getUserStatus());
        assertEquals(Role.ROLE_ADMIN, ecoNewsVO.getAuthor().getRole());

        assertTrue(ecoNewsVO.getUsersLikedNews().isEmpty());
        assertTrue(ecoNewsVO.getUsersDislikedNews().isEmpty());
        assertTrue(ecoNewsVO.getEcoNewsComments().isEmpty());
    }
}