package derson.com.httpsender.AsyncHttpClient.toolbox;

/**
 * Created by chengli on 15/7/12.
 */
public class HPHttpError extends Exception {

    public HPHttpError(){

    }

    public HPHttpError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public HPHttpError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }

    public HPHttpError(Throwable cause) {
        super(cause);
    }
}
