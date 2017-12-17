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
 * modify the data. Writers and readers notify each other via semaphore permits when data has been written or read. In this
 * solution, only one writer is allowed into CS, and it is also true for readers.
 * @author Alena Hramyka
 */
public class FlightInfoBoard {
    private Controller ctrl;
    private ArrayList<Flight> flights = new ArrayList<>();
    private Semaphore writeSemaphore;
    private Semaphore readSemaphore;
    private Semaphore mutex;
    private int now = 1200;
    private Timer timer;

	/**
	 * Constructor. Sets a reference to the main controller and instantiates a writer lock and a mutex.
	 * @param controller -- reference to the main controller.
	 */
	public FlightInfoBoard(Controller controller) {
        ctrl = controller;
        writeSemaphore = new Semaphore(31);
        readSemaphore = new Semaphore(0);
        mutex = new Semaphore(1);
    }

	/**
	 * Method called by writers when they want to add a new flight to the list.
	 * Writer acquires a permit from the write semaphore
	 * (blocks if is kept by a reader thread, but not the first time since it's initialised with 1),
	 * adds a flight and sorts the updated list. Releases a write permit afterwards, i.e. a waiting reader can now enter CS.
	 * @param flight -- new Flight object to add to the list.
	 */
	public void put(Flight flight) {
        try {

            writeSemaphore.acquire(); // there is initially one permit, i.e a writer is guaranteed to write first.
			System.out.println("Writer got permit, number left: " + writeSemaphore.availablePermits());

			mutex.acquire();
			flights.add(flight);
            Collections.sort(flights);
            mutex.release();

			System.out.println("Reader permit released, number left: " + (readSemaphore.availablePermits()+1));
			readSemaphore.release(); // after the first writing, the first reading permit is released, and one
			// reading thread is allowed to read; before it, it is waiting for a permit.

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Method called by readers when they want to get the updated list with flights.
	 * A reader must first get a permit, which is only released after the first write.
	 * I.e., in this solution, only one reader can read at the same time, all the others in queue have to wait
	 * for a new permit to be issue by a writer.
	 * At the end it issues a permit to write, i.e. one write can be performed after one read.
	 * @return -- ArrayList with updated flights.
	 */
	public ArrayList<Flight> get() {
        ArrayList<Flight> newFlights = null;

        try {

			readSemaphore.acquire();
			System.out.println("Reader got permit, number left: " + readSemaphore.availablePermits());

			mutex.acquire();
			newFlights = (ArrayList<Flight>)flights.clone();
			mutex.release();

			System.out.println("Write permit released, number left: " + (writeSemaphore.availablePermits()+1));
			writeSemaphore.release();

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
            writeSemaphore.acquire();
            System.out.println("Writer got permit, number left: " + writeSemaphore.availablePermits());

            mutex.acquire();
            updateTime();
            mutex.release();

            System.out.println("Reader permit released, number left: " + (readSemaphore.availablePermits()+1));
            readSemaphore.release();
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

//                System.out.println(now);
                Platform.runLater( () -> ctrl.updateTimerLabel(now));

                try {
                    sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
			System.out.println("Timer is dying");
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
