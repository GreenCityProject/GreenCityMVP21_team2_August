package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.FriendDtoResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendService {
    PageableDto<FriendDtoResponse> getAllUserFriends(Long userId, Pageable pageable);

    FriendDtoResponse getFriendProfile(Long userId);

    int countUserById(Long userId);


    List<FriendDtoResponse> searchNewFriends(Long userId, String searchTerm);

    void sendFriendRequest(Long userId, Long friendId);

    void cancelFriendRequest(Long userId, Long friendId);

    boolean isFriendRequestPending(Long userId, Long friendId);

    void acceptFriendRequest(Long userId, Long friendId);

    List<FriendDtoResponse> findFriendsByCity(Long userId);

    List<FriendDtoResponse> findFriendsOfFriends(Long userId);

    List<FriendDtoResponse> findFriendsOfFriendsByCity(Long userId);

    void deleteFriend(Long userId, Long friendId);

    int countFriendsByUserId(Long userId);

    List<FriendDtoResponse> getAllUserFriendsByCityHabitCommonFriends(Long userId);

}