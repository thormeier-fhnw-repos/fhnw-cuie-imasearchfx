package ImageSearchFX.ImageSearcher.HTTP.Wikipedia.Factory;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector.AbstractConnector;
import ImageSearchFX.ImageSearcher.HTTP.Abstract.Factory.AbstractHttpImageSearcherFactory;
import ImageSearchFX.ImageSearcher.HTTP.Abstract.Parser.ParserInterface;
import ImageSearchFX.ImageSearcher.HTTP.Wikipedia.Connector.WikipediaConnector;
import ImageSearchFX.ImageSearcher.HTTP.Wikipedia.Parser.WikipediaParser;

/**
 * Generates an instance of HttpImageSearcher that is linked to Wikipedia API
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class WikipediaHttpImageSearcherFactory extends AbstractHttpImageSearcherFactory
{
    @Override
    public AbstractConnector getConnector()
    {
        return new WikipediaConnector();
    }

    @Override
    public ParserInterface getParser()
    {
        return new WikipediaParser();
    }
}
