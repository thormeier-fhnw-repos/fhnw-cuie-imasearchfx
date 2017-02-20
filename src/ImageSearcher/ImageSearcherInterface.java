package ImageSearchFX.ImageSearcher;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for ImageSearchers, used to query a list of images, either static, by file system or via API call, etc.
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public interface ImageSearcherInterface
{
    /**
     * Returns a list of image URLs by a given search term
     * @param searchTerm The term to search for
     * @param limit      Maximum number of image URLs returned
     * @return A list of image URLs as Strings
     * @throws RuntimeException In case something goes wrong while fetching image URLs
     */
    CompletableFuture<List<String>> getUrlsBySearchTerm(String searchTerm, int limit) throws RuntimeException;
}
