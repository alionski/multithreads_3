package assignment3.threads;

import assignment3.beans.Flight;
import assignment3.FlightInfoBoard;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.Random;

/**
 * Abstract thread class that defines most of the functionality that both DatabaseThread and TerminalThread share.
 * @author Alena Hramyka
 */

public abstract class BaseThread extends Thread {
    ObservableList<Flight> list;
    FlightInfoBoard board;
    TableView table;
    private Random rand = new Random();
    volatile boolean isRunning = true;

	/**
	 * Constructor. It takes in a list of flights, a reference to a FlightInfoBoard object,
	 * and a table view from xml, which the thread will be updating.
	 * @param list -- observable list of Flight object.
	 * @param board -- common share FlightInfoBoard instance, from which the thread will be getting all the data.
	 * @param table -- a TableView element from the UI (see xml files and respective controllers).
	 */
    public BaseThread(ObservableList<Flight> list, FlightInfoBoard board, TableView table) {
        this.list = list;
        this.board = board;
        this.table = table;
    }

	/**
	 * Sets the volatile boolean to false, this preventing the while-loop from executing next time and hence
	 * letting the thread die.
	 */
	public void stopThread() {
        isRunning = false;
    }

	/**
	 * Method for sleeping that the children threads call when they need to sleep.
	 * @param milisec -- the amount of ms the threads will be sleeping
	 */
	public void nap(int milisec) {
        try {
            sleep(rand.nextInt(milisec));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
