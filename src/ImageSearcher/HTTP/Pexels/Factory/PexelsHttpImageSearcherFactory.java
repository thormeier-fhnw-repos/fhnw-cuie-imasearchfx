package ImageSearchFX.ImageSearcher.HTTP.Pexels.Factory;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector.AbstractConnector;
import ImageSearchFX.ImageSearcher.HTTP.Abstract.Factory.AbstractHttpImageSearcherFactory;
import ImageSearchFX.ImageSearcher.HTTP.Abstract.Parser.ParserInterface;
import ImageSearchFX.ImageSearcher.HTTP.Pexels.Connector.PexelsConnector;
import ImageSearchFX.ImageSearcher.HTTP.Pexels.Parser.PexelsParser;

/**
 * Generates an instance of HttpImageSearcher that is linked to Pexels API
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class PexelsHttpImageSearcherFactory extends AbstractHttpImageSearcherFactory
{
    @Override
    public AbstractConnector getConnector()
    {
        return new PexelsConnector();
    }

    @Override
    public ParserInterface getParser()
    {
        return new PexelsParser();
    }
}
