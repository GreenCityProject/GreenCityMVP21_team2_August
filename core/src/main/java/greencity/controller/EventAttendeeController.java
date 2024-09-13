package greencity.controller;

import greencity.annotations.RequiredPositive;
import greencity.dto.eventattendee.EventAttendeeCreateDTO;
import greencity.dto.eventattendee.EventAttendeeDto;
import greencity.service.EventAttendeeService;
import greencity.dto.eventattendee.EventAttendeeUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event-attendees")
@AllArgsConstructor
public class EventAttendeeController {
    private EventAttendeeService eventAttendeeService;

    /**
     * Creates a new event attendee.
     *
     * @param eventAttendeeCreateDTO The data transfer object containing the details of the new event attendee.
     * @return A ResponseEntity with the created event attendee and a 201 status code (Created).
     */
    @Operation(summary = "Create event attendee")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created event attendee"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<?> createEventAttendee(@Valid @RequestBody EventAttendeeCreateDTO eventAttendeeCreateDTO) {
        return new ResponseEntity<>(eventAttendeeService.createEventAttendee(eventAttendeeCreateDTO), HttpStatus.CREATED);
    }

    /**
     * Retrieves a list of event attendees for a given user ID.
     *
     * @param userId The ID of the user for whom the event attendees are to be retrieved.
     * @return A ResponseEntity with the list of event attendees and a 200 status code (OK).
     */
    @Operation(summary = "Get event attendees by user id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getEventAttendeesByUserId(@PathVariable @RequiredPositive Long userId) {
        return new ResponseEntity<>(eventAttendeeService.getEventAttendeesByUserId(userId), HttpStatus.OK);
    }

    /**
     * Retrieves a list of event attendees for a given event ID.
     *
     * @param eventId The ID of the event for which the event attendees are to be retrieved.
     * @return A ResponseEntity with the list of event attendees and a 200 status code (OK).
     */
    @Operation(summary = "Get event attendees by event id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<?> getEventAttendeesByEventId(@PathVariable @RequiredPositive Long eventId) {
        return new ResponseEntity<>(eventAttendeeService.getEventAttendeesByEventId(eventId), HttpStatus.OK);
    }

    /**
     * Updates an existing event attendee.
     *
     * @param id The ID of the event attendee to be updated.
     * @param eventAttendeeUpdateDto The data transfer object containing the updated details of the event attendee.
     * @return A ResponseEntity with the updated event attendee and a 200 status code (OK).
     */
    @Operation(summary = "Update event attendee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable @RequiredPositive Long id, @RequestBody @Valid EventAttendeeUpdateDto eventAttendeeUpdateDto) {
        final EventAttendeeDto eventAttendee = eventAttendeeService.update(id, eventAttendeeUpdateDto);
        return new ResponseEntity<>(eventAttendee, HttpStatus.OK);
    }

    /**
     * Deletes an event attendee by ID.
     *
     * @param id The ID of the event attendee to be deleted.
     * @return A ResponseEntity with a 200 status code (OK).
     */
    @Operation(summary = "Delete event attendee")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventAttendee(@PathVariable @RequiredPositive Long id) {
        eventAttendeeService.deleteEventAttendee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Deletes all event attendees for a given event ID.
     *
     * @param eventId The ID of the event for which the event attendees are to be deleted.
     * @return A ResponseEntity with a 200 status code (OK).
     */
    @Operation(summary = "Delete all event attendees by event id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/by-event/{eventId}")
    public ResponseEntity<?> deleteEventAttendeesByEventId(@PathVariable @RequiredPositive Long eventId) {
        eventAttendeeService.deleteEventAttendeesByEventId(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}