package greencity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventAttendanceStatus {
    PLANNED(0),
    ATTENDED(1);

    private final int numberOfSequence;
}
