package ImageSearchFX.ImageSearcher.HTTP;

import ImageSearchFX.ImageSearcher.HTTP.Abstract.Connector.AbstractConnector;
import ImageSearchFX.ImageSearcher.HTTP.Abstract.Parser.ParserInterface;
import ImageSearchFX.ImageSearcher.ImageSearcherInterface;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Image searcher to perfom an HTTP request with. Takes a connector and a parser, those need to be compatible.
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class HttpImageSearcher implements ImageSearcherInterface
{
    /**
     * Connector to response
     */
    private AbstractConnector connector;

    /**
     * Parser for response
     */
    private ParserInterface parser;

    /**
     * Executor for futures
     */
    private ExecutorService executor;

    /**
     * Flag for throttling of responses to simulate slow HTTP connections, nice for debugging
     */
    private boolean throttle = false;

    /**
     * Constructor
     * @param connector Performs the query
     * @param parser    Parses the response
     * @param executor  Executor service for futures
     */
    public HttpImageSearcher(AbstractConnector connector, ParserInterface parser, ExecutorService executor)
    {
        this.connector = connector;
        this.parser = parser;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<List<String>> getUrlsBySearchTerm(String searchTerm, int limit) throws RuntimeException
    {
        // Increase limit by 10, do have at least `limit` images
        final int queryLimit = limit + 10;
        final int definiteLimit = limit;

        // An async future is used here to have the possibility to interrupt a search, so images don't get overwritten
        // multiple times while typing. Only the last search is relevant.
        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
            try {
                if (throttle) {
                    TimeUnit.MILLISECONDS.sleep(500 + (int)(Math.random() * 1500));
                }

                HttpURLConnection connection = connector.buildConnection(searchTerm, queryLimit);
                InputStream responseStream = performRequest(connection);

                List<String> urls = parser.parseResponse(responseStream, definiteLimit);

                // Disconnect again to not run into memory issues
                connection.disconnect();

                return urls;
            } catch (Exception e) {
                throw new HttpAPIErrorException("Exception thrown while getting images: " + e.getMessage());
            }
        });

        // Execute the future
        executor.execute(() -> {
            try {
                future.get();
            } catch (CancellationException e) {
                // Do nothing, as the execution of the future was cancelled, which is
                // expected behaviour when a new search is triggered
            } catch (Exception e) {
                throw new HttpAPIErrorException(e.getMessage());
            }
        });

        return future;
    }


    /**
     * Performs a request and returns its response body
     * @param connection The connection to perform the request from
     * @return Response body as string
     * @throws IOException
     */
    protected InputStream performRequest(HttpURLConnection connection) throws IOException
    {
        connection.connect();

        if (connection.getResponseCode() >= 400) {
            throw new HttpAPIErrorException("HTTP Error " + connection.getResponseCode() + ": " + connection.getResponseMessage());
        }

        return connection.getInputStream();
    }

    /**
     * Sets throttling flag to true, simulates slow HTTP connections
     */
    public void enableThrottling()
    {
        throttle = true;
    }

    /**
     * Disables throttling, stops artificial slowdown of HTTP connections
     */
    public void disableThrottling()
    {
        throttle = false;
    }
}
