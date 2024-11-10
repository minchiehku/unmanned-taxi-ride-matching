package v20240721;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;


public class RideMatch {
	
	public static ArrayList<Match> shortestPath(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對

        	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDrivingTime = Double.MAX_VALUE;
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // 跳過已經配對的計程車	            
	            } else {
//	            	System.out.println("T"+taxi.getTaxiID()+"在N"+taxi.getCurrentNodeID());
		            double drivingTime = Calculate.drivingTimeByShortestPath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if (drivingTime < minDrivingTime) {
		                minDrivingTime = drivingTime;
		                matchedTaxi = taxi;
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer customer : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(customer);
	    }
	    
	    return newMatchList;
	}
	
	public static ArrayList<Match> shortestTravelTimePath(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對

        	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDrivingTime = Double.MAX_VALUE;
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // 跳過已經配對的計程車	            
	            } else {
		            double drivingTime = Calculate.drivingTimeByShortestPath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if (drivingTime < minDrivingTime) {
		                minDrivingTime = drivingTime;
		                matchedTaxi = taxi;
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer customer : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(customer);
	    }
	    
	    return newMatchList;
	}
	
	public static ArrayList<Match> withTolerableTime(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對

        	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // 跳過已經配對的計程車	            
	            } else {
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
//		                System.out.println("可容忍時間:" + customer.getTolerableWaitingTime() +
//		                		", drivingTime:" + drivingTime + ", tempDiff:" + diff);
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer customer : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(customer);
	    }
	    
