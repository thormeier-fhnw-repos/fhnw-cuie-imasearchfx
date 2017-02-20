package ImageSearchFX.ImageSearcher.HTTP.Wikipedia.Connector;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector.AbstractConnector;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Connector class specifically for Wikipedia API
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class WikipediaConnector extends AbstractConnector
{
    /**
     * Protocol to be used for API requests
     */
    private final String protocol = "https";

    /**
     * Host of the API
     */
    private final String host = "en.wikipedia.org";

    /**
     * Path for all image queries
     */
    private final String path = "w/api.php";

    /**
     * Default charset for connection
     */
    private final String charset = "UTF-8";

    @Override
    protected URL buildUrl(String searchTerm, int limit) throws IOException
    {
        String query = String.format(
                "action=query&format=%s&list=allimages&aiprop=url|mime&aifrom=%s&ailimit=%d",
                "xml", // Response format, JSON is rather hard to parse without 3rd party libraries, but xml is quite simple
                URLEncoder.encode(searchTerm, charset), // Encode to prevent highjacking of the URL
                limit // maximum number of images
        );

        return new URL(protocol + "://" + host + "/" + path + "?" + query);
    }

    @Override
    protected Map<String, String> getHeaders()
    {
        return new HashMap<>(); // No extra headers
    }
}
