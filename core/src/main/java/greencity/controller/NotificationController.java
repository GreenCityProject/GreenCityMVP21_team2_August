package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
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
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles all the RESTful API requests related to notifications.
 */
@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
@Validated
public class NotificationController {
    private NotificationService notificationService;

    /**
     * Creates a new notification.
     *
     * @param notificationCreateDto the notification to be created
     * @param locale the locale of the notification
     * @return the created notification
     */
    @Operation(summary = "Create a notification")
    @ApiLocale
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = NotificationReadDto.class)))
    })
    @PostMapping
    public ResponseEntity<NotificationReadDto> createNotification(@Valid @RequestBody NotificationCreateDto notificationCreateDto,
                                                                  @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(notificationCreateDto, locale.getLanguage()));
    }

    /**
     * Gets the count of unread notifications for the current user.
     *
     * @param userVO the current user
     * @return the count of unread notifications
     */
    @Operation(summary = "Get count of unread notifications for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/countUnread")
    public ResponseEntity<Long> countUnreadNotifications(@Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.ok().body(notificationService.countUnreadNotifications(userVO.getId()));
    }

    /**
     * Gets the 5 last unread notifications for the current user.
     *
     * @param userVO the current user
     * @param locale the locale of the notifications
     * @return the 5 last unread notifications
     */
    @Operation(summary = "Get 5 last unread notifications for the current user")
    @ApiLocale
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/unread/latest")
    public ResponseEntity<List<NotificationReadDto>> getFiveUnreadNotifications(@Parameter(hidden = true) @CurrentUser UserVO userVO,
                                                                                @Parameter(hidden = true) @ValidLanguage Locale locale) {
        List<NotificationReadDto> notifications = notificationService.getFiveUnreadNotifications(userVO.getId(), locale.getLanguage());
        return ResponseEntity.ok()
                .body(notifications);
    }

    /**
     * Gets all notifications for the current user.
     *
     * @param userVO the current user
     * @param pageable the pagination parameters
     * @param locale the locale of the notifications
     * @return all notifications for the current user
     */
    @Operation(summary = "Get all notifications for the current user")
    @ApiLocale
    @PageableAsQueryParam
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<Page<NotificationReadDto>> getAllNotifications(@Parameter(hidden = true) @CurrentUser UserVO userVO,
                                                                         @Parameter(hidden = true) Pageable pageable,
                                                                         @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.ok().body(notificationService.getAllNotifications(pageable, userVO.getId(), locale.getLanguage()));
    }

    /**
     * Gets notifications by filter for the current user.
     *
     * @param userVO the current user
     * @param pageable the pagination parameters
     * @param criteriaFilter the filter criteria
     * @param locale the locale of the notifications
     * @return notifications by filter for the current user
     */
    @Operation(summary = "Get notifications by filter for the current user")
    @ApiLocale
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
                                                           "If there are several criteria, then use ',' between") @RequestParam String criteriaFilter,
            @Parameter(hidden = true) @ValidLanguage Locale locale
    ) {
        final List<SearchCriteria> searchCriteria = parseSearchCriteria(criteriaFilter);
        return notificationService.getByFilter(userVO.getId(), pageable, searchCriteria, locale.getLanguage());
    }

    @Operation(summary = "Get notification by id")
    @ApiLocale
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = NotificationReadDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationReadDto> getNotificationById(@PathVariable @Positive Long id,
                                                                  @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.ok().body(notificationService.getById(id, locale.getLanguage()));
    }

    /**
     * Parses a string containing search criteria and converts it into a list of {@link SearchCriteria} objects.
     * The input string should contain criteria in the format "key:type:value," where each criterion is separated by a comma.
     * This method uses a regular expression to match each criterion and extract the key, type, and value components.
     *
     * @param criteriaFilter the string containing the search criteria to be parsed, formatted as "key:type:value,".
     * @return a list of {@link SearchCriteria} objects constructed from the parsed criteria in the input string.
     */
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

    /**
     * Marks a notification as viewed.
     *
     * @param notificationId the ID of the notification
     * @param userVO the current user
     * @return a successful response
     */
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

    /**
     * Marks a notification as unviewed.
     *
     * @param notificationId the ID of the notification
     * @param userVO the current user
     * @return a successful response
     */
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
