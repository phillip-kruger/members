package technology.overcast.member;

/**
 * Exception when trying to create a member that exist already
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class MemberExistAlreadyException extends Exception {

    public MemberExistAlreadyException() {
    }

    public MemberExistAlreadyException(String message) {
        super(message);
    }

    public MemberExistAlreadyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberExistAlreadyException(Throwable cause) {
        super(cause);
    }

    public MemberExistAlreadyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
