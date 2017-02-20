package ImageSearchFX.ImageSearcher.HTTP.Wikipedia.Parser;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector.AbstractConnector;
import ImageSearchFX.ImageSearcher.HTTP.Abstract.Parser.ParserInterface;
import ImageSearchFX.ImageSearcher.HTTP.Wikipedia.Connector.WikipediaConnector;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser specifically for Wikipedia API
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class WikipediaParser implements ParserInterface
{
    @Override
    public List<String> parseResponse(InputStream responseStream, int limit) throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(responseStream);

        // Parse the XML DOM returned by Wikipedia API
        NodeList imageNodes = document
            .getDocumentElement().getChildNodes() // <api>
            .item(1).getChildNodes() // <query>
            .item(0).getChildNodes() // <allimages>
        ;

        List<String> imageUrls = new ArrayList<>();

        int foundImages = 0;
        for (int i = 0; i < imageNodes.getLength(); i++) {
            Node imageNode = imageNodes.item(i);
            NamedNodeMap attributes = imageNode.getAttributes();

            // Filter non-images by mime-type. Mostly it will encounter .ogg files, which are sound files, only JPG
            if (!attributes.getNamedItem("mime").getNodeValue().contains("image/jpg")
                    && !attributes.getNamedItem("mime").getNodeValue().contains("image/jpeg")) {
                continue;
            }

            imageUrls.add(attributes.getNamedItem("url").getNodeValue());
            foundImages++;

            if (foundImages == limit) { // We don't need more images, limit is reached
                break;
            }
        }

        return imageUrls;
    }
}
