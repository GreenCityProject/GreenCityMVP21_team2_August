package greencity.service;

import greencity.dto.eventattendee.EventAttendeeCreateDTO;
import greencity.dto.eventattendee.EventAttendeeDto;
import greencity.dto.eventattendee.EventAttendeeUpdateDto;

import java.util.List;

public interface EventAttendeeService {
    /**
     * Returns a list of event attendees by event ID.
     *
     * @param id the event ID
     * @return a list of event attendees
     */
    List<EventAttendeeDto> getEventAttendeesByEventId(long id);

    /**
     * Returns a list of event attendees by user ID.
     *
     * @param id the user ID
     * @return a list of event attendees
     */
    List<EventAttendeeDto> getEventAttendeesByUserId(long id);

    /**
     * Creates an event attendee.
     *
     * @param eventAttendeeCreateDTO the event attendee data
     * @return the created event attendee
     */
    EventAttendeeDto createEventAttendee(EventAttendeeCreateDTO eventAttendeeCreateDTO);

    /**
     * Updates the 'status' field of an event attendee.
     * @param id the unique identifier of the event attendee
     * @param eventAttendeeUpdateDto the new value
     */
    EventAttendeeDto update(long id, EventAttendeeUpdateDto eventAttendeeUpdateDto);

    /**
     * Deletes an event attendee based on the provided ID.
     *
     * @param id the unique identifier of the event attendee to be deleted
     */
    void deleteEventAttendee(long id);

    /**
     * Deletes all event attendees by event ID.
     *
     * @param eventId the unique identifier of the event
     */
    void deleteEventAttendeesByEventId(long eventId);
}
