package v20240721;

import java.util.ArrayList;

public class Calculate {
	public static double twoNodesDistance(Node startNode, Node endNode) {
        double x1 = startNode.getNodeX();
        double y1 = startNode.getNodeY();
        double x2 = endNode.getNodeX();
        double y2 = endNode.getNodeY();

        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
	
	public static double drivingTimeByShortestPath(int startNodeID, int endNodeID, ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
        ArrayList<Node> shortestPath = FindPath.shortestPath(startNodeID, endNodeID, nodeList, linkList, arrayLinkList); // ¶Ç»¼arraylinks
        double totalTravelTime = 0;

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Node node1 = shortestPath.get(i);
            Node node2 = shortestPath.get(i + 1);
            double distance = Calculate.twoNodesDistance(node1, node2);
            double linkSpeed = linkList.get(arrayLinkList[node1.getNodeID() - 1][node2.getNodeID() - 1] - 1).getCurrentSpeed();
            double travelTime = (distance / linkSpeed) * 60 * 60;
            totalTravelTime += travelTime;
        }

        return totalTravelTime;
    }
	
	public static double drivingTimeByShortestTravelTimePath(int startNodeID, int endNodeID, ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
        ArrayList<Node> path = FindPath.shortestTravelTime(startNodeID, endNodeID, nodeList, linkList, arrayLinkList); // ¶Ç»¼arraylinks
        double totalTravelTime = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            Node node1 = path.get(i);
            Node node2 = path.get(i + 1);
            double distance = Calculate.twoNodesDistance(node1, node2);
            double linkSpeed = linkList.get(arrayLinkList[node1.getNodeID() - 1][node2.getNodeID() - 1] - 1).getCurrentSpeed();
            double travelTime = (distance / linkSpeed) * 60 * 60;
            totalTravelTime += travelTime;
        }

        return totalTravelTime;
    }
	
	public static double WalkingTimeByShortestPath(Customer customer, int startNodeID, int endNodeID, ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
    	double walkSpeed = customer.getWalkSpeed();
        ArrayList<Node> shortestPath = FindPath.shortestPath(startNodeID, endNodeID, nodeList, linkList, arrayLinkList); // ¶Ç»¼arraylinks
        double totalTravelTime = 0;


        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Node node1 = shortestPath.get(i);
            Node node2 = shortestPath.get(i + 1);
            double distance = Calculate.twoNodesDistance(node1, node2);
            double travelTime = (distance / walkSpeed) * 60 * 60;
            totalTravelTime += travelTime;
        }

        return totalTravelTime;
    }
	
	public static double pathTotalDistance(ArrayList<Link> pathLink) {
		double totalDistance = 0;
		
		for(Link link : pathLink) {
			totalDistance += link.getDistance();
		}
		
		return totalDistance;
	}
}
