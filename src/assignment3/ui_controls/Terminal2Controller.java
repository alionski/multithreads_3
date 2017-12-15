package assignment3.ui_controls;

import assignment3.threads.TerminalThread;

/**
 *
 * Class that controls the second terminal pane. It has minimal code because it inherits most of the needed
 * methods from InfoPaneController, but it has to be kept in a separate file and as a public class to be
 * accessible for the corresponding xml element (see main_window.xml) when xml is inflated and bound
 * with declared controllers. All the code in this particular file does is spawn a new thread.
 * @author Alena Hramyka
 *
 */

public class Terminal2Controller extends InfoPaneController {

	/**
	 * Spawns a new thread, which will be updating the second terminal pane.
	 * The parameter filePath is ignored: it is present in the abstract method in the parent, but it only
	 * used by database panes controllers to start a new database thread.
	 * @param filePath -- ignored in this case.
	 */
    @Override
    public void startThread(String filePath){
        thread = new TerminalThread(flights, board, table);
        thread.start();
    }
}
