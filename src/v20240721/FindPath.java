package v20240721;

import java.util.ArrayList;

public class FindPath {
	public static ArrayList<Node> shortestPath(int startNodeID, int endNodeID,
			ArrayList<Node> nodeList, ArrayList<Link> linkList, int [][] arrayLinkList) {
	    
//		System.out.println("�M��̵u���|, startNode:N" + startNodeID + "endNodeID:N" +endNodeID);
		
		//�ǳ��ܼ�
		int numNodes = nodeList.size(); //�@���X��node
	    int startNodeIndex = startNodeID - 1; //�}�lnodeID
	    int endNodeIndex = endNodeID - 1; //����nodeID
	    
        // Dijkstra �ǳ��ܼ�, ��l��
        double[] distances = new double[numNodes]; //�аO�̵u�Z��
        int[] previousNodes = new int[numNodes]; //�O���e�@���I
        boolean[] visited = new boolean[numNodes]; //�аO�w�y�X

        for (int i = 0; i < numNodes; i++) { // i= 0~299
            distances[i] = Double.MAX_VALUE;
            previousNodes[i] = -1;
            visited[i] = false;
        }

        distances[startNodeIndex] = 0;

        // �}�l��̵u���|
        for (int i = 0; i < numNodes - 1; i++) {
        	
        	//�ǳ��ܼ�
            int currentNode = -1;
            double minDistance = Double.MAX_VALUE;

            // ��쥼�d�ߪ�node�� �Z���̵u�� node
            for (int j = 0; j < numNodes; j++) {
            	//�p�G�٨S�d�� �B �Z���p��̵u�Z��
                if (!visited[j] && distances[j] < minDistance) {
                    currentNode = j; 
                    minDistance = distances[j];
                }
            }

            if (currentNode == -1) {
                break; // �Ҧ��`�I���w�d�ߡA���X�`��
            }

            visited[currentNode] = true;

            // ��s�P current �`�I�۾F�`�I���Z��
            for (int j = 0; j < numNodes; j++) { //j= 0~299
                if (arrayLinkList[currentNode][j] != -1) { //��link���ܧ�s�Z��
                    double distance = linkList.get(arrayLinkList[currentNode][j] - 1).getDistance();
                    if (!visited[j] && distances[currentNode] + distance < distances[j]) {
                        distances[j] = distances[currentNode] + distance;
                        previousNodes[j] = currentNode;
                    }
                }
            }
        }

        // ���ͳ̵u���|
        ArrayList<Node> path = new ArrayList<>();
        int current = endNodeIndex;
        //���eadd �e�@��node
        while (current != -1) {
            path.add(0, nodeList.get(current)); 
            current = previousNodes[current]; 
        }
        
//        System.out.print("�̵upath:");
//        for (Node n : path) {
//        	System.out.print("N" + n.getNodeID() + ", ");
//        }
//        System.out.println();
        
        return path;
    }
	
    public static ArrayList<Node> shortestTravelTimeOld(int startNodeID, int endNodeID,
                                                       ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
        int numNodes = nodeList.size(); // �`�I�`��
        int startNodeIndex = startNodeID - 1; // �_�I������
        int endNodeIndex = endNodeID - 1; // ���I������

        double[] travelTimes = new double[numNodes]; // �s�x�C�Ӹ`�I��_�I���̤p�樮�ɶ�
        int[] previousNodes = new int[numNodes]; // �s�x�C�Ӹ`�I���e�@�Ӹ`�I�A�H���ظ��|
        boolean[] visited = new boolean[numNodes]; // �O���C�Ӹ`�I�O�_�w�Q�X�ݹL

        // ��l�Ʀ樮�ɶ��B�e�@�`�I�M�X�ݪ��A
        for (int i = 0; i < numNodes; i++) {
            travelTimes[i] = Double.MAX_VALUE; // ��l�樮�ɶ��]���L���j
            previousNodes[i] = -1; // ��l�e�@�`�I�]�� -1�A��ܵL�e�@�`�I
            visited[i] = false; // ��l�]�����X��
        }

        travelTimes[startNodeIndex] = 0; // �_�I��ۨ����樮�ɶ��� 0

        // �D�`���A���q�_�I��C�Ӹ`�I���̵u�樮�ɶ����|
        for (int i = 0; i < numNodes - 1; i++) {
            int currentNode = -1; // ��e�`�I
            double minTravelTime = Double.MAX_VALUE; // ��e�̤p�樮�ɶ�

            // ��쥼�X�ݪ��`�I���樮�ɶ��̤p���`�I
            for (int j = 0; j < numNodes; j++) {
                if (!visited[j] && travelTimes[j] < minTravelTime) {
                    currentNode = j;
                    minTravelTime = travelTimes[j];
                }
            }

            if (currentNode == -1) {
                break; // �p�G�䤣�쥼�X�ݪ��`�I�A���X�`��
            }

            visited[currentNode] = true; // �аO��e�`�I���w�X��

            // ��s�P��e�`�I�۾F���`�I���樮�ɶ�
            for (int j = 0; j < numNodes; j++) {
                if (arrayLinkList[currentNode][j] != -1) { // �p�G��e�`�I�P�`�I j �������s��
                    Link link = linkList.get(arrayLinkList[currentNode][j] - 1); // ����s��
                    double linkTravelTime = link.getDistance() / link.getCurrentSpeed(); // �p��樮�ɶ�

                    // �p�G���X�ݸ`�I j�A�B�q�_�I�� j ���s�樮�ɶ��p��w���樮�ɶ��A��s�樮�ɶ��M�e�@�`�I
                    if (!visited[j] && travelTimes[currentNode] + linkTravelTime < travelTimes[j]) {
                        travelTimes[j] = travelTimes[currentNode] + linkTravelTime;
                        previousNodes[j] = currentNode;
                    }
                }
            }
        }

        // ���رq�_�I����I���̵u�樮�ɶ����|
        ArrayList<Node> path = new ArrayList<>();
        int current = endNodeIndex;
        while (current != -1) {
            path.add(0, nodeList.get(current)); // �N�`�I�K�[����|���}�Y
            current = previousNodes[current]; // ���ʨ�e�@�`�I
        }

        return path; // ��^�̵u�樮�ɶ����|
    }


