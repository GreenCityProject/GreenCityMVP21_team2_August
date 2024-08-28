package greencity.controller;

import greencity.constant.AppConstant;
import greencity.dto.newssubscription.NewsSubscriptionDto;
import greencity.exception.handler.ExceptionResponse;
import greencity.service.NewsSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/newsSubscriptions")
@AllArgsConstructor
public class NewsSubscriptionController {
    private NewsSubscriptionService newsSubscriptionService;

    @Operation(summary = "Get all news subscriptions",
            description = "Returns a list of all news subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden access",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @GetMapping
    public ResponseEntity<List<NewsSubscriptionDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(newsSubscriptionService.findAll());
    }

    @Operation(summary = "Subscribe to a news subscription",
            description = "Returns a news subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = NewsSubscriptionDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping(value = "/subscribe")
    public ResponseEntity<NewsSubscriptionDto> subscribe(@RequestBody @Valid NewsSubscriptionDto newsSubscriptionDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newsSubscriptionService.subscribe(newsSubscriptionDto));
    }

    @Operation(summary = "Unsubscribe from a news subscription",
            description = "Returns a news subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = NewsSubscriptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),

    })
    @PostMapping("/unsubscribe")
    public ResponseEntity<NewsSubscriptionDto> unsubscribe(
            @Parameter(description = "Token which is used to unsubscribe from a news subscription. It is received via email") @RequestParam String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(newsSubscriptionService.unsubscribe(token));
    }

    @Operation(summary = "Check if a user is subscribed to a news subscription",
            description = "Returns a boolean indicating whether a user is subscribed to a news subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden access",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @GetMapping("/isSubscribed")
    public ResponseEntity<Boolean> isSubscribed(@RequestParam @Email(regexp = AppConstant.VALID_EMAIL) String email) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(newsSubscriptionService.isSubscribed(email));
    }
}
