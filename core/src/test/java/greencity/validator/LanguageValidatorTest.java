package greencity.validator;

import greencity.service.LanguageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LanguageValidatorTest {

    @InjectMocks
    private LanguageValidator languageValidator;

    @Mock
    private LanguageService languageService;

    @BeforeEach
    void setUp() {
        List<String> mockLanguageCodes = Arrays.asList("en", "es", "fr");
        Mockito.when(languageService.findAllLanguageCodes()).thenReturn(mockLanguageCodes);
        ReflectionTestUtils.setField(languageValidator, "codes", mockLanguageCodes);
        languageValidator.initialize(null);
    }

    @Test
    void testValidLanguageCode() {
        Assertions.assertTrue(languageValidator.isValid(Locale.ENGLISH, null));
        Assertions.assertTrue(languageValidator.isValid(Locale.FRENCH, null));
    }

    @Test
    void testInvalidLanguageCode() {
        Assertions.assertFalse(languageValidator.isValid(Locale.GERMAN, null));
        Assertions.assertFalse(languageValidator.isValid(Locale.CHINESE, null));
    }
}
