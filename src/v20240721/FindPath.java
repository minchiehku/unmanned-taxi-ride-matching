package v20240721;

import java.util.ArrayList;

public class FindPath {
	public static ArrayList<Node> shortestPath(int startNodeID, int endNodeID,
			ArrayList<Node> nodeList, ArrayList<Link> linkList, int [][] arrayLinkList) {
	    
//		System.out.println("尋找最短路徑, startNode:N" + startNodeID + "endNodeID:N" +endNodeID);
		
		//準備變數
		int numNodes = nodeList.size(); //共有幾個node
	    int startNodeIndex = startNodeID - 1; //開始nodeID
	    int endNodeIndex = endNodeID - 1; //結束nodeID
	    
        // Dijkstra 準備變數, 初始化
        double[] distances = new double[numNodes]; //標記最短距離
        int[] previousNodes = new int[numNodes]; //記錄前一個點
        boolean[] visited = new boolean[numNodes]; //標記已造訪

        for (int i = 0; i < numNodes; i++) { // i= 0~299
            distances[i] = Double.MAX_VALUE;
            previousNodes[i] = -1;
            visited[i] = false;
        }

        distances[startNodeIndex] = 0;

        // 開始找最短路徑
        for (int i = 0; i < numNodes - 1; i++) {
        	
        	//準備變數
            int currentNode = -1;
            double minDistance = Double.MAX_VALUE;

            // 找到未查詢的node中 距離最短的 node
            for (int j = 0; j < numNodes; j++) {
            	//如果還沒查詢 且 距離小於最短距離
                if (!visited[j] && distances[j] < minDistance) {
                    currentNode = j; 
                    minDistance = distances[j];
                }
            }

            if (currentNode == -1) {
                break; // 所有節點都已查詢，跳出循環
            }

            visited[currentNode] = true;

            // 更新與 current 節點相鄰節點的距離
            for (int j = 0; j < numNodes; j++) { //j= 0~299
                if (arrayLinkList[currentNode][j] != -1) { //有link的話更新距離
                    double distance = linkList.get(arrayLinkList[currentNode][j] - 1).getDistance();
                    if (!visited[j] && distances[currentNode] + distance < distances[j]) {
                        distances[j] = distances[currentNode] + distance;
                        previousNodes[j] = currentNode;
                    }
                }
            }
        }

        // 產生最短路徑
        ArrayList<Node> path = new ArrayList<>();
        int current = endNodeIndex;
        //往前add 前一個node
        while (current != -1) {
            path.add(0, nodeList.get(current)); 
            current = previousNodes[current]; 
        }
        
//        System.out.print("最短path:");
//        for (Node n : path) {
//        	System.out.print("N" + n.getNodeID() + ", ");
//        }
//        System.out.println();
        
        return path;
    }
	
    public static ArrayList<Node> shortestTravelTimeOld(int startNodeID, int endNodeID,
                                                       ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
        int numNodes = nodeList.size(); // 節點總數
        int startNodeIndex = startNodeID - 1; // 起點的索引
        int endNodeIndex = endNodeID - 1; // 終點的索引

        double[] travelTimes = new double[numNodes]; // 存儲每個節點到起點的最小行車時間
        int[] previousNodes = new int[numNodes]; // 存儲每個節點的前一個節點，以重建路徑
        boolean[] visited = new boolean[numNodes]; // 記錄每個節點是否已被訪問過

        // 初始化行車時間、前一節點和訪問狀態
        for (int i = 0; i < numNodes; i++) {
            travelTimes[i] = Double.MAX_VALUE; // 初始行車時間設為無限大
            previousNodes[i] = -1; // 初始前一節點設為 -1，表示無前一節點
            visited[i] = false; // 初始設為未訪問
        }

        travelTimes[startNodeIndex] = 0; // 起點到自身的行車時間為 0

        // 主循環，找到從起點到每個節點的最短行車時間路徑
        for (int i = 0; i < numNodes - 1; i++) {
            int currentNode = -1; // 當前節點
            double minTravelTime = Double.MAX_VALUE; // 當前最小行車時間

            // 找到未訪問的節點中行車時間最小的節點
            for (int j = 0; j < numNodes; j++) {
                if (!visited[j] && travelTimes[j] < minTravelTime) {
                    currentNode = j;
                    minTravelTime = travelTimes[j];
                }
            }

            if (currentNode == -1) {
                break; // 如果找不到未訪問的節點，跳出循環
            }

            visited[currentNode] = true; // 標記當前節點為已訪問

            // 更新與當前節點相鄰的節點的行車時間
            for (int j = 0; j < numNodes; j++) {
                if (arrayLinkList[currentNode][j] != -1) { // 如果當前節點與節點 j 之間有連結
                    Link link = linkList.get(arrayLinkList[currentNode][j] - 1); // 獲取連結
                    double linkTravelTime = link.getDistance() / link.getCurrentSpeed(); // 計算行車時間

                    // 如果未訪問節點 j，且從起點到 j 的新行車時間小於已有行車時間，更新行車時間和前一節點
                    if (!visited[j] && travelTimes[currentNode] + linkTravelTime < travelTimes[j]) {
                        travelTimes[j] = travelTimes[currentNode] + linkTravelTime;
                        previousNodes[j] = currentNode;
                    }
                }
            }
        }

        // 重建從起點到終點的最短行車時間路徑
        ArrayList<Node> path = new ArrayList<>();
        int current = endNodeIndex;
        while (current != -1) {
            path.add(0, nodeList.get(current)); // 將節點添加到路徑的開頭
            current = previousNodes[current]; // 移動到前一節點
        }

        return path; // 返回最短行車時間路徑
    }


