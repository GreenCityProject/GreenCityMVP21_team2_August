package greencity.service;

import greencity.mapping.FriendDtoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import greencity.dto.PageableDto;
import greencity.dto.user.FriendDtoResponse;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.repository.EcoNewsRepo;
import greencity.repository.FriendRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserRepo;

@RunWith(MockitoJUnitRunner.class)
public class FriendServiceImplTest {

    @InjectMocks
    private FriendServiceImpl friendService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private FriendRepo friendRepo;

    @Mock
    private HabitAssignRepo habitAssignRepo;

    @Mock
    private EcoNewsRepo ecoNewsRepo;

    @Mock
    private FriendDtoMapper mapper;

    @Test
    public void testGetAllUserFriends() {
        Long userId = 1L;
        String city = "Kyiv";
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(userId);
        user.setCity(city);

        List<HabitAssign> habits = new ArrayList<>();
        habits.add(new HabitAssign());

        List<Long> habitIds = habits.stream()
                .map(habit -> habit.getHabit().getId())
                .collect(Collectors.toList());

        List<User> friends = new ArrayList<>();
        friends.add(user);

        Page<User> friendsPage = new PageImpl<>(friends, pageable, 1);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(habitAssignRepo.getHabitsByUserID(userId)).thenReturn(habits);
        when(friendRepo.getAllFriendsByUserId(userId, city, habitIds, pageable)).thenReturn(friendsPage);
        when(mapper.convert(any(User.class))).thenReturn(new FriendDtoResponse());

        PageableDto<FriendDtoResponse> result = friendService.getAllUserFriends(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetFriendProfile() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.convert(user)).thenReturn(new FriendDtoResponse());

        FriendDtoResponse result = friendService.getFriendProfile(userId);

        assertNotNull(result);
        verify(mapper, times(1)).convert(user);
    }
}
