package ImageSearchFX.ImageSearcher.HTTP;

/**
 * Exception that is thrown if *anything* went wrong during an API call
 */
public class HttpAPIErrorException extends RuntimeException
{
    /**
     * Constructor
     * @param message Message of the exception
     */
    public HttpAPIErrorException(String message) {
        super(message);
    }
}
