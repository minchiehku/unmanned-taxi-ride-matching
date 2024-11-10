package v20240721;

public class Link {
    private int linkID;
    
    private Node startNode;
    private Node endNode;
    
    private double currentSpeed;
    private double targetSpeed;
    private double avgSpeed;
    private double distance;
    private double travelTime; //行駛完link要多少時間(秒)

    public Link(int linkID, 
    		Node startNode, Node endNode, 
    		double currentSpeed, double distance) {
        this.linkID = linkID;
        
        this.startNode = startNode;
        this.endNode = endNode;
        
        this.currentSpeed = currentSpeed;
        this.targetSpeed = currentSpeed;
        this.avgSpeed = currentSpeed;
        this.distance = distance;
        travelTime = (distance/currentSpeed) * 3600;
    }

	public int getLinkID() {
		return linkID;
	}

	public Node getStartNode() {
		return startNode;
	}

	public Node getEndNode() {
		return endNode;
	}

	public double getAvgSpeed() {
		return avgSpeed;
	}

	public void setAvgSpeed(double avgSpeed) {
		this.avgSpeed = avgSpeed;
		travelTime = distance/avgSpeed;
	}

	public double getDistance() {
		return distance;
	}

	public double getTravelTime() {
		return travelTime;
	}

	

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(double currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public double getTargetSpeed() {
		return targetSpeed;
	}

	public void setTargetSpeed(double targetSpeed) {
		this.targetSpeed = targetSpeed;
	}

	public void setTravelTime(double travelTime) {
		this.travelTime = travelTime;
	}

	
}
