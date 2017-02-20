package ImageSearchFX.ImageSearcher.Static;

import ImageSearchFX.ImageSearcher.ImageSearcherInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Dummy image searcher, returns a static list of images of the empire state building, ignoring the limit.
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class StaticImageSearcher implements ImageSearcherInterface
{
    /**
     * Executor for async tasks
     */
    private final ExecutorService executor;

    /**
     * Default constructor
     */
    public StaticImageSearcher(ExecutorService executor)
    {
        this.executor = executor;
    }

    @Override
    public CompletableFuture<List<String>> getUrlsBySearchTerm(String searchTerm, int limit) throws RuntimeException
    {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        future.thenApplyAsync((Function<List<String>, Object>) strings -> {
            List<String> imageUrls = new ArrayList<>();

            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/b/bb/Empire_State_Building_from_5th_Avenue.jpg");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/6/6d/Empire_State_Building_timsdad.jpg");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/e/ec/Empire_State_Building_top.jpg");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/a/ab/Empire_State_College_logo.png");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/b/b6/Empire_State_Development_Corporation_%28logo%29.jpg");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/4/49/Empire_State_Games_Collage.jpg");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/5/54/Empire_State_Games_Logo.jpg");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/9/9d/Empire_State_Human.ogg");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/3/32/Empire_State_Pride_Agenda_logo.png");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/5/5f/Empire_State_Railway_Museum_logo.png");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/5/5f/Empire_State_Railway_Museum_logo.png");
            imageUrls.add("https://upload.wikimedia.org/wikipedia/en/5/5f/Empire_State_Railway_Museum_logo.png");

            return imageUrls;
        });

        executor.execute(() -> {
            try {
                future.get();
            } catch (CancellationException e) {
                // Do nothing, as the execution of the future was cancelled
            } catch (Exception e) {
                throw new RuntimeException("Exception thrown while getting images: " + e.getMessage());
            }
        });

        return future;
    }
}
