package ImageSearchFX.ImageSearcher.HTTP.Abstract.Factory;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector.AbstractConnector;
import ImageSearchFX.ImageSearcher.HTTP.Abstract.Parser.ParserInterface;
import ImageSearchFX.ImageSearcher.HTTP.HttpImageSearcher;

import java.util.concurrent.Executors;

/**
 * Created by pascal.thormeier on 09.01.2017.
 */
public abstract class AbstractHttpImageSearcherFactory
{
    /**
     * Returns an HttpImageSearcher
     * @return A fully functional HttpImageSearcher
     */
    public HttpImageSearcher get() {
        return new HttpImageSearcher(
            getConnector(),
            getParser(),
            Executors.newSingleThreadExecutor()
        );
    }

    /**
     * Returns a Connector instance
     * @return A connector
     */
    public abstract AbstractConnector getConnector();

    /**
     * Returns a Parser instance
     * @return A parser
     */
    public abstract ParserInterface getParser();
}
