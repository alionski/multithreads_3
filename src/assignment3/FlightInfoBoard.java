package assignment3;

import assignment3.beans.Flight;
import assignment3.ui_controls.Controller;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

/**
 * The "repository" class that keeps the data from the database put by writer threads and serves it to the reader threads.
 * It follows the Reader-Writer model, meaning the writers have exclusive access to the data, and the readers do not
 * modify the data; all of the readers are allowed to read at the same time, but only one writer can write at a time, and no reader
 * is allowed to read while the writer has the writer lock.
 * @author Alena Hramyka
 */
public class FlightInfoBoard {
    private Controller ctrl;
    private ArrayList<Flight> flights = new ArrayList<>();
    private Semaphore writerLock;
    private Semaphore readerMutex;
    private int readerCount;
    private int now = 1200;
    private Timer timer;

	/**
	 * Constructor. Sets a reference to the main controller and instantiates a writer lock and a mutex.
	 * @param controller -- reference to the main controller.
	 */
	public FlightInfoBoard(Controller controller) {
        ctrl = controller;
        writerLock = new Semaphore(1);
        readerMutex = new Semaphore(1);
    }

	/**
	 * Method called by writers when they want to add a new flight to the list.
	 * Writer acquires a writer lock (blocks if is kept by a reader thread), adds a flight and sorts the updated list.
	 * Releases the lock afterwards.
	 * @param flight -- new Flight object to add to the list.
	 */
	public void put(Flight flight) {
        try {
            writerLock.acquire(); // going to get blocked if a reader has the mutex

			flights.add(flight);
            Collections.sort(flights); // added sorting just to make the method do a little bit more than just put flights

            writerLock.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Method called by readers when they want to get the updated list with flights.
	 * A reader must first get a mutex and then check if it's he first reader to enter, and if so,
	 * it also gets the writer lock and then releases the mutex.
	 * Then it copies the updated list with flights, after which it checks if it's the last reade to leave, does the
	 * same locking-unlocking but in the reverse.
	 * @return -- ArrayList with updated flights.
	 */
	public ArrayList<Flight> get() {
        ArrayList<Flight> newFlights = null;

        try {
			readerMutex.acquire(); // now get the reading lock
			readerCount++;
            if (readerCount == 1) { // get the write lock if we are the first reading thread
                writerLock.acquire();
            }
			readerMutex.release();

            newFlights = (ArrayList<Flight>)flights.clone();

			readerMutex.acquire();
			if (readerCount == 1) { // release the write lock if we are the last to leave
                writerLock.release();
            }
			readerMutex.release(); // release the read lock

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return newFlights;
    }

	/**
	 *	Method called by writers once they are done storing flights, and have now proceeded to
	 * updating the flight status. The logic behind locks is the same as in get().
	 */
	public void update() {

        if (timer == null) {
            timer = new Timer();
            timer.start();
        }

        try {
            writerLock.acquire();

            updateTime();

            writerLock.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

	/**
	 * Method that updates time in the flights and status info, relying on the _now_ time, which is being changed
	 * by the Timer.
	 */
	private void updateTime() {
        // if > 1200 -- departed or landed
            // if > 1200 + 2h -- landed
        // if < 1200 -- boarding or checkin
            // if < 1200 - 30 min -- boarding
            // if < 1200 --50 min -- checkin

        for (Flight flight : flights) {
            String[] split = flight.getDepartureTime().split(":");
            int time = Integer.parseInt(split[0]+split[1]);

            if (time < (now - 200)) {
                flight.setGate("-");
                flight.setStatus("LANDED");
            } else if (time < now) {
                flight.setGate("-");
                flight.setStatus("DEPARTED");
            } else if (time <= now + 30) {
                flight.setStatus("BOARDING");
            } else if (time <= now + 200 ){
                flight.setStatus("CHECK-IN");
            } else {
                flight.setStatus("N/A");
            }
        }
    }

	/**
	 * Private thread class that represents a timer. Its job is to update the time on with equal terminals.
	 * It emulates the real time changes on airport boards, only in fast-forward and skipping ten minutes at a time.
	 */
	private class Timer extends Thread {
        private volatile boolean isRunning = true;

        public void run() {
            while (isRunning) {
                if (now % 100 == 50) {
                    now += 100 - 50;
                } else {
                    now += 10;
                }

                System.out.println(now);
                Platform.runLater( () -> ctrl.updateTimerLabel(now));

                try {
                    sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void kill() {
            isRunning = false;
        }
    }

	/**
	 *	Method called by Controller upon exiting.
	 */
	public void stopTimer() {
        if (timer!= null) {
            try {
                timer.kill();
                timer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
