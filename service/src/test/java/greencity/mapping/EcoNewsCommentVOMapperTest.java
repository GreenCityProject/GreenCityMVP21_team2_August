package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcoNewsCommentVOMapperTest {

    EcoNewsCommentVOMapper ecoNewsCommentVOMapper;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        ecoNewsCommentVOMapper = new EcoNewsCommentVOMapper();
    }

    private void setupUser(Long id, String name) {
        when(user.getId()).thenReturn(id);
        when(user.getName()).thenReturn(name);
    }

    @Test
    void convertWithoutParentComment() {
        EcoNews ecoNews = Mockito.mock(EcoNews.class);
        when(ecoNews.getId()).thenReturn(1L);

        EcoNewsComment ecoNewsComment = Mockito.mock(EcoNewsComment.class);
        when(ecoNewsComment.getId()).thenReturn(1L);
        when(ecoNewsComment.getText()).thenReturn("New comment");
        when(ecoNewsComment.getParentComment()).thenReturn(null);
        when(ecoNewsComment.getEcoNews()).thenReturn(ecoNews);

        setupUser(1L, "John");
        when(ecoNewsComment.getUser()).thenReturn(user);
        when(ecoNewsComment.getUsersLiked()).thenReturn(Collections.emptySet());

        EcoNewsCommentVO ecoNewsCommentVO = ecoNewsCommentVOMapper.convert(ecoNewsComment);

        assertEquals(ecoNewsComment.getId(), ecoNewsCommentVO.getId());
        assertEquals(ecoNewsComment.getText(), ecoNewsCommentVO.getText());
        assertNull(ecoNewsCommentVO.getParentComment());
        assertTrue(ecoNewsCommentVO.getUsersLiked().isEmpty());
    }

    @Test
    void convertWithParentComment() {

        EcoNews ecoNews = Mockito.mock(EcoNews.class);
        when(ecoNews.getId()).thenReturn(1L);

        EcoNewsComment parentComment = Mockito.mock(EcoNewsComment.class);
        when(parentComment.getId()).thenReturn(2L);
        when(parentComment.getText()).thenReturn("Parent comment");
        when(parentComment.getEcoNews()).thenReturn(ecoNews);

        setupUser(2L, "John");
        when(parentComment.getUser()).thenReturn(user);
        when(parentComment.getUsersLiked()).thenReturn(Collections.emptySet());

        EcoNewsComment ecoNewsComment = Mockito.mock(EcoNewsComment.class);
        when(ecoNewsComment.getId()).thenReturn(1L);
        when(ecoNewsComment.getText()).thenReturn("Test comment");
        when(ecoNewsComment.getParentComment()).thenReturn(parentComment);
        when(ecoNewsComment.getEcoNews()).thenReturn(ecoNews);

        setupUser(3L, "Test User");
        when(ecoNewsComment.getUser()).thenReturn(user);
        when(ecoNewsComment.getUsersLiked()).thenReturn(Collections.emptySet());

        EcoNewsCommentVO commentVO = ecoNewsCommentVOMapper.convert(ecoNewsComment);

        assertEquals(ecoNewsComment.getId(), commentVO.getId());
        assertEquals(ecoNewsComment.getText(), commentVO.getText());
        assertEquals(2L, commentVO.getParentComment().getId());
        assertEquals("Parent comment", commentVO.getParentComment().getText());
        assertEquals(1L, commentVO.getEcoNews().getId());
        assertTrue(commentVO.getUsersLiked().isEmpty());
    }
}