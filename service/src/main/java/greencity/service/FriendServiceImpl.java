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

    public User getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
    }

    public FriendDtoResponse populateFriendDto(User user, Long userId) {
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

    public int countUserFriends(Long userId) {
        return friendRepo.countUserById(userId);
    }

}