package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ImageListValidation;
import greencity.annotations.ValidAddEventDtoRequest;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import greencity.dto.event.UpdateEventDTO;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Add new event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AddEventDtoResponse> save(
            @Parameter(description = SwaggerExampleModel.ADD_EVENT, required = true)
            @ValidAddEventDtoRequest @RequestPart AddEventDtoRequest addEventDtoRequest,
            @Parameter(description = "Images of the event")
            @RequestPart(required = false) @ImageListValidation List<MultipartFile> images,
            @Parameter(hidden = true) Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                eventService.saveEvent(addEventDtoRequest, images, principal.getName()));
    }

    @Operation(summary = "Update event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @PutMapping(path = "/update", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AddEventDtoResponse> update(
            @Parameter(description = SwaggerExampleModel.UPDATE_EVENT,
                    required = true) @RequestPart @Valid UpdateEventDTO updateEventDTO,
            @Parameter(description = "Image of event") @RequestPart(
                    required = false) @ImageListValidation List<MultipartFile> images,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(
                eventService.updateEvent(updateEventDTO, images, user));
    }

    @Operation(summary = "Delete event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventId,
                                         @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventService.delete(eventId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
