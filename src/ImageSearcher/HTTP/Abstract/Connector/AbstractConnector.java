package ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Abstract connector to gather same code of all connectors.
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public abstract class AbstractConnector
{
    /**
     * Default charset for connection
     */
    public final String charset = "UTF-8";

    /**
     * Builds a connection object that can be used to query an API
     * @param searchTerm Phrase to search for
     * @param limit      Maximum number of images
     * @return Connection object, ready to be fired
     * @throws IOException
     */
    public HttpURLConnection buildConnection(String searchTerm, int limit) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) buildUrl(searchTerm, limit).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);

        getHeaders().forEach(connection::setRequestProperty);

        return connection;
    }

    /**
     * Build a URL for this connector
     * @param searchTerm Phrase to search for
     * @param limit      Maximum number of images
     * @return URL to build a connection with
     */
    protected abstract URL buildUrl(String searchTerm, int limit) throws IOException;

    /**
     * Creates a list of headers to apply to the request
     * @return Map of headers
     */
    protected abstract Map<String, String> getHeaders();
}
