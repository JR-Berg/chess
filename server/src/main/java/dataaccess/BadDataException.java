package dataaccess;

public class BadDataException extends RuntimeException {
    public BadDataException(String message) {
        super(message);
    }
}
