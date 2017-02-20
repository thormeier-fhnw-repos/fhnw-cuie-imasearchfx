package ImageSearchFX.demo;

import ImageSearchFX.Flippanel.FlipPanel;
import ImageSearchFX.ImageSearchControl;
import ImageSearchFX.ImageSearcher.HTTP.Pexels.Connector.PexelsConnector;
import ImageSearchFX.ImageSearcher.HTTP.HttpImageSearcher;
import ImageSearchFX.ImageSearcher.HTTP.Pexels.Factory.PexelsHttpImageSearcherFactory;
import ImageSearchFX.ImageSearcher.HTTP.Pexels.Parser.PexelsParser;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.concurrent.Executors;

/**
 * BorderPane for showcasing of ImageSearchControl
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class DemoPane extends BorderPane
{
    /**
     * The control under test
     */
    private ImageSearchControl imageSearchControl;

    /**
     * Label for text input field of the name
     */
    private Label nameLabel;

    /**
     * Actual text input field for the name
     */
    private TextField nameField;

    /**
     * An image to showcase selected URLs
     */
    private Image image;

    /**
     * View for said image
     */
    private ImageView imageView;

    /**
     * Flip panel by Gerrit Grunwald for hiding the search result box
     */
    private FlipPanel flipPanel;

    /**
     * The initial image URL, Empire State Building
     */
    private final String initialImageUrl = "https://images.pexels.com/photos/164988/pexels-photo-164988.jpeg?w=1200&h=1200&fit=crop&auto=compress&cs=tinysrgb";

    /**
     * Default controler
     */
    public DemoPane() throws Exception
    {
        initializeControls();
        layoutControls();
        addValueChangeListeners();
    }

    /**
     * Initializes all controls
     */
    private void initializeControls() throws Exception
    {
        flipPanel = new FlipPanel();

        // That's the actual control!
        PexelsHttpImageSearcherFactory factory = new PexelsHttpImageSearcherFactory();
        imageSearchControl = new ImageSearchControl(factory.get(), 48);

        nameLabel = new Label("Building name");
        nameField = new TextField("Empire State Building");

        image = new Image(initialImageUrl, 200, 200, true, true, true);

        imageView = new ImageView(image);
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        imageView.getStyleClass().add("image-search-toggle");

        // Only add binding if search is actually visible to prevent unnecessary HTTP requests done by ImageSearchFX,
        // which would probably slow down the application as a whole and is wasting precious CPU cycles.
        imageView.setOnMouseClicked(e -> {
            if (flipPanel.isBackVisible()) {
                flipPanel.flipToFront();
                imageSearchControl.searchTermProperty().unbind();
            } else {
                flipPanel.flipToBack();
                // Setting of value triggers an initial search when toggling the search, bind afterwards to enable
                // live search. Don't forget to unbind again when hiding, see comment above.
                imageSearchControl.searchTermProperty().setValue(nameField.textProperty().getValue());
                imageSearchControl.searchTermProperty().bind(nameField.textProperty());
            }
        });
    }

    /**
     * Put controls into the pane
     */
    private void layoutControls()
    {
        VBox rightBox = new VBox(nameLabel, nameField, imageView);
        rightBox.setSpacing(10);
        rightBox.setPadding(new Insets(20));
        setTop(rightBox);

        flipPanel.getBack().getChildren().add(imageSearchControl);
        flipPanel.getFront().getChildren().add(new Text(
            "This is a dummy node, imagine a form being here or something. " +
                "Click on the image to toggle image search mode."
        ));
        setCenter(flipPanel);
    }

    /**
     * Adds value changed listeners
     */
    private void addValueChangeListeners()
    {
        imageSearchControl.imageUrlProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                imageView.setImage(new Image(newValue, 300, 300, true, true, true));
            }
        });
    }
}
