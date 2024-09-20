package greencity.controller;

import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentRequestDto;
import greencity.dto.eventcomment.EventCommentResponseDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/events/{eventId}/comments")
public class EventCommentController {
    private final EventCommentService eventCommentService;

    @Operation(summary = "Add comment to an event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = AddEcoNewsCommentDtoResponse.class))),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventCommentResponseDto> save(
            @PathVariable Long eventId,
            @Valid @RequestBody EventCommentRequestDto request,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventCommentService.save(eventId, request, user));
    }

    @Operation(summary = "Count comments of an event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/count")
    public int getCountOfComments(@PathVariable Long eventId) {
        return eventCommentService.countOfComments(eventId);
    }

    @Operation(summary = "Get all comments of an event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping
    @ApiPageableWithoutSort
    public ResponseEntity<PageableDto<EventCommentResponseDto>> getAllEventComments(
            @Parameter(hidden = true) Pageable pageable,
            @PathVariable Long eventId) {
        Field[] fields = EventCommentResponseDto.class.getDeclaredFields();
        List<String> fieldsNames = Arrays.stream(fields).map(Field::getName).toList();
        for(Sort.Order order : pageable.getSort()) {
            for (Field field: fields){
                if (!fieldsNames.contains(order.getProperty())) {
                    throw new BadRequestException(order.getProperty() + " property not exist");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventCommentService.getAllEventComments(pageable, eventId));
    }

    @Operation(summary = "Get comment to event by event id and comment id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{commentId}")
    @ApiPageableWithoutSort
    public ResponseEntity<EventCommentResponseDto> getByEventCommentId(
            @PathVariable Long eventId,
            @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventCommentService.getByEventCommentId(eventId, commentId));
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
    @PreAuthorize("isAuthenticated()")

    public ResponseEntity<Object> delete(
            @PathVariable Long eventCommentId,
            @Parameter(hidden = true) Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(eventCommentService.delete(eventCommentId, principal.getName()));
    }

    @Operation(summary = "Update comment.")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PatchMapping("/{commentId}")
    public ResponseEntity<Object> update(
            @PathVariable Long commentId,
            @RequestBody @Size(min = 1, max = 8000) String commentText,
            @Parameter(hidden = true) Principal principal) {
        eventCommentService.update(commentId, commentText, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Reply to a comment on an event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = EventCommentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<EventCommentResponseDto> reply(
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @Valid @RequestBody EventCommentRequestDto request,
            @Parameter(hidden = true) @CurrentUser UserVO user) {

        EventCommentResponseDto responseDto = eventCommentService.reply(eventId, commentId, request, user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @Operation(summary = "Count comments of an event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{commentId}/replies/count")
    public int getCountOfReplies(@PathVariable Long eventId,
                                 @PathVariable Long commentId) {
        return eventCommentService.countOfReplies(commentId);
    }

    @Operation(summary = "Get all replies to a specific comment with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{commentId}/replies")
    @ApiPageableWithoutSort
    public ResponseEntity<PageableDto<EventCommentResponseDto>> getAllCommentReplies(
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @Parameter(hidden = true) Pageable pageable) {
        PageableDto<EventCommentResponseDto> replies = eventCommentService.getAllCommentsReplies(commentId, eventId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(replies);
    }

}