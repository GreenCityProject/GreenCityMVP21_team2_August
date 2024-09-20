package greencity.repository;

import greencity.entity.Event;
import greencity.entity.EventAttendee;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.enums.EventAttendanceStatus;
import greencity.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ModelUtils {
    public static Language getLanguage() {
        return Language.builder()
                .id(1L)
                .code("ua")
                .build();
    }

    public static List<String> getAllLanguages() {
        List<String> languages = new ArrayList<>();
        languages.add("ua");
        languages.add("en");
        languages.add("ru");
        return languages;
    }

    public static User getUser() {
        final User user = new User();
        user.setFirstName("FirstName");
        user.setName("Username");
        user.setEmail("user@gmail.com");
        user.setCity("Kyiv");
        user.setRole(Role.ROLE_ADMIN);
        user.setRefreshTokenKey("token");
        user.setDateOfRegistration(LocalDateTime.now());
        return user;
    }

    public static Event getEvent1() {
        return Event.builder()
                .title("Title1")
                .description("Description1")
                .author(User.builder().id(1L).build())
                .build();
    }

    public static Event getEvent2() {
        return Event.builder()
                .title("Title2")
                .description("Description2")
                .author(User.builder().id(1L).build())
                .build();
    }

    public static EventAttendee getEventAttendee1() {
        return EventAttendee.builder()
                .user(User.builder().id(1L).build())
                .event(Event.builder().id(1L).build())
                .status(EventAttendanceStatus.PLANNED)
                .build();
    }

    public static EventAttendee getEventAttendee2() {
        return EventAttendee.builder()
                .user(User.builder().id(1L).build())
                .event(Event.builder().id(2L).build())
                .status(EventAttendanceStatus.ATTENDED)
                .build();
    }
}