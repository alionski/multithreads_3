package assignment3.ui_controls;

import assignment3.beans.Flight;
import assignment3.FlightInfoBoard;
import assignment3.threads.BaseThread;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * The "parent" abstract controller class from which all pane controllers inherit (DB1PaneController, Terminal1Controller, etc.).
 * It defines most of the functionality and its children only have to override one method -- startThread().
 * @author Alena Hramyka
 */
public abstract class InfoPaneController {
    @FXML
    protected TableView<Flight> table;
    @FXML
    private TableColumn<Flight, String> flightNumberColumn;
    @FXML
    private TableColumn<Flight, String> departureColumn;
    @FXML
    private TableColumn<Flight, String> gateColumn;
    @FXML
    private TableColumn<Flight, String> statusColumn;

    protected ObservableList<Flight> flights = FXCollections.observableArrayList();

    protected BaseThread thread;
    protected FlightInfoBoard board;

	/**
	 * Method called by JavaFX when initialising the UI after the instance variables have bee injected with
	 * references to UI elements.
	 */
	@FXML
    private void initialize() {
        flightNumberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
        departureColumn.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty());
        gateColumn.setCellValueFactory(cellData -> cellData.getValue().gateProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        table.setItems(flights);
    }

	/**
	 * Abstract method for starting threads which is overridden in children classes.
	 * @param file -- -- path to the file containing the mock database entries (flights_db_1.txt or flights_db_2.txt)
	 */
	public abstract void startThread(String file);

	/**
	 * Method for stopping the thread that the controller spawned. It is called from Controller upon exiting
	 * the program.
	 */
    public void stopThread() {
        if (thread != null) {
            try {
                thread.stopThread();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	/**
	 * Method for passing a reference to a FlightInfoBoard object to the class.
	 * @param  board -- a reference to a shared FlightInfoBoard instance.
	 */
	public void setFlightInfoBoard(FlightInfoBoard board) {
        this.board = board;
    }

}
