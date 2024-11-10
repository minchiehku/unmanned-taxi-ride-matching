package v20240721;

public class Event {
	private int eventID;
	private int eventCategory;
	private double eventTimePoint;
	private Customer relateCustomer;
	private Taxi relateTaxi;
	private Match relateMatch;
	private Order relateOrder;
	private ChargingStation relateStation;
	
	public Event(int eventID, int eventCategory, double eventTimePoint) {
		this.eventID = eventID;
		this.eventTimePoint = eventTimePoint;
		this.eventCategory = eventCategory;
		relateCustomer = null;
		relateTaxi = null;
	}
		

	public int getEventCategory() {
		return eventCategory;
	}


	public int getEventID() {
		return eventID;
	}

	public void setEventID(int eventID) {
		this.eventID = eventID;
	}


	public double getEventTimePoint() {
		return eventTimePoint;
	}

	public void setEventTimePoint(double eventTimePoint) {
		this.eventTimePoint = eventTimePoint;
	}

	public Customer getRelateCustomer() {
		return relateCustomer;
	}

	public void setRelateCustomer(Customer relateCustomer) {
		this.relateCustomer = relateCustomer;
	}

	public Taxi getRelateTaxi() {
		return relateTaxi;
	}

	public void setRelateTaxi(Taxi relateTaxi) {
		this.relateTaxi = relateTaxi;
	}

	public Match getRelateMatch() {
		return relateMatch;
	}

	public void setRelateMatch(Match relateMatch) {
		this.relateMatch = relateMatch;
	}


	public Order getRelateOrder() {
		return relateOrder;
	}


	public void setRelateOrder(Order relateOrder) {
		this.relateOrder = relateOrder;
	}


	public ChargingStation getRelateStation() {
		return relateStation;
	}


	public void setRelateStation(ChargingStation relateStation) {
		this.relateStation = relateStation;
	}
	
	
	
}
