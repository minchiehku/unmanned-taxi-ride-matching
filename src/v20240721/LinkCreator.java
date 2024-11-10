package v20240721;

import java.util.ArrayList;
import java.util.Random;

public class LinkCreator {
	
	/**
	 * 建立兩點間連線link
	 * 建立每點鄰居
	 * 1. 初始化一個link計算器 linkCounter
	 * 2. 計算有多少nodes, 以相鄰矩陣來表示兩點間是否有link 
	 * 3. 開始建立link, 計算兩點間距離
	 * 4. forloop循環計算, 並建立每點的鄰居(有link的)紀錄
	 * 
	 */
	
	public static ArrayList<Link> forLinkList(ArrayList<Node> nodeList) {
        ArrayList<Link> linkList = new ArrayList<>();
        int linkCounter = 1; //計算linkID
        
        //開始建立link, 計算每個node到別的node的距離
        for (int i = 0; i < nodeList.size(); i++) {
        	
        	//初始化
        	ArrayList<Node> neighborNodeList = new ArrayList<>(); //紀錄鄰居, 順序為編號小-大
            Node startNode = nodeList.get(i); //開始點
            int currentSpeed = 0; //初始化道路速度
            Node minEndNode = null; //最短結束點
            double shortestDistance = Double.MAX_VALUE; //表示double資料類型的最大正數值。它是一個非常大的數值，約為1.7976931348623157 x 10^308。
            
            //尋找連接到的節點(EndNode)
            for (int j = 0; j < nodeList.size(); j++) {
            	//相同node跳過
                if (i == j) {
                    continue;
                }
                
                Node endNode = nodeList.get(j); //結束點
                double distance = Calculate.twoNodesDistance(startNode, endNode);
                
                //"""設定距離""" 的範圍內給予正式建立link
                if (distance <= 1) {
                    int linkNum = linkCounter; //給link編號
//                    currentSpeed = random.nextInt(41) + 20; //給予隨機路況20-60km/hr
                    currentSpeed = 60;
                    Link link = new Link(linkNum, startNode, endNode, currentSpeed, distance); //建立link
                    linkList.add(link);  //加入link集合
                    neighborNodeList.add(endNode); //儲存鄰居  
                    
                    //紀錄最小的距離
                    if (distance < shortestDistance) {
                    	shortestDistance = distance;
                    }
                    
                    linkCounter++;
                    continue;
                }
                
                //如果都大於1, 則紀錄最小的距離
                if (distance < shortestDistance) {
                	minEndNode = endNode;
                	shortestDistance = distance;
                }                
            }
            
            //"""設定距離""", 如果都沒有小於1的, 則給最短的
            if (!(shortestDistance <= 1)) {
            	int linkNum = linkCounter;            	
//            	currentSpeed = random.nextInt(41) + 20; //給予隨機路況20-60km/hr
            	currentSpeed = 60; 
            	Link link = new Link(linkNum, startNode, minEndNode, currentSpeed, shortestDistance);
                linkList.add(link);
                neighborNodeList.add(minEndNode);
                         
                linkCounter++;
            }
            
            //設定鄰居
            startNode.setNeighborNodes(neighborNodeList);
                        
        }

        return linkList;
    }
	
	public static int[][] forMatrix(ArrayList<Link> linkList, ArrayList<Node> nodeList) {
	    int numNodes = nodeList.size();  
    	int[][] arraylinks = new int[numNodes][numNodes]; //相鄰矩陣紀錄link [0-299][0-299]
    	
    	//初始化每段link, 預設沒有連接
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                arraylinks[i][j] = -1;
            }
        }
	        // 
        for (Link link : linkList) {
            Node startNode = link.getStartNode();
            Node endNode = link.getEndNode();
            int startIndex = startNode.getNodeID() - 1;
            int endIndex = endNode.getNodeID() - 1;
	        arraylinks[startIndex][endIndex] = link.getLinkID();
	        }
        return arraylinks;
    }
}
