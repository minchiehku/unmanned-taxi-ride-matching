package v20240721;

import java.util.ArrayList;

public class Taxi {
	
    private int taxiID;
    //基本資料
    private double taxiSpeed;
    private boolean isMoving = false;
    private double batteryLevel = 300; //滿電續航里程(km)
    private int targetChargingStationNodeID = -1;
    private int targetChargingStationID = -1;
    private int indexOfStation = -1;
    
    //目前位置
    private int currentNodeID; //目前 Node
    private double taxiX;
    private double taxiY;
    
    private ArrayList<Node> currentPathNode = new ArrayList<>();
    private ArrayList<Link> currentPathLink = new ArrayList<>();
    
    private int linkID; //目前所在Link
    private Link currentLink = null;
    private int indexOfLink; //path中的index
    private double remainLinkDistance;
    private double remainLinkTime;
    
    private int nextNodeID; //下一個 node
    private double nextX;
    private double nextY;

    private int oldTaxiFinalNodeID;
    
    //taxi狀態
    private Match currentMatch;
    private Order currentOrder;
    private Event currentEvent;

    public Taxi(int taxiID, 
    		int currentNodeID,double taxiX, double taxiY) {
        this.taxiID = taxiID;
        this.currentNodeID = currentNodeID;
        this.taxiX = taxiX;
        this.taxiY = taxiY;
    }

	public int getTaxiID() {
		return taxiID;
	}

	public int getCurrentNodeID() {
		return currentNodeID;
	}

	public void setCurrentNodeID(int currentNodeID) {
		this.currentNodeID = currentNodeID;
	}

	public double getTaxiX() {
		return taxiX;
	}

	public void setTaxiX(double taxiX) {
		this.taxiX = taxiX;
	}

	public double getTaxiY() {
		return taxiY;
	}

	public void setTaxiY(double taxiY) {
		this.taxiY = taxiY;
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

	public Node asNode() {
        return new Node(getCurrentNodeID(), getTaxiX(), getTaxiY());
    }

	public int getLinkID() {
		return linkID;
	}

	public void setLinkID(int linkID) {
		this.linkID = linkID;
	}

	public double getCurrentSpeed() {
		return taxiSpeed;
	}

	public void setCurrentSpeed(double currentSpeed) {
		this.taxiSpeed = currentSpeed;
	}

	public double getRemainLinkTime() {
		return remainLinkTime;
	}

	public void setRemainLinkTime(double remainLinkTime) {
		this.remainLinkTime = remainLinkTime;
	}

	public Match getCurrentMatch() {
		return currentMatch;
	}

	public void setCurrentMatch(Match currentMatch) {
		this.currentMatch = currentMatch;
	}

	public Link getCurrentLink() {
		return currentLink;
	}

	public void setCurrentLink(Link currentLink) {
		this.currentLink = currentLink;
		if(currentLink == null) {
			this.taxiSpeed = 0;
		}else{
			this.taxiSpeed = currentLink.getAvgSpeed();
		}
	}

	public Order getCurrentOrder() {
		return currentOrder;
	}

	public void setCurrentOrder(Order currentOrder) {
		this.currentOrder = currentOrder;
	}

	public int getIndexOfLink() {
		return indexOfLink;
	}

	public void setIndexOfLink(int indexOfLink) {
		this.indexOfLink = indexOfLink;
	}

	public double getTaxiSpeed() {
		return taxiSpeed;
	}

	public void setTaxiSpeed(double taxiSpeed) {
		this.taxiSpeed = taxiSpeed;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
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
		if(currentPathLink == null) {
			System.out.println("被清空了");
		}
	}

	public double getRemainLinkDistance() {
		return remainLinkDistance;
	}

	public void setRemainLinkDistance(double remainLinkDistance) {
		this.remainLinkDistance = remainLinkDistance;
	}

	public double getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public int getTargetChargingStationNodeID() {
		return targetChargingStationNodeID;
	}

	public void setTargetChargingStationNodeID(int targetChargingStationNodeID) {
		this.targetChargingStationNodeID = targetChargingStationNodeID;
	}

	public int getTargetChargingStationID() {
		return targetChargingStationID;
	}

	public void setTargetChargingStationID(int targetChargingStationID) {
		this.targetChargingStationID = targetChargingStationID;
	}

	public int getIndexOfStation() {
		return indexOfStation;
	}

	public void setIndexOfStation(int indexOfStation) {
		this.indexOfStation = indexOfStation;
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(Event currentEvent) {
		this.currentEvent = currentEvent;
	}

	public int getOldTaxiFinalNodeID() {
		return oldTaxiFinalNodeID;
	}

	public void setOldTaxiFinalNodeID(int oldTaxiFinalNodeID) {
		this.oldTaxiFinalNodeID = oldTaxiFinalNodeID;
	}
	
	

}
