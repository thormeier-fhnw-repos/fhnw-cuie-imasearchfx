package ImageSearchFX;

import ch.fhnw.cuie.ImageSearchFX.ImageSearcher.ImageSearcherInterface;
import ch.fhnw.cuie.ImageSearchFX.TileSkin.TileSkin;
import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Image search control: Finding images by a given searchterm via a given searcher implementation
 * and select a single one
 *
 * Has two major properties:
 *  - imageUrlProperty: Changes when a new image is chosen from a list of found images
 *  - searchTermProperty: Change to trigger a new search
 *
 *  For the Buildings app it is recommended, that the name field of the building is bound to searchTermProperty
 *  and that, as soon as the imageUrlProperty changes, also the respective image is changed. See DemoPane for
 *  working example.
 *
 *  @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class ImageSearchControl extends Control
{
    /**
     * Current URL chosen
     */
    private StringProperty imageUrlProperty = new SimpleStringProperty();

    /**
     * Property for the current search term
     */
    private StringProperty searchTermProperty = new SimpleStringProperty();

    /**
     * Property that stores all image URLs
     */
    private ObjectProperty<List<String>> imageListProperty = new SimpleObjectProperty<>();

    /**
     * Image searcher to get images to display
     */
    private ImageSearcherInterface imageSearcher;

    /**
     * Future for async requests
     */
    private CompletableFuture<List<String>> future;

    /**
     * Simple boolean property that is used as a trigger for searching behaviour on skin side
     */
    private BooleanProperty isSearching = new SimpleBooleanProperty(false);

    /**
     * Maximum number of images shown
     */
    private int limit;

    /**
     * Maximum number of images per row
     */
    private static int MAX_PER_ROW = 8;

    /**
     * Default constructor
     * @param imageSearcher Instance of ImageSearcherInterface, either StaticImageSearcher or HttpImageSearcher
     * @param limit         Maximum number of images
     */
    public ImageSearchControl(ImageSearcherInterface imageSearcher, int limit)
    {
        this.imageSearcher = imageSearcher;
        this.limit = limit;

        // Generate an already completed future to avoid checks for null
        this.future = CompletableFuture.completedFuture(new ArrayList<>());

        initializeSelf();
        addValueChangeListeners();
    }

    /**
     * Basic setup of this control itself
     */
    private void initializeSelf()
    {
        getStyleClass().add("image-search-control");
    }

    /**
     * Adds all value change listeners
     */
    private void addValueChangeListeners()
    {
        searchTermProperty.addListener(((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                performSearch(newValue);
            }
        }));
    }

    /**
     * Performs an actual search
     * @param searchTerm The new searchterm
     */
    private void performSearch(String searchTerm)
    {
        if (!future.isCancelled() && !future.isDone()) {
            future.cancel(true); // Interrupt current request, so that callback doesn't get executed
        }

        // Toggle it once on and back off again, so that the next hit will also trigger it once
        isSearchingProperty().setValue(true);
        isSearchingProperty().setValue(false);

        future = imageSearcher.getUrlsBySearchTerm(searchTerm, limit);
        future.thenAcceptAsync((list) -> {
            imageListProperty.setValue(new ArrayList<>()); // Empty first to trigger change, even if list hasn't changed
            imageListProperty.setValue(list);
        });
    }

    @Override
    protected Skin<ImageSearchControl> createDefaultSkin() {
        return new TileSkin(this, limit, MAX_PER_ROW);
    }

    /**
     * Returns the current image URL
     * @return Image URL as String
     */
    public String getImageUrl()
    {
        return imageUrlProperty.getValue();
    }

    /**
     * Returns the imageUrlProperty
     * @return Image URL property
     */
    public StringProperty imageUrlProperty()
    {
        return imageUrlProperty;
    }

    /**
     * Returns the current search term
     * @return Search term as String
     */
    public String getSearchTerm()
    {
        return searchTermProperty.getValue();
    }

    /**
     * Returns the searchTermProperty
     * @return Search term property
     */
    public StringProperty searchTermProperty()
    {
        return searchTermProperty;
    }

    /**
     * Returns the imageListProperty which consists of image URLs
     * @return A list of URLs
     */
    public ObjectProperty<List<String>> imageListProperty()
    {
        return imageListProperty;
    }

    /**
     * isSearching flag
     * @return The properties value
     */
    public boolean getIsSearching()
    {
        return isSearching.get();
    }

    /**
     * isSearching property
     * @return The property
     */
    public BooleanProperty isSearchingProperty()
    {
        return isSearching;
    }
}
