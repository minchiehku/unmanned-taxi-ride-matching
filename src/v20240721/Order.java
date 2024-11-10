package v20240721;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Order {
	private int orderID = -1;
	
	//訂單建立者
	private Customer orderCreator;
	private double tolerableWaitingTime = -1;
	private boolean isReservation = false;
	private boolean isPending = false;
	private double reservedPickUpTimePoint = -1;
	private double pendingTimePoint = -1;
	private boolean isReAsigned = false;
	private double pickUpToDropOffToStationDistance = -1;
	private double taxiToPickUpTimePoint;
	private double totalTravelDistance = 0;
	private double oldTaxiToPickUpDistance = 0;
	
	//服務地點
	private int requestNodeID; //叫車Node = 建立訂單時node
	private double requestX;
	private double requestY;
	
    private int pickUpNodeID; //上車Node
    private double pickUpX;
    private double pickUpY;
    
    private ArrayList<Node> customerToPickUpPathNode = new ArrayList<>();  //到上車點路徑
    private ArrayList<Link> customerToPickUpPathLink = new ArrayList<>();
    private ArrayList<Node> taxiToPickUpPathNode = new ArrayList<>();
    private ArrayList<Link> taxiToPickUpPathLink = new ArrayList<>();
     
    private int dropOffNodeID; //下車Node
    private double dropOffX;
    private double dropOffY;
    
    private ArrayList<Node> toDropOffPathNode = new ArrayList<>(); //到下車點路徑
    private ArrayList<Link> toDropOffPathLink = new ArrayList<>();
    
    //訂單狀態&時間
    private int orderStatus = 1; //1=未分配, 2=分配完成客戶等上車,3 = 客戶到上車點, 4 = 計程車到上車點  , 5 = 都到上車點開始上車 , 6 = 出發目的地
    Map<Integer, Double> estimatedOrderTimePoint = new HashMap<>(); //預估時間
	Map<Integer, Double> orderTimePoint = new HashMap<>(); //實際時間
	
		
	//目前配對
	private Match currentMatch = null;
	private Taxi matchedTaxi = null;
	private Event currentEvent = null;
	private Event taxiToPickUpEvent = null;

	
	public Order(int orderID, Customer orderCreator, double orderStartTimePoint) {
		
		this.orderID = orderID;
		this.orderCreator = orderCreator;
		if(orderCreator.isReservation()) {
			this.reservedPickUpTimePoint = orderCreator.getReservedPickUpTimePoint();
			this.estimatedOrderTimePoint.put(32, orderCreator.getReservedPickUpTimePoint());
		}
		
		//叫車Node = 建立訂單時node
		this.requestNodeID = orderCreator.getCurrentNodeID();
		this.requestX = orderCreator.getCustomerX();
		this.requestY = orderCreator.getCustomerY();
		
		this.pickUpNodeID = orderCreator.getPickUpNodeID();
		this.pickUpX = orderCreator.getPickUpX();
		this.pickUpY = orderCreator.getPickUpY();
		
		this.dropOffNodeID = orderCreator.getDropOffNodeID();
		this.dropOffX = orderCreator.getDropOffX();
		this.dropOffY = orderCreator.getDropOffY();
		
		this.orderTimePoint.put(1, orderStartTimePoint);
	}


	public int getOrderID() {
		return orderID;
	}

	public Customer getCreatorCustomer() {
		return orderCreator;
	}

	public int getPickUpNodeID() {
		return pickUpNodeID;
	}

	public void setPickUpNodeID(int pickUpNodeID) {
		this.pickUpNodeID = pickUpNodeID;
	}

	public double getPickUpX() {
		return pickUpX;
	}

	public void setPickUpX(double pickUpX) {
		this.pickUpX = pickUpX;
	}

	public double getPickUpY() {
		return pickUpY;
	}

	public void setPickUpY(double pickUpY) {
		this.pickUpY = pickUpY;
	}

	public int getDropOffNodeID() {
		return dropOffNodeID;
	}

	public void setDropOffNodeID(int dropOffNodeID) {
		this.dropOffNodeID = dropOffNodeID;
	}

	public double getDropOffX() {
		return dropOffX;
	}

	public void setDropOffX(double dropOffX) {
		this.dropOffX = dropOffX;
	}

	public double getDropOffY() {
		return dropOffY;
	}

	public void setDropOffY(double dropOffY) {
		this.dropOffY = dropOffY;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Map<Integer, Double> getOrderTimePoint() {
		return orderTimePoint;
	}

	public void setOrderTimePoint(Map<Integer, Double> orderTimePoint) {
		this.orderTimePoint = orderTimePoint;
	}

	public Match getCurrentMatch() {
		return currentMatch;
	}

	public void setCurrentMatch(Match currentMatch) {
		this.currentMatch = currentMatch;
	}


	public int getRequestNodeID() {
		return requestNodeID;
	}


	public void setRequestNodeID(int requestNodeID) {
		this.requestNodeID = requestNodeID;
	}


	public double getRequestX() {
		return requestX;
	}


	public void setRequestX(double requestX) {
		this.requestX = requestX;
	}


	public double getRequestY() {
		return requestY;
	}


	public void setRequestY(double requestY) {
		this.requestY = requestY;
	}


	public Taxi getMatchTaxi() {
		return matchedTaxi;
	}


	public void setMatchTaxi(Taxi matchTaxi) {
		this.matchedTaxi = matchTaxi;
	}


	public Customer getOrderCreator() {
		return orderCreator;
	}


	public void setOrderCreator(Customer orderCreator) {
		this.orderCreator = orderCreator;
	}


	public ArrayList<Node> getCustomerToPickUpPathNode() {
		return customerToPickUpPathNode;
	}


	public void setCustomerToPickUpPathNode(ArrayList<Node> customerToPickUpPathNode) {
		this.customerToPickUpPathNode = customerToPickUpPathNode;
	}


	public ArrayList<Link> getCustomerToPickUpPathLink() {
		return customerToPickUpPathLink;
	}


	public void setCustomerToPickUpPathLink(ArrayList<Link> customerToPickUpPathLink) {
		this.customerToPickUpPathLink = customerToPickUpPathLink;
	}


	public ArrayList<Node> getTaxiToPickUpPathNode() {
		return taxiToPickUpPathNode;
	}


	public void setTaxiToPickUpPathNode(ArrayList<Node> taxiToPickUpPathNode) {
		this.taxiToPickUpPathNode = taxiToPickUpPathNode;
	}


	public ArrayList<Link> getTaxiToPickUpPathLink() {
		return taxiToPickUpPathLink;
	}


	public void setTaxiToPickUpPathLink(ArrayList<Link> taxiToPickUpPathLink) {
		this.taxiToPickUpPathLink = taxiToPickUpPathLink;
	}


	public Map<Integer, Double> getEstimatedOrderTimePoint() {
		return estimatedOrderTimePoint;
	}


	public void setEstimatedOrderTimePoint(Map<Integer, Double> estimatedOrderTimePoint) {
		this.estimatedOrderTimePoint = estimatedOrderTimePoint;
	}


	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}


	public ArrayList<Node> getToDropOffPathNode() {
		return toDropOffPathNode;
	}


	public void setToDropOffPathNode(ArrayList<Node> toDropOffPathNode) {
		this.toDropOffPathNode = toDropOffPathNode;
	}


	public ArrayList<Link> getToDropOffPathLink() {
		return toDropOffPathLink;
	}


	public void setToDropOffPathLink(ArrayList<Link> toDropOffPathLink) {
		this.toDropOffPathLink = toDropOffPathLink;
	}


	public Taxi getMatchedTaxi() {
		return matchedTaxi;
	}


	public void setMatchedTaxi(Taxi matchedTaxi) {
		this.matchedTaxi = matchedTaxi;
	}


	public Event getCurrentEvent() {
		return currentEvent;
	}


	public void setCurrentEvent(Event currentEvent) {
		this.currentEvent = currentEvent;
	}


	public boolean isReservation() {
		return isReservation;
	}


	public void setReservation(boolean isReservation) {
		this.isReservation = isReservation;
	}


	public double getReservedPickUpTimePoint() {
		return reservedPickUpTimePoint;
	}


	public void setReservedPickUpTimePoint(double reservedPickUpTimePoint) {
		this.reservedPickUpTimePoint = reservedPickUpTimePoint;
	}


	public boolean isPending() {
		return isPending;
	}


	public void setPending(boolean isPending) {
		this.isPending = isPending;
	}


	public double getPendingTimePoint() {
		return pendingTimePoint;
	}


	public void setPendingTimePoint(double pendingTimePoint) {
		this.pendingTimePoint = pendingTimePoint;
	}


	public double getTolerableWaitingTime() {
		return tolerableWaitingTime;
	}


	public void setTolerableWaitingTime(double tolerableWaitingTime) {
		this.tolerableWaitingTime = tolerableWaitingTime;
	}


	public boolean isReAsigned() {
		return isReAsigned;
	}


	public void setReAsigned(boolean isReAsigned) {
		this.isReAsigned = isReAsigned;
	}


	public double getPickUpToDropOffToStationDistance() {
		return pickUpToDropOffToStationDistance;
	}


	public void setPickUpToDropOffToStationDistance(double pickUpToDropOffToStationDistance) {
		this.pickUpToDropOffToStationDistance = pickUpToDropOffToStationDistance;
	}


	public double getTaxiToPickUpTimePoint() {
		return taxiToPickUpTimePoint;
	}


	public void setTaxiToPickUpTimePoint(double taxiToPickUpTimePoint) {
		this.taxiToPickUpTimePoint = taxiToPickUpTimePoint;
	}


	public Event getTaxiToPickUpEvent() {
		return taxiToPickUpEvent;
	}


	public void setTaxiToPickUpEvent(Event taxiToPickUpEvent) {
		this.taxiToPickUpEvent = taxiToPickUpEvent;
	}


	public double getTotalTravelDistance() {
		return totalTravelDistance;
	}


	public void setTotalTravelDistance(double totalTravelDistance) {
		this.totalTravelDistance = totalTravelDistance;
	}


	public double getOldTaxiToPickUpDistance() {
		return oldTaxiToPickUpDistance;
	}


	public void setOldTaxiToPickUpDistance(double oldTaxiToPickUpDistance) {
		this.oldTaxiToPickUpDistance = oldTaxiToPickUpDistance;
	}

	
	
	
	
}
