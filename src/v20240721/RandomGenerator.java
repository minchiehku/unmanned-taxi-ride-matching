package v20240721;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class RandomGenerator {
	public static Customer forCustomer(int customerID, ArrayList<Node> nodeList, ArrayList<Link> linkList) {
		Random random = new Random();
		
		//���H�������t��
		// �̤p�M�̤j�B��t�� (����/�p��)
        double minSpeed = 3.0;
        double maxSpeed = 4.0;
        double walkSpeed = getRandomWalkingSpeed(minSpeed, maxSpeed);
        
		//�����i�e�Ե��ݮɶ�
        double tolerableWaitingTime = (5 + (random.nextDouble() * 10)) * 60;
		
		//�H�������ثenode (�s����m)
		int currentNodeID = random.nextInt(300) + 1; //1~300 
        double customerX = nodeList.get(currentNodeID - 1).getNodeX();
        double customerY = nodeList.get(currentNodeID - 1).getNodeY();
        
        //�s���I�P�W���I�O�_���P�@���I
        double probabilityOfSameNode = 0.8; // 80% �����v���P�ӤW���I
        boolean isSameNode = random.nextDouble() < probabilityOfSameNode;

        //�]�w�W���a�I
        int pickUpNodeID = currentNodeID; //�]�w�W���a�I = �s����m (�Ȯ�)
        double pickUpX = customerX;
        double pickUpY = customerY;
        
        if(isSameNode) {
            pickUpNodeID = currentNodeID; //�]�w�W���a�I = �s����m (�Ȯ�)
            pickUpX = customerX;
            pickUpY = customerY;
        }else { //���P�I, �q�F�~node�H���D�@���I
            int neighborNums = nodeList.get(currentNodeID - 1).getNeighborNodes().size();
            int neighborIndex = random.nextInt(neighborNums); //random ���]�t�̫�@�Ӥ���
            Node pickUpNode = nodeList.get(currentNodeID - 1).getNeighborNodes().get(neighborIndex);
            pickUpNodeID = pickUpNode.getNodeID();
            pickUpX = pickUpNode.getNodeX();
            pickUpY = pickUpNode.getNodeY();
        }	

          
        //�H�������U��node
        int dropOffNodeID = random.nextInt(300) + 1;
        while(dropOffNodeID == pickUpNodeID) {
        	dropOffNodeID = random.nextInt(300) + 1;
        }
        double dropOffX = nodeList.get(dropOffNodeID - 1).getNodeX();
        double dropOffY = nodeList.get(dropOffNodeID - 1).getNodeY();
        
        Customer customer = new Customer(customerID, tolerableWaitingTime, walkSpeed,
        		currentNodeID, customerX, customerY,
        		pickUpNodeID, pickUpX, pickUpY,
        		dropOffNodeID, dropOffX, dropOffY); 
        
        System.out.println("C" + customer.getCustomerID());
        System.out.println("1");
        
        return customer;
	}
	
	public static Customer forCustomerWithHotSpots(int customerID, ArrayList<Node> nodeList, ArrayList<Link> linkList
			, double currentTime, ArrayList<Node> urbanNodeList, ArrayList<Node> suburbsNodeList, ArrayList<Node> restAreaNodeList) {
		Random random = new Random();
		
		//�ϰ���v
		double urbanProb = 0.25;
		double suburbsProb = 0.25;
		double restAreaProb = 0.5;
		
		if(currentTime > 25200 && currentTime <= 32400) { //7-9 
			urbanProb = 0.5;
			suburbsProb = 0.1;
			restAreaProb = 0.4;
    	}else if(currentTime > 43200 && currentTime <= 46800) {//12-13
			urbanProb = 0.6;
			suburbsProb = 0.1;
			restAreaProb = 0.3;
    	}else if(currentTime > 61200 && currentTime <= 68400) {//17-19
			urbanProb = 0.7;
			suburbsProb = 0.1;
			restAreaProb = 0.2;
    	}else { // ��L�ɶ�
			urbanProb = 0.4;
			suburbsProb = 0.2;
			restAreaProb = 0.4;
    	}
    	
		
		// �ھھ��v��ܰϰ�
        String selectedRegion = getRandomRegion(urbanProb, suburbsProb, restAreaProb, random);
        String selectedDropOffRegion;
        System.out.println("�{�b�ɶ�:" + currentTime + ",��ܥs���ϰ�:" + selectedRegion);

        // �ھڿ�ܪ��ϰ��ܸ`�I
        ArrayList<Node> targetRegionNodes = new ArrayList<>();
        if(selectedRegion.equals("urbanArea")) {
        	targetRegionNodes = urbanNodeList;
        }else if(selectedRegion.equals("suburbsArea")) {
        	targetRegionNodes = suburbsNodeList;
		}else {
			targetRegionNodes = restAreaNodeList;
		}
        	
        // �p�G�ؼаϰ�`�I���šA�ϥΩҦ��`�I
        if (targetRegionNodes.isEmpty()) {
            targetRegionNodes = nodeList;
        }
       
        // �H����ܤ@�Ӹ`�I�@�����ȥثe���s���I
        Node currentNode = targetRegionNodes.get(random.nextInt(targetRegionNodes.size()));
		int currentNodeID = currentNode.getNodeID();
        double customerX = currentNode.getNodeX();
        double customerY = currentNode.getNodeY();
        
        //�s���I�P�W���I�O�_���P�@���I
        double probabilityOfSameNode = 0.8; // 80% �����v���P�ӤW���I
        boolean isSameNode = random.nextDouble() < probabilityOfSameNode;
        
        //�]�w�W���a�I
        int pickUpNodeID = currentNodeID; //�]�w�W���a�I = �s����m (�Ȯ�)
        double pickUpX = customerX;
        double pickUpY = customerY;
        
        if(!isSameNode) {
        	//���P�I, �q�F�~node�H���D�@���I
            int neighborNums = nodeList.get(currentNodeID - 1).getNeighborNodes().size();
            int neighborIndex = random.nextInt(neighborNums); //random ���]�t�̫�@�Ӥ���
            Node pickUpNode = nodeList.get(currentNodeID - 1).getNeighborNodes().get(neighborIndex);
            pickUpNodeID = pickUpNode.getNodeID();
            pickUpX = pickUpNode.getNodeX();
            pickUpY = pickUpNode.getNodeY();
        }
        
		// �ھھ��v��ܰϰ�U���I
        Double sameRegionProbility = 0.2;
        if (random.nextDouble() < sameRegionProbility) {
        	selectedDropOffRegion = selectedRegion;
        }else {
        	selectedDropOffRegion = getRandomRegion(urbanProb, suburbsProb, restAreaProb, random);
        	while(selectedDropOffRegion.equals(selectedRegion)) {
            	selectedDropOffRegion = getRandomRegion(urbanProb, suburbsProb, restAreaProb, random);
        	}
        }
     
        System.out.println("�{�b�ɶ�:" + currentTime + ",��ܤU���ϰ�:" + selectedDropOffRegion);

        // �ھڿ�ܪ��ϰ��ܸ`�I
        targetRegionNodes = new ArrayList<>();
        if(selectedDropOffRegion.equals("urbanArea")) {
        	targetRegionNodes = urbanNodeList;
        }else if(selectedDropOffRegion.equals("suburbsArea")) {
        	targetRegionNodes = suburbsNodeList;
		}else {
			targetRegionNodes = restAreaNodeList;
		}
        	
        // �p�G�ؼаϰ�`�I���šA�ϥΩҦ��`�I
        if (targetRegionNodes.isEmpty()) {
            targetRegionNodes = nodeList;
        }
        
        // �H����ܤ@�Ӹ`�I�@�����ȤU���I
        Node dropOffNode = targetRegionNodes.get(random.nextInt(targetRegionNodes.size()));
        while(dropOffNode.getNodeID() == pickUpNodeID) { //�קK�Pnode
        	dropOffNode = targetRegionNodes.get(random.nextInt(targetRegionNodes.size()));
        }
        int dropOffNodeID = dropOffNode.getNodeID();
        double dropOffX = nodeList.get(dropOffNodeID - 1).getNodeX();
        double dropOffY = nodeList.get(dropOffNodeID - 1).getNodeY();
        
//        //�H�������U��node
//        int dropOffNodeID = random.nextInt(300) + 1;
//        while(dropOffNodeID == pickUpNodeID) {
//        	dropOffNodeID = random.nextInt(300) + 1;
//        }
//        double dropOffX = nodeList.get(dropOffNodeID - 1).getNodeX();
//        double dropOffY = nodeList.get(dropOffNodeID - 1).getNodeY();
    	
		//���H�������t��
		// �̤p�M�̤j�B��t�� (����/�p��)
        double minSpeed = 3.0;
        double maxSpeed = 4.0;
        double walkSpeed = getRandomWalkingSpeed(minSpeed, maxSpeed);
        
		//�����i�e�Ե��ݮɶ�
        double tolerableWaitingTime = (5 + (random.nextDouble() * 10)) * 60;
        
        System.out.println("�إ߫Ȥ�C" + customerID);
        System.out.println("�s���IID" + currentNodeID);
        System.out.println("�W���IID:" + pickUpNodeID);
        System.out.println("�U���IID:" + dropOffNodeID);
        
        Customer customer = new Customer(customerID, tolerableWaitingTime, walkSpeed,
        		currentNodeID, customerX, customerY,
        		pickUpNodeID, pickUpX, pickUpY,
        		dropOffNodeID, dropOffX, dropOffY); 
        
        System.out.println("C" + customer.getCustomerID());
        System.out.println("2");
                   
        return customer;
	}
	
	public static Customer forReservedCustomer(int customerID, ArrayList<Node> nodeList, ArrayList<Link> linkList, double currentTime) {
		Random random = new Random();
		
		//���H�������t��
		// �̤p�M�̤j�B��t�� (����/�p��)
	    double minSpeed = 3.0;
	    double maxSpeed = 4.0;
	    double walkSpeed = getRandomWalkingSpeed(minSpeed, maxSpeed);
	    
		//�����i�e�Ե��ݮɶ�
		
		//�H�������W���I 
		int pickUpNodeID = random.nextInt(300) + 1; //1~300 
	    double pickUpX = nodeList.get(pickUpNodeID - 1).getNodeX();
	    double pickUpY = nodeList.get(pickUpNodeID - 1).getNodeY();       
	    
		//�ثenode (�s����m)
		int currentNodeID = pickUpNodeID;
	    double customerX = pickUpX;
	    double customerY = pickUpY;  
	    
	    //�H�������U��node
	    int dropOffNodeID = random.nextInt(300) + 1;
	    while(dropOffNodeID == pickUpNodeID) {
	    	dropOffNodeID = random.nextInt(300) + 1;
	    }
	    double dropOffX = nodeList.get(dropOffNodeID - 1).getNodeX();
	    double dropOffY = nodeList.get(dropOffNodeID - 1).getNodeY();
	    
	    Customer customer = new Customer(customerID, 0, walkSpeed,
	    		currentNodeID, customerX, customerY,
	    		pickUpNodeID, pickUpX, pickUpY,
	    		dropOffNodeID, dropOffX, dropOffY); 
	    //�]�w�w����
	    customer.setReservation(true);
	    double pickUpTimePoint = currentTime + ((30 + (random.nextDouble() * 150)) * 60); //�ثe�ɶ� + 0.5hr~3hr ����
	    customer.setReservedPickUpTimePoint(pickUpTimePoint);
	    customer.setTolerableWaitingTime(0);
	
        System.out.println("C" + customer.getCustomerID());
        System.out.println("3");
	                    
	    return customer;
	}

	public static Customer forReservedCustomerWithHotSpots(int customerID, ArrayList<Node> nodeList, ArrayList<Link> linkList,
			 double currentTime, ArrayList<Node> urbanNodeList, ArrayList<Node> suburbsNodeList, ArrayList<Node> restAreaNodeList) {
		Random random = new Random();
		
		//���H�������t��
		// �̤p�M�̤j�B��t�� (����/�p��)
        double minSpeed = 3.0;
        double maxSpeed = 4.0;
        double walkSpeed = getRandomWalkingSpeed(minSpeed, maxSpeed);
        
		//�����i�e�Ե��ݮɶ�
		
        //�ϰ���v
  		double urbanProb = 0.25;
  		double suburbsProb = 0.25;
  		double restAreaProb = 0.5;
  		
  		if(currentTime > 25200 && currentTime <= 32400) { //7-9 
  			urbanProb = 0.5;
  			suburbsProb = 0.1;
  			restAreaProb = 0.4;
      	}else if(currentTime > 43200 && currentTime <= 46800) {//12-13
  			urbanProb = 0.6;
  			suburbsProb = 0.1;
  			restAreaProb = 0.3;
      	}else if(currentTime > 61200 && currentTime <= 68400) {//17-19
  			urbanProb = 0.7;
  			suburbsProb = 0.1;
  			restAreaProb = 0.2;
      	}else { // ��L�ɶ�
  			urbanProb = 0.4;
  			suburbsProb = 0.2;
  			restAreaProb = 0.4;
      	}
      	
  		
  		// �ھھ��v��ܰϰ�
        String selectedRegion = getRandomRegion(urbanProb, suburbsProb, restAreaProb, random);
        String selectedDropOffRegion;
        System.out.println("�{�b�ɶ�:" + currentTime + ",��ܥs���ϰ�:" + selectedRegion);
          
        // �ھڿ�ܪ��ϰ��ܸ`�I
        ArrayList<Node> targetRegionNodes = new ArrayList<>();
        if(selectedRegion.equals("urbanArea")) {
          targetRegionNodes = urbanNodeList;
        }else if(selectedRegion.equals("suburbsArea")) {
          	targetRegionNodes = suburbsNodeList;
  		}else {
  			targetRegionNodes = restAreaNodeList;
  		}
      	
	    // �p�G�ؼаϰ�`�I���šA�ϥΩҦ��`�I
	    if (targetRegionNodes.isEmpty()) {
	        targetRegionNodes = nodeList;
	    }
	    
        // �H����ܤ@�Ӹ`�I�@�����ȥثe���s���I
        Node currentNode = targetRegionNodes.get(random.nextInt(targetRegionNodes.size()));
		int currentNodeID = currentNode.getNodeID();
        double customerX = currentNode.getNodeX();
        double customerY = currentNode.getNodeY();
        
		//�w����W���I�P�s���I�@�P 
		int pickUpNodeID = currentNodeID;
        double pickUpX = customerX;
        double pickUpY = customerY;
        
		// �ھھ��v��ܰϰ�U���I
        Double sameRegionProbility = 0.2;
        if (random.nextDouble() < sameRegionProbility) {
        	selectedDropOffRegion = selectedRegion;
        }else {
        	selectedDropOffRegion = getRandomRegion(urbanProb, suburbsProb, restAreaProb, random);
        	while(selectedDropOffRegion.equals(selectedRegion)) {
            	selectedDropOffRegion = getRandomRegion(urbanProb, suburbsProb, restAreaProb, random);
        	}
        }
        
        if(selectedDropOffRegion.equals("urbanArea")) {
        	targetRegionNodes = urbanNodeList;
        }else if(selectedDropOffRegion.equals("suburbsArea")) {
        	targetRegionNodes = suburbsNodeList;
		}else {
			targetRegionNodes = restAreaNodeList;
		}
        
        // �H����ܤ@�Ӹ`�I�@�����ȤU���I
        Node dropOffNode = targetRegionNodes.get(random.nextInt(targetRegionNodes.size()));
        while(dropOffNode.getNodeID() == pickUpNodeID) { //�קK�Pnode
        	dropOffNode = targetRegionNodes.get(random.nextInt(targetRegionNodes.size()));
        }
        int dropOffNodeID = dropOffNode.getNodeID();
        double dropOffX = nodeList.get(dropOffNodeID - 1).getNodeX();
        double dropOffY = nodeList.get(dropOffNodeID - 1).getNodeY();
        
//	    //�H�������U��node
//	    int dropOffNodeID = random.nextInt(300) + 1;
//	    while(dropOffNodeID == pickUpNodeID) {
//	    	dropOffNodeID = random.nextInt(300) + 1;
//	    }
//	    double dropOffX = nodeList.get(dropOffNodeID - 1).getNodeX();
//	    double dropOffY = nodeList.get(dropOffNodeID - 1).getNodeY();
        
        Customer customer = new Customer(customerID, 0, walkSpeed,
        		currentNodeID, customerX, customerY,
        		pickUpNodeID, pickUpX, pickUpY,
        		dropOffNodeID, dropOffX, dropOffY); 
        //�]�w�w����
        customer.setReservation(true);
        double pickUpTimePoint = currentTime + ((30 + (random.nextDouble() * 150)) * 60); //�ثe�ɶ� + 0.5hr~3hr ����
        customer.setReservedPickUpTimePoint(pickUpTimePoint);
        customer.setTolerableWaitingTime(0);

        System.out.println("C" + customer.getCustomerID());
        System.out.println("4");
        
        return customer;
	}
	
	public static ArrayList<Taxi> forTaxiList(int numTaxis, ArrayList<Node> nodeList) {
        ArrayList<Taxi> taxiList = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numTaxis; i++) {
            int taxiID = i + 1; // Taxi ID starts from 1
            int currentNodeID = random.nextInt(nodeList.size()) + 1; // Random node ID
            Node currentNode = nodeList.get(currentNodeID - 1); // Get the node from the list
            
            Taxi taxi = new Taxi(taxiID, currentNodeID, currentNode.getNodeX(), currentNode.getNodeY());
            taxiList.add(taxi);
        }
        return taxiList;
    }
	

	public static ArrayList<ChargingStation> forhargingStationsWithKmeans(ArrayList<Node> nodeList, int numStations) {
        ArrayList<ChargingStation> chargingStationList = new ArrayList<>();

        if (nodeList == null || nodeList.size() == 0 || numStations <= 0) {
            return chargingStationList;
        }

        ArrayList<Node> nodeListCopy = new ArrayList<>(nodeList);
        List<List<Node>> clusters = kMeansClustering(nodeListCopy, numStations);

        for (int i = 0; i < clusters.size(); i++) {
            List<Node> cluster = clusters.get(i);
            if (!cluster.isEmpty()) {
                Node centerNode = findClusterCenter(cluster);
                ChargingStation station = new ChargingStation(i + 1, centerNode.getNodeID(), centerNode.getNodeX(), centerNode.getNodeY());
                chargingStationList.add(station);
            }
        }

        return chargingStationList;
    }

    private static List<List<Node>> kMeansClustering(ArrayList<Node> nodeList, int k) {
        List<Node> centroids = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < k; i++) {
            centroids.add(nodeList.get(random.nextInt(nodeList.size())));
        }

        boolean converged = false;
        List<List<Node>> clusters = new ArrayList<>();

        while (!converged) {
            clusters.clear();
            for (int i = 0; i < k; i++) {
                clusters.add(new ArrayList<>());
            }

            for (Node node : nodeList) {
                int closestCentroidIndex = 0;
                double minDistance = Double.MAX_VALUE;

                for (int i = 0; i < centroids.size(); i++) {
                    double distance = calculateDistance(node, centroids.get(i));
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestCentroidIndex = i;
                    }
                }

                clusters.get(closestCentroidIndex).add(node);
            }

            List<Node> newCentroids = new ArrayList<>();
            for (List<Node> cluster : clusters) {
                if (!cluster.isEmpty()) {
                    newCentroids.add(findClusterCenter(cluster));
                } else {
                    newCentroids.add(centroids.get(random.nextInt(centroids.size())));
                }
            }

            converged = true;
            for (int i = 0; i < centroids.size(); i++) {
                if (calculateDistance(centroids.get(i), newCentroids.get(i)) > 1e-4) {
                    converged = false;
                    break;
                }
            }

            centroids = newCentroids;
        }

        return clusters;
    }

    private static double calculateDistance(Node n1, Node n2) {
        return Math.sqrt(Math.pow(n1.getNodeX() - n2.getNodeX(), 2) + Math.pow(n1.getNodeY() - n2.getNodeY(), 2));
    }

    private static Node findClusterCenter(List<Node> cluster) {
        double sumX = 0;
        double sumY = 0;
        for (Node node : cluster) {
            sumX += node.getNodeX();
            sumY += node.getNodeY();
        }
        double centerX = sumX / cluster.size();
        double centerY = sumY / cluster.size();

        Node closestNode = cluster.get(0);
        double minDistance = Double.MAX_VALUE;
        for (Node node : cluster) {
            double distance = Math.sqrt(Math.pow(node.getNodeX() - centerX, 2) + Math.pow(node.getNodeY() - centerY, 2));
            if (distance < minDistance) {
                minDistance = distance;
                closestNode = node;
            }
        }

        return closestNode;
    }

	
    public static ArrayList<ChargingStation> forChargingStationsWithCircle(ArrayList<Node> nodeList, int numStations) {
        ArrayList<ChargingStation> chargingStationList = new ArrayList<>();

        if (nodeList == null || nodeList.size() == 0 || numStations <= 0) {
            return chargingStationList;
        }

        // �ƻs nodeList
        ArrayList<Node> nodeListCopy = new ArrayList<>(nodeList);

        // �N nodeListCopy ���� x ���бƧǡA�p�G x �ۦP�h�� y ���бƧ�
        Collections.sort(nodeListCopy, new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                if (n1.getNodeX() != n2.getNodeX()) {
                    return Double.compare(n1.getNodeX(), n2.getNodeX());
                } else {
                    return Double.compare(n1.getNodeY(), n2.getNodeY());
                }
            }
        });

        // �N�a�Ϲ����� sqrt(numStations) * sqrt(numStations) ������
        int gridSize = (int) Math.ceil(Math.sqrt(numStations));
        double gridWidth = 10.0 / gridSize;
        double gridHeight = 10.0 / gridSize;

        // �b�C�Ӻ��椤��ܤ@�Ӹ`�I�@���R�q��
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (chargingStationList.size() >= numStations) {
                    break;
                }

                double centerX = (i + 0.5) * gridWidth;
                double centerY = (j + 0.5) * gridHeight;
                Node selectedNode = findNodeInArea(nodeListCopy, centerX, centerY, gridWidth, gridHeight);

                if (selectedNode != null) {
                    int chargerID = chargingStationList.size() + 1;
                    ChargingStation station = new ChargingStation(chargerID, selectedNode.getNodeID(), selectedNode.getNodeX(), selectedNode.getNodeY());
                    chargingStationList.add(station);
                }
            }
        }

        return chargingStationList;
    }

    private static Node findNodeInArea(ArrayList<Node> nodeList, double centerX, double centerY, double gridWidth, double gridHeight) {
        Node selectedNode = null;
        double expansionFactor = 1.0;

        while (selectedNode == null && expansionFactor <= 10.0) {
            double minX = centerX - (gridWidth / 2) * expansionFactor;
            double maxX = centerX + (gridWidth / 2) * expansionFactor;
            double minY = centerY - (gridHeight / 2) * expansionFactor;
            double maxY = centerY + (gridHeight / 2) * expansionFactor;

            for (Node node : nodeList) {
                if (node.getNodeX() >= minX && node.getNodeX() < maxX &&
                    node.getNodeY() >= minY && node.getNodeY() < maxY) {
                    selectedNode = node;
                    break;
                }
            }

            expansionFactor *= 1.5;
        }

        return selectedNode;
    }
	
    public static ArrayList<ChargingStation> forChargingStations(ArrayList<Node> nodeList, int numStations) {
        ArrayList<ChargingStation> chargingStationList = new ArrayList<>();

        if (nodeList == null || nodeList.size() == 0 || numStations <= 0) {
            return chargingStationList;
        }

        // �ƻs nodeList
        ArrayList<Node> nodeListCopy = new ArrayList<>(nodeList);

        // �N nodeListCopy ���� x ���бƧǡA�p�G x �ۦP�h�� y ���бƧ�
        Collections.sort(nodeListCopy, new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                if (n1.getNodeX() != n2.getNodeX()) {
                    return Double.compare(n1.getNodeX(), n2.getNodeX());
                } else {
                    return Double.compare(n1.getNodeY(), n2.getNodeY());
                }
            }
        });

        // �N�a�Ϲ����� sqrt(numStations) * sqrt(numStations) ������
        int gridSize = (int) Math.ceil(Math.sqrt(numStations));
        double gridWidth = 10.0 / gridSize;
        double gridHeight = 10.0 / gridSize;

        // �b�C�Ӻ��椤��ܤ@�Ӹ`�I�@���R�q��
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double minX = i * gridWidth;
                double maxX = (i + 1) * gridWidth;
                double minY = j * gridHeight;
                double maxY = (j + 1) * gridHeight;

                Node selectedNode = findNodeInArea(nodeListCopy, minX, maxX, minY, maxY);

                if (selectedNode != null) {
                    int chargerID = chargingStationList.size() + 1;
                    ChargingStation station = new ChargingStation(chargerID, selectedNode.getNodeID(), selectedNode.getNodeX(), selectedNode.getNodeY());
                    chargingStationList.add(station);
                    if (chargingStationList.size() >= numStations) {
                        return chargingStationList;
                    }
                }
            }
        }

        return chargingStationList;
    }

    private static Node findNodeInArea2(ArrayList<Node> nodeList, double minX, double maxX, double minY, double maxY) {
        Node selectedNode = null;
        double expansionFactor = 1.0;

        while (selectedNode == null) {
            for (Node node : nodeList) {
                if (node.getNodeX() >= minX && node.getNodeX() < maxX &&
                    node.getNodeY() >= minY && node.getNodeY() < maxY) {
                    selectedNode = node;
                    break;
                }
            }

            if (selectedNode == null) {
                // �X�i�j���ϰ�
                expansionFactor *= 1.5;
                minX /= expansionFactor;
                maxX *= expansionFactor;
                minY /= expansionFactor;
                maxY *= expansionFactor;

                // �����X�i�ϰ�W�X�a�Ͻd��
                minX = Math.max(minX, 0);
                maxX = Math.min(maxX, 10);
                minY = Math.max(minY, 0);
                maxY = Math.min(maxY, 10);
            }
        }

        return selectedNode;
    }
	
	public static double getRandomWalkingSpeed(double minSpeed, double maxSpeed) {
        Random random = new Random();
        return minSpeed + (maxSpeed - minSpeed) * random.nextDouble();
    }
	
	private static String getRandomRegion(double urbanProb, double suburbsProb, double restAreaProb, Random random) {
        double rand = random.nextDouble();
        if (rand <= urbanProb) {
            return "urbanArea";
        } else if (rand <= urbanProb + suburbsProb) {
            return "suburbsArea";
        } else {
            return "restArea";
        }
    }
}
