package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageableWithLocale;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.user.FriendDtoResponse;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    @Operation(summary = "Get friends by user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @GetMapping("/{userId}")
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<FriendDtoResponse>> getAllFriendsByUserID(
            @PathVariable Long userId,
            @Parameter(hidden = true)
            @PageableDefault(size = 6, sort = "u.rating", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
                friendService.getAllUserFriends(userId, pageable));
    }

    @Operation(summary = "Get info about a friend by user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @GetMapping("/user/{userId}")
    @ApiLocale
    public ResponseEntity<FriendDtoResponse> getFriendProfile(
            @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                friendService.getFriendProfile(userId));
    }

    @GetMapping("/{userId}/count")
    public ResponseEntity<Integer> getTotalFriendsCount(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(friendService.countUserById(userId));
    }

}