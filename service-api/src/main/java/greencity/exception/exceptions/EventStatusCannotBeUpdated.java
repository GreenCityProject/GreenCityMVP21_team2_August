package greencity.exception.exceptions;

/**
 * The exception is thrown when the event status cannot be updated.
 */
public class EventStatusCannotBeUpdated extends IllegalArgumentException {
    public EventStatusCannotBeUpdated(final String s) {
        super(s);
    }
}
