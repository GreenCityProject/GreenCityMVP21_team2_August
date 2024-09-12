package greencity.controller;

import greencity.annotations.RequiredPositive;
import greencity.dto.eventattendee.EventAttendeeCreateDTO;
import greencity.dto.eventattendee.EventAttendeeDto;
import greencity.service.EventAttendeeService;
import greencity.dto.eventattendee.EventAttendeeUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Create event attendee")
    @PostMapping
    public ResponseEntity<?> createEventAttendee(@Valid @RequestBody EventAttendeeCreateDTO eventAttendeeCreateDTO) {
        return new ResponseEntity<>(eventAttendeeService.createEventAttendee(eventAttendeeCreateDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get event attendees by user id")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getEventAttendeesByUserId(@PathVariable @RequiredPositive Long userId) {
        return new ResponseEntity<>(eventAttendeeService.getEventAttendeesByUserId(userId), HttpStatus.OK);
    }

    @Operation(summary = "Get event attendees by event id")
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<?> getEventAttendeesByEventId(@PathVariable @RequiredPositive Long eventId) {
        return new ResponseEntity<>(eventAttendeeService.getEventAttendeesByEventId(eventId), HttpStatus.OK);
    }

    @Operation(summary = "Update event attendee")
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable @RequiredPositive Long id, @RequestBody @Valid EventAttendeeUpdateDto eventAttendeeUpdateDto) {
        final EventAttendeeDto eventAttendee = eventAttendeeService.update(id, eventAttendeeUpdateDto);
        return new ResponseEntity<>(eventAttendee, HttpStatus.OK);
    }

    @Operation(summary = "Delete event attendee")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventAttendee(@PathVariable @RequiredPositive Long id) {
        eventAttendeeService.deleteEventAttendee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete all event attendees by event id")
    @DeleteMapping("/by-event/{eventId}")
    public ResponseEntity<?> deleteEventAttendeesByEventId(@PathVariable @RequiredPositive Long eventId) {
        eventAttendeeService.deleteEventAttendeesByEventId(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}