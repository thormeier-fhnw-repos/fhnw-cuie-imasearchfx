package ImageSearchFX.TileSkin;

import ImageSearchFX.ImageSearchControl;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Displays the found images as tiles
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class TileSkin extends SkinBase<ImageSearchControl>
{
    /**
     * Fix list of maximum image nodes
     */
    private List<TileImageView> images = new ArrayList<>();

    /**
     * Pane to actually draw in
     */
    private GridPane drawingGridPane;

    /**
     * Contains the drawingPane to make it scrollable in case of too much images for current height
     */
    private ScrollPane containerPane;

    /**
     * Maximum number of images per row
     */
    private int maxPerRow;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     * @param limit   Maximum number of images
     * @param perRow  Number of images per row
     */
    public TileSkin(ImageSearchControl control, int limit, int perRow)
    {
        super(control);

        initializeSelf(perRow);
        initializeParts(limit);
        layoutParts();
        addValueChangeListeners();
    }

    /**
     * Initializes basic stuff
     *
     * @param maxPerRow Maximum number of images per row
     */
    private void initializeSelf(int maxPerRow)
    {
        this.maxPerRow = maxPerRow;

        String stylesheet = getClass().getResource("tileSkin.css").toExternalForm();
        getSkinnable().getStylesheets().add(stylesheet);
    }

    /**
     * Initializes all parts of this skin
     * @param numberOfImages Maximum number of images
     */
    private void initializeParts(int numberOfImages)
    {
        double percentageWidth = Math.floor(100 / maxPerRow);

        // ScrollPane to allow scrolling of grid. Careful: GridPane within ScrollPane without horizontal scrolling
        // leads to weird behaviour, so bind width of child (GridPane) to width of parent (ScrollPane).
        containerPane = new ScrollPane();
        containerPane.getStyleClass().add("edge-to-edge");

        // Grid pane that contains all TileImageViews
        drawingGridPane = new GridPane();
        drawingGridPane.getStyleClass().add("tile-skin-content");
        drawingGridPane.maxWidthProperty().bind(containerPane.widthProperty());
        drawingGridPane.prefWidthProperty().bind(containerPane.widthProperty());
        for (int cols = 0; cols < maxPerRow; cols++) {
            drawingGridPane.getColumnConstraints().add(
                columnWithPercentage(percentageWidth)
            );
        }

        //
        for (int i = 0; i < numberOfImages; i++) {
            TileImageView imageView = new TileImageView();

            // Resize behaviour: Change with and height when resizing horizontally
            getSkinnable().widthProperty().addListener((observable, oldValue, newValue) -> {
                int newWidth = (int) Math.floor(newValue.doubleValue() / maxPerRow);

                imageView.setImageWidthHeight(newWidth);
            });

            imageView.setPreserveRatio(true);

            // Set new image URL on property when clicking
            imageView.isLoadingProperty().addListener(((observable1, oldValue, newValue) -> {
                if (!newValue) { // Finished loading
                    imageView.setOnMouseClicked(e -> getSkinnable() // Me no like long lines.
                        .imageUrlProperty()
                        .setValue(imageView.getCurrentlyLoadedUrl())
                    );
                }
            }));

            images.add(imageView);
        }
    }

    /**
     * Arrange parts. We're using a flow pane for clean resize behaviour
     * since resizing ImageViews is kind of a hassle
     */
    private void layoutParts()
    {
        int i = 0;

        // Tiles into Grid
        for (TileImageView imageView : images) {
            GridPane.setConstraints(imageView, i % maxPerRow, ((int) Math.floor(i / maxPerRow)));
            drawingGridPane.add(imageView, i % maxPerRow, ((int) Math.floor(i / maxPerRow)));
            i++;
        }

        containerPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        containerPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        containerPane.setFitToHeight(true);
        containerPane.setFitToWidth(true);

        // Grid into scroll
        containerPane.setContent(drawingGridPane);

        // Scroll into ImageSearchControl
        getChildren().add(containerPane);
    }

    /**
     * Add value changed listeners
     */
    private void addValueChangeListeners()
    {
        // Change of loading state should trigger replacement of images with loading spinner, see TileImageView
        getSkinnable().isSearchingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                for (TileImageView imageView : images) {
                    imageView.startLoadingState();
                }
            }
        });

        // As soon as a new list was loaded
        getSkinnable().imageListProperty().addListener((observable, oldList, newList) -> {
            if (newList.size() > 0) {

                int imageWidthHeight = (int) (Math.floor(getSkinnable().getWidth() / maxPerRow)) * 2;

                // Set the new necessary images
                int i;

                for (i = 0; i < newList.size(); i++) {
                    TileImageView image = images.get(i);
                    final String url = newList.get(i);
                    image.loadImage(url, new Image(url, imageWidthHeight, imageWidthHeight, false, true, true));
                }

                // Empty the ones not being used
                for (int j = i; j < images.size(); j++) {
                    TileImageView image = images.get(j);
                    image.endLoadingState();
                    image.emptyView();
                }
            }
        });
    }

    /**
     * Generates a constainr to be used inside the drawingGridPane, purely for convenience.
     * @param percentage Percentage width of the constraint
     * @return A column constraint
     */
    private ColumnConstraints columnWithPercentage(final double percentage)
    {
        ColumnConstraints constraints = new ColumnConstraints();

        constraints.setPercentWidth(percentage);
        constraints.fillWidthProperty().set(true);
        constraints.setHgrow(Priority.ALWAYS);

        return constraints;
    }
}
