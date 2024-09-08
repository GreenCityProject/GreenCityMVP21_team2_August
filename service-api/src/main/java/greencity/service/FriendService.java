package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.FriendDtoResponse;
import org.springframework.data.domain.Pageable;

public interface FriendService {
    PageableDto<FriendDtoResponse> getAllUserFriends(Long userId, Pageable pageable);

    FriendDtoResponse getFriendProfile(Long userId);

    int countUserFriends(Long userId);

}