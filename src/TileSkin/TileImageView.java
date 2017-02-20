package ImageSearchFX.TileSkin;

import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Extended ImageView to give some convenience methods and keep things where they belong.
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class TileImageView extends ImageView
{
    /**
     * Boolean flag to determine if the image is loading
     */
    private BooleanProperty isLoading = new SimpleBooleanProperty(false);

    /**
     * The currently loaded URL
     */
    private String currentlyLoadedUrl = null;

    /**
     * Placeholder for current image that is being loaded
     */
    private Image currentlyLoadingImage = null;

    /**
     * Loading spinner image
     */
    private Image loadingSpinner;

    /**
     * The current onload listener, this attribute is used to detach it, in case another image is being loaded
     */
    private ChangeListener<Number> currentOnloadListener = null;

    /**
     * Should be changing to have a spinner
     */
    private Rotate rotatingTransform;

    /**
     * Animation for rotation
     */
    private Timeline rotationAnimation;

    /**
     * Animation for hover shadow
     */
    private Timeline dropshadowInAnimation;

    /**
     * Removes shadow on mouse exit
     */
    private Timeline dropshadowOutAnimation;

    /**
     * Scale for hover
     */
    private ScaleTransition scaleTransition;

    /**
     * Drop shadow when hovering
     */
    private DropShadow dropShadow;

    /**
     * Handles with and height as property since ImageView doesn't have this as a property...
     */
    private DoubleProperty imageWidthHeight = new SimpleDoubleProperty();

    /**
     * Default constructor
     */
    public TileImageView()
    {
        super();
        initializeSelf();
        initializeParts();
        setupTransitions();
        addListeners();
        addValueChangeListeners();
    }

    /**
     * Initializes the underlying ImageView itself
     */
    private void initializeSelf()
    {
        getStyleClass().add("tile-skin-image-view");
    }

    /**
     * Initialize sub-parts of the ImageView
     */
    private void initializeParts()
    {
        loadingSpinner = new Image(
            getClass().getResource("spinner.png").toExternalForm(),
            150, 150, true, true
        );

        dropShadow = new DropShadow(BlurType.GAUSSIAN, new Color(0, 0, 0, 1), 0.0, 0.0, 0.0, 0.0);
        setEffect(dropShadow); // Already apply drop shadow so it only needs to be animated
    }

    /**
     * Set up all transitions necessary
     */
    private void setupTransitions()
    {
        rotatingTransform = new Rotate(0, 0, 0);
        rotationAnimation = new Timeline();
        rotationAnimation.getKeyFrames().add(
            new KeyFrame(
                Duration.millis(800),
                new KeyValue(
                    rotatingTransform.angleProperty(),
                    360
                )
            )
        );

        rotationAnimation.setCycleCount(Animation.INDEFINITE);
        rotationAnimation.play(); // Already start rotating, shouldn't impact performance since there's no repaint

        dropshadowInAnimation = new Timeline();
        dropshadowInAnimation.getKeyFrames().add(
            new KeyFrame(
                Duration.millis(150),
                new KeyValue(
                    dropShadow.radiusProperty(),
                    15.0
                )
            )
        );

        dropshadowOutAnimation = new Timeline();
        dropshadowOutAnimation.getKeyFrames().add(
            new KeyFrame(
                Duration.millis(150),
                new KeyValue(
                    dropShadow.radiusProperty(),
                    0.0
                )
            )
        );

        scaleTransition = new ScaleTransition(Duration.millis(150), this);
    }

    /**
     * Sets up a value change listener
     */
    private void addValueChangeListeners()
    {
        // Resize the image
        imageWidthHeight.addListener((observable, oldValue, newValue) -> {
            // Adjust image size
            double newWidthHeight = newValue.doubleValue();

            setFitWidth(newWidthHeight);
            setFitHeight(newWidthHeight);

            rotatingTransform.setPivotX(newWidthHeight / 2);
            rotatingTransform.setPivotY(newWidthHeight / 2);
        });
    }

    /**
     * Adds zoom listener
     */
    private void addListeners()
    {
        setOnMouseEntered(e -> {
            if (isLoading()) {
                return;
            }

            toFront();
            scaleTransition.stop();

            scaleTransition.setToX(1.2);
            scaleTransition.setToY(1.2);
            scaleTransition.setToZ(1.2);

            scaleTransition.play();
            dropshadowInAnimation.play();
        });

        setOnMouseExited(e -> {
            if (isLoading()) {
                return;
            }

            scaleTransition.stop();

            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.setToZ(1.0);

            scaleTransition.play();
            dropshadowOutAnimation.play();
        });
    }

    /**
     * Load an image
     * @param url   The URL that is being loaded
     * @param image The Image that is being loaded
     */
    public void loadImage(String url, Image image)
    {
        System.out.println(image.progressProperty().getValue());
        ChangeListener<Number> onLoadListener = (observable, oldValue, newValue) -> {
            if (newValue.equals(1.0) && null != currentlyLoadingImage && currentlyLoadingImage.equals(image)) {
                endLoadingState();
                setImage(image);
                currentlyLoadedUrl = url;
            }
        };

        // If there's an image already being loaded, cancel the load and trigger this one to load instead
        if (isLoading() && currentlyLoadingImage != null) {
            currentlyLoadingImage.cancel();
            currentlyLoadingImage.progressProperty().removeListener(currentOnloadListener);

            // In rare cases the new image load is triggered the exact moment the other image finished loading and
            // displays. This leads to just replacing of an image without showing the loading spinner first.
            if (currentlyLoadingImage.progressProperty().getValue().equals(1.0) && !isLoading()) {
                startLoadingState();
            }
        }

        currentlyLoadingImage = image;

        // On load
        currentlyLoadingImage.progressProperty().addListener(onLoadListener);
        currentOnloadListener = onLoadListener;
    }

    /**
     * Replaces the image with a loading spinner
     */
    void startLoadingState()
    {
        if (isLoading()) { // If we're already loading, don't even care about this
            return;
        }

        setImage(loadingSpinner);
        getTransforms().add(rotatingTransform);
        getStyleClass().add("is-loading");

        isLoading.setValue(true);
        currentlyLoadedUrl = null;
    }

    /**
     * Switches the loading state off and replaces the loading spinner with an actual image
     */
    void endLoadingState()
    {
        if (!isLoading()) {
            return;
        }

        getTransforms().remove(rotatingTransform);
        getStyleClass().remove("is-loading");
        isLoading.setValue(false);
        currentlyLoadingImage = null;
    }

    /**
     * Empties this view
     */
    void emptyView()
    {
        setImage(null);
    }

    /**
     * Determine if the image is still loading
     * @return Value if isLoadingProperty
     */
    public boolean isLoading()
    {
        return isLoading.get();
    }

    /**
     * Get the isLoadingProperty itself
     * @return isLoadingProperty
     */
    public BooleanProperty isLoadingProperty()
    {
        return isLoading;
    }

    /**
     * Returns the last loaded URL
     * @return The last loaded URL
     */
    public String getCurrentlyLoadedUrl()
    {
        return currentlyLoadedUrl;
    }

    /**
     * Manually set the width/height
     * @param imageWidthHeight Width/height
     */
    void setImageWidthHeight(double imageWidthHeight)
    {
        this.imageWidthHeight.set(imageWidthHeight);
    }
}
