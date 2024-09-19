package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class FriendDtoResponse {
    private Long id;
    private String name;
    private Double rating;
    private boolean onlineStatus;
    private String profilePicturePath;
    private String city;
    private String userCredo;
    private int amountHabitsInProgress;
    private int amountHabitsAcquired;
    private int amountNewsPublished;
    private int mutualFriends;
}