package assignment3.threads;

import assignment3.beans.Flight;
import assignment3.FlightInfoBoard;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import java.util.ArrayList;

/**
 * Thread class representing a reader. it accesses information in FlightInfoBoard and displays it one corresponding
 * terminal panes in the UI.
 * @author Alena Hramyka
 */
public class TerminalThread extends BaseThread {

	/**
	 * Constructor. It takes in a list of flights, a reference to a FlightInfoBoard object,
	 * and a table view from xml, which the thread will be updating.
	 * @param list -- observable list of Flight object.
	 * @param board -- common share FlightInfoBoard instance, from which the thread will be getting all the data.
	 * @param table -- a TableView element from the UI (see xml files and respective controllers).
	 */
    public TerminalThread(ObservableList<Flight> list, FlightInfoBoard board, TableView table) {
        super(list, board, table);
    }

	/**
	 * Standard run() method.
	 * It continuously gets the updated list of flights from FlightInfoBoard and saves it into the observable list.
	 * Since the observable list is self-refreshing data structure and any changes in it would be instantly visible
	 * in the corresponding UI element, it would be much easier for all terminal panes to have a common observable list,
	 * to which FlightInfoBoard would be saving new flights, but it would in a way kill the point of the assignment,
	 * hence the quite rude clearing and reassignment at each execution.
	 * Since the observable list notifies the UI of its changes and updates the table, there's no need to explicitly
	 * update anything in the UI.
	 */
	public void run() {
        while(isRunning) {

            ArrayList<Flight> newList = board.get();
            list.clear();
            list.addAll(newList);

//            Platform.runLater( () -> table.scrollTo(list.size()-1));

            nap(3000);
        }

        System.out.println(this.getName() + "is dying");
    }
}
