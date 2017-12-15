package assignment3.ui_controls;

import assignment3.FlightInfoBoard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * The main controller class, which is responsible for the whole window and for initialisation of panes controllers.
 * Start and stops threads, as well as updates the time label.
 * @author Alena Hramyka
 */
public class Controller {

    public Label timer;
    private FlightInfoBoard board;

    @FXML private DB2PaneController db2PaneController;
    @FXML private DB1PaneController db1PaneController;
    @FXML private Terminal1Controller terminal1Controller;
    @FXML private Terminal2Controller terminal2Controller;
    @FXML private Terminal3Controller terminal3Controller;

	/**
	 * Constructor. Creates a new FlightInfoBoard instance.
	 */
	public Controller() {
        board = new FlightInfoBoard(this);
    }

	/**
	 * Initializing method called by JavaFX after the call to controller.
	 * The setters it calls can only be called after the constructor _and_ population of FXML annotated fields,
	 * otherwise one will be served an NPE.
	 */
	@FXML
    public void initialize() {
        System.out.println("main");

        db1PaneController.setFlightInfoBoard(board);
        db2PaneController.setFlightInfoBoard(board);
        terminal1Controller.setFlightInfoBoard(board);
        terminal2Controller.setFlightInfoBoard(board);
        terminal3Controller.setFlightInfoBoard(board);
    }

	/**
	 * Method called from Main once this controller and all the pane controllers have been initialised.
	 * Starts all the threads in all controllers.
	 */
	public void startThreads() {
        db1PaneController.startThread("src/assignment3/files/flights_db_1.txt");
        db2PaneController.startThread("src/assignment3/files//flights_db_2.txt");

        terminal1Controller.startThread(null);
        terminal2Controller.startThread(null);
        terminal3Controller.startThread(null);
    }

	/**
	 * Method called once the user clicked the exit button (red cross).
	 * Allows the threads to exit cleanly, also making them join the UI thread (see controllers for details).
	 */
	public void stopThreads() {
        board.stopTimer();

        db1PaneController.stopThread();
        db2PaneController.stopThread();

        terminal1Controller.stopThread();
        terminal2Controller.stopThread();
        terminal3Controller.stopThread();

        Platform.exit();
    }

	/**
	 * Method called from FlightInforBoard's inner class Timer, once it has incremented the time.
	 * Since the timer label belongs not to panes but to the main window (see main_window.xml), it is this controller's
	 * job to do it.
	 * @param time -- the new time to display in the form XXXX which will be converted to "XX:XX"
	 */
	public void updateTimerLabel(int time) {
        String timeString = String.valueOf(time);
        String newTime = timeString.substring(0, 2) + ":" + timeString.substring(2, timeString.length());
        timer.setText(newTime);
    }
}