    public static ArrayList<Node> shortestTravelTime(int startNodeID, int endNodeID,
    		ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
    	
		int numNodes = nodeList.size(); // 節點總數
		int startNodeIndex = startNodeID - 1; // 起點的索引
		int endNodeIndex = endNodeID - 1; // 終點的索引
		
		double[] travelTimes = new double[numNodes]; // 存儲每個節點到起點的最小行車時間
		int[] previousNodes = new int[numNodes]; // 存儲每個節點的前一個節點，以重建路徑
		boolean[] visited = new boolean[numNodes]; // 記錄每個節點是否已被訪問過
		
		// 初始化行車時間、前一節點和訪問狀態
		for (int i = 0; i < numNodes; i++) {
		travelTimes[i] = Double.MAX_VALUE; // 初始行車時間設為無限大
		previousNodes[i] = -1; // 初始前一節點設為 -1，表示無前一節點
		visited[i] = false; // 初始設為未訪問
		}
			
		travelTimes[startNodeIndex] = 0; // 起點到自身的行車時間為 0
			
		// 主循環，找到從起點到每個節點的最短行車時間路徑
		for (int i = 0; i < numNodes - 1; i++) {
			int currentNode = -1; // 當前節點
			double minTravelTime = Double.MAX_VALUE; // 當前最小行車時間
			
			// 找到未訪問的節點中行車時間最小的節點
			for (int j = 0; j < numNodes; j++) {
				if (!visited[j] && travelTimes[j] < minTravelTime) {
				currentNode = j;
				minTravelTime = travelTimes[j];
				}
			}
				
			if (currentNode == -1) {
				break; // 如果找不到未訪問的節點，跳出循環
			}
				
			visited[currentNode] = true; // 標記當前節點為已訪問
				
			// 更新與當前節點相鄰的節點的行車時間
			for (int j = 0; j < numNodes; j++) {
				if (currentNode < arrayLinkList.length && j < arrayLinkList[currentNode].length && arrayLinkList[currentNode][j] != -1) { // 如果當前節點與節點 j 之間有連結
					Link link = linkList.get(arrayLinkList[currentNode][j] - 1); // 獲取連結
					double linkTravelTime = link.getDistance() / link.getCurrentSpeed(); // 計算行車時間
			
					// 如果未訪問節點 j，且從起點到 j 的新行車時間小於已有行車時間，更新行車時間和前一節點
					if (!visited[j] && travelTimes[currentNode] + linkTravelTime < travelTimes[j]) {
						travelTimes[j] = travelTimes[currentNode] + linkTravelTime;
						previousNodes[j] = currentNode;
					}
				}
			}
		}
			
		// 重建從起點到終點的最短行車時間路徑
		ArrayList<Node> path = new ArrayList<>();
		int current = endNodeIndex;
		while (current != -1) {
			path.add(0, nodeList.get(current)); // 將節點添加到路徑的開頭
			current = previousNodes[current]; // 移動到前一節點
		}
		
	return path; // 返回最短行車時間路徑
	}
    
    public static ArrayList<Link> pathLinkGenerator(ArrayList<Node> pathNode,ArrayList<Link> linkList, int[][] arrayLinkList) {
    	ArrayList<Link> pathLink = new ArrayList<>();
    	if(pathNode.size() < 2) { //在同個點
        	pathLink.clear();
        }else {
        	pathLink = new ArrayList<>();
	        if (!pathNode.isEmpty()) {
	            for (int i = 0; i < pathNode.size() - 1; i++) {
	                int linkId = arrayLinkList[pathNode.get(i).getNodeID() - 1][pathNode.get(i + 1).getNodeID() - 1];
	                Link link = linkList.get(linkId - 1);
	                    pathLink.add(link);
	            }
	        }
        }
    	
    	return pathLink;
    }
}
