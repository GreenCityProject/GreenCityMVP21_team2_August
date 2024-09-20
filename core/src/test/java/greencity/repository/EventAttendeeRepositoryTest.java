package greencity.repository;

import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;

import greencity.entity.Event;
import greencity.entity.EventAttendee;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static greencity.repository.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GreenCityApplication.class)
class EventAttendeeRepositoryTest extends IntegrationTestBase {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventAttendeeRepository eventAttendeeRepository;

    private long userId;

    private long event1Id;

    private long event2Id;

    @BeforeEach
    void setUp() {
        final User user = userRepo.save(getUser());
        userId = user.getId();

        final Event event1 = eventRepository.save(getEvent1());
        event1.setAuthor(user);
        event1Id = event1.getId();

        final Event event2 = eventRepository.save(getEvent2());
        event2.setAuthor(user);
        event2Id = event2.getId();

        final EventAttendee eventAttendee1 = getEventAttendee1();
        eventAttendee1.setEvent(event1);
        eventAttendee1.setUser(user);
        eventAttendeeRepository.save(eventAttendee1);

        final EventAttendee eventAttendee2 = getEventAttendee2();
        eventAttendee2.setEvent(event2);
        eventAttendee2.setUser(user);
        eventAttendeeRepository.save(eventAttendee2);
    }

    @Test
    void testExistsByEventIdAndUserId_Exists() {
        assertTrue(eventAttendeeRepository.existsByEventIdAndUserId(event1Id, userId));
    }

    @Test
    void testExistsByEventIdAndUserId_NotExists() {
        assertFalse(eventAttendeeRepository.existsByEventIdAndUserId(20L, 99L));
    }

    @Test
    void testDeleteAll_ByEventId() {
        eventAttendeeRepository.deleteAllByEvent_Id(event1Id);
        assertEquals(1, eventAttendeeRepository.findAll().size());
    }

    @Test
    void testFindAllByUser_Id() {
        int actual = eventAttendeeRepository.findAllByEvent_Id(event1Id).size();
        assertEquals(1, actual);
    }

    @Test
    void testFindAllByEvent_Id() {
        int actual = eventAttendeeRepository.findAllByUser_Id(userId).size();
        assertEquals(2, actual);
    }
}
