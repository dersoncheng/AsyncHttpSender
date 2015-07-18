package derson.com.httpsender.AsyncHttpClient.toolbox;

/**
 * Created by chengli on 15/7/13.
 */
public class HPNetworkError extends HPHttpError{

    public HPNetworkError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public HPNetworkError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }
}
