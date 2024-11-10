package v20240721;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;


public class RideMatch {
	
	public static ArrayList<Match> shortestPath(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��

        	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDrivingTime = Double.MAX_VALUE;
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {
//	            	System.out.println("T"+taxi.getTaxiID()+"�bN"+taxi.getCurrentNodeID());
		            double drivingTime = Calculate.drivingTimeByShortestPath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if (drivingTime < minDrivingTime) {
		                minDrivingTime = drivingTime;
		                matchedTaxi = taxi;
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer customer : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(customer);
	    }
	    
	    return newMatchList;
	}
	
	public static ArrayList<Match> shortestTravelTimePath(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��

        	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDrivingTime = Double.MAX_VALUE;
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {
		            double drivingTime = Calculate.drivingTimeByShortestPath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if (drivingTime < minDrivingTime) {
		                minDrivingTime = drivingTime;
		                matchedTaxi = taxi;
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer customer : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(customer);
	    }
	    
	    return newMatchList;
	}
	
	public static ArrayList<Match> withTolerableTime(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��

        	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
//		                System.out.println("�i�e�Ԯɶ�:" + customer.getTolerableWaitingTime() +
//		                		", drivingTime:" + drivingTime + ", tempDiff:" + diff);
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer customer : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(customer);
	    }
	    
	    return newMatchList;
	}
	
	public static ArrayList<Match> priorityTolerableTime(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��

        	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
		                if(diff < 0) {
		                	break; //�t���e�Ԯɶ����Nbreak
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer customer : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(customer);
	    }
	    
	    return newMatchList;
	}
	
	public static ArrayList<Match> priorityTolerableTimeWithReservedCustomer(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��
	    	  	
	    	//�T�{�O�_�w��
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //�p��pending�ɶ�
	    			double sumDrivingTime = 0;
	    			double serviceableTaxi = 0;
	    			for(Taxi taxi : taxiList) {
	    				if (matchTaxiList.contains(taxi)) {
	    					continue;
	    				} else {
	    				sumDrivingTime += Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
			            serviceableTaxi++;
	    				}
	    			}
	    			
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min�w���Y��
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //�~��
	    			
	    		}else { //�w�g���b����
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //�~��
	    				System.out.println("C" + customer.getCustomerID() + "�~��");
	    				continue;
	    			}else {
	    				System.out.println("C" + customer.getCustomerID() + "�t�����");
	    			}
	    		}
	    	}
	    	
	       	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;    
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
		                if(diff < 0) {
		                	break; //�t���e�Ԯɶ����Nbreak
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer c : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(c);
	    }
	    
	    return newMatchList;
	    
	}
	
	
	public static ArrayList<Match> priorityTolerableTimeWithReservedCustomerWithCharging(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��
	    	  	
	    	//�T�{�O�_�w��
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //�p��pending�ɶ�
	    			double sumDrivingTime = 0;
	    			double serviceableTaxi = 0; 
	    			for(Taxi taxi : taxiList) {
	    				if (matchTaxiList.contains(taxi)) {
	    					continue;
	    				} else {
	    				sumDrivingTime += Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), 
	    						taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
			            serviceableTaxi++;
	    				}
	    			}
	    			 
	    			//�p��ǰt���ɶ� = �Ȥ�w���ɶ� - (�p�{��������F�W���I���ɶ� + �w���Y��)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min�w���Y��
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //�~��
	    			
	    		}else { //�w�g���b����
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //�~��
	    				System.out.println("C" + customer.getCustomerID() + "�~��");
	    				continue;
	    			}else { 
	    				System.out.println("�t�����");
	    			}
	    		}
	    	}
	    	
	       	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        
	        //�p��� �U���I �P ��̪�R�q���Z��
	        System.out.println(customer.getPickUpNodeID() + ", " + customer.getDropOffNodeID());
	        
        	ArrayList<Node> toDropOffPathNode = FindPath.shortestTravelTime(customer.getPickUpNodeID(), customer.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
         	ArrayList<Link> toDropOffPathLink = FindPath.pathLinkGenerator(toDropOffPathNode, linkList, arrayLinkList);
         	sumPathDistance = Calculate.pathTotalDistance(toDropOffPathLink);
         	
         	if(!nodeList.get(customer.getDropOffNodeID() - 1).isHaveChargingStation()) {
             	int  nearestStationNodeID = findNearestStation(customer.getDropOffNodeID(), chargingStationList, nodeList, linkList, arrayLinkList);
             	ArrayList<Node> toChargingStationPathNode = FindPath.shortestTravelTime(customer.getDropOffNodeID(), nearestStationNodeID, nodeList, linkList, arrayLinkList);
             	ArrayList<Link> toChargingStationPathLink = FindPath.pathLinkGenerator(toChargingStationPathNode, linkList, arrayLinkList);
             	sumPathDistance += Calculate.pathTotalDistance(toChargingStationPathLink);
         	}
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("�]�tT:" + taxi.getTaxiID());
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {

	            	//�p���`��{, �T�{�q�q
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"�q�q:" + taxi.getBatteryLevel() + ", �`��{" + sumPathDistance + "�q�q����");
	            		continue;
	            	}
	            		
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
		                if(diff < 0) {
		                	break; //�t���e�Ԯɶ����Nbreak
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer c : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(c);
	    }
	    
	    
	    return newMatchList;
	    
	}
	
	public static ArrayList<Match> adjustWorstDiff(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��
	    	  	
	    	//�T�{�O�_�w��
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //�p��pending�ɶ�
	    			double sumDrivingTime = 0;
	    			double serviceableTaxi = 0;
	    			for(Taxi taxi : taxiList) {
	    				if (matchTaxiList.contains(taxi)) {
	    					continue;
	    				} else {
	    				sumDrivingTime += Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
			            serviceableTaxi++;
	    				}
	    			}
	    			
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min�w���Y��
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //�~��
	    			
	    		}else { //�w�g���b����
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //�~��
	    				System.out.println("C" + customer.getCustomerID() + "�~��");
	    				continue;
	    			}else {
	    				System.out.println("�t�����");
	    			}
	    		}
	    	}
	    	
	       	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        
	        //�p��� �U���I �P ��̪�R�q���Z��
        	ArrayList<Node> toDropOffPathNode = FindPath.shortestTravelTime(customer.getPickUpNodeID(), customer.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
         	ArrayList<Link> toDropOffPathLink = FindPath.pathLinkGenerator(toDropOffPathNode, linkList, arrayLinkList);
         	sumPathDistance = Calculate.pathTotalDistance(toDropOffPathLink);
         	
         	if(!nodeList.get(customer.getDropOffNodeID() - 1).isHaveChargingStation()) {
             	int  nearestStationNodeID = findNearestStation(customer.getDropOffNodeID(), chargingStationList, nodeList, linkList, arrayLinkList);
             	ArrayList<Node> toChargingStationPathNode = FindPath.shortestTravelTime(customer.getDropOffNodeID(), nearestStationNodeID, nodeList, linkList, arrayLinkList);
             	ArrayList<Link> toChargingStationPathLink = FindPath.pathLinkGenerator(toChargingStationPathNode, linkList, arrayLinkList);
             	sumPathDistance += Calculate.pathTotalDistance(toChargingStationPathLink);
         	}
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("�]�tT:" + taxi.getTaxiID());
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {

	            	//�p���`��{, �T�{�q�q
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"�q�q:" + taxi.getBatteryLevel() + ", �`��{" + sumPathDistance + "�q�q����");
	            		continue;
	            	}
	            	
	            	//�p�ⵥ�ݮɶ�,
	            	if(!customer.isReservation()) {
	            		//��ڵ��ݮɶ� = (�Ȥ�s�� �� �Ȥ�P�p�{������W���I�ɶ�) - ( �Ȥ�q�s���I���ʨ�W���I�ɶ� ) 
	            		Order order = customer.getCurrentOrder();
	            		double requestTimePoint = order.getOrderTimePoint().get(1);
			            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
			            double walkingTime = Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
			            double tempMatchedTimePoint = currentTime;
			            double estimatedPickUpTimePoint; //(5)����W���I
			            double waitingTime = 0;
			            if (drivingTime > walkingTime) {//�p�{����Ȥ�ߨ�, ���ݮɶ��n�����Ȥᨫ���ɶ�
			            	estimatedPickUpTimePoint = tempMatchedTimePoint + drivingTime;
			            	waitingTime  = (estimatedPickUpTimePoint - requestTimePoint) - walkingTime;
			            }else {//�Ȥ��p�{���ߨ�, �S������, �ȵ��ݤǰt�ɶ�
			            	estimatedPickUpTimePoint = tempMatchedTimePoint + walkingTime;
			            	waitingTime = tempMatchedTimePoint - requestTimePoint; //���ݳQ�ǰt���ɶ�
			            }
			            double diff = waitingTime - customer.getTolerableWaitingTime();
			            double penalty = Math.pow(diff, 2); //����
			            ArrayList<Node> pathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
			            ArrayList<Link> pathLink = FindPath.pathLinkGenerator(pathNode, linkList, arrayLinkList);
			            double taxiToPickUpDistance = Calculate.pathTotalDistance(pathLink);
			            double maxPenalty = Double.MIN_VALUE;
			            double minDistance = Double.MAX_VALUE;
			            
			            //��̤j��
			            if(penalty > maxPenalty && taxiToPickUpDistance <= minDistance) {
			            	matchedTaxi = taxi;
			            }
			           
			            
	            	}
	            	
	            	
	            	
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
		                if(diff < 0) {
		                	break; //�t���e�Ԯɶ����Nbreak
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer c : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(c);
	    }
	    
	    return newMatchList;
	    
	}
	
	public static ArrayList<Match> reservedPriority(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime, int pendingReservedOrderNums){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		//�Ƨ�customerList
		sortCustomersByReservation(customerList);
		
		
		System.out.println("�Ѿl����:" + taxiList.size());
		//�L�X
//		for(Customer c : customerList) {
//			System.out.println(" C"+c.getCustomerID());
//			System.out.println("O" + c.getCurrentOrder().getOrderID());
//			System.out.println(" �w��:"+c.isReservation());
//			System.out.println("�ɶ�:" + c.getReservedPickUpTimePoint());
//
//		}
//		
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
		int remainPendingCustomer = pendingReservedOrderNums;
		int remainTaxiNums = taxiList.size();
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��
	    	  	
	    	//�T�{�O�_�w��
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //�p��pending�ɶ�
	    			double sumDrivingTime = 0;
	    			double serviceableTaxi = 0; 
	    			for(Taxi taxi : taxiList) {
	    				if (matchTaxiList.contains(taxi)) {
	    					continue;
	    				} else {
	    				sumDrivingTime += Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), 
	    						taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
			            serviceableTaxi++;
	    				}
	    			}
	    			 
	    			//�p��ǰt���ɶ� = �Ȥ�w���ɶ� - (�p�{��������F�W���I���ɶ� + �w���Y��)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min�w���Y��
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //�~��
	    			
	    		}else { //�w�g���b����
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //�~��
	    				System.out.println("C" + customer.getCustomerID() + "�~��");
	    				continue;
	    			}else { 
	    				System.out.println("�t�����");
	    			}
	    		}
	    	}else { //�i�J�D�w����
	    		System.out.println("�Ѿltaxi:" + remainTaxiNums + ", �Ѿlpending:" + remainPendingCustomer);
	    		
	    		//�p��p�G�ثe�̪�pending�٫ܤ[, �����t��
	    		if(remainTaxiNums <= remainPendingCustomer) {
	    			break;
	    		}
	    	}
	    		    	
	       	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        
	        //�p��� �U���I �P ��̪�R�q���Z��
	        System.out.println(customer.getPickUpNodeID() + ", " + customer.getDropOffNodeID());
	        
        	ArrayList<Node> toDropOffPathNode = FindPath.shortestTravelTime(customer.getPickUpNodeID(), customer.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
         	ArrayList<Link> toDropOffPathLink = FindPath.pathLinkGenerator(toDropOffPathNode, linkList, arrayLinkList);
         	sumPathDistance = Calculate.pathTotalDistance(toDropOffPathLink);
         	
         	if(!nodeList.get(customer.getDropOffNodeID() - 1).isHaveChargingStation()) {
             	int  nearestStationNodeID = findNearestStation(customer.getDropOffNodeID(), chargingStationList, nodeList, linkList, arrayLinkList);
             	ArrayList<Node> toChargingStationPathNode = FindPath.shortestTravelTime(customer.getDropOffNodeID(), nearestStationNodeID, nodeList, linkList, arrayLinkList);
             	ArrayList<Link> toChargingStationPathLink = FindPath.pathLinkGenerator(toChargingStationPathNode, linkList, arrayLinkList);
             	sumPathDistance += Calculate.pathTotalDistance(toChargingStationPathLink);
         	}
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("�]�tT:" + taxi.getTaxiID());
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {

	            	//�p���`��{, �T�{�q�q
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"�q�q:" + taxi.getBatteryLevel() + ", �`��{" + sumPathDistance + "�q�q����");
	            		continue;
	            	}
	            		
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
		                if(diff < 0) {
		                	break; //�t���e�Ԯɶ����Nbreak
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak
	        
	        remainPendingCustomer--;
	        remainTaxiNums--;
	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer c : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(c);
	    }
	    
	    
	    return newMatchList;
	    
	}
	

	public static ArrayList<Match> negativeDiffAndNearest(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime, int pendingReservedOrderNums, boolean processWaitingTooLong){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		//�Ƨ�customerList
		sortCustomersForTolerableWaitingTime(customerList);
		
		if(processWaitingTooLong == true) {
			prioritizeCustomersWaitingTooLong(customerList, currentTime, nodeList, linkList, arrayLinkList);
		}
		
		System.out.println("�Ѿl����:" + taxiList.size());
		//�L�X
//		for(Customer c : customerList) {
//			System.out.println(" C"+c.getCustomerID());
//			System.out.println("O" + c.getCurrentOrder().getOrderID());
//			System.out.println(" �w��:"+c.isReservation());
//			System.out.println("�ɶ�:" + c.getReservedPickUpTimePoint());
//
//		}
		
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
		int remainPendingCustomer = pendingReservedOrderNums;
		int remainTaxiNums = taxiList.size();
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��
	    	  	
	    	//�T�{�O�_�w��
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //�p��pending�ɶ�
	    			double sumDrivingTime = 0;
	    			double serviceableTaxi = 0; 
	    			for(Taxi taxi : taxiList) {
	    				if (matchTaxiList.contains(taxi)) {
	    					continue;
	    				} else {
	    				sumDrivingTime += Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), 
	    						taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
			            serviceableTaxi++;
	    				}
	    			}
	    			 
	    			//�p��ǰt���ɶ� = �Ȥ�w���ɶ� - (�p�{��������F�W���I���ɶ� + �w���Y��)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min�w���Y��
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //�~��
	    			
	    		}else { //�w�g���b����
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //�~��
	    				System.out.println("C" + customer.getCustomerID() + "�~��");
	    				continue;
	    			}else { 
	    				System.out.println("�t�����");
	    			}
	    		}
	    	}else { //�i�J�D�w����
	    		System.out.println("�Ѿltaxi:" + remainTaxiNums + ", �Ѿlpending:" + remainPendingCustomer);
	    		
	    		//�p��p�G�ثe�̪�pending�٫ܤ[, �����t��
	    		if(remainTaxiNums <= remainPendingCustomer) {
	    			break;
	    		}
	    	}
	    		    	
	       	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        double walkingTime = 0;
	        double requestTimePoint = customer.getCurrentOrder().getOrderTimePoint().get(1);
	        double estimatedPickUpTimePoint = 0;
	        
	        
	        //�p��walkingTime
	        if(customer.getCurrentNodeID() != customer.getPickUpNodeID()) {
	        	walkingTime = Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	        }
	        
	        //�p��� �U���I �P ��̪�R�q���Z��
	        System.out.println(customer.getPickUpNodeID() + ", " + customer.getDropOffNodeID());
	        
        	ArrayList<Node> toDropOffPathNode = FindPath.shortestTravelTime(customer.getPickUpNodeID(), customer.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
         	ArrayList<Link> toDropOffPathLink = FindPath.pathLinkGenerator(toDropOffPathNode, linkList, arrayLinkList);
         	sumPathDistance = Calculate.pathTotalDistance(toDropOffPathLink);
         	
         	if(!nodeList.get(customer.getDropOffNodeID() - 1).isHaveChargingStation()) {
             	int  nearestStationNodeID = findNearestStation(customer.getDropOffNodeID(), chargingStationList, nodeList, linkList, arrayLinkList);
             	ArrayList<Node> toChargingStationPathNode = FindPath.shortestTravelTime(customer.getDropOffNodeID(), nearestStationNodeID, nodeList, linkList, arrayLinkList);
             	ArrayList<Link> toChargingStationPathLink = FindPath.pathLinkGenerator(toChargingStationPathNode, linkList, arrayLinkList);
             	sumPathDistance += Calculate.pathTotalDistance(toChargingStationPathLink);
         	}
         	
         	double minDrivingTime = Double.MAX_VALUE;
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("�]�tT:" + taxi.getTaxiID());
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {

	            	//�p���`��{, �T�{�q�q
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"�q�q:" + taxi.getBatteryLevel() + ", �`��{" + sumPathDistance + "�q�q����");
	            		continue;
	            	}
	            	
	            	//�ǰt���
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if(drivingTime > walkingTime) {
		            	estimatedPickUpTimePoint = currentTime + drivingTime;
		            }else {
		            	estimatedPickUpTimePoint = currentTime + walkingTime;
		            }
		            //diff = ��ڵ��ݮɶ� - �i�e�Ե��ݮɶ�
		            double diff = ((estimatedPickUpTimePoint - requestTimePoint) - walkingTime) - customer.getTolerableWaitingTime();
		            System.out.println(String.format("T%d, �w�]�W���ɶ��I:%.2f , �s���ɶ��I:%.2f, �����ɶ�:%.2f, �i�e�Ԯɶ�:%.2f, diff:%.2f",
		            		taxi.getTaxiID(), estimatedPickUpTimePoint, requestTimePoint, walkingTime, customer.getTolerableWaitingTime(), diff));
		            //��̪�
		            if(diff < 0 && drivingTime < minDrivingTime) {
		            	minDrivingTime = drivingTime;
		            	matchedTaxi = taxi;
		            }
		            
		            //�p�G�S���N��diff�̤p
		            if(matchedTaxi == null) {
			            if (diff < minDiff) {
			            	minDiff = diff;
			                matchedTaxi = taxi;
			            }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak
	        
	        remainPendingCustomer--;
	        remainTaxiNums--;
	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer c : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(c);
	    }
	    
	    
	    return newMatchList;
	    
	}

	
	public static ArrayList<Match> negativeDiffAndShortestPath(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime, int pendingReservedOrderNums, boolean processWaitingTooLong){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		//�Ƨ�customerList
		sortCustomersForShortestPath(customerList, nodeList, linkList, arrayLinkList);
		
		if(processWaitingTooLong == true) {
			prioritizeCustomersWaitingTooLong(customerList, currentTime, nodeList, linkList, arrayLinkList);
		}
		
		System.out.println("�Ѿl����:" + taxiList.size());
//		//�L�X
//		for(Customer c : customerList) {
//			System.out.println(" C"+c.getCustomerID());
//			System.out.println("O" + c.getCurrentOrder().getOrderID());
//			System.out.println(" �w��:"+c.isReservation());
//			System.out.println("�ɶ�:" + c.getReservedPickUpTimePoint());
//
//		}
		
		
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
		int remainPendingCustomer = pendingReservedOrderNums;
		int remainTaxiNums = taxiList.size();
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��
	    	  	
	    	//�T�{�O�_�w��
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //�p��pending�ɶ�
	    			double sumDrivingTime = 0;
	    			double serviceableTaxi = 0; 
	    			for(Taxi taxi : taxiList) {
	    				if (matchTaxiList.contains(taxi)) {
	    					continue;
	    				} else {
	    				sumDrivingTime += Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), 
	    						taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
			            serviceableTaxi++;
	    				}
	    			}
	    			 
	    			//�p��ǰt���ɶ� = �Ȥ�w���ɶ� - (�p�{��������F�W���I���ɶ� + �w���Y��)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min�w���Y��
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //�~��
	    			
	    		}else { //�w�g���b����
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //�~��
	    				System.out.println("C" + customer.getCustomerID() + "�~��");
	    				continue;
	    			}else { 
	    				System.out.println("�t�����");
	    			}
	    		}
	    	}else { //�i�J�D�w����
	    		System.out.println("�Ѿltaxi:" + remainTaxiNums + ", �Ѿlpending:" + remainPendingCustomer);
	    		
	    		//�p��p�G�ثe�̪�pending�٫ܤ[, �����t��
	    		if(remainTaxiNums <= remainPendingCustomer) {
	    			break;
	    		}
	    	}
	    		    	
	       	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        double walkingTime = 0;
	        double requestTimePoint = customer.getCurrentOrder().getOrderTimePoint().get(1);
	        double estimatedPickUpTimePoint = 0;
	        
	        
	        //�p��walkingTime
	        if(customer.getCurrentNodeID() != customer.getPickUpNodeID()) {
	        	walkingTime = Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	        }
	        
	        //�p��� �U���I �P ��̪�R�q���Z��
	        System.out.println(customer.getPickUpNodeID() + ", " + customer.getDropOffNodeID());
	        
        	ArrayList<Node> toDropOffPathNode = FindPath.shortestTravelTime(customer.getPickUpNodeID(), customer.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
         	ArrayList<Link> toDropOffPathLink = FindPath.pathLinkGenerator(toDropOffPathNode, linkList, arrayLinkList);
         	sumPathDistance = Calculate.pathTotalDistance(toDropOffPathLink);
         	
         	if(!nodeList.get(customer.getDropOffNodeID() - 1).isHaveChargingStation()) {
             	int  nearestStationNodeID = findNearestStation(customer.getDropOffNodeID(), chargingStationList, nodeList, linkList, arrayLinkList);
             	ArrayList<Node> toChargingStationPathNode = FindPath.shortestTravelTime(customer.getDropOffNodeID(), nearestStationNodeID, nodeList, linkList, arrayLinkList);
             	ArrayList<Link> toChargingStationPathLink = FindPath.pathLinkGenerator(toChargingStationPathNode, linkList, arrayLinkList);
             	sumPathDistance += Calculate.pathTotalDistance(toChargingStationPathLink);
         	}
         	
         	double minDrivingTime = Double.MAX_VALUE;
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("�]�tT:" + taxi.getTaxiID());
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {

	            	//�p���`��{, �T�{�q�q
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"�q�q:" + taxi.getBatteryLevel() + ", �`��{" + sumPathDistance + "�q�q����");
	            		continue;
	            	}
	            	
	            	//�ǰt���
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if(drivingTime > walkingTime) {
		            	estimatedPickUpTimePoint = currentTime + drivingTime;
		            }else {
		            	estimatedPickUpTimePoint = currentTime + walkingTime;
		            }
		            //diff = ��ڵ��ݮɶ� - �i�e�Ե��ݮɶ�
		            double diff = ((estimatedPickUpTimePoint - requestTimePoint) - walkingTime) - customer.getTolerableWaitingTime();
		            System.out.println(String.format("T%d, �w�]�W���ɶ��I:%.2f , �s���ɶ��I:%.2f, �����ɶ�:%.2f, �i�e�Ԯɶ�:%.2f, diff:%.2f",
		            		taxi.getTaxiID(), estimatedPickUpTimePoint, requestTimePoint, walkingTime, customer.getTolerableWaitingTime(), diff));
		            //��̪�
		            if(diff < 0 && drivingTime < minDrivingTime) {
		            	minDrivingTime = drivingTime;
		            	matchedTaxi = taxi;
		            }
		            
		            //�p�G�S���N��diff�̤p
		            if(matchedTaxi == null) {
			            if (diff < minDiff) {
			            	minDiff = diff;
			                matchedTaxi = taxi;
			            }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak
	        
	        remainPendingCustomer--;
	        remainTaxiNums--;
	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer c : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(c);
	    }
	    
	    
	    return newMatchList;
	    
	}
	
	public static ArrayList<Match> negativeDiffAndNearest2(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime, int pendingReservedOrderNums, boolean processWaitingTooLong){
		
		//�t�粒��customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		//�Ƨ�customerList
		sortCustomersForTolerableWaitingTime(customerList);
		
		if(processWaitingTooLong == true) {
			prioritizeCustomersWaitingTooLong(customerList, currentTime, nodeList, linkList, arrayLinkList);
		}
		
		
//			System.out.println("�Ѿl����:" + taxiList.size());
//			//�L�X
//			for(Customer c : customerList) {
//				System.out.println(" C"+c.getCustomerID());
//				System.out.println("O" + c.getCurrentOrder().getOrderID());
//				System.out.println(" �w��:"+c.isReservation());
//				System.out.println("�ɶ�:" + c.getReservedPickUpTimePoint());
//
//			}
			
			
		/**
		 * �}�l�����ǰt for �W���I
		 */
		
		int remainPendingCustomer = pendingReservedOrderNums;
		int remainTaxiNums = taxiList.size();
		
	    for (Customer customer : customerList) { //�q�Ȥ�C����Ӷ��Ǯ��X�t��
	    	  	
	    	//�T�{�O�_�w��
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //�p��pending�ɶ�
	    			double sumDrivingTime = 0;
	    			double serviceableTaxi = 0; 
	    			for(Taxi taxi : taxiList) {
	    				if (matchTaxiList.contains(taxi)) {
	    					continue;
	    				} else {
	    				sumDrivingTime += Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), 
	    						taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
			            serviceableTaxi++;
	    				}
	    			}
	    			 
	    			//�p��ǰt���ɶ� = �Ȥ�w���ɶ� - (�p�{��������F�W���I���ɶ� + �w���Y��)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min�w���Y��
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //�~��
	    			
	    		}else { //�w�g���b����
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //�~��
	    				System.out.println("C" + customer.getCustomerID() + "�~��");
	    				continue;
	    			}else { 
	    				System.out.println("�t�����");
	    			}
	    		}
	    	}else { //�i�J�D�w����
	    		System.out.println("�Ѿltaxi:" + remainTaxiNums + ", �Ѿlpending:" + remainPendingCustomer);
	    		
	    		//�p��p�G�ثe�̪�pending�٫ܤ[, �����t��
	    		if(remainTaxiNums <= remainPendingCustomer) {
	    			break;
	    		}
	    	}
	    		    	
	       	// ��l���ܼ�
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        double walkingTime = 0;
	        double requestTimePoint = customer.getCurrentOrder().getOrderTimePoint().get(1);
	        double estimatedPickUpTimePoint = 0;
	        
	        
	        //�p��walkingTime
	        if(customer.getCurrentNodeID() != customer.getPickUpNodeID()) {
	        	walkingTime = Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	        }
	        
	        //�p��� �U���I �P ��̪�R�q���Z��
	        System.out.println(customer.getPickUpNodeID() + ", " + customer.getDropOffNodeID());
	        
        	ArrayList<Node> toDropOffPathNode = FindPath.shortestTravelTime(customer.getPickUpNodeID(), customer.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
         	ArrayList<Link> toDropOffPathLink = FindPath.pathLinkGenerator(toDropOffPathNode, linkList, arrayLinkList);
         	sumPathDistance = Calculate.pathTotalDistance(toDropOffPathLink);
         	
         	if(!nodeList.get(customer.getDropOffNodeID() - 1).isHaveChargingStation()) {
             	int  nearestStationNodeID = findNearestStation(customer.getDropOffNodeID(), chargingStationList, nodeList, linkList, arrayLinkList);
             	ArrayList<Node> toChargingStationPathNode = FindPath.shortestTravelTime(customer.getDropOffNodeID(), nearestStationNodeID, nodeList, linkList, arrayLinkList);
             	ArrayList<Link> toChargingStationPathLink = FindPath.pathLinkGenerator(toChargingStationPathNode, linkList, arrayLinkList);
             	sumPathDistance += Calculate.pathTotalDistance(toChargingStationPathLink);
         	}
         	
         	double minDrivingTime = Double.MAX_VALUE;
         	double closestDiff = -Double.MAX_VALUE;
	
	        // �M���i�A�Ȫ��p�{������Z���̪� (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("�]�tT:" + taxi.getTaxiID());
	            	continue;  // ���L�w�g�t�諸�p�{��	            
	            } else {

	            	//�p���`��{, �T�{�q�q
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"�q�q:" + taxi.getBatteryLevel() + ", �`��{" + sumPathDistance + "�q�q����");
	            		continue;
	            	}
	            	
	            	//�ǰt���
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if(drivingTime > walkingTime) {
		            	estimatedPickUpTimePoint = currentTime + drivingTime;
		            }else {
		            	estimatedPickUpTimePoint = currentTime + walkingTime;
		            }
		            //diff = ��ڵ��ݮɶ� - �i�e�Ե��ݮɶ�
		            double diff = ((estimatedPickUpTimePoint - requestTimePoint) - walkingTime) - customer.getTolerableWaitingTime();
		            System.out.println(String.format("T%d, �w�]�W���ɶ��I:%.2f , �s���ɶ��I:%.2f, �����ɶ�:%.2f, �i�e�Ԯɶ�:%.2f, diff:%.2f",
		            		taxi.getTaxiID(), estimatedPickUpTimePoint, requestTimePoint, walkingTime, customer.getTolerableWaitingTime(), diff));
		            //�̱��� d = 0 
		            if(diff < 0 && diff > closestDiff) {
		            	closestDiff = diff;
		            	minDrivingTime = drivingTime;
		            	matchedTaxi = taxi;
		            }
		            
		            //�p�G�S���N��diff�̤p
		            if(matchedTaxi == null) {
			            if (diff < minDiff) {
			            	minDiff = diff;
			                matchedTaxi = taxi;
			            }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // �ҥ~�B�z:��u���@�x�i�A�Ȯ�, �t�粒��nbreak
	        
	        remainPendingCustomer--;
	        remainTaxiNums--;
	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //�إ�newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //�w�t�諸taxi
	        taxiList.remove(matchedTaxi);  // �q�i�έp�{���C�������t�諸�p�{��, �w�grun��taxiList�ҥH�i�Hremove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d�t���T%d taxi�bN%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer c : newMatchedCustomer) { //�t���Ȥ�R��
	    	customerList.remove(c);
	    }
	    
	    
	    return newMatchList;
	    
	}

	
	public static int findNearestStation(int DropOffNodeID, ArrayList<ChargingStation> chargingStationList, 
			 ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
		 double minDrivingTime = Double.MAX_VALUE;
		 int chargingStationNodeID = -1;
		 
		 for(ChargingStation station : chargingStationList) {
			 double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(DropOffNodeID, station.getLocateNodeID(), nodeList, linkList, arrayLinkList);
			 if (drivingTime < minDrivingTime) {
				 chargingStationNodeID = station.getChargerID();
			 }
		 }
		 
		 if(chargingStationNodeID == -1) {
			 System.out.println("�S���station");
		 }
		 
		 return chargingStationNodeID;
	 }
	 
	 

	
	/**
	 * �ƧǤ�k
	 * @param customerList
	 */
	
	// �p�ƨ�j
    public static void sortCustomersByWaitingTime(ArrayList<Customer> customerList) {
        // �ϥ� Collections.sort �öǤJ�۩w�q Comparator
        Collections.sort(customerList, new Comparator<Customer>() {
            @Override
            public int compare(Customer c1, Customer c2) {
                // ����Ȥ᪺ tolerableWaitingTime
                return Double.compare(c1.getTolerableWaitingTime(), c2.getTolerableWaitingTime());
            }
        });
    }
    
    public static void sortCustomersByReservation(ArrayList<Customer> customerList) {
        // �w����Ȥ�C��
    	ArrayList<Customer> reservedCustomers = new ArrayList<>();
        // �D�w����Ȥ�C��
    	ArrayList<Customer> nonReservedCustomers = new ArrayList<>();

        // �N�Ȥ���}��w����M�D�w����C��
        for (Customer customer : customerList) {
            if (customer.isReservation()) {
                reservedCustomers.add(customer);
            } else {
                nonReservedCustomers.add(customer);
            }
        }

        // ���ӹw�����W���ɶ���w����Ȥ�i��Ƨ�
        reservedCustomers.sort(Comparator.comparingDouble(Customer::getReservedPickUpTimePoint));

        // �M�ŭ�l�C��
        customerList.clear();
        // �N�Ƨǫ᪺�w����Ȥ�[�J��l�C��
        customerList.addAll(reservedCustomers);
        // �N�D�w����Ȥ�[�J��l�C��
        customerList.addAll(nonReservedCustomers);
    }
    
    public static void prioritizeCustomersWaitingTooLong(ArrayList<Customer> customerList, double currentTime, ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
        // �w����Ȥ�C��
        ArrayList<Customer> reservedCustomers = new ArrayList<>();
        // �D�w����Ȥ�C��
        ArrayList<Customer> nonReservedCustomers = new ArrayList<>();
        // ���ݮɶ��W�L���D�w����Ȥ�C��
        ArrayList<Customer> waitingTooLongCustomers = new ArrayList<>();

        // �N�Ȥ���}��w����M�D�w����C��
        for (Customer customer : customerList) {
            if (customer.isReservation()) {
                reservedCustomers.add(customer);
            } else {
            	double walkingTime = 0;
            	if(customer.getCurrentNodeID() != customer.getPickUpNodeID()) {
            		walkingTime = Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
            	}
                double waitingTime = currentTime - customer.getCurrentOrder().getOrderTimePoint().get(1);
                if (waitingTime > (customer.getTolerableWaitingTime() + walkingTime)) {
                    waitingTooLongCustomers.add(customer);
                } else {
                    nonReservedCustomers.add(customer);
                }
            }
        }

        // �M�ŭ�l�C��
        customerList.clear();
        // �N�Ƨǫ᪺�w����Ȥ�[�J��l�C��
        customerList.addAll(reservedCustomers);
        // �N���ݮɶ��W�L5�������D�w����Ȥ�[�J��l�C��
        customerList.addAll(waitingTooLongCustomers);
        // �N�Ѿl���D�w����Ȥ�[�J��l�C��
        customerList.addAll(nonReservedCustomers);
    }
    
    public static void sortCustomersForShortestPath(ArrayList<Customer> customerList, ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
        // �w����Ȥ�C��
    	ArrayList<Customer> reservedCustomers = new ArrayList<>();
        // �D�w����Ȥ�C��
    	ArrayList<Customer> nonReservedCustomers = new ArrayList<>();

        // �N�Ȥ���}��w����M�D�w����C��
        for (Customer customer : customerList) {
            if (customer.isReservation()) {
                reservedCustomers.add(customer);
            } else {
                nonReservedCustomers.add(customer);
            }
        }

        // ���ӹw�����W���ɶ���w����Ȥ�i��Ƨ�
        reservedCustomers.sort(Comparator.comparingDouble(Customer::getReservedPickUpTimePoint));
        

        for(Customer customer : nonReservedCustomers) {
        	//�p�⨽�{
        	int pickUpNodeID = customer.getPickUpNodeID();
        	int dropOffNodeID = customer.getDropOffNodeID();
        	double travelTime = Calculate.drivingTimeByShortestTravelTimePath(pickUpNodeID, dropOffNodeID, nodeList, linkList, arrayLinkList);
        	customer.setEstimatedTravelTime(travelTime);
        }
        
        nonReservedCustomers.sort(Comparator.comparingDouble(Customer::getEstimatedTravelTime));

        
        
        // �M�ŭ�l�C��
        customerList.clear();
        // �N�Ƨǫ᪺�w����Ȥ�[�J��l�C��
        customerList.addAll(reservedCustomers);
        // �N�D�w����Ȥ�[�J��l�C��
        customerList.addAll(nonReservedCustomers);
    }
    
    public static void sortCustomersForTolerableWaitingTime(ArrayList<Customer> customerList) {
        // �w����Ȥ�C��
    	ArrayList<Customer> reservedCustomers = new ArrayList<>();
        // �D�w����Ȥ�C��
    	ArrayList<Customer> nonReservedCustomers = new ArrayList<>();

        // �N�Ȥ���}��w����M�D�w����C��
        for (Customer customer : customerList) {
            if (customer.isReservation()) {
                reservedCustomers.add(customer);
            } else {
                nonReservedCustomers.add(customer);
            }
        }

        // ���ӹw�����W���ɶ���w����Ȥ�i��Ƨ�
        reservedCustomers.sort(Comparator.comparingDouble(Customer::getReservedPickUpTimePoint));
        
        // �D�w����ӥi�e�Ե��ݮɶ��Ƨ�
        nonReservedCustomers.sort(Comparator.comparingDouble(Customer::getTolerableWaitingTime));

        // �M�ŭ�l�C��
        customerList.clear();
        // �N�Ƨǫ᪺�w����Ȥ�[�J��l�C��
        customerList.addAll(reservedCustomers);
        // �N�D�w����Ȥ�[�J��l�C��
        customerList.addAll(nonReservedCustomers);
    }
}
