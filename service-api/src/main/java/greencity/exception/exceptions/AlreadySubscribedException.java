package greencity.exception.exceptions;

public class AlreadySubscribedException extends IllegalArgumentException {
    public AlreadySubscribedException() {
    }

    public AlreadySubscribedException(String s) {
        super(s);
    }

    public AlreadySubscribedException(Throwable cause) {
        super(cause);
    }
}
