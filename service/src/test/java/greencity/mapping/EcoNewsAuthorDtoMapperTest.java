package greencity.mapping;

import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.User;
import greencity.mapping.EcoNewsAuthorDtoMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import static org.junit.jupiter.api.Assertions.*;

public class EcoNewsAuthorDtoMapperTest {

    private final EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper = new EcoNewsAuthorDtoMapper();

    @Test
    public void testConvert() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        EcoNewsAuthorDto ecoNewsAuthorDto = ecoNewsAuthorDtoMapper.convert(user);

        assertNotNull(ecoNewsAuthorDto, "The converted EcoNewsAuthorDto should not be null");
        assertEquals(1L, ecoNewsAuthorDto.getId(), "The ID should match the ID of the User");
        assertEquals("John Doe", ecoNewsAuthorDto.getName(), "The name should match the name of the User");
    }
}
