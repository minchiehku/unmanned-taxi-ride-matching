package v20240721;

import java.util.ArrayList;


public class Node {
    private int nodeID;
    private double nodeX;
    private double nodeY;
    
    private ArrayList<Node> neighborNodes = new ArrayList<>(); // 儲存有連通link的節點
    private boolean canPark; //此點是否能停車(上下車)
    private boolean haveChargingStation = false;
    private int chargingStationID = -1;


    public Node(int nodeID, double nodeX, double nodeY) {
        this.nodeID = nodeID;
        this.nodeX = nodeX;
        this.nodeY = nodeY;
    }

	public int getNodeID() {
		return nodeID;
	}

	public double getNodeX() {
		return nodeX;
	}

	public void setNodeX(double nodeX) {
		this.nodeX = nodeX;
	}

	public double getNodeY() {
		return nodeY;
	}

	public void setNodeY(double nodeY) {
		this.nodeY = nodeY;
	}

	public ArrayList<Node> getNeighborNodes() {
		return neighborNodes;
	}

	public void setNeighborNodes(ArrayList<Node> neighborNodes) {
		this.neighborNodes = neighborNodes;
	}

	public boolean isCanPark() {
		return canPark;
	}

	public void setCanPark(boolean canPark) {
		this.canPark = canPark;
	}

	public boolean isHaveChargingStation() {
		return haveChargingStation;
	}

	public void setHaveChargingStation(boolean haveChargingStation) {
		this.haveChargingStation = haveChargingStation;
	}

	public int getChargingStationID() {
		return chargingStationID;
	}

	public void setChargingStationID(int chargingStationNodeID) {
		this.chargingStationID = chargingStationNodeID;
	}
	
	

}
