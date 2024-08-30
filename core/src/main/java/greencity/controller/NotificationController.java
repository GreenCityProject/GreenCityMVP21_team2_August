package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.notification.NotificationCreateDto;
import greencity.dto.notification.NotificationReadDto;
import greencity.dto.user.UserVO;
import greencity.exception.handler.ExceptionResponse;
import greencity.filters.SearchCriteria;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationController {
    private NotificationService notificationService;

    @Operation(summary = "Create a notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = NotificationReadDto.class)))
    })
    @PostMapping
    public ResponseEntity<NotificationReadDto> createNotification(@Valid @RequestBody NotificationCreateDto notificationCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(notificationCreateDto));
    }

    @Operation(summary = "Get 5 last unread notifications for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/unread/latest")
    public ResponseEntity<List<NotificationReadDto>> getFiveUnreadNotifications(@Parameter(hidden = true) @CurrentUser UserVO userVO) {
        List<NotificationReadDto> notifications = notificationService.getFiveUnreadNotifications(userVO.getId());
        return ResponseEntity.ok()
                .body(notifications);
    }

    @Operation(summary = "Get all notifications for the current user")
    @PageableAsQueryParam
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<Page<NotificationReadDto>> getAllNotifications(@Parameter(hidden = true) @CurrentUser UserVO userVO,
                                                                         @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok().body(notificationService.getAllNotifications(pageable, userVO.getId()));
    }

    @Operation(summary = "Get count of unread notifications for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/countUnread")
    public ResponseEntity<Long> countUnreadNotifications(@Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.ok().body(notificationService.countUnreadNotifications(userVO.getId()));
    }

    @Operation(summary = "Get notifications by filter for the current user")
    @PageableAsQueryParam
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/filter")
    public Page<NotificationReadDto> getNotificationsByFilter(
            @Parameter(hidden = true) @CurrentUser UserVO userVO,
            @Parameter(hidden = true) Pageable pageable,
            @Parameter(allowReserved = true, description = "Criteria query which is used in format {key}{operator}{value}. " +
                                                           "If there are several criteria, then use ',' between") @RequestParam String criteriaFilter
    ) {
        final List<SearchCriteria> searchCriteria = parseSearchCriteria(criteriaFilter);
        return notificationService.getByFilter(userVO.getId(), pageable, searchCriteria);
    }

    private List<SearchCriteria> parseSearchCriteria(final String criteriaFilter) {
        final Pattern pattern = Pattern.compile("(\\w+)(:)(\\w+),");
        final Matcher matcher = pattern.matcher(criteriaFilter + ",");
        final List<SearchCriteria> searchCriteria = new ArrayList<>();
        while (matcher.find()) {
            searchCriteria.add(SearchCriteria.builder()
                    .key(matcher.group(1))
                    .type(matcher.group(2))
                    .value(matcher.group(3))
                    .build());
        }
        return searchCriteria;
    }

    @Operation(summary = "Mark notification as viewed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PatchMapping("/view/{notificationId}")
    public ResponseEntity<Void> markAsViewed(@PathVariable long notificationId, @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        notificationService.markAsViewed(notificationId, userVO.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Mark notification as unviewed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PatchMapping("/unview/{notificationId}")
    public ResponseEntity<Void> markAsUnviewed(@PathVariable long notificationId, @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        notificationService.markAsUnviewed(notificationId, userVO.getId());
        return ResponseEntity.ok().build();
    }
}
