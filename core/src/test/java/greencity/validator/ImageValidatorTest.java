package greencity.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ImageValidatorTest {

    private ImageValidator imageValidator;

    @BeforeEach
    void setUp(){
        imageValidator = new ImageValidator();
    }


    @ParameterizedTest
    @ValueSource(strings = {"image/jpeg", "image/png", "image/jpg"})
    void isValidImageFileShouldContainsContentType(String contentType) {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", contentType, new byte[]{1, 2, 3, 4, 5});

        assertTrue(imageValidator.isValid(multipartFile, null));
    }

    @Test
    void isValidImageFileShouldReturnTrueWhenFileIsNull() {
        MultipartFile multipartFileIsNull = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFileIsNull.getContentType()).thenReturn(null);
        Mockito.when(multipartFileIsNull.isEmpty()).thenReturn(true);

        assertFalse(imageValidator.isValid(multipartFileIsNull, null));
    }
}