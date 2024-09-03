package greencity.mapping;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import greencity.dto.user.FriendDtoResponse;
import greencity.entity.User;

public class FriendDtoMapperTest {

    private final FriendDtoMapper mapper = new FriendDtoMapper();

    @Test
    public void testConvert() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setRating(4.5);
        user.setProfilePicturePath("/path/to/picture.jpg");
        user.setCity("Kyiv");
        user.setUserCredo("Live and let live~~~");

        FriendDtoResponse friendDtoResponse = mapper.convert(user);

        assertNotNull(friendDtoResponse);
        assertEquals(user.getId(), friendDtoResponse.getId());
        assertEquals(user.getName(), friendDtoResponse.getName());
        assertEquals(user.getRating(), friendDtoResponse.getRating());
        assertEquals(user.getProfilePicturePath(), friendDtoResponse.getProfilePicturePath());
        assertEquals(user.getCity(), friendDtoResponse.getCity());
        assertEquals(user.getUserCredo(), friendDtoResponse.getUserCredo());
    }
}
