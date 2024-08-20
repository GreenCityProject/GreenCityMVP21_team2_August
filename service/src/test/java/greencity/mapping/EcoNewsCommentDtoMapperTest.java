package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.CommentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcoNewsCommentDtoMapperTest {

    EcoNewsCommentDtoMapper ecoNewsCommentDtoMapper;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        ecoNewsCommentDtoMapper = new EcoNewsCommentDtoMapper();
    }

    private EcoNewsComment createEcoNewsComment(Long id, LocalDateTime createdDate, LocalDateTime modifiedDate, String text, boolean isDeleted) {
        EcoNewsComment ecoNewsComment = new EcoNewsComment();
        ecoNewsComment.setId(id);
        ecoNewsComment.setCreatedDate(createdDate);
        ecoNewsComment.setModifiedDate(modifiedDate);
        ecoNewsComment.setText(text);
        ecoNewsComment.setDeleted(isDeleted);
        return ecoNewsComment;
    }

    private void setupUser(Long id, String name, String profilePicturePath) {
        when(user.getId()).thenReturn(id);
        when(user.getName()).thenReturn(name);
        when(user.getProfilePicturePath()).thenReturn(profilePicturePath);
    }

    @Test
    void convertWhenCommentIsDeleted() {
        EcoNewsComment ecoNewsComment = createEcoNewsComment(1L, LocalDateTime.now(), LocalDateTime.now(), null, true);

        EcoNewsCommentDto ecoNewsCommentDto = ecoNewsCommentDtoMapper.convert(ecoNewsComment);

        assertEquals(1L, ecoNewsCommentDto.getId());
        assertEquals(CommentStatus.DELETED, ecoNewsCommentDto.getStatus());
    }

    @Test
    void convertWhenCommentIsOriginal() {
        LocalDateTime now = LocalDateTime.now();
        EcoNewsComment ecoNewsComment = createEcoNewsComment(2L, now, now, "This is a comment", false);

        setupUser(1L, "John", "profile_picture");
        ecoNewsComment.setUser(user);
        ecoNewsComment.setUsersLiked(Collections.emptySet());

        EcoNewsCommentDto ecoNewsCommentDto = ecoNewsCommentDtoMapper.convert(ecoNewsComment);

        assertEquals(CommentStatus.ORIGINAL, ecoNewsCommentDto.getStatus());
        assertEquals("This is a comment", ecoNewsCommentDto.getText());
    }

    @Test
    void convertWhenCommentIsEdited() {
        LocalDateTime now = LocalDateTime.now();
        EcoNewsComment ecoNewsComment = createEcoNewsComment(3L, now.minusDays(1), now, "This is an edit comment", false);

        setupUser(3L, "John", "profile_picture");
        ecoNewsComment.setUser(user);
        ecoNewsComment.setUsersLiked(Collections.emptySet());

        EcoNewsCommentDto ecoNewsCommentDto = ecoNewsCommentDtoMapper.convert(ecoNewsComment);

        assertEquals(CommentStatus.EDITED, ecoNewsCommentDto.getStatus());
        assertEquals("This is an edit comment", ecoNewsCommentDto.getText());
    }

    @Test
    void convertWhenUserIsPresent() {
        EcoNewsComment ecoNewsComment = createEcoNewsComment(4L, LocalDateTime.now(), LocalDateTime.now(), "This is a comment", false);

        setupUser(2L, "User Name", "path/to/profile/picture");
        ecoNewsComment.setUser(user);
        ecoNewsComment.setUsersLiked(Collections.emptySet());

        EcoNewsCommentDto ecoNewsCommentDto = ecoNewsCommentDtoMapper.convert(ecoNewsComment);

        assertEquals(2L, ecoNewsCommentDto.getAuthor().getId());
        assertEquals("User Name", ecoNewsCommentDto.getAuthor().getName());
        assertEquals("path/to/profile/picture", ecoNewsCommentDto.getAuthor().getUserProfilePicturePath());
    }
}