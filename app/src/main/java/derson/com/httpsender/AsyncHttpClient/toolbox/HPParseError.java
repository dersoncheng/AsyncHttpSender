package derson.com.httpsender.AsyncHttpClient.toolbox;

/**
 * Created by chengli on 15/7/13.
 */
public class HPParseError extends HPHttpError{
    public HPParseError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public HPParseError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }
}
