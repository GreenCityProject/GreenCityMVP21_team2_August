package greencity.mapping;

import greencity.dto.user.FriendDtoResponse;
import greencity.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FriendDtoMapper {
    public FriendDtoResponse convert(User user) {
        return FriendDtoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .rating(user.getRating())
                .profilePicturePath(user.getProfilePicturePath())
                .city(user.getCity())
                .userCredo(user.getUserCredo())
                .build();
    }
}