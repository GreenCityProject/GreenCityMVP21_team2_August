package greencity.service;

import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.FriendDtoMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
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

    @Ignore("This test is temporarily ignored")
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

    @Test
    public void searchNewFriendsShouldReturnListOfFriends() {
        Long userId = 1L;
        String searchTerm = "test";
        String searchTermWithWildcards = "%" + searchTerm + "%";

        User user1 = new User();
        user1.setId(2L);
        user1.setName("Tets user 1");

        User user2 = new User();
        user2.setId(3L);
        user2.setName("Tets user 2");

        List<User> friends = Arrays.asList(user1, user2);

        FriendDtoResponse friendDto1 = new FriendDtoResponse();
        friendDto1.setId(user1.getId());
        friendDto1.setName(user1.getName());

        FriendDtoResponse friendDto2 = new FriendDtoResponse();
        friendDto2.setId(user2.getId());
        friendDto2.setName(user2.getName());

        when(friendRepo.searchNewFriends(userId, searchTermWithWildcards)).thenReturn(friends);
        when(mapper.convert(user1)).thenReturn(friendDto1);
        when(mapper.convert(user2)).thenReturn(friendDto2);

        List<FriendDtoResponse> result = friendService.searchNewFriends(userId, searchTerm);

        assertEquals(2, result.size());
        assertEquals(friendDto1, result.get(0));
        assertEquals(friendDto2, result.get(1));

        verify(friendRepo, times(1)).searchNewFriends(userId, searchTermWithWildcards);

        verify(mapper, times(1)).convert(user1);
        verify(mapper, times(1)).convert(user2);
    }

    @Test
    public void sendFriendRequestShouldSendRequestWhenNotPending() {
        Long userId = 1L;
        Long friendId = 2L;

        when(friendService.isFriendRequestPending(userId, friendId)).thenReturn(false);

        friendService.sendFriendRequest(userId, friendId);

        verify(friendRepo, times(1)).sendFriendRequest(userId, friendId);
    }

    @Test
    public void sendFriendRequestShouldThrowExceptionWhenPending(){
        Long userId = 1L;
        Long friendId = 2L;
        when(friendService.isFriendRequestPending(userId, friendId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> friendService.sendFriendRequest(userId, friendId));

        verify(friendRepo, never()).sendFriendRequest(userId, friendId);
    }

    @Test
    public void canselFriendRequestShouldSendRequestWhenNotPending(){
        Long userId = 1L;
        Long friendId = 2L;
        when(friendService.isFriendRequestPending(userId, friendId)).thenReturn(true);

        friendService.cancelFriendRequest(userId, friendId);

        verify(friendRepo, times(1)).cancelFriendRequest(userId, friendId);
    }

    @Test
    public void canselFriendRequestShouldThrowExceptionWhenPending(){
        Long userId = 1L;
        Long friendId = 2L;
        when(friendService.isFriendRequestPending(userId, friendId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> friendService.cancelFriendRequest(userId, friendId));

        verify(friendRepo, never()).cancelFriendRequest(userId, friendId);
    }

    @Test
    public void isFriendRequestPendingShouldReturnTrueWhenPending(){
        Long userId = 1L;
        Long friendId = 2L;
        when(friendService.isFriendRequestPending(userId, friendId)).thenReturn(true);

        boolean result = friendService.isFriendRequestPending(userId, friendId);
        assertTrue(result);
        verify(friendRepo, times(1)).isFriendRequestPending(userId, friendId);
    }

    @Test
    public void isFriendRequestPendingShouldReturnFalseWhenNotPending(){
        Long userId = 1L;
        Long friendId = 2L;
        when(friendService.isFriendRequestPending(userId, friendId)).thenReturn(false);

        boolean result = friendService.isFriendRequestPending(userId, friendId);
        assertFalse(result);
        verify(friendRepo, times(1)).isFriendRequestPending(userId, friendId);
    }

    @Test
    public void findFriendsByCityShouldReturnListOfFriends() {
        Long userId = 1L;

        User user1 = new User();
        user1.setId(2L);
        user1.setCity("Kyiv");

        User user2 = new User();
        user2.setId(3L);
        user2.setCity("Kyiv");

        List<User> friends = Arrays.asList(user1, user2);

        FriendDtoResponse friendDto1 = new FriendDtoResponse();
        friendDto1.setId(user1.getId());
        friendDto1.setCity(user1.getCity());

        FriendDtoResponse friendDto2 = new FriendDtoResponse();
        friendDto2.setId(user2.getId());
        friendDto2.setCity(user2.getCity());

        when(friendRepo.findFriendsByCity(userId)).thenReturn(friends);
        when(mapper.convert(user1)).thenReturn(friendDto1);
        when(mapper.convert(user2)).thenReturn(friendDto2);

        List<FriendDtoResponse> result = friendService.findFriendsByCity(userId);

        assertEquals(2, result.size());
        assertEquals(friendDto1, result.get(0));
        assertEquals(friendDto2, result.get(1));

        verify(friendRepo, times(1)).findFriendsByCity(userId);

        verify(mapper, times(1)).convert(user1);
        verify(mapper, times(1)).convert(user2);
    }

    @Test
    public void findFriendsByCityShouldReturnEmptyListWhenNoFriendsFound() {
        Long userId = 1L;

        when(friendRepo.findFriendsByCity(userId)).thenReturn(Collections.emptyList());

        List<FriendDtoResponse> result = friendService.findFriendsByCity(userId);

        assertTrue(result.isEmpty());

        verify(friendRepo, times(1)).findFriendsByCity(userId);

        verifyNoInteractions(mapper);
    }

    @Test
    public void findFriendsOfFriendsShouldReturnListOfFriends() {
        Long userId = 1L;

        User user1 = new User();
        user1.setId(2L);

        User user2 = new User();
        user2.setId(3L);

        List<User> friends = Arrays.asList(user1, user2);

        FriendDtoResponse friendDto1 = new FriendDtoResponse();
        friendDto1.setId(user1.getId());
        friendDto1.setCity(user1.getCity());

        FriendDtoResponse friendDto2 = new FriendDtoResponse();
        friendDto2.setId(user2.getId());
        friendDto2.setCity(user2.getCity());

        when(friendRepo.findFriendsOfFriends(userId)).thenReturn(friends);
        when(mapper.convert(user1)).thenReturn(friendDto1);
        when(mapper.convert(user2)).thenReturn(friendDto2);

        List<FriendDtoResponse> result = friendService.findFriendsOfFriends(userId);

        assertEquals(2, result.size());
        assertEquals(friendDto1, result.get(0));
        assertEquals(friendDto2, result.get(1));

        verify(friendRepo, times(1)).findFriendsOfFriends(userId);

        verify(mapper, times(1)).convert(user1);
        verify(mapper, times(1)).convert(user2);
    }

    @Test
    public void findFriendsOfFriendsShouldReturnEmptyListWhenNoFriendsFound() {
        Long userId = 1L;

        when(friendRepo.findFriendsOfFriends(userId)).thenReturn(Collections.emptyList());

        List<FriendDtoResponse> result = friendService.findFriendsOfFriends(userId);

        assertTrue(result.isEmpty());

        verify(friendRepo, times(1)).findFriendsOfFriends(userId);

        verifyNoInteractions(mapper);
    }

    @Test
    public void findFriendsOfFriendsByCityShouldReturnListOfFriends() {
        Long userId = 1L;

        User friend1 = new User();
        friend1.setId(2L);
        friend1.setCity("Kyiv");

        User friend2 = new User();
        friend2.setId(3L);
        friend2.setCity("Kyiv");

        List<User> friends = Arrays.asList(friend1, friend2);

        FriendDtoResponse friendDto1 = new FriendDtoResponse();
        friendDto1.setId(friend1.getId());
        friendDto1.setCity(friend1.getCity());

        FriendDtoResponse friendDto2 = new FriendDtoResponse();
        friendDto2.setId(friend2.getId());
        friendDto2.setCity(friend2.getCity());

        when(friendRepo.findFriendsOfFriendsByCity(userId)).thenReturn(friends);
        when(mapper.convert(friend1)).thenReturn(friendDto1);
        when(mapper.convert(friend2)).thenReturn(friendDto2);

        List<FriendDtoResponse> result = friendService.findFriendsOfFriendsByCity(userId);

        assertEquals(2, result.size());
        assertEquals(friendDto1, result.get(0));
        assertEquals(friendDto2, result.get(1));

        verify(friendRepo, times(1)).findFriendsOfFriendsByCity(userId);

        verify(mapper, times(1)).convert(friend1);
        verify(mapper, times(1)).convert(friend2);
    }

    @Test
    public void findFriendsOfFriendsByCityShouldReturnEmptyListWhenNoFriendsFound() {
        Long userId = 1L;

        when(friendRepo.findFriendsOfFriendsByCity(userId)).thenReturn(Collections.emptyList());

        List<FriendDtoResponse> result = friendService.findFriendsOfFriendsByCity(userId);

        assertTrue(result.isEmpty());

        verify(friendRepo, times(1)).findFriendsOfFriendsByCity(userId);

        verifyNoInteractions(mapper);
    }

    @Test
    public void deleteFriendShouldDeleteWhenUserExist(){
        Long userId = 1L;
        Long friendId = 2L;
        when(friendRepo.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);

        friendService.deleteFriend(userId, friendId);

        verify(friendRepo, times(1)).deleteByUserIdAndFriendId(userId, friendId);
    }

    @Test
    public void deleteFriendShouldThrowExceptionWhenUserNotExist(){
        Long userId = 1L;
        Long friendId = 2L;
        when(friendRepo.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> friendService.deleteFriend(userId, friendId));

        verify(friendRepo, never()).cancelFriendRequest(userId, friendId);
    }

    @Test
    public void countFriendsByUserIdShouldReturnCountOfFriends(){
        Long userId = 1L;
        Integer countExcepted = 3;
        when(friendRepo.countFriendsByUserIdAndStatus(userId)).thenReturn(countExcepted);

        Integer resultActual = friendService.countFriendsByUserId(userId);

        assertEquals(countExcepted, resultActual);
        verify(friendRepo, times(1)).countFriendsByUserIdAndStatus(userId);
    }

    @Test
    public void getAllUserFriendsByCityHabitCommonFriendsShouldReturnListOfFriends() {
        Long userId = 1L;

        User friend1 = new User();
        friend1.setId(2L);
        friend1.setCity("Kyiv");

        User friend2 = new User();
        friend2.setId(3L);
        friend2.setCity("Kyiv");

        List<User> friends = Arrays.asList(friend1, friend2);

        FriendDtoResponse friendDto1 = new FriendDtoResponse();
        friendDto1.setId(friend1.getId());
        friendDto1.setCity(friend1.getCity());

        FriendDtoResponse friendDto2 = new FriendDtoResponse();
        friendDto2.setId(friend2.getId());
        friendDto2.setCity(friend2.getCity());

        when(friendRepo.getAllFriendsByCityHabitCommonFriends(userId)).thenReturn(friends);
        when(mapper.convert(friend1)).thenReturn(friendDto1);
        when(mapper.convert(friend2)).thenReturn(friendDto2);

        List<FriendDtoResponse> result = friendService.getAllUserFriendsByCityHabitCommonFriends(userId);

        assertEquals(2, result.size());
        assertEquals(friendDto1, result.get(0));
        assertEquals(friendDto2, result.get(1));

        verify(friendRepo, times(1)).getAllFriendsByCityHabitCommonFriends(userId);

        verify(mapper, times(1)).convert(friend1);
        verify(mapper, times(1)).convert(friend2);
    }

    @Test
    public void getAllUserFriendsByCityHabitCommonFriendsShouldReturnEmptyListWhenNoFriendsFound() {
        Long userId = 1L;

        when(friendRepo.getAllFriendsByCityHabitCommonFriends(userId)).thenReturn(Collections.emptyList());

        List<FriendDtoResponse> result = friendService.getAllUserFriendsByCityHabitCommonFriends(userId);

        assertTrue(result.isEmpty());

        verify(friendRepo, times(1)).getAllFriendsByCityHabitCommonFriends(userId);

        verifyNoInteractions(mapper);
    }
}
