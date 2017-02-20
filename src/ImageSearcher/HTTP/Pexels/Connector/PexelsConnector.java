package ImageSearchFX.ImageSearcher.HTTP.Pexels.Connector;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector.AbstractConnector;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Connector class specifically for Pexels API
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class PexelsConnector extends AbstractConnector
{
    /**
     * API key for Pexel API - this is Pascal Thormeier's private one, please don't abuse...
     */
    private final String API_KEY = "563492ad6f9170000100000100b8bc64db6743e976e3e78cfa475b79";

    /**
     * Protocol to be used for API requests
     */
    private final String PROTOCOL = "http";

    /**
     * Host of the API
     */
    private final String HOST = "api.pexels.com";

    /**
     * Path for all image queries
     */
    private final String PATH = "v1/search";

    /**
     * Standard user agent of all requests. Needs to be changed for Pexels since they are returning
     * 403 Forbidden if the user agent is standard Java.
     */
    private final String USER_AGENT = "javafx-custom-control-thormeier";

    /**
     * Constructor, overwrites java's user agent
     */
    public PexelsConnector()
    {
        System.setProperty("http.agent", USER_AGENT);
    }

    @Override
    protected URL buildUrl(String searchTerm, int limit) throws IOException
    {
        String query = String.format(
            "query=%s&per_page=%s",
            URLEncoder.encode(searchTerm, charset),
            limit
        );

        return new URL(PROTOCOL + "://" + HOST + "/" + PATH + "?" + query);
    }

    @Override
    protected Map<String, String> getHeaders()
    {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", API_KEY);

        return headers;
    }
}
