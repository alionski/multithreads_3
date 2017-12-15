package assignment3.ui_controls;

import assignment3.threads.DatabaseThread;

/**
 *
 * Class that controls the second pane (database 2). It has minimal code because it inherits most of the needed
 * methods from InfoPaneController, but it has to be kept in a separate file and as a public class to be
 * accessible for the corresponding xml element (pane, see in main_window.xml) when xml is inflated and bound
 * with declared controllers. All the code in this particular file does is spawn a new thread.
 * @author Alena Hramyka
 *
 */

public class DB2PaneController extends InfoPaneController {

	/**
	 * Spawns a new thread, which will be updating the Database 2 Pane.
	 * @param filePath -- path to the file containing the mock database entries (flights_db_2.txt)
	 */
    @Override
    public void startThread(String filePath) {
        thread = new DatabaseThread(flights, board, table, filePath);
        thread.start();
    }
}
