package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.user.FriendDtoResponse;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.FriendDtoMapper;
import greencity.repository.EcoNewsRepo;
import greencity.repository.FriendRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private static final String STATUS_ACQUIRED = "ACQUIRED";
    private static final String STATUS_INPROGRESS = "INPROGRESS";
    private final UserRepo userRepo;
    private final FriendRepo friendRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final UserServiceImpl userService;
    private final FriendDtoMapper mapper;

    @Override
    public PageableDto<FriendDtoResponse> getAllUserFriends(Long userId, Pageable pageable) {

        User userFound = getUserById(userId);

        List<Long> habitIds = habitAssignRepo.getHabitsByUserID(userId)
                .stream()
                .map(habit -> habit.getHabit().getId())
                .collect(Collectors.toList());

        Page<User> friendsPage = friendRepo.getAllFriendsByUserId(userId, userFound.getCity(), habitIds, pageable);

        List<FriendDtoResponse> friendDtoResponses = friendsPage.stream()
                .map(user -> {
                    return populateFriendDto(user, userId);
                })
                .collect(Collectors.toList());

        return new PageableDto<>(friendDtoResponses,
                friendsPage.getTotalElements(),
                friendsPage.getPageable().getPageNumber(),
                friendsPage.getTotalPages());
    }

    @Override
    public FriendDtoResponse getFriendProfile(Long userId) {
        User user = getUserById(userId);
        return populateFriendDto(user, userId);
    }

    @Override
    public List<FriendDtoResponse> searchNewFriends(Long userId, String searchTerm) {
        String searchTermWithWildcards = "%" + searchTerm + "%";
        List<User> users = friendRepo.searchNewFriends(userId, searchTermWithWildcards);
        return users.stream()
                .map(user -> populateFriendDto(user, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void sendFriendRequest(Long userId, Long friendId) {
        if (!isFriendRequestPending(userId, friendId)) {
            friendRepo.sendFriendRequest(userId, friendId);
        } else {
            throw new IllegalStateException("Friend request already pending");
        }
    }

    @Override
    @Transactional
    public void cancelFriendRequest(Long userId, Long friendId) {
        if (isFriendRequestPending(userId, friendId)) {
            friendRepo.cancelFriendRequest(userId, friendId);
        } else {
            throw new NotFoundException("No pending friend request found");
        }
    }

    @Override
    public boolean isFriendRequestPending(Long userId, Long friendId) {
        return friendRepo.isFriendRequestPending(userId, friendId);
    }

    @Override
    public List<FriendDtoResponse> findFriendsByCity(Long userId) {
        List<User> friends = friendRepo.findFriendsByCity(userId);
        return friends.stream()
                .map(user -> populateFriendDto(user, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendDtoResponse> findFriendsOfFriends(Long userId) {
        List<User> friends = friendRepo.findFriendsOfFriends(userId);
        return friends.stream()
                .map(user -> populateFriendDto(user, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendDtoResponse> findFriendsOfFriendsByCity(Long userId) {
        List<User> friends = friendRepo.findFriendsOfFriendsByCity(userId);
        return friends.stream()
                .map(user -> populateFriendDto(user, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        if(!friendRepo.existsByUserIdAndFriendId(userId, friendId)) {
            throw new NotFoundException(ErrorMessage.USER_HAS_NO_FRIEND_WITH_ID + friendId);
        }
        friendRepo.deleteByUserIdAndFriendId(userId, friendId);
    }

    @Override
    public int countFriendsByUserId(Long userId) {
        return friendRepo.countFriendsByUserIdAndStatus(userId);
    }

    @Override
    public List<FriendDtoResponse> getAllUserFriendsByCityHabitCommonFriends(Long userId) {
        List<User> recommendationsFriends = friendRepo.getAllFriendsByCityHabitCommonFriends(userId);
        return recommendationsFriends.stream()
                .map(user -> populateFriendDto(user, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Long userId, Long friendId) {
        if (friendRepo.isFriendRequestPending(userId, friendId)) {
            friendRepo.updateFriendshipStatus(userId, friendId);
        } else {
            throw new NotFoundException(ErrorMessage.USER_HAS_NO_FRIEND_WITH_ID + friendId);
        }
    }

    private User getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
    }

    private FriendDtoResponse populateFriendDto(User user, Long userId) {
        FriendDtoResponse friendDtoResponse = mapper.convert(user);
        if (friendDtoResponse != null) {
            friendDtoResponse.setMutualFriends(
                    friendRepo.findMutualFriends(userId, user.getId()).size());
            friendDtoResponse.setAmountHabitsAcquired(
                    habitAssignRepo.getHabitAssignByUserIdAndStatus(user.getId(), STATUS_ACQUIRED).size());
            friendDtoResponse.setAmountHabitsInProgress(
                    habitAssignRepo.getHabitAssignByUserIdAndStatus(user.getId(), STATUS_INPROGRESS).size());
            friendDtoResponse.setAmountNewsPublished(
                    ecoNewsRepo.findAllByUserId(user.getId()).size());
        }
        return friendDtoResponse;
    }

    @Override
    public int countUserById(Long userId) {
        return friendRepo.countUserById(userId);
    }

}