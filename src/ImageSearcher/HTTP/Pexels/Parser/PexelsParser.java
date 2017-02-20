package ImageSearchFX.ImageSearcher.HTTP.Pexels.Parser;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Parser.ParserInterface;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser specifically for Pexels API
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class PexelsParser implements ParserInterface
{
    @Override
    public List<String> parseResponse(InputStream responseStream, int limit) throws ParserConfigurationException, IOException, SAXException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));

        String line, fullResponse = "";

        while ((line = reader.readLine()) != null) {
            fullResponse += line;
        }

        List<String> urls = new ArrayList<>();

        // Since Java doesn't come with JSON parser, simply use regex here to get out the URLs
        // String in quotes (i.e. original, square, etc.) is image type in response.
        Pattern pattern = Pattern.compile("[\"]?square[\"]?:[\\s]*\"([-a-zA-Z0-9@:%_\\+.~#?&//=]*)\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fullResponse);

        int foundImages = 0;
        while (matcher.find()) {
            urls.add(fullResponse.substring(matcher.start(1), matcher.end(1)));

            foundImages++;

            if (foundImages == limit) { // We don't need more images, limit is reached
                break;
            }
        }

        return urls;
    }
}
