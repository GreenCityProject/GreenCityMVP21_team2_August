package greencity.exception.exceptions;

public class UserAlreadyAttached extends RuntimeException {
    public UserAlreadyAttached(String message) {
        super(message);
    }
}
