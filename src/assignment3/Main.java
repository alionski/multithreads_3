package assignment3;

import assignment3.ui_controls.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The very starting point of the application.
 * It inflates the xml files, prepares the stage and initialises the main Controller, which in its turn
 * will initialise the database pane/terminal controllers.
 * @author Alena Hramyka
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ui_controls/main_window.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Flight Information");
        primaryStage.setScene(new Scene(root, 1500, 900));
        primaryStage.show();

        Controller ctrl = loader.getController();
        ctrl.initialize();
        ctrl.startThreads();

        primaryStage.setOnCloseRequest(event -> Platform.runLater(
                ctrl::stopThreads));
    }

	/**
	 * Is not needed for JavaFX but is kept to make it possible to rung it with CLI or if the app
	 * is embedded/is used together with Swing modules.
	 * @param args -- CLI args.
	 */
	public static void main(String[] args) {
        launch(args);
    }
}
