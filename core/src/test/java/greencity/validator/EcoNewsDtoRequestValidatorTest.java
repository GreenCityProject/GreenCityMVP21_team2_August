package greencity.validator;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.WrongCountOfTagsException;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static greencity.ModelUtils.getAddEcoNewsDtoRequest;
import static java.util.Collections.EMPTY_LIST;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EcoNewsDtoRequestValidatorTest {
    @InjectMocks
    private EcoNewsDtoRequestValidator validator;
    @Mock
    private ConstraintValidatorContext context;
    private AddEcoNewsDtoRequest request;

    @BeforeEach
    void setUp() {
        request = getAddEcoNewsDtoRequest();
    }

    @Test
    void isValid_CorrectUrl_ReturnsTrue() {
        request = getAddEcoNewsDtoRequest();
        request.setSource("https://eco-lavca.ua/");
        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValid_Numbers_ThrowsInvalidURLException() {
        request = getAddEcoNewsDtoRequest();
        request.setSource("123");
        Assertions.assertThrowsExactly(InvalidURLException.class, () -> validator.isValid(request, context));
    }

    @Test
    void isValid_EmptyListOfTags_ThrowsWrongCountOfTagsException() {
        request = getAddEcoNewsDtoRequest();
        request.setTags(EMPTY_LIST);
        Assertions.assertThrowsExactly(WrongCountOfTagsException.class, () -> validator.isValid(request, context));
    }

    @Test
    void isValid_RedundantQuantityOfTags_ThrowsWrongCountOfTagsException() {
        request = getAddEcoNewsDtoRequest();
        final ArrayList<String> tags = new ArrayList<>();
        tags.add("eco");
        tags.add("news");
        tags.add("offers");
        tags.add("all");
        request.setTags(tags);

        Assertions.assertThrowsExactly(WrongCountOfTagsException.class, () -> validator.isValid(request, context));
    }

    @Test
    void isValid_EmptyUrl_ReturnsTrue() {
        request = getAddEcoNewsDtoRequest();
        request.setSource("");
        assertTrue(validator.isValid(request, context));
    }
}
