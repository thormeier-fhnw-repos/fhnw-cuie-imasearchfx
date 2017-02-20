package ImageSearchFX.ImageSearcher.HTTP.Abstract.Parser;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector.AbstractConnector;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Interface for all parsers. Those parse responses of requests done by the HttpImageSearcher
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public interface ParserInterface
{
    /**
     * Parses a given response and resturns a list of image URLs
     * @param responseStream Stream of the response of the connection built by a connector
     * @param limit          Maximum number of images
     * @return List of image URLs as Strings
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public List<String> parseResponse(InputStream responseStream, int limit) throws ParserConfigurationException, IOException, SAXException;
}
