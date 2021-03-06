package diplomna.error.exception;

public class AlreadyExistsException extends RuntimeException {
    private String fieldName;

    public String getField(){
        return this.fieldName;
    }

    public AlreadyExistsException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public AlreadyExistsException(String fieldName, String message, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
    }
}
