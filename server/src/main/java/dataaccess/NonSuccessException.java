package dataaccess;
//This is the error I'll throw till I can make more specific errors.
public class NonSuccessException extends RuntimeException {
    public NonSuccessException(String message) {
        super(message);
    }
}