	    return newMatchList;
	}
	
	public static ArrayList<Match> priorityTolerableTime(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對

        	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // 跳過已經配對的計程車	            
	            } else {
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
		                if(diff < 0) {
		                	break; //配對到容忍時間內就break
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer customer : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(customer);
	    }
	    
	    return newMatchList;
	}
	
	public static ArrayList<Match> priorityTolerableTimeWithReservedCustomer(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對
	    	  	
	    	//確認是否預約
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //計算pending時間
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
	    			
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min安全係數
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //繼續等
	    			
	    		}else { //已經正在等的
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //繼續等
	    				System.out.println("C" + customer.getCustomerID() + "繼續等");
	    				continue;
	    			}else {
	    				System.out.println("C" + customer.getCustomerID() + "配對指派");
	    			}
	    		}
	    	}
	    	
	       	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;    
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	continue;  // 跳過已經配對的計程車	            
	            } else {
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
		                if(diff < 0) {
		                	break; //配對到容忍時間內就break
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer c : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(c);
	    }
	    
	    return newMatchList;
	    
	}
	
	
	public static ArrayList<Match> priorityTolerableTimeWithReservedCustomerWithCharging(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對
	    	  	
	    	//確認是否預約
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //計算pending時間
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
	    			 
	    			//計算匹配的時間 = 客戶預約時間 - (計程車平均到達上車點的時間 + 安全係數)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min安全係數
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //繼續等
	    			
	    		}else { //已經正在等的
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //繼續等
	    				System.out.println("C" + customer.getCustomerID() + "繼續等");
	    				continue;
	    			}else { 
	    				System.out.println("配對指派");
	    			}
	    		}
	    	}
	    	
	       	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        
	        //計算到 下車點 與 到最近充電站距離
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
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("包含T:" + taxi.getTaxiID());
	            	continue;  // 跳過已經配對的計程車	            
	            } else {

	            	//計算總行程, 確認電量
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"電量:" + taxi.getBatteryLevel() + ", 總行程" + sumPathDistance + "電量不夠");
	            		continue;
	            	}
	            		
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
		                if(diff < 0) {
		                	break; //配對到容忍時間內就break
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer c : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(c);
	    }
	    
	    
	    return newMatchList;
	    
	}
	
	public static ArrayList<Match> adjustWorstDiff(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對
	    	  	
	    	//確認是否預約
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //計算pending時間
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
	    			
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min安全係數
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //繼續等
	    			
	    		}else { //已經正在等的
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //繼續等
	    				System.out.println("C" + customer.getCustomerID() + "繼續等");
	    				continue;
	    			}else {
	    				System.out.println("配對指派");
	    			}
	    		}
	    	}
	    	
	       	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        
	        //計算到 下車點 與 到最近充電站距離
        	ArrayList<Node> toDropOffPathNode = FindPath.shortestTravelTime(customer.getPickUpNodeID(), customer.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
         	ArrayList<Link> toDropOffPathLink = FindPath.pathLinkGenerator(toDropOffPathNode, linkList, arrayLinkList);
         	sumPathDistance = Calculate.pathTotalDistance(toDropOffPathLink);
         	
         	if(!nodeList.get(customer.getDropOffNodeID() - 1).isHaveChargingStation()) {
             	int  nearestStationNodeID = findNearestStation(customer.getDropOffNodeID(), chargingStationList, nodeList, linkList, arrayLinkList);
             	ArrayList<Node> toChargingStationPathNode = FindPath.shortestTravelTime(customer.getDropOffNodeID(), nearestStationNodeID, nodeList, linkList, arrayLinkList);
             	ArrayList<Link> toChargingStationPathLink = FindPath.pathLinkGenerator(toChargingStationPathNode, linkList, arrayLinkList);
             	sumPathDistance += Calculate.pathTotalDistance(toChargingStationPathLink);
         	}
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("包含T:" + taxi.getTaxiID());
	            	continue;  // 跳過已經配對的計程車	            
	            } else {

	            	//計算總行程, 確認電量
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"電量:" + taxi.getBatteryLevel() + ", 總行程" + sumPathDistance + "電量不夠");
	            		continue;
	            	}
	            	
	            	//計算等待時間,
	            	if(!customer.isReservation()) {
	            		//實際等待時間 = (客戶叫車 到 客戶與計程車都到上車點時間) - ( 客戶從叫車點移動到上車點時間 ) 
	            		Order order = customer.getCurrentOrder();
	            		double requestTimePoint = order.getOrderTimePoint().get(1);
			            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
			            double walkingTime = Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
			            double tempMatchedTimePoint = currentTime;
			            double estimatedPickUpTimePoint; //(5)都到上車點
			            double waitingTime = 0;
			            if (drivingTime > walkingTime) {//計程車比客戶晚到, 等待時間要扣除客戶走路時間
			            	estimatedPickUpTimePoint = tempMatchedTimePoint + drivingTime;
			            	waitingTime  = (estimatedPickUpTimePoint - requestTimePoint) - walkingTime;
			            }else {//客戶比計程車晚到, 沒有等待, 僅等待匹配時間
			            	estimatedPickUpTimePoint = tempMatchedTimePoint + walkingTime;
			            	waitingTime = tempMatchedTimePoint - requestTimePoint; //等待被匹配的時間
			            }
			            double diff = waitingTime - customer.getTolerableWaitingTime();
			            double penalty = Math.pow(diff, 2); //平方
			            ArrayList<Node> pathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
			            ArrayList<Link> pathLink = FindPath.pathLinkGenerator(pathNode, linkList, arrayLinkList);
			            double taxiToPickUpDistance = Calculate.pathTotalDistance(pathLink);
			            double maxPenalty = Double.MIN_VALUE;
			            double minDistance = Double.MAX_VALUE;
			            
			            //找最大的
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
		                	break; //配對到容忍時間內就break
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break

	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer c : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(c);
	    }
	    
	    return newMatchList;
	    
	}
	
	public static ArrayList<Match> reservedPriority(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime, int pendingReservedOrderNums){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		//排序customerList
		sortCustomersByReservation(customerList);
		
		
		System.out.println("剩餘車數:" + taxiList.size());
		//印出
//		for(Customer c : customerList) {
//			System.out.println(" C"+c.getCustomerID());
//			System.out.println("O" + c.getCurrentOrder().getOrderID());
//			System.out.println(" 預約:"+c.isReservation());
//			System.out.println("時間:" + c.getReservedPickUpTimePoint());
//
//		}
//		
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
		int remainPendingCustomer = pendingReservedOrderNums;
		int remainTaxiNums = taxiList.size();
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對
	    	  	
	    	//確認是否預約
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //計算pending時間
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
	    			 
	    			//計算匹配的時間 = 客戶預約時間 - (計程車平均到達上車點的時間 + 安全係數)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min安全係數
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //繼續等
	    			
	    		}else { //已經正在等的
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //繼續等
	    				System.out.println("C" + customer.getCustomerID() + "繼續等");
	    				continue;
	    			}else { 
	    				System.out.println("配對指派");
	    			}
	    		}
	    	}else { //進入非預約制
	    		System.out.println("剩餘taxi:" + remainTaxiNums + ", 剩餘pending:" + remainPendingCustomer);
	    		
	    		//計算如果目前最近的pending還很久, 先給配對
	    		if(remainTaxiNums <= remainPendingCustomer) {
	    			break;
	    		}
	    	}
	    		    	
	       	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        
	        //計算到 下車點 與 到最近充電站距離
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
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("包含T:" + taxi.getTaxiID());
	            	continue;  // 跳過已經配對的計程車	            
	            } else {

	            	//計算總行程, 確認電量
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"電量:" + taxi.getBatteryLevel() + ", 總行程" + sumPathDistance + "電量不夠");
	            		continue;
	            	}
	            		
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            double diff = drivingTime - customer.getTolerableWaitingTime();
		            if (diff < minDiff) {
		            	minDiff = diff;
		                matchedTaxi = taxi;
		                if(diff < 0) {
		                	break; //配對到容忍時間內就break
		                }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break
	        
	        remainPendingCustomer--;
	        remainTaxiNums--;
	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	        System.out.println("diff:" + minDiff);
	    }
	    
	    for(Customer c : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(c);
	    }
	    
	    
	    return newMatchList;
	    
	}
	

	public static ArrayList<Match> negativeDiffAndNearest(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime, int pendingReservedOrderNums, boolean processWaitingTooLong){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		//排序customerList
		sortCustomersForTolerableWaitingTime(customerList);
		
		if(processWaitingTooLong == true) {
			prioritizeCustomersWaitingTooLong(customerList, currentTime, nodeList, linkList, arrayLinkList);
		}
		
		System.out.println("剩餘車數:" + taxiList.size());
		//印出
//		for(Customer c : customerList) {
//			System.out.println(" C"+c.getCustomerID());
//			System.out.println("O" + c.getCurrentOrder().getOrderID());
//			System.out.println(" 預約:"+c.isReservation());
//			System.out.println("時間:" + c.getReservedPickUpTimePoint());
//
//		}
		
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
		int remainPendingCustomer = pendingReservedOrderNums;
		int remainTaxiNums = taxiList.size();
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對
	    	  	
	    	//確認是否預約
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //計算pending時間
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
	    			 
	    			//計算匹配的時間 = 客戶預約時間 - (計程車平均到達上車點的時間 + 安全係數)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min安全係數
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //繼續等
	    			
	    		}else { //已經正在等的
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //繼續等
	    				System.out.println("C" + customer.getCustomerID() + "繼續等");
	    				continue;
	    			}else { 
	    				System.out.println("配對指派");
	    			}
	    		}
	    	}else { //進入非預約制
	    		System.out.println("剩餘taxi:" + remainTaxiNums + ", 剩餘pending:" + remainPendingCustomer);
	    		
	    		//計算如果目前最近的pending還很久, 先給配對
	    		if(remainTaxiNums <= remainPendingCustomer) {
	    			break;
	    		}
	    	}
	    		    	
	       	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        double walkingTime = 0;
	        double requestTimePoint = customer.getCurrentOrder().getOrderTimePoint().get(1);
	        double estimatedPickUpTimePoint = 0;
	        
	        
	        //計算walkingTime
	        if(customer.getCurrentNodeID() != customer.getPickUpNodeID()) {
	        	walkingTime = Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	        }
	        
	        //計算到 下車點 與 到最近充電站距離
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
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("包含T:" + taxi.getTaxiID());
	            	continue;  // 跳過已經配對的計程車	            
	            } else {

	            	//計算總行程, 確認電量
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"電量:" + taxi.getBatteryLevel() + ", 總行程" + sumPathDistance + "電量不夠");
	            		continue;
	            	}
	            	
	            	//匹配選擇
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if(drivingTime > walkingTime) {
		            	estimatedPickUpTimePoint = currentTime + drivingTime;
		            }else {
		            	estimatedPickUpTimePoint = currentTime + walkingTime;
		            }
		            //diff = 實際等待時間 - 可容忍等待時間
		            double diff = ((estimatedPickUpTimePoint - requestTimePoint) - walkingTime) - customer.getTolerableWaitingTime();
		            System.out.println(String.format("T%d, 預設上車時間點:%.2f , 叫車時間點:%.2f, 走路時間:%.2f, 可容忍時間:%.2f, diff:%.2f",
		            		taxi.getTaxiID(), estimatedPickUpTimePoint, requestTimePoint, walkingTime, customer.getTolerableWaitingTime(), diff));
		            //找最近
		            if(diff < 0 && drivingTime < minDrivingTime) {
		            	minDrivingTime = drivingTime;
		            	matchedTaxi = taxi;
		            }
		            
		            //如果沒找到就找diff最小
		            if(matchedTaxi == null) {
			            if (diff < minDiff) {
			            	minDiff = diff;
			                matchedTaxi = taxi;
			            }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break
	        
	        remainPendingCustomer--;
	        remainTaxiNums--;
	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer c : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(c);
	    }
	    
	    
	    return newMatchList;
	    
	}

	
	public static ArrayList<Match> negativeDiffAndShortestPath(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime, int pendingReservedOrderNums, boolean processWaitingTooLong){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		//排序customerList
		sortCustomersForShortestPath(customerList, nodeList, linkList, arrayLinkList);
		
		if(processWaitingTooLong == true) {
			prioritizeCustomersWaitingTooLong(customerList, currentTime, nodeList, linkList, arrayLinkList);
		}
		
		System.out.println("剩餘車數:" + taxiList.size());
//		//印出
//		for(Customer c : customerList) {
//			System.out.println(" C"+c.getCustomerID());
//			System.out.println("O" + c.getCurrentOrder().getOrderID());
//			System.out.println(" 預約:"+c.isReservation());
//			System.out.println("時間:" + c.getReservedPickUpTimePoint());
//
//		}
		
		
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
		int remainPendingCustomer = pendingReservedOrderNums;
		int remainTaxiNums = taxiList.size();
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對
	    	  	
	    	//確認是否預約
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //計算pending時間
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
	    			 
	    			//計算匹配的時間 = 客戶預約時間 - (計程車平均到達上車點的時間 + 安全係數)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min安全係數
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //繼續等
	    			
	    		}else { //已經正在等的
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //繼續等
	    				System.out.println("C" + customer.getCustomerID() + "繼續等");
	    				continue;
	    			}else { 
	    				System.out.println("配對指派");
	    			}
	    		}
	    	}else { //進入非預約制
	    		System.out.println("剩餘taxi:" + remainTaxiNums + ", 剩餘pending:" + remainPendingCustomer);
	    		
	    		//計算如果目前最近的pending還很久, 先給配對
	    		if(remainTaxiNums <= remainPendingCustomer) {
	    			break;
	    		}
	    	}
	    		    	
	       	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        double walkingTime = 0;
	        double requestTimePoint = customer.getCurrentOrder().getOrderTimePoint().get(1);
	        double estimatedPickUpTimePoint = 0;
	        
	        
	        //計算walkingTime
	        if(customer.getCurrentNodeID() != customer.getPickUpNodeID()) {
	        	walkingTime = Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	        }
	        
	        //計算到 下車點 與 到最近充電站距離
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
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("包含T:" + taxi.getTaxiID());
	            	continue;  // 跳過已經配對的計程車	            
	            } else {

	            	//計算總行程, 確認電量
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"電量:" + taxi.getBatteryLevel() + ", 總行程" + sumPathDistance + "電量不夠");
	            		continue;
	            	}
	            	
	            	//匹配選擇
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if(drivingTime > walkingTime) {
		            	estimatedPickUpTimePoint = currentTime + drivingTime;
		            }else {
		            	estimatedPickUpTimePoint = currentTime + walkingTime;
		            }
		            //diff = 實際等待時間 - 可容忍等待時間
		            double diff = ((estimatedPickUpTimePoint - requestTimePoint) - walkingTime) - customer.getTolerableWaitingTime();
		            System.out.println(String.format("T%d, 預設上車時間點:%.2f , 叫車時間點:%.2f, 走路時間:%.2f, 可容忍時間:%.2f, diff:%.2f",
		            		taxi.getTaxiID(), estimatedPickUpTimePoint, requestTimePoint, walkingTime, customer.getTolerableWaitingTime(), diff));
		            //找最近
		            if(diff < 0 && drivingTime < minDrivingTime) {
		            	minDrivingTime = drivingTime;
		            	matchedTaxi = taxi;
		            }
		            
		            //如果沒找到就找diff最小
		            if(matchedTaxi == null) {
			            if (diff < minDiff) {
			            	minDiff = diff;
			                matchedTaxi = taxi;
			            }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break
	        
	        remainPendingCustomer--;
	        remainTaxiNums--;
	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer c : newMatchedCustomer) { //配對後客戶刪除
	    	customerList.remove(c);
	    }
	    
	    
	    return newMatchList;
	    
	}
	
	public static ArrayList<Match> negativeDiffAndNearest2(ArrayList<Customer> customerList, ArrayList<Taxi> taxiList, ArrayList<Order> orderList, 
            int matchNum, HashSet<Taxi> matchTaxiList, HashSet<Customer> matchCustomerList, ArrayList<ChargingStation> chargingStationList,
            ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList, double currentTime, int pendingReservedOrderNums, boolean processWaitingTooLong){
		
		//配對完的customer
		ArrayList<Match> newMatchList = new ArrayList<>();
		ArrayList<Customer> newMatchedCustomer = new ArrayList<>();
		
		//排序customerList
		sortCustomersForTolerableWaitingTime(customerList);
		
		if(processWaitingTooLong == true) {
			prioritizeCustomersWaitingTooLong(customerList, currentTime, nodeList, linkList, arrayLinkList);
		}
		
		
//			System.out.println("剩餘車數:" + taxiList.size());
//			//印出
//			for(Customer c : customerList) {
//				System.out.println(" C"+c.getCustomerID());
//				System.out.println("O" + c.getCurrentOrder().getOrderID());
//				System.out.println(" 預約:"+c.isReservation());
//				System.out.println("時間:" + c.getReservedPickUpTimePoint());
//
//			}
			
			
		/**
		 * 開始車輛匹配 for 上車點
		 */
		
		int remainPendingCustomer = pendingReservedOrderNums;
		int remainTaxiNums = taxiList.size();
		
	    for (Customer customer : customerList) { //從客戶列表按照順序拿出配對
	    	  	
	    	//確認是否預約
	    	if(customer.isReservation()) {
	    		if(!customer.getCurrentOrder().isPending()) { //計算pending時間
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
	    			 
	    			//計算匹配的時間 = 客戶預約時間 - (計程車平均到達上車點的時間 + 安全係數)
	    			double pendingTimePoint = customer.getReservedPickUpTimePoint() - (sumDrivingTime / serviceableTaxi) + 5 * 60; //10min安全係數
	    			System.out.println("avgDrivingTime:" + (sumDrivingTime / serviceableTaxi));
	    			customer.getCurrentOrder().setPending(true);
	    			customer.getCurrentOrder().setPendingTimePoint(pendingTimePoint);
	    			System.out.println("pendingTimePoint:" + customer.getCurrentOrder().getPendingTimePoint());
	    			
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300) continue; //繼續等
	    			
	    		}else { //已經正在等的
	    			if(currentTime < customer.getCurrentOrder().getPendingTimePoint() - 300){ //繼續等
	    				System.out.println("C" + customer.getCustomerID() + "繼續等");
	    				continue;
	    			}else { 
	    				System.out.println("配對指派");
	    			}
	    		}
	    	}else { //進入非預約制
	    		System.out.println("剩餘taxi:" + remainTaxiNums + ", 剩餘pending:" + remainPendingCustomer);
	    		
	    		//計算如果目前最近的pending還很久, 先給配對
	    		if(remainTaxiNums <= remainPendingCustomer) {
	    			break;
	    		}
	    	}
	    		    	
	       	// 初始化變數
	        Taxi matchedTaxi = null;
	        double minDiff = Double.MAX_VALUE;
	        double sumPathDistance;
	        double walkingTime = 0;
	        double requestTimePoint = customer.getCurrentOrder().getOrderTimePoint().get(1);
	        double estimatedPickUpTimePoint = 0;
	        
	        
	        //計算walkingTime
	        if(customer.getCurrentNodeID() != customer.getPickUpNodeID()) {
	        	walkingTime = Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	        }
	        
	        //計算到 下車點 與 到最近充電站距離
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
	
	        // 遍歷可服務的計程車中找距離最近 (Dijkstra)
	        for (Taxi taxi : taxiList) { 	
	            if (matchTaxiList.contains(taxi)){
	            	System.out.println("包含T:" + taxi.getTaxiID());
	            	continue;  // 跳過已經配對的計程車	            
	            } else {

	            	//計算總行程, 確認電量
	            	ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestTravelTime(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
	            	ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
	            	sumPathDistance += Calculate.pathTotalDistance(taxiToPickUpPathLink);
	            	
	            	if((taxi.getBatteryLevel() - 10) < sumPathDistance) {
	            		System.out.println("T" + taxi.getTaxiID() +"電量:" + taxi.getBatteryLevel() + ", 總行程" + sumPathDistance + "電量不夠");
	            		continue;
	            	}
	            	
	            	//匹配選擇
		            double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(customer.getPickUpNodeID(), taxi.getCurrentNodeID(), nodeList, linkList, arrayLinkList);
		            if(drivingTime > walkingTime) {
		            	estimatedPickUpTimePoint = currentTime + drivingTime;
		            }else {
		            	estimatedPickUpTimePoint = currentTime + walkingTime;
		            }
		            //diff = 實際等待時間 - 可容忍等待時間
		            double diff = ((estimatedPickUpTimePoint - requestTimePoint) - walkingTime) - customer.getTolerableWaitingTime();
		            System.out.println(String.format("T%d, 預設上車時間點:%.2f , 叫車時間點:%.2f, 走路時間:%.2f, 可容忍時間:%.2f, diff:%.2f",
		            		taxi.getTaxiID(), estimatedPickUpTimePoint, requestTimePoint, walkingTime, customer.getTolerableWaitingTime(), diff));
		            //最接近 d = 0 
		            if(diff < 0 && diff > closestDiff) {
		            	closestDiff = diff;
		            	minDrivingTime = drivingTime;
		            	matchedTaxi = taxi;
		            }
		            
		            //如果沒找到就找diff最小
		            if(matchedTaxi == null) {
			            if (diff < minDiff) {
			            	minDiff = diff;
			                matchedTaxi = taxi;
			            }
		            }
	            }
	        }   
	        if (matchedTaxi == null) break;  // 例外處理:當只有一台可服務時, 配對完後要break
	        
	        remainPendingCustomer--;
	        remainTaxiNums--;
	        
	        Match newMatch = new Match(++matchNum, customer, matchedTaxi); //建立newMatch
	        newMatchList.add(newMatch); 
	        matchTaxiList.add(matchedTaxi); //已配對的taxi
	        taxiList.remove(matchedTaxi);  // 從可用計程車列表中移除配對的計程車, 已經run完taxiList所以可以remove
	        newMatchedCustomer.add(customer);
	        Order order = customer.getCurrentOrder();
	        System.out.println(String.format("O%d C%d配對到T%d taxi在N%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		matchedTaxi.getTaxiID(),
	        		matchedTaxi.getCurrentNodeID()));
	    }
	    
	    for(Customer c : newMatchedCustomer) { //配對後客戶刪除
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
			 System.out.println("沒找到station");
		 }
		 
		 return chargingStationNodeID;
	 }
	 
	 

	
	/**
	 * 排序方法
	 * @param customerList
	 */
	
	// 小排到大
    public static void sortCustomersByWaitingTime(ArrayList<Customer> customerList) {
        // 使用 Collections.sort 並傳入自定義 Comparator
        Collections.sort(customerList, new Comparator<Customer>() {
            @Override
            public int compare(Customer c1, Customer c2) {
                // 比較客戶的 tolerableWaitingTime
                return Double.compare(c1.getTolerableWaitingTime(), c2.getTolerableWaitingTime());
            }
        });
    }
    
    public static void sortCustomersByReservation(ArrayList<Customer> customerList) {
        // 預約制的客戶列表
    	ArrayList<Customer> reservedCustomers = new ArrayList<>();
        // 非預約制的客戶列表
    	ArrayList<Customer> nonReservedCustomers = new ArrayList<>();

        // 將客戶分開到預約制和非預約制的列表
        for (Customer customer : customerList) {
            if (customer.isReservation()) {
                reservedCustomers.add(customer);
            } else {
                nonReservedCustomers.add(customer);
            }
        }

        // 按照預約的上車時間對預約制的客戶進行排序
        reservedCustomers.sort(Comparator.comparingDouble(Customer::getReservedPickUpTimePoint));

        // 清空原始列表
        customerList.clear();
        // 將排序後的預約制客戶加入原始列表
        customerList.addAll(reservedCustomers);
        // 將非預約制客戶加入原始列表
        customerList.addAll(nonReservedCustomers);
    }
    
    public static void prioritizeCustomersWaitingTooLong(ArrayList<Customer> customerList, double currentTime, ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
        // 預約制的客戶列表
        ArrayList<Customer> reservedCustomers = new ArrayList<>();
        // 非預約制的客戶列表
        ArrayList<Customer> nonReservedCustomers = new ArrayList<>();
        // 等待時間超過的非預約制客戶列表
        ArrayList<Customer> waitingTooLongCustomers = new ArrayList<>();

        // 將客戶分開到預約制和非預約制的列表
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

        // 清空原始列表
        customerList.clear();
        // 將排序後的預約制客戶加入原始列表
        customerList.addAll(reservedCustomers);
        // 將等待時間超過5分鐘的非預約制客戶加入原始列表
        customerList.addAll(waitingTooLongCustomers);
        // 將剩餘的非預約制客戶加入原始列表
        customerList.addAll(nonReservedCustomers);
    }
    
    public static void sortCustomersForShortestPath(ArrayList<Customer> customerList, ArrayList<Node> nodeList, ArrayList<Link> linkList, int[][] arrayLinkList) {
        // 預約制的客戶列表
    	ArrayList<Customer> reservedCustomers = new ArrayList<>();
        // 非預約制的客戶列表
    	ArrayList<Customer> nonReservedCustomers = new ArrayList<>();

        // 將客戶分開到預約制和非預約制的列表
        for (Customer customer : customerList) {
            if (customer.isReservation()) {
                reservedCustomers.add(customer);
            } else {
                nonReservedCustomers.add(customer);
            }
        }

        // 按照預約的上車時間對預約制的客戶進行排序
        reservedCustomers.sort(Comparator.comparingDouble(Customer::getReservedPickUpTimePoint));
        

        for(Customer customer : nonReservedCustomers) {
        	//計算里程
        	int pickUpNodeID = customer.getPickUpNodeID();
        	int dropOffNodeID = customer.getDropOffNodeID();
        	double travelTime = Calculate.drivingTimeByShortestTravelTimePath(pickUpNodeID, dropOffNodeID, nodeList, linkList, arrayLinkList);
        	customer.setEstimatedTravelTime(travelTime);
        }
        
        nonReservedCustomers.sort(Comparator.comparingDouble(Customer::getEstimatedTravelTime));

        
        
        // 清空原始列表
        customerList.clear();
        // 將排序後的預約制客戶加入原始列表
        customerList.addAll(reservedCustomers);
        // 將非預約制客戶加入原始列表
        customerList.addAll(nonReservedCustomers);
    }
    
    public static void sortCustomersForTolerableWaitingTime(ArrayList<Customer> customerList) {
        // 預約制的客戶列表
    	ArrayList<Customer> reservedCustomers = new ArrayList<>();
        // 非預約制的客戶列表
    	ArrayList<Customer> nonReservedCustomers = new ArrayList<>();

        // 將客戶分開到預約制和非預約制的列表
        for (Customer customer : customerList) {
            if (customer.isReservation()) {
                reservedCustomers.add(customer);
            } else {
                nonReservedCustomers.add(customer);
            }
        }

        // 按照預約的上車時間對預約制的客戶進行排序
        reservedCustomers.sort(Comparator.comparingDouble(Customer::getReservedPickUpTimePoint));
        
        // 非預約制照可容忍等待時間排序
        nonReservedCustomers.sort(Comparator.comparingDouble(Customer::getTolerableWaitingTime));

        // 清空原始列表
        customerList.clear();
        // 將排序後的預約制客戶加入原始列表
        customerList.addAll(reservedCustomers);
        // 將非預約制客戶加入原始列表
        customerList.addAll(nonReservedCustomers);
    }
}
