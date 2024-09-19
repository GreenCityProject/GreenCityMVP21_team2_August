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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @Operation(summary = "Search for new friends by user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @GetMapping("/{userId}/search")
    public ResponseEntity<List<FriendDtoResponse>> searchNewFriends(
            @PathVariable Long userId,
            @RequestParam String searchTerm) {
        return ResponseEntity.status(HttpStatus.OK).body(
                friendService.searchNewFriends(userId, searchTerm));
    }

    @Operation(summary = "Send a friend request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @PostMapping("/{userId}/request/{friendId}")
    public ResponseEntity<Void> sendFriendRequest(
            @PathVariable Long userId,
            @PathVariable Long friendId) {
        friendService.sendFriendRequest(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Cancel a friend request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @DeleteMapping("/{userId}/reject/{friendId}")
    public ResponseEntity<Void> cancelFriendRequest(
            @PathVariable Long userId,
            @PathVariable Long friendId) {
        friendService.cancelFriendRequest(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Accept a friend request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @PostMapping("/{userId}/accept/{friendId}")
    public ResponseEntity<Void> acceptFriendRequest(
            @PathVariable Long userId,
            @PathVariable Long friendId) {
        friendService.acceptFriendRequest(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Find friends by city.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @GetMapping("/{userId}/city")
    public ResponseEntity<List<FriendDtoResponse>> findFriendsByCity(
            @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                friendService.findFriendsByCity(userId));
    }

    @Operation(summary = "Find friends of friends.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @GetMapping("/{userId}/mutual-friends")
    public ResponseEntity<List<FriendDtoResponse>> findFriendsOfFriends(
            @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                friendService.findFriendsOfFriends(userId));
    }

    @Operation(summary = "Find friends of friends in own city.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = "No friends found in the city."),
    })
    @GetMapping("/{userId}/mutual-city-friends")
    public ResponseEntity<List<FriendDtoResponse>> findFriendsOfFriendsByCity(
            @PathVariable Long userId) {
        List<FriendDtoResponse> friends = friendService.findFriendsOfFriendsByCity(userId);
        if (friends.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(friends);
    }

    @Operation(summary = "Delete friend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{userId}/unfriend/{friendId}")
    public ResponseEntity<String> deleteFriend(
            @PathVariable Long userId,
            @PathVariable Long friendId) {
        friendService.deleteFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).body("Friend deleted successfully.");
    }

    @Operation(summary = "Count friends by user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{userId}/friend-count")
    public ResponseEntity<Integer> getFriendsCount(
            @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(friendService.countFriendsByUserId(userId));
    }

    @Operation(summary = "Get friends by recommendations for user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{userId}/friend-recommendations")
    public ResponseEntity<List<FriendDtoResponse>> getRecommendationsFriends(
            @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(friendService.getAllUserFriendsByCityHabitCommonFriends(userId));
    }
}