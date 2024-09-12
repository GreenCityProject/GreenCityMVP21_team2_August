package greencity.controller;

import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/events/comments")
public class EventCommentController {
    private final EventCommentService eventCommentService;

    @Operation(summary = "Add comment to event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = AddEcoNewsCommentDtoResponse.class))),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{eventId}")
    public ResponseEntity<EventCommentResponseDto> save(
            @PathVariable Long eventId,
            @Valid @RequestBody EventCommentRequestDto request,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventCommentService.save(eventId, request, user));
    }

    @Operation(summary = "Count comments of event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{eventId}/count")
    public int getCountOfComments(@PathVariable Long eventId) {
        return eventCommentService.countOfComments(eventId);
    }

    @Operation(summary = "Get all comments of event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/{eventId}")
    @ApiPageableWithoutSort
    public ResponseEntity<PageableDto<EventCommentResponseDto>> getAllEventComments(
            @Parameter(hidden = true) Pageable pageable,
            @PathVariable Long eventId,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventCommentService.getAllEventComments(pageable, eventId, user));
    }

    @Operation(summary = "Delete comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })

    @DeleteMapping("/{eventCommentId}")
    public ResponseEntity<Object> delete(
            @PathVariable Long eventCommentId,
            @Parameter(hidden = true) Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(eventCommentService.delete(eventCommentId, principal.getName()));
    }
}