    public static ArrayList<Node> shortestTravelTime(int startNodeID, int endNodeID,
    		ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
    	
		int numNodes = nodeList.size(); // �`�I�`��
		int startNodeIndex = startNodeID - 1; // �_�I������
		int endNodeIndex = endNodeID - 1; // ���I������
		
		double[] travelTimes = new double[numNodes]; // �s�x�C�Ӹ`�I��_�I���̤p�樮�ɶ�
		int[] previousNodes = new int[numNodes]; // �s�x�C�Ӹ`�I���e�@�Ӹ`�I�A�H���ظ��|
		boolean[] visited = new boolean[numNodes]; // �O���C�Ӹ`�I�O�_�w�Q�X�ݹL
		
		// ��l�Ʀ樮�ɶ��B�e�@�`�I�M�X�ݪ��A
		for (int i = 0; i < numNodes; i++) {
		travelTimes[i] = Double.MAX_VALUE; // ��l�樮�ɶ��]���L���j
		previousNodes[i] = -1; // ��l�e�@�`�I�]�� -1�A��ܵL�e�@�`�I
		visited[i] = false; // ��l�]�����X��
		}
			
		travelTimes[startNodeIndex] = 0; // �_�I��ۨ����樮�ɶ��� 0
			
		// �D�`���A���q�_�I��C�Ӹ`�I���̵u�樮�ɶ����|
		for (int i = 0; i < numNodes - 1; i++) {
			int currentNode = -1; // ��e�`�I
			double minTravelTime = Double.MAX_VALUE; // ��e�̤p�樮�ɶ�
			
			// ��쥼�X�ݪ��`�I���樮�ɶ��̤p���`�I
			for (int j = 0; j < numNodes; j++) {
				if (!visited[j] && travelTimes[j] < minTravelTime) {
				currentNode = j;
				minTravelTime = travelTimes[j];
				}
			}
				
			if (currentNode == -1) {
				break; // �p�G�䤣�쥼�X�ݪ��`�I�A���X�`��
			}
				
			visited[currentNode] = true; // �аO��e�`�I���w�X��
				
			// ��s�P��e�`�I�۾F���`�I���樮�ɶ�
			for (int j = 0; j < numNodes; j++) {
				if (currentNode < arrayLinkList.length && j < arrayLinkList[currentNode].length && arrayLinkList[currentNode][j] != -1) { // �p�G��e�`�I�P�`�I j �������s��
					Link link = linkList.get(arrayLinkList[currentNode][j] - 1); // ����s��
					double linkTravelTime = link.getDistance() / link.getCurrentSpeed(); // �p��樮�ɶ�
			
					// �p�G���X�ݸ`�I j�A�B�q�_�I�� j ���s�樮�ɶ��p��w���樮�ɶ��A��s�樮�ɶ��M�e�@�`�I
					if (!visited[j] && travelTimes[currentNode] + linkTravelTime < travelTimes[j]) {
						travelTimes[j] = travelTimes[currentNode] + linkTravelTime;
						previousNodes[j] = currentNode;
					}
				}
			}
		}
			
		// ���رq�_�I����I���̵u�樮�ɶ����|
		ArrayList<Node> path = new ArrayList<>();
		int current = endNodeIndex;
		while (current != -1) {
			path.add(0, nodeList.get(current)); // �N�`�I�K�[����|���}�Y
			current = previousNodes[current]; // ���ʨ�e�@�`�I
		}
		
	return path; // ��^�̵u�樮�ɶ����|
	}
    
    public static ArrayList<Link> pathLinkGenerator(ArrayList<Node> pathNode,ArrayList<Link> linkList, int[][] arrayLinkList) {
    	ArrayList<Link> pathLink = new ArrayList<>();
    	if(pathNode.size() < 2) { //�b�P���I
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
