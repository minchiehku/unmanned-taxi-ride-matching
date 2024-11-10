package v20240721;

import java.util.ArrayList;

public class Customer {
	
	//客戶ID
    private int customerID;
    
    //基本資料
    private boolean isReservation = false;
    private double reservedPickUpTimePoint = -1;
    private double tolerableWaitingTime;
    private double walkSpeed = 3; //走路速度(時速) 3-5 km/hr
    private boolean isMoving = false;
    private int requestTimes = 1;
    private double estimatedTravelTime = -1;
    
    //目前位置資訊
    private int currentNodeID; //目前所在Node
    private double customerX;
    private double customerY;
    
    private ArrayList<Node> currentPathNode = new ArrayList<>();
    private ArrayList<Link> currentPathLink = new ArrayList<>();
    
    private Link currentLink = null; //目前所在Link
    private int currentLinkID = -1;
    private int indexOfLink = -1; 
    private double remainLinkTime = -1; //還有多久走完link 
    private double remainLinkDistance;

    
    private int nextNodeID = -1; //下一個Node
    private double nextX = -1;
    private double nextY = -1;
    
    //需求服務地點
    private int pickUpNodeID; //上車Node
    private double pickUpX;
    private double pickUpY;
    
    private int dropOffNodeID; //下車Node
    private double dropOffX;
    private double dropOffY;
    
    //訂單與配對
    private Order currentOrder = null;
    private Match currentMatch = null;
    
    //歷史訂單
    private ArrayList<Order> pastOrder = new ArrayList<>();
    
    public Customer(int customerID, double tolerableWaitingTime, double walkSpeed,
    		int currentNodeID, double customerX, double customerY,
    		int pickUpNodeID, double pickUpX, double pickUpY,
    		int dropOffNodeID, double dropOffX, double dropOffY) {
    	
        this.customerID = customerID;
        this.tolerableWaitingTime = tolerableWaitingTime;
        this.walkSpeed = walkSpeed;

        this.currentNodeID = currentNodeID;
        this.customerX = customerX;
        this.customerY = customerY;
        
        this.pickUpNodeID = pickUpNodeID;
        this.pickUpX = pickUpX;
        this.pickUpY = pickUpY;
        
        this.dropOffNodeID = dropOffNodeID;
        this.dropOffX = dropOffX;
        this.dropOffY = dropOffY;
        
    }
    
    
    //基本資料
	public int getCustomerID() { //客戶ID
		return customerID;
	}

	public double getTolerableWaitingTime() { //可容忍等待時間
		return tolerableWaitingTime;
	}

	public void setTolerableWaitingTime(double tolerableWaitingTime) {
		this.tolerableWaitingTime = tolerableWaitingTime;
	}
	
	public double getWalkSpeed() { //走路速度
		return walkSpeed;
	}

	public void setWalkSpeed(double walkSpeed) {
		this.walkSpeed = walkSpeed;
	}
	
	//目前位置資訊
	public int getCurrentNodeID() { //目前node
		return currentNodeID;
	}

	public void setCurrentNodeID(int currentNodeID) {
		this.currentNodeID = currentNodeID;
	}

	public double getCustomerX() {
		return customerX;
	}

	public void setCustomerX(double customerX) {
		this.customerX = customerX;
	}

	public double getCustomerY() {
		return customerY;
	}

	public void setCustomerY(double customerY) {
		this.customerY = customerY;
	}
	


	public ArrayList<Node> getCurrentPathNode() {
		return currentPathNode;
	}


	public void setCurrentPathNode(ArrayList<Node> currentPathNode) {
		this.currentPathNode = currentPathNode;
	}


	public ArrayList<Link> getCurrentPathLink() {
		return currentPathLink;
	}


	public void setCurrentPathLink(ArrayList<Link> currentPathLink) {
		this.currentPathLink = currentPathLink;
	}

	public Link getCurrentLink() { //目前 link
		return currentLink;
	}

	public void setCurrentLink(Link currentLink) {
		this.currentLink = currentLink;
	}
	
	public int getCurrentLinkID() {
		return currentLinkID;
	}

	public void setCurrentLinkID(int currentLinkID) {
		this.currentLinkID = currentLinkID;
	}
	
	public int getIndexOfLink() {
		return indexOfLink;
	}

	public void setIndexOfLink(int indexOfLink) { //currentPathLink的Index
		this.indexOfLink = indexOfLink;
	}
	
	public double getRemainLinkTime() {
		return remainLinkTime;
	}

	public void setRemainLinkTime(double remainLinkTime) {
		this.remainLinkTime = remainLinkTime;
	}
	
	public int getNextNodeID() {
		return nextNodeID;
	}
	
	public void setNextNodeID(int nextNodeID) {
		this.nextNodeID = nextNodeID;
	}

	public double getNextX() {
		return nextX;
	}

	public void setNextX(double nextX) {
		this.nextX = nextX;
	}

	public double getNextY() {
		return nextY;
	}

	public void setNextY(double nextY) {
		this.nextY = nextY;
	}
	
	//需求服務地點
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
	
	//訂單與配對
	public Order getCurrentOrder() {
		return currentOrder;
	}

	public void setCurrentOrder(Order currentOrder) {
		this.currentOrder = currentOrder;
	}
	
	public Match getCurrentMatch() {
		return currentMatch;
	}

	public void setCurrentMatch(Match currentMatch) {
		this.currentMatch = currentMatch;
	}
	
	//歷史訂單
	public ArrayList<Order> getPastOrder() {
		return pastOrder;
	}

	public void setPastOrder(ArrayList<Order> pastOrder) {
		this.pastOrder = pastOrder;
	}
	

	public boolean isMoving() {
		return isMoving;
	}


	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}
	
	
	
	public double getRemainLinkDistance() {
		return remainLinkDistance;
	}


	public void setRemainLinkDistance(double remainLinkDistance) {
		this.remainLinkDistance = remainLinkDistance;
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
	

	public int getRequestTimes() {
		return requestTimes;
	}


	public void setRequestTimes(int requestTimes) {
		this.requestTimes = requestTimes;
	}
	
	

	public double getEstimatedTravelTime() {
		return estimatedTravelTime;
	}


	public void setEstimatedTravelTime(double estimatedTravelTime) {
		this.estimatedTravelTime = estimatedTravelTime;
	}


	//其他方法
	public Node asNode() {
        return new Node(getCurrentNodeID(), getCustomerX(), getCustomerY());
    }   
}
