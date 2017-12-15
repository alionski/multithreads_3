package assignment3.threads;

import assignment3.beans.Flight;
import assignment3.FlightInfoBoard;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.io.*;
import java.util.LinkedList;

/**
 * Thread class representing a writer. It reads "database entries" from .txt files and feeds them into
 * FlightInfoBoard.
 * @author Alena Hramyka
 */
public class DatabaseThread extends BaseThread {
    private String filePath;
    private LinkedList<Flight> flightsFromFile = new LinkedList<>();

	/**
	 * Constructor. Takes in references to an observable list with flights, a reference to a FlightInfoBoard object
	 * @param list -- reference to observable list with Flight objects.
	 * @param board -- reference to a shared FlightInfoBoard object.
	 * @param table -- the table from the UI (see xml files) which the thread will be adding entries to.
	 * @param filePath -- the file path to the "database" file.
	 */
    public DatabaseThread(ObservableList<Flight> list, FlightInfoBoard board, TableView table, String filePath) {
        super(list, board, table);
        this.filePath = filePath;
        loadFlights();
    }

	/**
	 * Method that loads the data from "database" .txt file into a LinkedList, from which the thread will later
	 * be feeding FlightInfoBoard.
	 */
	private void loadFlights() {
        File file = new File(filePath);
        FileInputStream fis = null;
        BufferedReader reader;

        try {
            fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis));

            String line = reader.readLine();
            while(line != null){
                String[] split = line.split(" ");
                Flight flight = new Flight(split[0], split[1], split[2], "N/A");
                flightsFromFile.add(flight);
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

	/**
	 * Standard run() method.
	 * It first goes through all the database entries in the LinkedList and saves them into FlightInfoBoard and updates
	 * the database pane in the UI.
	 * Then, it starts updating the status information of the flights. It does it via calling the corresponding
	 * methods in the FlightInfoBoard object.
	 */
	public void run() {
        while(isRunning && flightsFromFile.size() != 0) {
            Flight flight = flightsFromFile.remove();
            board.put(flight);
            list.add(flight);

            Platform.runLater( () -> table.scrollTo(list.size()-1));

            nap(5000);
        }

        while(isRunning) {
            board.update();

            nap(5000);
        }

        System.out.println(this.getName() + "is dying");
    }
}
