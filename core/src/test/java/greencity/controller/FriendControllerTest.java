package greencity.controller;

import greencity.dto.user.FriendDtoResponse;
import greencity.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FriendControllerTest {

    private static final String FRIEND_URL = "/friends";

    private MockMvc mockMvc;

    @Mock
    private FriendService friendService;

    @InjectMocks
    private FriendController friendController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(friendController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testSearchNewFriendsShouldReturnStatusOk() throws Exception{
        Long userId = 1L;
        String searchTerm = "test";

        FriendDtoResponse friend1 = mock(FriendDtoResponse.class);
        FriendDtoResponse friend2 = mock(FriendDtoResponse.class);
        List<FriendDtoResponse> friendDtoResponses = Arrays.asList(friend1, friend2);

        when(friendService.searchNewFriends(userId, searchTerm)).thenReturn(friendDtoResponses);

        mockMvc.perform(get(FRIEND_URL + "/{userId}/search", userId)
                        .param("searchTerm", searchTerm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendService).searchNewFriends(userId, searchTerm);
    }

    @Test
    void testSendFriendRequestShouldReturnStatusOk() throws Exception {
        Long userId = 1L;
        Long friendId = 2L;

        doNothing().when(friendService).sendFriendRequest(userId, friendId);

        mockMvc.perform(post(FRIEND_URL + "/{userId}/request/{friendId}", userId, friendId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendService, times(1)).sendFriendRequest(userId, friendId);
    }

    @Test
    void testCancelFriendRequestShouldReturnStatusOk() throws Exception {
        Long userId = 1L;
        Long friendId = 2L;

        doNothing().when(friendService).cancelFriendRequest(userId, friendId);

        mockMvc.perform(delete(FRIEND_URL + "/{userId}/reject/{friendId}", userId, friendId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendService, times(1)).cancelFriendRequest(userId, friendId);
    }

    @Test
    void testFindFriendsByCityShouldReturnStatusOk() throws Exception {
        Long userId = 1L;

        when(friendService.findFriendsByCity(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(FRIEND_URL + "/{userId}/city", userId )
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendService, times(1)).findFriendsByCity(userId);
    }

    @Test
    void testFindFriendsOfFriendsShouldReturnStatusOk() throws Exception {
        Long userId = 1L;

        when(friendService.findFriendsOfFriends(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(FRIEND_URL + "/{userId}/mutual-friends", userId )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendService, times(1)).findFriendsOfFriends(userId);
    }

    @Test
    void testFindFriendsOfFriendsByCityShouldReturnStatusNotFound() throws Exception {
        Long userId = 1L;

        when(friendService.findFriendsOfFriendsByCity(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(FRIEND_URL + "/{userId}/mutual-city-friends", userId )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(friendService, times(1)).findFriendsOfFriendsByCity(userId);
    }

    @Test
    void testFindFriendsOfFriendsByCityShouldReturnStatusOK() throws Exception {
        Long userId = 1L;

        FriendDtoResponse friend1 = mock(FriendDtoResponse.class);
        FriendDtoResponse friend2 = mock(FriendDtoResponse.class);
        List<FriendDtoResponse> friendDtoResponses = Arrays.asList(friend1, friend2);

        when(friendService.findFriendsOfFriendsByCity(userId)).thenReturn(friendDtoResponses);

        mockMvc.perform(get(FRIEND_URL + "/{userId}/mutual-city-friends", userId )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendService, times(1)).findFriendsOfFriendsByCity(userId);
    }

    @Test
    void testDeleteFriendStatusOKShouldReturnContentString() throws Exception {
        Long userId = 1L;
        Long friendId = 2L;
        doNothing().when(friendService).deleteFriend(userId, friendId);

        mockMvc.perform(delete(FRIEND_URL + "/{userId}/unfriend/{friendId}", userId, friendId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Friend deleted successfully."))
                .andExpect(status().isOk());

        verify(friendService, times(1)).deleteFriend(userId, friendId);
    }

    @Test
    void getFriendsCount() throws Exception {
        Long userId = 1L;
        Integer numberOfFriends = 2;

        when(friendService.countFriendsByUserId(userId)).thenReturn(numberOfFriends);

        mockMvc.perform(get(FRIEND_URL + "/{userId}/friend-count", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendService, times(1)).countFriendsByUserId(userId);
    }

    @Test
    void getRecommendationsFriends() throws Exception {
        Long userId = 1L;

        FriendDtoResponse friend1 = mock(FriendDtoResponse.class);
        FriendDtoResponse friend2 = mock(FriendDtoResponse.class);
        List<FriendDtoResponse> friendDtoResponses = Arrays.asList(friend1, friend2);

        when(friendService.getAllUserFriendsByCityHabitCommonFriends(userId)).thenReturn(friendDtoResponses);


        mockMvc.perform(get(FRIEND_URL + "/{userId}/friend-recommendations", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendService, times(1)).getAllUserFriendsByCityHabitCommonFriends(userId);
    }
}