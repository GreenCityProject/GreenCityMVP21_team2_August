package greencity.dto.eventattendee;


import greencity.enums.EventAttendanceStatus;
import greencity.enums.EventAttendeeMark;
import lombok.*;

/**
 * DTO for reading EventAttendee entity.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EventAttendeeDto  {
    private long id;
    private long eventId;
    private String eventTitle;
    private long userId;
    private String userName;
    private String userProfilePicturePath;
    private EventAttendanceStatus status;
    private EventAttendeeMark mark;
}