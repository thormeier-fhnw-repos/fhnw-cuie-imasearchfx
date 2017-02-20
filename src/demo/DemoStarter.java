package ImageSearchFX.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * Starter for showcasing of LatLongControl
 *
 * @author Pascal Thormeier <pascal.thormeier@students.fhnw.ch>
 */
public class DemoStarter extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Region rootPanel = new DemoPane();

        Scene scene = new Scene(rootPanel, 1200, 950);

        primaryStage.setTitle("Showcasing of ImageSearchControl with TileSkin");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
