package ImageSearchFX.demo;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector.AbstractConnector;
import ImageSearchFX.ImageSearcher.HTTP.Abstract.Parser.ParserInterface;
import ImageSearchFX.ImageSearcher.HTTP.Pexels.Connector.PexelsConnector;
import ImageSearchFX.ImageSearcher.HTTP.HttpImageSearcher;
import ImageSearchFX.ImageSearcher.HTTP.Pexels.Factory.PexelsHttpImageSearcherFactory;
import ImageSearchFX.ImageSearcher.HTTP.Pexels.Parser.PexelsParser;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Small console application to showcase image search via API
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class ImageSearchDemoStarter
{
    /**
     * Main method
     * @param args Arguments
     * @throws Exception
     */
    public static void main(String args[]) throws Exception
    {
        // AbstractConnector connector = new WikipediaConnector();
        // ParserInterface parser = new WikipediaParser();

        PexelsHttpImageSearcherFactory factory = new PexelsHttpImageSearcherFactory();
        HttpImageSearcher searcher = factory.get();

        // Simulate slow HTTP connections by throttling. Necessary to showcase the async stuff and cancelling
        searcher.enableThrottling();

        Scanner in = new Scanner(System.in);

        System.out.println("Please enter a search term:");
        String input = in.nextLine();

        CompletableFuture<List<String>> future = searcher.getUrlsBySearchTerm(input, 24);
        future.thenAcceptAsync(imageUrls -> {
            for (String imageUrl : imageUrls) {
                System.out.println(imageUrl);
            }
        });

        // Print 3 ticks
        tick(1, 3);

        // Use the following to simulate a cancellation after 750ms, usually to late to cancel though
        //future.cancel(true);

        // Print another 27 ticks, separated because of cancel simulation above
        tick(4, 30);
    }

    /**
     * Prints ticks after 250ms
     * @param start Tick start number
     * @param times Number of ticks
     * @throws InterruptedException
     */
    private static void tick(int start, int times) throws InterruptedException
    {
        for (int i = start; i <= times; i++) {
            TimeUnit.MILLISECONDS.sleep(250);
            System.out.println("Tick " + i + "...");
        }
    }
}
