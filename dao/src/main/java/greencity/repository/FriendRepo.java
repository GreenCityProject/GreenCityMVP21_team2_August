package greencity.repository;

import greencity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRepo  extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query(nativeQuery = true, value = "SELECT DISTINCT u.* "
            + "FROM users_friends uf "
            + "JOIN habit_assign ha ON uf.friend_id = ha.user_id "
            + "JOIN users u ON uf.friend_id = u.id "
            + "WHERE uf.user_id = :userId AND ha.habit_id IN (:habitIds) AND u.city = :city")
    Page<User> getAllFriendsByUserId(@Param("userId") Long userId,
                                     @Param("city") String city,
                                     @Param("habitIds") List<Long> habitIds,
                                     Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT DISTINCT u.* "
            + "FROM users u "
            + "JOIN users_friends uf1 ON u.id = uf1.friend_id "
            + "JOIN users_friends uf2 ON u.id = uf2.friend_id "
            + "WHERE uf1.user_id = :userId1 AND uf2.user_id = :userId2")
    List<User> findMutualFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query(nativeQuery = true, value = "SELECT u.* "
            + "FROM users u "
            + "WHERE (u.name LIKE :searchTerm OR u.first_name LIKE :searchTerm)"
            + "AND u.id != :userId")
    List<User> searchNewFriends(@Param("userId") Long userId,
                                @Param("searchTerm") String searchTerm);

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO users_friends (user_id, friend_id, status) "
            + "VALUES (:userId, :friendId, 'PENDING')")
    void sendFriendRequest(@Param("userId") Long userId,
                           @Param("friendId") Long friendId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM users_friends "
            + "WHERE user_id = :userId AND friend_id = :friendId AND status = 'PENDING'")
    void cancelFriendRequest(@Param("userId") Long userId,
                             @Param("friendId") Long friendId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) > 0 "
            + "FROM users_friends "
            + "WHERE user_id = :userId AND friend_id = :friendId AND status = 'PENDING'")
    boolean isFriendRequestPending(@Param("userId") Long userId,
                                   @Param("friendId") Long friendId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE users_friends "
            + "SET status = 'ACCEPTED' WHERE user_id = :userId AND friend_id = :friendId AND status = 'PENDING'")
    void updateFriendshipStatus(@Param("userId") Long userId,
                                @Param("friendId") Long friendId);

    @Query(nativeQuery = true, value = "SELECT u.* "
            + "FROM users u "
            + "WHERE u.city = (SELECT u2.city FROM users u2 WHERE u2.id = :userId) AND u.id != :userId")
    List<User> findFriendsByCity(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT u.* "
            + "FROM users u "
            + "JOIN users_friends uf1 ON u.id = uf1.friend_id "
            + "JOIN users_friends uf2 ON uf1.user_id = uf2.friend_id "
            + "WHERE uf2.user_id = :userId AND u.id != :userId")
    List<User> findFriendsOfFriends(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT u.* "
            + "FROM users u "
            + "JOIN users_friends uf1 ON u.id = uf1.friend_id "
            + "JOIN users_friends uf2 ON uf1.user_id = uf2.friend_id "
            + "WHERE u.city = (SELECT u2.city FROM users u2 WHERE u2.id = :userId) "
            + "AND u.id != :userId")
    List<User> findFriendsOfFriendsByCity(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) > 0 FROM users_friends "
            + "WHERE user_id = :userId AND friend_id = :friendId AND status = 'ACCEPTED'")
    boolean existsByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM users_friends "
            + "WHERE user_id = :userId AND friend_id = :friendId AND status = 'ACCEPTED'")
    void deleteByUserIdAndFriendId(@Param("userId") Long userId,
                                   @Param("friendId") Long friendId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM users_friends "
            +" WHERE user_id = :userId AND status = 'ACCEPTED'")
    int countFriendsByUserIdAndStatus(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT u.* "
            + "FROM users u "
            + "JOIN users_friends uf1 ON u.id = uf1.friend_id "
            + "JOIN users_friends uf2 ON uf1.user_id = uf2.friend_id "
            + "JOIN habit_assign h ON u.id = h.user_id "
            + "WHERE u.city = (SELECT u2.city FROM users u2 WHERE u2.id = :userId) "
            + "AND u.id != :userId "
            + "AND h.status = 'INPROGRESS' OR h.status = 'ACQUIRED'")
    List<User> getAllFriendsByCityHabitCommonFriends(@Param("userId") Long userId);
}