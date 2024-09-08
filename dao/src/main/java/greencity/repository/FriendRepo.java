package greencity.repository;

import greencity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

   // @Query("SELECT COUNT(f) FROM User u JOIN u.friends f WHERE u.id = :userId")
    int countByUserId(@Param("userId") Long userId);

}