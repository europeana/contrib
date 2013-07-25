package harvesterUI.shared.servletResponseStates;

import java.io.Serializable;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 20-01-2012
 * Time: 16:33
 */
public class RepoxServletResponseStates implements Serializable {

    public enum SaveDataState {
        INVALID_ARGUMENTS,
        sameIdFound,
        NOT_FOUND,
        INCOMPATIBLE_TYPE,
        ERROR_DATABASE,
        ALREADY_EXISTS,
        FTP_CONNECTION_FAILED,
        OTHER
    }

    public enum UrlValidationState{
        URL_MALFORMED,
        URL_NOT_EXISTS,
        HTTP_URL_MALFORMED,
        HTTP_URL_NOT_EXISTS
    }

    public enum ContainerType{
        SEQUENTIAL,
        PARALLEL
    }

    public enum GeneralStates{
        SUCCESS,
        ERROR,
        NO_INTERNET_CONNECTION
    }
}
