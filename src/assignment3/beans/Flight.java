package assignment3.beans;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class that represents a flight.
 * @author Alena Hramyka
 */

public class Flight implements Comparable{
    private StringProperty flightNumber;
    private StringProperty departureTime;
    private StringProperty gate;
    private StringProperty status;

	/**
	 * Constructor. Takes in string parameters and saves them into SimpleStringProperty objects, which is
	 * necessary to make it work with an observable list and an cell factory.
	 * @param flightNumber -- flight number
	 * @param departureTime -- departure time in the form "XX:XX"
	 * @param gate -- gate number
	 * @param status -- status (CHECK-IN, BOARDING, DEPARTED, LANDED, N/A)
	 */
    public Flight(String flightNumber, String departureTime, String gate, String status) {
        this.flightNumber = new SimpleStringProperty(flightNumber);
        this.departureTime = new SimpleStringProperty(departureTime);
        this.gate = new SimpleStringProperty(gate);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty flightNumberProperty() {
        return flightNumber;
    }

    public String getDepartureTime() {
        return departureTime.get();
    }

    public StringProperty departureTimeProperty() {
        return departureTime;
    }

    public StringProperty gateProperty() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate.set(gate);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

	/**
	 * Comparable interface method used when sorting the flights as per their departure time.
	 * @param o -- the other Flight object reference.
	 * @return -- result of comparison (>0, 0, <0)
	 */
	@Override
    public int compareTo(Object o) {
        String[] myTimeSplit = this.getDepartureTime().split(":");
        int myTime = Integer.parseInt(myTimeSplit[0]+myTimeSplit[1]);

        String[] theirTimeSplit = ((Flight)o).getDepartureTime().split(":");
        int theirTime = Integer.parseInt(theirTimeSplit[0]+theirTimeSplit[1]);

        return Integer.compare(myTime, theirTime);
    }
}
