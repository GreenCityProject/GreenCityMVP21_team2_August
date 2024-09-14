package greencity.repository;

import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;
import greencity.ModelUtils;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GreenCityApplication.class)
public class FriendRepoTest extends IntegrationTestBase {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FriendRepo friendRepo;

    @Autowired
    private HabitAssignRepo habitAssignRepo;

    @BeforeEach
    void setUp() {
        User user1 = ModelUtils.createUser(1L, "John@gmail.com", "John", "Doe", "Kyiv");
        User user2 = ModelUtils.createUser(2L, "Jane@gmail.com","Jane", "Doe", "Kyiv");
        User user3 = ModelUtils.createUser(3L, "Jack@gmail.com","Jack", "Smith", "Lviv");
        User user4 = ModelUtils.createUser(4L, "Jill@gmail.com","Jill", "Smith", "Kyiv");
        User user5 = ModelUtils.createUser(5L, "Andrea@gmail.com","Andrea", "Dir", "Kyiv");

        user1.setRefreshTokenKey("refresh-token");
        user2.setRefreshTokenKey("refresh-token");
        user3.setRefreshTokenKey("refresh-token");
        user4.setRefreshTokenKey("refresh-token");
        user5.setRefreshTokenKey("refresh-token");

        userRepo.saveAll(List.of(user1, user2, user3, user4, user5));

        friendRepo.sendFriendRequest(user1.getId(), user2.getId());
        friendRepo.sendFriendRequest(user2.getId(), user3.getId());
        friendRepo.sendFriendRequest(user2.getId(), user4.getId());
        friendRepo.sendFriendRequest(user2.getId(), user5.getId());


        HabitAssign habitAssignForUser1 = ModelUtils.getHabitAssign();
        habitAssignForUser1.setUser(user1);
        HabitAssign habitAssignForUser5 = ModelUtils.getHabitAssign();
        habitAssignForUser5.setUser(user5);

        habitAssignRepo.save(habitAssignForUser1);
        habitAssignRepo.save(habitAssignForUser5);

    }

    @Test
    void testSearchNewFriends() {
        Long userId = 1L;
        String searchTerm = "Doe";

        List<User> foundUsers = friendRepo.searchNewFriends(userId, "%" + searchTerm + "%");

        assertThat(foundUsers).hasSize(1);
        assertThat(foundUsers.get(0).getName()).isEqualTo("Jane");
    }

    @Test
    void testSendFriendRequest(){
        friendRepo.sendFriendRequest(1L, 3L);

        boolean isRequestPending = friendRepo.isFriendRequestPending(1L, 3L);

        assertThat(isRequestPending).isTrue();
    }

    @Test
    void testCancelFriendRequest(){
        friendRepo.cancelFriendRequest(1L, 3L);

        boolean isRequestPending = friendRepo.isFriendRequestPending(1L, 3L);

        assertFalse(isRequestPending);
    }

    @Test
    void testIsFriendRequestPending(){
        boolean isRequestPending = friendRepo.isFriendRequestPending(1L, 2L);

        assertThat(isRequestPending).isTrue();

        boolean isNotRequestPending = friendRepo.isFriendRequestPending(1L, 3L);

        assertThat(isNotRequestPending).isFalse();
    }

    @Test
    void testFindFriendsByCity(){
        List<User> friendsByCity = friendRepo.findFriendsByCity(1L);

        assertThat(friendsByCity).hasSize(3);
        assertThat(friendsByCity.get(0).getName()).isEqualTo("Jill");
    }

    @Test
    void testFindFriendsOfFriends(){
        List<User> mutualFriends = friendRepo.findFriendsOfFriends(1L);

        assertThat(mutualFriends).hasSize(3);
        assertThat(mutualFriends.get(0).getName()).isEqualTo("Jack");
    }

    @Test
    void testFindFriendsOfFriendsByCity(){
        List<User> friendsOfFriendsByCity = friendRepo.findFriendsOfFriendsByCity(1L);

        assertThat(friendsOfFriendsByCity).hasSize(2);
    }

    @Test
    void testExistsByUserIdAndFriendId(){
        boolean doesNotExist = friendRepo.existsByUserIdAndFriendId(1L, 3L);

        assertThat(doesNotExist).isFalse();
    }

    @Test
    @Transactional
    void testDeleteByUserIdAndFriendId(){
        friendRepo.deleteByUserIdAndFriendId(2L, 4L);

        boolean exists = friendRepo.existsByUserIdAndFriendId(2L, 4L);

        assertThat(exists).isFalse();
    }

    @Test
    void testGetAllFriendsByCityHabitCommonFriends(){
        List<User> friends = friendRepo.getAllFriendsByCityHabitCommonFriends(1L);

        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getName()).isEqualTo("Andrea");
    }

    @Test
    void testUpdateFriendshipStatus(){
        User user8 = ModelUtils.createUser(8L, "Britny@gmail.com","Britny", "Spy", "Texas");
        User user9 = ModelUtils.createUser(9L, "Max@gmail.com","Max", "Gir", "Monreal");
        user8.setRefreshTokenKey("refresh-token");
        user9.setRefreshTokenKey("refresh-token");

        userRepo.saveAll(List.of(user8, user9));

        friendRepo.sendFriendRequest(user8.getId(), user9.getId());

        friendRepo.updateFriendshipStatus(user8.getId(), user9.getId());

        boolean exists = friendRepo.existsByUserIdAndFriendId(user8.getId(), user9.getId());

        assertThat(exists).isTrue();
    }
}
