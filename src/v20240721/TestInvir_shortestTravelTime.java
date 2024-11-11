package v20240721;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class TestInvir_shortestTravelTime {
	//路徑
    private static final String NODES_FILE_PATH = "D:\\Java_project\\UnmannedTaxi\\SimulationData\\coordinate_data.xlsx";
    private static final String TAXIS_FILE_PATH = "D:\\Java_project\\UnmannedTaxi\\SimulationData\\taxi_data.xlsx";
    private static final String OUTPUT_FILE_PATH = "D:\\Java_project\\UnmannedTaxi\\output\\case4\\4.5.600.xlsx";
    private static final String OUTPUT_SHEET_NAME = "sheet1";
    
    //資料結構
    private static ArrayList<Node> nodeList;
    private static ArrayList<Link> linkList;
    private static int[][] arrayLinkList;
    private static ArrayList<Taxi> taxiList;
    private static ArrayList<ChargingStation> chargingStationList = new ArrayList<>();
    private static ArrayList<Customer> customerList = new ArrayList<>();
    private static ArrayList<Customer> finishedCustomerList = new ArrayList<>();
    private static ArrayList<Order> orderList = new ArrayList<>();
    private static ArrayList<Order> pastOrderList = new ArrayList<>();
    private static ArrayList<Match> matchList = new ArrayList<>();
    private static ArrayList<Event> eventList = new ArrayList<>();
    private static ArrayList<Taxi> chargingTaxiList = new ArrayList<>();
    private static HashSet<Taxi> matchTaxiList = new HashSet<>();
    private static ArrayList<Taxi> movingToNextTaxiList = new ArrayList<>();
    private static HashSet<Customer> matchCustomerList = new HashSet<>();
    private static ArrayList<Node> urbanNodeList = new ArrayList<>();
    private static ArrayList<Node> suburbsNodeList = new ArrayList<>();
    private static ArrayList<Node> restAreaNodeList = new ArrayList<>();
    
    //參數設定
    private static final double MIN_TIME_STEP = 1E-10; // 最小時間步長閾值
    private static final int MAX_BETTERY = 300;
    private static final double REQUEST_AGAIN_PRO = 0.1;
    
    private static final double SIMULATION_HOURS = 2; 
    private static final int REPEAT_TIMES = 1;
    
    private static double RESERVED_CUSTOMER_PER_DAY = 50; //預約case1-3:50, case4:120,240,360, 480,600, 720 
    private static double CUSTOMER_PER_DAY = 1000 - RESERVED_CUSTOMER_PER_DAY; //非預約case1-3:1000-1600, case4:1500
    private static double INCREASE_NUMS = 0;
    private static int taxiNums = 60; //case1:20, case2&3:60
    private static final int CHARGING_STATION_NUMS = 30; //10*10面積, 每座服務範圍2平方公里, 平均分散
    private static boolean DRRM = false;
    private static String matchMethod = "TDNRM"; // TDNRM or TDSPRM 
    private static boolean INTENSIVE_REQUEST_PERIOD = false; //case2 密集時間叫車
    private static boolean INTENSIVE_AREA = false; //case3 密集地區
    private static boolean OUTPUT = false;
    private static boolean PROCESS_WAITING_TOO_LONG = false; //處理等太久未配對客戶 
    
    private static double SIMULATION_DURATION = SIMULATION_HOURS * 60 * 60;
    private static double LAMBDA = CUSTOMER_PER_DAY / 24 / 60 / 60;
    private static double RESERVED_LAMBDA = RESERVED_CUSTOMER_PER_DAY / 24 / 60 / 60;

    private static double currentTime = 0.0;
    private static double advancedTime = 0.0;
    private static int customerNums = 0;
    private static int orderNums = 0;
    private static int eventNums = 0;
    private static int matchNums = 0;
    private static int serviceCompleteNums = 0;
    private static int pendingReservedOrderNums = 0;
    private static int simulationTimes = 1;
    
    
    /**
     * Main
     * @throws IOException 
     */

    public static void main(String[] arg) throws IOException {
    	
    	initializeData();   	
    	
        for (int i = 0; i < REPEAT_TIMES; i++) {
        	resetSimulation();
        	
	    	long startTime = System.currentTimeMillis(); // 記錄開始時間
	        simulate();
	        long endTime = System.currentTimeMillis(); // 記錄結束時間
	        long executionTime = (endTime - startTime) / 1000; // 計算執行時間
	        if(OUTPUT == true) {
		        outPutToExcel(pastOrderList, OUTPUT_FILE_PATH, OUTPUT_SHEET_NAME, executionTime);
	        }
 
	        
	        for(Taxi taxi : taxiList) {
	        	System.out.println("T" + taxi.getTaxiID() + "剩餘電量:" + taxi.getBatteryLevel());
	        }
	        
	        simulationTimes++;
	        CUSTOMER_PER_DAY += INCREASE_NUMS;
	        LAMBDA = CUSTOMER_PER_DAY / 24 / 60 / 60;
        }
    }
    
    private static void initializeData() {
        nodeList = ExcelReader.withNodes(NODES_FILE_PATH, "sheet1");
        linkList = LinkCreator.forLinkList(nodeList);
        arrayLinkList = LinkCreator.forMatrix(linkList, nodeList);
        chargingStationList = RandomGenerator.forhargingStationsWithKmeans(nodeList, CHARGING_STATION_NUMS);
        setNodeWithChargingStationList();
        setAreaNodeList(nodeList, urbanNodeList, suburbsNodeList, restAreaNodeList);
        
        
        System.out.println("Charging Stations:");
        for (ChargingStation station : chargingStationList) {
            System.out.println("Charger ID: " + station.getChargerID() + 
                               " Node ID: " + station.getLocateNodeID() + 
                               " X: " + station.getChargerX() + 
                               " Y: " + station.getChargerY());
        }
    }
    
    private static void setNodeWithChargingStationList() {
    	if(!chargingStationList.isEmpty()) {
    		for(ChargingStation station : chargingStationList) {
    			Node node = nodeList.get(station.getLocateNodeID() - 1);
    			node.setHaveChargingStation(true);
    			node.setChargingStationID(station.getChargerID());
    		}
    	}
    }
    
    private static void resetSimulation() {
        taxiList = RandomGenerator.forTaxiList(taxiNums, nodeList);
        customerList.clear();
        orderList.clear();
        pastOrderList.clear();
        matchList.clear();
        eventList.clear();
        matchTaxiList.clear();
        matchCustomerList.clear();
        currentTime = 0.0;
        advancedTime = 0.0;
        customerNums = 0;
        orderNums = 0;
        eventNums = 0;
        matchNums = 0;
        serviceCompleteNums = 0;
        pendingReservedOrderNums = 0;
        scheduleNextCustomerEvent(); // 產生第一個隨機客戶
        scheduleNextReservedCustomerEvent(); // 產生第一個預約制客戶
    }
    
	private static void scheduleNextCustomerEvent() {
	    eventList.add(new Event(++eventNums, 1, currentTime + nextCustomerInterval(LAMBDA))); //1= 客戶下訂單, 未車輛匹配
	}
	
	private static void scheduleNextReservedCustomerEvent() {
	    eventList.add(new Event(++eventNums, 12, currentTime + nextCustomerInterval(RESERVED_LAMBDA))); //1= 客戶下訂單, 未車輛匹配
	}

	private static double nextCustomerInterval(double lambda) { 
		ExponentialDistribution exponentialDistribution = new ExponentialDistribution(1/lambda); // 創建指數分佈物件
		return exponentialDistribution.sample();
	}
	
	
	/**
	 * 模擬
	 */

	public static void simulate() {
        while (currentTime < SIMULATION_DURATION) {
            Event nextEvent = findNextEvent(); //找下一個最近要執行的event
            System.out.println("E" + nextEvent.getEventID() + ", 種類:" + nextEvent.getEventCategory());
            if (nextEvent != null) {
            	advanceTimeTo(nextEvent); // 時間前進
            	if(INTENSIVE_REQUEST_PERIOD == true) updateCustomerByTimePeriod(currentTime); //集中客叫車
            	updateRoadCondition(linkList, currentTime); //更新targetSpeed
                updateOrderPositions(orderList); // 更新所有位置
                updateMovingToChargingTaxiPositions(chargingTaxiList);
                updateMovingNextTaxiPositions(movingToNextTaxiList);
                setRoadNextCondition(linkList);
                
                if(eventNeedsPostpone(nextEvent)) {  //check 是否執行
                	postPoneNextEvent(nextEvent);
                }else {
                    executeEvent(nextEvent); // 執行此事件
                    eventList.remove(nextEvent); 
                }
            }
        }
        
        System.out.println("客戶總數:" + customerNums);
        System.out.println("等待配對數:" + customerList.size());
        System.out.println("計程車數:" + taxiList.size());
    }
	
	public static boolean eventNeedsPostpone(Event event) {
		boolean needsPostpone = false;
		if(event.getEventCategory() != 1) {
			switch(event.getEventCategory()) {
				case 3:
					if(!isAtPickUpNode(event.getRelateOrder(), event.getRelateOrder().getCreatorCustomer())) needsPostpone = true;
					break;
				case 4:
					if(!isAtPickUpNode(event.getRelateOrder(), event.getRelateOrder().getMatchedTaxi())) needsPostpone = true;
					break;
				case 7:
					if(!isAtDropOffNode(event.getRelateOrder(), event.getRelateOrder().getMatchedTaxi())) needsPostpone = true;
					break;	
				case 9:
					if(!isAtChargingStationNode(event.getRelateTaxi())) needsPostpone = true;
					break;
				case 42:
					if(!isAtNextNode(event.getRelateTaxi())) needsPostpone =true;
					break;
				default:
					break;
			}
		}		
		return needsPostpone;
	}
	
	public static void postPoneNextEvent(Event event) {
		
		System.out.println("------事件延後------");
		
		System.out.println("E" + event.getEventID() + "T" + event.getRelateTaxi().getTaxiID());
		
		int ec = event.getEventCategory();
		
		if(ec == 3 || ec == 4 || ec == 7) {
			
			Order order = event.getRelateOrder();
			Customer customer = order.getCreatorCustomer();
			Taxi taxi = order.getMatchedTaxi();
						
			switch(event.getEventCategory()) {
				case 3:		
					double newCustomerToPickUpTimePoint;
					double remainWalkingTime = (customer.getRemainLinkDistance() / customer.getWalkSpeed()) * 3600;		
					if(!isAtLastLink(customer)) {
						double walkingTime =  Calculate.WalkingTimeByShortestPath(customer, customer.getCurrentLink().getEndNode().getNodeID(), order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
						remainWalkingTime += walkingTime;
					}
					
					if (remainWalkingTime < MIN_TIME_STEP) {
				        remainWalkingTime = MIN_TIME_STEP; // 如果時間增量小於最小步長閾值，則設置為最小步長閾值
				    }
					
					System.out.println(currentTime);
					newCustomerToPickUpTimePoint = currentTime + remainWalkingTime;
					System.out.println(newCustomerToPickUpTimePoint);
					event.setEventTimePoint(newCustomerToPickUpTimePoint);
					System.out.println("delayTime:"+remainWalkingTime);
					System.out.println("C" + customer.getCustomerID());
					System.out.println("E" + event.getEventID() + "種類:" + event.getEventCategory() + ",延後到:" + newCustomerToPickUpTimePoint);
					break;
				case 4:
					double newTaxiToPickUpTimePoint;
					double remainToPickUpTime = (taxi.getRemainLinkDistance() / taxi.getCurrentSpeed()) * 3600;
					System.out.println("E種類:"+ ec + " T" + taxi.getTaxiID() + "剩餘L"+ taxi.getLinkID() +"link距離:" + taxi.getRemainLinkDistance());
					System.out.println("剩餘時間:" + remainToPickUpTime);
					
					if(!isAtLastLink(taxi)) {
						double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(taxi.getCurrentNodeID(), order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
						remainToPickUpTime += drivingTime;							
					}
					
					if (remainToPickUpTime < MIN_TIME_STEP) {
						remainToPickUpTime = MIN_TIME_STEP; // 如果時間增量小於最小步長閾值，則設置為最小步長閾值
				    }
					
					newTaxiToPickUpTimePoint = currentTime + remainToPickUpTime;
					event.setEventTimePoint(newTaxiToPickUpTimePoint);
					System.out.println("delayTime:"+remainToPickUpTime);
					System.out.println("T" + taxi.getTaxiID());
					System.out.println("E" + event.getEventID() + "種類:" + event.getEventCategory() + ",延後到:" + newTaxiToPickUpTimePoint);
					break;
				case 7:
					double newTaxiToDropOffTimePoint;
					double remainToDropOffTime = (taxi.getRemainLinkDistance() / taxi.getCurrentSpeed()) * 3600;
					System.out.println("E種類:"+ ec + "T" + taxi.getTaxiID() + "剩餘L"+ taxi.getLinkID() +"link距離:" + taxi.getRemainLinkDistance());
					System.out.println("剩餘時間:" + remainToDropOffTime);
					
					if(!isAtLastLink(taxi)) {
						double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(taxi.getCurrentNodeID(), order.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
						remainToDropOffTime += drivingTime;							
					}
					
					if (remainToDropOffTime < MIN_TIME_STEP) {
						remainToDropOffTime = MIN_TIME_STEP; // 如果時間增量小於最小步長閾值，則設置為最小步長閾值
				    }
					
					newTaxiToDropOffTimePoint = currentTime + remainToDropOffTime;
					event.setEventTimePoint(newTaxiToDropOffTimePoint);
					System.out.println("delayTime:"+remainToDropOffTime);
					System.out.println("E" + event.getEventID() + "種類:" + event.getEventCategory() + ",延後到:" + newTaxiToDropOffTimePoint);
					break;
				}
		}else if(ec == 9){
			Taxi taxi = event.getRelateTaxi();
			if(taxi.isMoving()) {
				double newAtChargingStationTimePoint;
				double remainToChargingStationTime = (taxi.getRemainLinkDistance() / taxi.getCurrentSpeed()) * 3600;
				
				System.out.println("E種類:"+ ec + "T" + taxi.getTaxiID() + "剩餘L"+ taxi.getLinkID() +"link距離:" + taxi.getRemainLinkDistance());
				System.out.println("剩餘時間:" + remainToChargingStationTime);
				
				if(!isAtLastLink(taxi)) {
					System.out.println("Taxi next N" + taxi.getCurrentNodeID() + "TargetStation CH:" + taxi.getTargetChargingStationNodeID());
					double drivingTime =Calculate.drivingTimeByShortestTravelTimePath(taxi.getCurrentNodeID(), taxi.getTargetChargingStationNodeID(), nodeList, linkList, arrayLinkList);
					remainToChargingStationTime += drivingTime;
				}
				
				if (remainToChargingStationTime < MIN_TIME_STEP) {
					remainToChargingStationTime = MIN_TIME_STEP; // 如果時間增量小於最小步長閾值，則設置為最小步長閾值
			    }
				
				newAtChargingStationTimePoint = currentTime + remainToChargingStationTime;
				event.setEventTimePoint(newAtChargingStationTimePoint);
				System.out.println("delayTime:"+remainToChargingStationTime);
				System.out.println("E" + event.getEventID() + "種類:" + event.getEventCategory() + ",延後到:" + newAtChargingStationTimePoint);
			
			}
		}else if(ec == 42) {
			Taxi taxi = event.getRelateTaxi();
			if(taxi.isMoving()) {
				double newAtNextNodeTimePoint;
				double remainLinkTime = (taxi.getRemainLinkDistance() / taxi.getCurrentSpeed()) * 3600;
				
				System.out.println("E種類:"+ ec +"T" + taxi.getTaxiID() + "剩餘L"+ taxi.getLinkID() +"link距離:" + taxi.getRemainLinkDistance());
				System.out.println("剩餘時間:" + remainLinkTime);	
				
				if (remainLinkTime < MIN_TIME_STEP) {
					remainLinkTime = MIN_TIME_STEP; // 如果時間增量小於最小步長閾值，則設置為最小步長閾值
			    }
				
				newAtNextNodeTimePoint = currentTime + remainLinkTime;
				event.setEventTimePoint(newAtNextNodeTimePoint);
				System.out.println("delayTime:"+remainLinkTime);
				System.out.println("E" + event.getEventID() + "種類:" + event.getEventCategory() + ",延後到:" + newAtNextNodeTimePoint);
			}
		}
		
		System.out.println("------事件延後 完成------");
	}
    
    private static Event findNextEvent() {
        return Collections.min(eventList, Comparator.comparingDouble(Event::getEventTimePoint));
    }

    private static void advanceTimeTo(Event event) {
        advancedTime = event.getEventTimePoint() - currentTime; //計算前進的時間
        currentTime = event.getEventTimePoint(); //更新目前時間
        logTimeAdvance(); //印出
    }
    
    private static void logTimeAdvance() {
        System.out.println("------------------------");
        System.out.println("advancedTime: " + advancedTime + "s");
		System.out.println("currentTime: " + currentTime + "s");
		System.out.println("------------------------\n");
    }
    
    /**
     * 更新所有位置
     */

    private static void updateOrderPositions(ArrayList<Order> orderList) {
    	if (orderList.isEmpty()) {
    		System.out.println("[目前沒有訂單, 不需更新位置]\n");
    		return;
    	}	
    	if (advancedTime == 0) {
    		System.out.println("[時間沒有前進, 不用更新位置]\n");
    		return;
    	}  	   	
		System.out.println("------更新各訂單位置------");	
        for (Order order : orderList) {	
        	if(!needsPositionUpdate(order)) {
//        		System.out.println("Order: " + order.getOrderID() + ", orderStatus:" + order.getOrderStatus() + ", 位置不需要更新");
        		continue;
        	}
        	
//    		System.out.println(String.format("O%d sts:%d 位置更新", order.getOrderID(), order.getOrderStatus()));	
 	
    		Customer customer = order.getCreatorCustomer();
    		Taxi taxi = order.getMatchTaxi();
    		
    		updateOrderPosition(order, customer, taxi);	
        }
    	System.out.println("---------------更新訂單位置完成-----------------\n");
    }
    
    private static boolean needsPositionUpdate(Order order) {
    	Set<Integer> updateStatus = new HashSet<>(Arrays.asList(2, 3, 4, 6));
    	return updateStatus.contains(order.getOrderStatus());
    }
    
    private static void updateOrderPosition(Order order, Customer customer, Taxi taxi) {
    	switch (order.getOrderStatus()) {
	        case 2: // customer與taxi前往上車點
	            if (customer.isMoving()) updatePositionForCustomer(customer);
	            if (taxi.isMoving()) updatePositionForTaxi(taxi);
	            break;
	        case 3: // 客戶在上車點等待計程車
	            if (taxi.isMoving()) updatePositionForTaxi(taxi);
	            break;
	        case 4: // 計程車在上車點等待客戶
	            if (customer.isMoving()) updatePositionForCustomer(customer);
	            break;
	        case 6: // 載客服務中, 更新taxi位置連動customer
	            if (taxi.isMoving()) {
	                updatePositionForTaxi(taxi);
	                customerIsTakingTaxi(taxi, customer);
	            }
	            break;
	        default:
	            // 其他狀態不需更新
	            break;
    	}
    }
    
    private static void updateMovingToChargingTaxiPositions(ArrayList<Taxi> chargingTaxiList) {
    	if(chargingTaxiList.isEmpty()) {
    		return;
    	}else {
    		System.out.println("---更新前往充電車輛位置---");
	    	for (Taxi taxi : chargingTaxiList) {
	    		System.out.println("T" + taxi.getTaxiID() + "isMoving:" + taxi.isMoving());
	    		if (taxi.isMoving()) updatePositionForTaxi(taxi);
	    	}
    		System.out.println("---更新前往充電車輛位置 完成---");

    	}
    }
    
    private static void updateMovingNextTaxiPositions(ArrayList<Taxi> movingToNextTaxiList) {
    	if(movingToNextTaxiList.isEmpty()) {
    		return;
    	}else {
    		System.out.println("---更新old taxi to next位置---");
        	for (Taxi taxi : movingToNextTaxiList) {
        		System.out.println("T" + taxi.getTaxiID() + "isMoving:" + taxi.isMoving());
        		if (taxi.isMoving()) updatePositionForTaxi(taxi);
        	}
    		System.out.println("---更新old taxi to next位置 完成---");

    	}

    }
    
    private static void updatePositionForCustomer(Customer customer) {
		double customerSpeed = customer.getWalkSpeed();
		double walkingTime = advancedTime; //秒
		double movingDistance = (customerSpeed/3600) * walkingTime; //前進的距離 km/秒
		double remainLinkDistance = customer.getRemainLinkDistance(); //目前link剩餘距離
		boolean isMoveToNextLink = movingDistance >= remainLinkDistance; //判斷有沒有超過剩餘距離 
		
		System.out.println("C" + customer.getCustomerID() + "O" + customer.getCurrentOrder().getOrderID());
		
		if(customer.getCurrentLink() == null) {
			System.out.println("問題點確認");
		}
		
//		logCurrentPosition("C", customer.getCustomerID(), customer.getCurrentLink(), remainLinkDistance, customerSpeed, movingDistance);
//		logCurrentPath(customer.getCurrentPathLink());
		
		while (isMoveToNextLink) { 
			if(isAtLastLink(customer)) {
				setPositionAtEndNode(customer);
				updateEventForArrival(customer);//更新事件, 提早到或晚到
				break;
			} else {
				walkingTime -= (remainLinkDistance / customerSpeed) * 3600; //更新剩餘時間
				
				//確保機制
				if(isAtLastLink(customer)) {
					setPositionAtEndNode(customer);
					updateEventForArrival(customer);//更新事件, 提早到或晚到
					break;
				}
				
				moveToNextLink(customer); 	
				movingDistance = customerSpeed * (walkingTime/3600);
				remainLinkDistance = customer.getRemainLinkDistance();
				isMoveToNextLink = movingDistance > remainLinkDistance;			
			}
		}	
		
        if (!isMoveToNextLink) {
        	remainLinkDistance -= movingDistance;
        	setRemainDistance(customer, remainLinkDistance);
        }
        
    }
    
    private static void updateEventForArrival(Customer customer) {
    	Order order = customer.getCurrentOrder();
    	Event event = order.getCurrentEvent();
    	if(currentTime < event.getEventTimePoint()) { //提早到目的地
    		event.setEventTimePoint(currentTime); //提早事件執行時間
    		System.out.println("E" + event.getEventID() + "提前");
    	}else {
    		return;
    	} 	
    }
    
    public static void updatePositionForTaxi(Taxi taxi) {
		Link currentLink = taxi.getCurrentLink();
		double totalMovingDistance = 0;
		double betteryLevel = taxi.getBatteryLevel();
		double taxiSpeed = currentLink.getAvgSpeed();
		double taxiTravelTime = advancedTime; //秒
		double movingDistance = (taxiSpeed/3600) * taxiTravelTime; //前進的距離
		double remainLinkDistance = taxi.getRemainLinkDistance(); //目前link剩餘距離
		boolean isMoveToNextLink = movingDistance >= remainLinkDistance; //判斷有沒有超過剩餘距離 
		
        logCurrentPosition("T", taxi.getTaxiID(), currentLink, remainLinkDistance, taxiSpeed, movingDistance);
        logCurrentPath(taxi.getCurrentPathLink());
			
		while (isMoveToNextLink) { 
			if(isAtLastLink(taxi)) { 
				setPositionAtEndNode(taxi);
				updateEventForArrival(taxi);//更新事件, 提早到或晚到
				
				totalMovingDistance += remainLinkDistance;
				break;
			}else {
				taxiTravelTime -= (remainLinkDistance / taxiSpeed) * 3600; //更新剩餘時間
				moveToNextLink(taxi);	
				taxiSpeed = taxi.getCurrentSpeed();
				movingDistance = taxiSpeed * (taxiTravelTime/3600);
				remainLinkDistance = taxi.getRemainLinkDistance();
				isMoveToNextLink = movingDistance >= remainLinkDistance;			
				totalMovingDistance += remainLinkDistance;
			}
		}		
		
	    if (!isMoveToNextLink) {
	    	remainLinkDistance -= movingDistance;
	    	setRemainDistance(taxi, remainLinkDistance);
	    	totalMovingDistance += movingDistance;
	    }
	    betteryLevel -= totalMovingDistance;
		taxi.setBatteryLevel(betteryLevel);
		if(betteryLevel < 0) {
			System.out.println("電量負的有問題");
		}
		System.out.println("T" + taxi.getTaxiID() + " 剩餘電量:" + betteryLevel);
	}

    private static void updateEventForArrival(Taxi taxi) {
    	Event event = null;
    	if(taxi.getCurrentOrder() == null) {
    		event = taxi.getCurrentEvent();
    	}else {
        	Order order = taxi.getCurrentOrder();
        	event = order.getCurrentEvent();
    	}
    	if(currentTime < event.getEventTimePoint()) { //提早到目的地
    		event.setEventTimePoint(currentTime); //提早事件執行時間
    		System.out.println("E" + event.getEventID() + "提前");
    	}else {
    		return;
    	} 	
    }
    
    private static void moveToNextLink(Customer customer) {
	    ArrayList<Link> currentPathLink = customer.getCurrentPathLink();
	    int nextLinkIndex = customer.getIndexOfLink() + 1;
	    Link nextLink = currentPathLink.get(nextLinkIndex); //得到 nextLink
	    
	    setNextLink(customer, nextLink, nextLinkIndex);       	
	    System.out.println("C" + customer.getCustomerID() + "移動到L" + nextLink.getLinkID());
	}
	
	private static void moveToNextLink(Taxi taxi) {
        ArrayList<Link> currentPathLink = taxi.getCurrentPathLink();
        int nextLinkIndex = taxi.getIndexOfLink() + 1;
        
        //重新check
        if(taxi.getCurrentPathLink().size() < nextLinkIndex) {
        	System.out.println("T" + taxi.getTaxiID() + "path有問題");
        }
        
        
        Link nextLink = currentPathLink.get(nextLinkIndex);
        
        setNextLink(taxi, nextLink, nextLinkIndex);    	   	
	    System.out.println("T" + taxi.getTaxiID() + "移動到L" + nextLink.getLinkID());
	}
	  
    
    private static void setNextLink(Customer customer, Link nextLink, int nextLinkIndex) {
	    customer.setCurrentNodeID(customer.getNextNodeID());
	    customer.setCustomerX(customer.getNextX());
	    customer.setCustomerY(customer.getNextY());
	    customer.setCurrentLinkID(nextLink.getLinkID());
	    customer.setCurrentLink(nextLink);
	    customer.setIndexOfLink(nextLinkIndex);
	    customer.setRemainLinkDistance(nextLink.getDistance());
	    customer.setNextNodeID(nextLink.getEndNode().getNodeID());
	    customer.setNextX(nextLink.getEndNode().getNodeX());
	    customer.setNextY(nextLink.getEndNode().getNodeY());
	}
    
    private static void setNextLink(Taxi taxi, Link nextLink, int nextLinkIndex) {
        taxi.setTaxiSpeed(nextLink.getCurrentSpeed());
        taxi.setCurrentNodeID(taxi.getNextNodeID());
        taxi.setTaxiX(taxi.getNextX());
        taxi.setTaxiY(taxi.getNextY());
        taxi.setLinkID(nextLink.getLinkID());
        taxi.setCurrentLink(nextLink);
        taxi.setIndexOfLink(nextLinkIndex);
        taxi.setRemainLinkDistance(nextLink.getDistance());
        taxi.setNextNodeID(nextLink.getEndNode().getNodeID());
        taxi.setNextX(nextLink.getEndNode().getNodeX());
        taxi.setNextY(nextLink.getEndNode().getNodeY());
    }

	private static boolean isAtLastLink(Customer customer) {
		ArrayList<Link> currentPathLink = customer.getCurrentPathLink();
		Integer indexOfLink = customer.getIndexOfLink();
		if(indexOfLink.equals(currentPathLink.size() - 1)) {
			return true;
		}else {
			return false;
		}
	}

	private static boolean isAtLastLink(Taxi taxi) {
    	ArrayList<Link> currentPathLink = taxi.getCurrentPathLink();
    	Integer indexOfLink = taxi.getIndexOfLink();
    	if(indexOfLink.equals(currentPathLink.size() - 1)) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public static void setPositionAtEndNode(Customer customer) {
    	customer.setMoving(false);
    	clearPosition(customer);
	}
    
    private static void setPositionAtEndNode(Taxi taxi) {
    	System.out.println("T" + taxi.getTaxiID() + "到達終點N" + taxi.getCurrentNodeID());
        taxi.setTaxiSpeed(0);
        taxi.setMoving(false);
        clearPosition(taxi);		
	}

	private static void clearPosition(Customer customer) {
		customer.setCurrentNodeID(customer.getNextNodeID());
		customer.setCustomerX(customer.getNextX());
		customer.setCustomerY(customer.getNextY());
		customer.getCurrentPathNode().clear();
		customer.getCurrentPathLink().clear();
        System.out.println("clear C" + customer.getCustomerID() + "path at:" + currentTime);
		customer.setCurrentLinkID(-1);
		customer.setCurrentLink(null);
		customer.setIndexOfLink(-1);
		customer.setRemainLinkTime(0);
		customer.setNextNodeID(-1);
		customer.setNextX(-1);
		customer.setNextY(-1);
    }
	
	private static void clearPosition(Taxi taxi) {
        taxi.setCurrentNodeID(taxi.getNextNodeID());
        taxi.setTaxiX(taxi.getNextX());
        taxi.setTaxiY(taxi.getNextY());
        taxi.getCurrentPathNode().clear();
        taxi.getCurrentPathLink().clear();
        System.out.println("clear T" + taxi.getTaxiID() + "path at:" + currentTime);
        taxi.setLinkID(-1);
        taxi.setCurrentLink(null);
        taxi.setIndexOfLink(-1);
        taxi.setRemainLinkTime(0);
        taxi.setRemainLinkDistance(0);
        taxi.setNextNodeID(-1);
        taxi.setNextX(-1);
        taxi.setNextY(-1);
    }

	private static void setRemainDistance(Customer customer, double remainLinkDistance) {
		customer.setRemainLinkDistance(remainLinkDistance);
//		System.out.println(String.format("C%d 更新在L%d 剩餘%.5f(km)",
//				customer.getCustomerID(),
//				customer.getCurrentLink().getLinkID(),
//				customer.getRemainLinkDistance()
//				));
		System.out.println("remain:" + customer.getRemainLinkDistance());
	}

	private static void setRemainDistance(Taxi taxi, double remainLinkDistance) {
    	taxi.setRemainLinkDistance(remainLinkDistance);
    	System.out.println(String.format("T%d 更新在L%d 剩餘%.5f(km)",
    			taxi.getTaxiID(),
    			taxi.getCurrentLink().getLinkID(),
    			taxi.getRemainLinkDistance()
    			));
		System.out.println("remain:" + taxi.getRemainLinkDistance());

    }
    
    private static void logCurrentPosition(String entityType, int id, Link currentLink, double remainLinkDistance, double speed, double movingDistance) {

    	System.out.println(String.format("%s%d 在L%d dist:%.5f(km) 剩餘:%.5f(km) \n速度:%.2f(km/hr) 前進距離:%.5f(m)",
                entityType, id, currentLink.getLinkID(), currentLink.getDistance(), remainLinkDistance, speed, movingDistance * 1000));
    }

    private static void logCurrentPath(ArrayList<Link> currentPathLink) {
        System.out.print("目前path:");
        for (Link link : currentPathLink) {
            System.out.print("L" + link.getLinkID() + ",");
        }
        System.out.println();
    }
    
    
    private static void customerIsTakingTaxi(Taxi taxi, Customer customer) {
		customer.setCurrentNodeID(taxi.getCurrentNodeID());
		customer.setCustomerX(taxi.getTaxiX());
		customer.setCustomerY(taxi.getTaxiY());
		customer.setCurrentLink(taxi.getCurrentLink());
		customer.setCurrentLinkID(taxi.getLinkID());
		customer.setIndexOfLink(taxi.getIndexOfLink());
		customer.setRemainLinkTime(taxi.getRemainLinkTime());
		customer.setNextNodeID(taxi.getNextNodeID());
		customer.setNextX(taxi.getNextX());
		customer.setNextY(taxi.getNextY());
	}

	/**
     * 處理各種事件
     * 
     */
    
    public static void executeEvent(Event event) {
	    if (event == null) {
	        System.out.println("[Skipping event execution as event is null.]");
	        return;
	    }
    	switch (event.getEventCategory()) {
        case 1:
            customerRequestingTaxi(event);
            break;
        case 12:
        	reservedCustomerRequestingTaxi(event);
        	break;
        case 3:
        	customerArrivedAtPickUp(event);
        	break;
        case 4:
        	taxiArrivedAtPickup(event);
            break;
        case 42:
            oldTaxiAtNextNode(event);
            break;
        case 5:
        	customerBoarding(event);
        	break;
        case 6:
        	departureForDestination(event);
        	break;
        case 7:
        	arrivalDestination(event);
        	break;
        case 8:
        	serviceCompletion(event);
            break;
        case 9:
        	taxiArrivedAtChargingStation(event);
        	break;
        case 10:
        	chargingCompletion(event);
        	break;
        default:
            System.out.println("Unknown event category: " + event.getEventCategory());
            break;
    	}
	}
    
    private static void customerRequestingTaxi(Event event) { //orderStatus = 1
        System.out.println("------ 客戶叫車 orderStatus = 1 ------\n" + "現在時間:" + currentTime);
        
        Random random = new Random();
        boolean isRequestAgain = false;
        Customer newCustomer = null;
        
        if(finishedCustomerList.size() > 1) { //已經有人服務完成
            isRequestAgain = random.nextDouble() < REQUEST_AGAIN_PRO;
        }
        
        if(!isRequestAgain) {
            newCustomer = createCustomer(); 
        }else {
        	newCustomer = customerRequestAgain();
        }
        
        if(newCustomer.isReservation() && newCustomer.getTolerableWaitingTime() > 0) {
        	System.out.println("可容忍時間有問題");
        }
        
        Order newOrder = createOrderForNewCustomer(newCustomer); 
        
        logCustomerOrderInfo(newCustomer, newOrder); //印出
             
        matchCustomerWithTaxis(customerList); 
        scheduleNextCustomerEvent(); 
        eventList.remove(event); 
        
        System.out.println("------ 叫車事件完成 ------\n");
    }
    
    private static void reservedCustomerRequestingTaxi(Event event) { //orderStatus = 12
        System.out.println("------ 預約制客戶叫車 orderStatus = 12 ------\n" + "現在時間:" + currentTime);
        
        Random random = new Random();
        boolean isRequestAgain = false;
        Customer newCustomer = null;
        
        if(finishedCustomerList.size() > 1) {
        	isRequestAgain = random.nextDouble() < REQUEST_AGAIN_PRO;
        }
        
        if(!isRequestAgain) {
        	newCustomer = createReservedCustomer();
        }else {
        	newCustomer = reservedCustomerRequestAgain();
        }
        
        if(newCustomer.isReservation() && newCustomer.getTolerableWaitingTime() > 0) {
        	System.out.println("可容忍時間有問題");
        }
        
        Order newOrder = createOrderForNewReservedCustomer(newCustomer); 
        pendingReservedOrderNums++;
        System.out.println("目前pending數:" + pendingReservedOrderNums);
        
        logReservedCustomerOrderInfo(newCustomer, newOrder); //印出
        
        matchCustomerWithTaxis(customerList); 
        scheduleNextReservedCustomerEvent(); 
        eventList.remove(event); 
        
        System.out.println("------ 叫車事件完成 ------\n");
    }
    
	public static Customer createCustomer() {
		Customer customer = null;
		if(INTENSIVE_AREA != true) {
			customer = RandomGenerator.forCustomer(++customerNums, nodeList, linkList);
		}else {
			customer = RandomGenerator.forCustomerWithHotSpots(++customerNums, nodeList, linkList, currentTime, urbanNodeList, suburbsNodeList, restAreaNodeList); 
		}
	    customerList.add(customer);
	    return customer;
	}
	
	private static Customer customerRequestAgain() { //又叫車的隨機客戶
		System.out.println("客戶又叫車");
		//隨機抽取已經服務完成的客戶
		Random random = new Random();
		int totalNums = finishedCustomerList.size();
		int indexOfCustomer = random.nextInt(totalNums);
		Customer requestAgainCustomer = finishedCustomerList.get(indexOfCustomer);
		if(requestAgainCustomer.getRequestTimes() == 2) { //如果已經找過找別的看看, 有可能又找到一樣的
			indexOfCustomer = random.nextInt(totalNums);
			requestAgainCustomer = finishedCustomerList.get(indexOfCustomer);
		}
		
        //叫車點與上車點是否為同一個點
        double probabilityOfSameNode = 0.8; // 80% 的機率為同個上車點
        boolean isSameNode = random.nextDouble() < probabilityOfSameNode;

        //設定上車地點
        int pickUpNodeID = requestAgainCustomer.getCurrentNodeID(); //設定上車地點 = 叫車位置 (暫時)
        double pickUpX = requestAgainCustomer.getCustomerX();
        double pickUpY = requestAgainCustomer.getCustomerY();
               	       
        if(!isSameNode) {//不同點, 從鄰居node隨機挑一個點
            int neighborNums = nodeList.get(requestAgainCustomer.getCurrentNodeID() - 1).getNeighborNodes().size();
            int neighborIndex = random.nextInt(neighborNums); //random 不包含最後一個元素
            Node pickUpNode = nodeList.get(requestAgainCustomer.getCurrentNodeID() - 1).getNeighborNodes().get(neighborIndex);
            pickUpNodeID = pickUpNode.getNodeID();
            pickUpX = pickUpNode.getNodeX();
            pickUpY = pickUpNode.getNodeY();
        }
        
        requestAgainCustomer.setPickUpNodeID(pickUpNodeID);
        requestAgainCustomer.setPickUpX(pickUpX);
        requestAgainCustomer.setPickUpY(pickUpY);

		
		//設定下車點
		int dropOffNodeID = random.nextInt(300) + 1;
		while (dropOffNodeID == pickUpNodeID) { //避免上下車點相同
			dropOffNodeID = random.nextInt(300) + 1;
		}
		requestAgainCustomer.setDropOffNodeID(dropOffNodeID);//設定下車點
		requestAgainCustomer.setDropOffX(nodeList.get(dropOffNodeID - 1).getNodeX());
		requestAgainCustomer.setDropOffY(nodeList.get(dropOffNodeID - 1).getNodeY());
		
		//設定其他
		int requestTimes = requestAgainCustomer.getRequestTimes() + 1;
		requestAgainCustomer.setRequestTimes(requestTimes);
        double tolerableWaitingTime = (5 + (random.nextDouble() * 10)) * 60;
		requestAgainCustomer.setTolerableWaitingTime(tolerableWaitingTime);
		requestAgainCustomer.setReservation(false);
		
	    customerList.add(requestAgainCustomer);
	    return requestAgainCustomer;
	}
	
	private static Customer reservedCustomerRequestAgain() { //又叫車的隨機客戶
		System.out.println("客戶又叫車, 預約制");
		//隨機抽取已經服務完成的客戶
		Random random = new Random();
		int totalNums = finishedCustomerList.size();
		int indexOfCustomer = random.nextInt(totalNums);
		Customer requestAgainCustomer = finishedCustomerList.get(indexOfCustomer);
		if(requestAgainCustomer.getRequestTimes() == 2) { //如果已經找過找別的看看, 有可能又找到一樣的
			indexOfCustomer = random.nextInt(totalNums);
			requestAgainCustomer = finishedCustomerList.get(indexOfCustomer);
		}
		
		//設定customer上車點, 預約制設定目前與上車點要一樣
		int pickUpNodeID = random.nextInt(300) + 1;
		requestAgainCustomer.setPickUpNodeID(pickUpNodeID);//設定上車點
		requestAgainCustomer.setPickUpX(nodeList.get(pickUpNodeID - 1).getNodeX());
		requestAgainCustomer.setPickUpY(nodeList.get(pickUpNodeID - 1).getNodeY());
		requestAgainCustomer.setCurrentNodeID(pickUpNodeID);
		requestAgainCustomer.setCustomerX(nodeList.get(pickUpNodeID - 1).getNodeX());
		requestAgainCustomer.setCustomerY(nodeList.get(pickUpNodeID - 1).getNodeY());

			
		//設定下車點
		int dropOffNodeID = random.nextInt(300) + 1;
		while (dropOffNodeID == pickUpNodeID) { //避免上下車點相同
			dropOffNodeID = random.nextInt(300) + 1;
		}
		requestAgainCustomer.setDropOffNodeID(dropOffNodeID);//設定下車點
		requestAgainCustomer.setDropOffX(nodeList.get(dropOffNodeID - 1).getNodeX());
		requestAgainCustomer.setDropOffY(nodeList.get(dropOffNodeID - 1).getNodeY());
		
		//設定其他
		int requestTimes = requestAgainCustomer.getRequestTimes() + 1;
		requestAgainCustomer.setRequestTimes(requestTimes);
		requestAgainCustomer.setTolerableWaitingTime(0);
		requestAgainCustomer.setReservation(true);
        double pickUpTimePoint = currentTime + ((30 + (random.nextDouble() * 150)) * 60); //目前時間 + 0.5hr~3hr 內的
        requestAgainCustomer.setReservedPickUpTimePoint(pickUpTimePoint);
	        
	    customerList.add(requestAgainCustomer);
	    return requestAgainCustomer;
	}
		
	public static Customer createReservedCustomer() {
		Customer customer = null;
		if(INTENSIVE_AREA != true) {
			customer = RandomGenerator.forReservedCustomer(++customerNums, nodeList, linkList, currentTime);
		}else {
			customer = RandomGenerator.forReservedCustomerWithHotSpots(++customerNums, nodeList, linkList, currentTime, urbanNodeList, suburbsNodeList, restAreaNodeList);

		}

		customerList.add(customer);
	    return customer;
	}
	

	private static void logCustomerOrderInfo(Customer customer, Order order) {
        System.out.println(String.format(
            "C%d在N%d叫車於時間:%.3f 建立訂單O%d pickUp:N%d dropOff:N%d walkSpeed:%.2f(km/hr)",
            customer.getCustomerID(),
            order.getRequestNodeID(),
            currentTime,
            order.getOrderID(),
            order.getPickUpNodeID(),
            order.getDropOffNodeID(),
            customer.getWalkSpeed()
        ));
    }
    
    private static void logReservedCustomerOrderInfo(Customer customer, Order order) {
	    System.out.println(String.format(
	        "C%d在N%d預約叫車於時間:%.3f 預約上車時間:%.2f 建立訂單O%d pickUp:N%d dropOff:N%d walkSpeed:%.2f(km/hr)",
	        customer.getCustomerID(),
	        order.getRequestNodeID(),
	        currentTime,
	        order.getReservedPickUpTimePoint(),
	        order.getOrderID(),
	        order.getPickUpNodeID(),
	        order.getDropOffNodeID(),
	        customer.getWalkSpeed()
	    ));
	}

	public static void matchCustomerWithTaxis(ArrayList<Customer> customerList) {
		
		ArrayList<Match> newMatchList = new ArrayList<>();
		
		switch(matchMethod) {
			case "TDNRM":
			    newMatchList = RideMatch.negativeDiffAndNearest(customerList, taxiList, orderList, matchNums, 
			    		matchTaxiList, matchCustomerList, chargingStationList, nodeList, linkList, arrayLinkList,
			    		currentTime, pendingReservedOrderNums, PROCESS_WAITING_TOO_LONG
			    );
				
				break;
			case "TDSPRM":
			    newMatchList = RideMatch.negativeDiffAndShortestPath(customerList, taxiList, orderList, matchNums, 
			    		matchTaxiList, matchCustomerList, chargingStationList, nodeList, linkList, arrayLinkList,
			    		currentTime, pendingReservedOrderNums, PROCESS_WAITING_TOO_LONG
			    );
				
				break;
			case "TDNRM2":
			    newMatchList = RideMatch.negativeDiffAndNearest2(customerList, taxiList, orderList, matchNums, 
			    		matchTaxiList, matchCustomerList, chargingStationList, nodeList, linkList, arrayLinkList,
			    		currentTime, pendingReservedOrderNums, PROCESS_WAITING_TOO_LONG
			    );
				
				break;
		
		}
		
			
	    
	    if(!newMatchList.isEmpty()) {
	        updateMatchInformation(orderList, newMatchList); //建立資料, 更新事件
	        
	        //計算pending
	        for(Match m : newMatchList) {
	        	if(m.getOrder().isReservation()) {
	        		pendingReservedOrderNums--;
	        		
	        	}
	        }
	        System.out.println("剩餘pending預約數:" + pendingReservedOrderNums);
	    }
	    
//	    //配對完check剩下的車輛要不要充電
//	    checkTaxiListBetteryLevel(taxiList);
	}
	
	private static void checkTaxiListBetteryLevel(ArrayList<Taxi> taxiList) {
		
		
		for(Taxi taxi : taxiList) {
			if(taxi.getBatteryLevel() < 20) { //20km
				chargingTaxiList.add(taxi);
			}
		}
		
		if(!chargingTaxiList.isEmpty()) {
			for(Taxi t : chargingTaxiList) {
				if(taxiList.contains(t)) {
					taxiList.remove(t);
					goToChargingStation(t);
				}else {
					System.out.println("不包含t, 出錯");
				}	
			}
		}
	}
	
	public static void updateMatchInformation(ArrayList<Order> orderList, ArrayList<Match> newMatchList) {
        for (Match match : newMatchList) {
            Customer customer = match.getCustomer();
            Taxi taxi = match.getTaxi();
            Order order = customer.getCurrentOrder();
            match.setOrder(order);
            double totalDistance = 0;
            
            // 計算路徑時間

            ArrayList<Node> taxiToPickUpPathNode = FindPath.shortestPath(taxi.getCurrentNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
            ArrayList<Link> taxiToPickUpPathLink = FindPath.pathLinkGenerator(taxiToPickUpPathNode, linkList, arrayLinkList);
            double taxiToPickUpDistance = Calculate.pathTotalDistance(taxiToPickUpPathLink);
            order.setOldTaxiToPickUpDistance(taxiToPickUpDistance);
            
            ArrayList<Node> customerToPickUpPathNode = FindPath.shortestPath(order.getRequestNodeID(), customer.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
            ArrayList<Link> customerToPickUpPathLink = FindPath.pathLinkGenerator(customerToPickUpPathNode, linkList, arrayLinkList);

            double estimatedTaxiAtPickUpNodeTimePoint = currentTime + Calculate.drivingTimeByShortestTravelTimePath(taxi.getCurrentNodeID(), order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
            double estimatedCustomerAtPickUpNodeTimePoint = currentTime + Calculate.WalkingTimeByShortestPath(customer, order.getRequestNodeID(), order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
            order.setTaxiToPickUpTimePoint(estimatedTaxiAtPickUpNodeTimePoint);
            
            ArrayList<Node> toDropOffPathNode = FindPath.shortestPath(customer.getPickUpNodeID(), customer.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
            ArrayList<Link> toDropOffPathLink = FindPath.pathLinkGenerator(toDropOffPathNode, linkList, arrayLinkList);
            double toDropOffTravelDistance = Calculate.pathTotalDistance(toDropOffPathLink);
            
            findNearestStationForDropOffNode(taxi, order.getDropOffNodeID());
            int stationNodeID = taxi.getTargetChargingStationNodeID();
            ArrayList<Node> toStationPathNode = FindPath.shortestPath(customer.getDropOffNodeID(), stationNodeID, nodeList, linkList, arrayLinkList);
            ArrayList<Link> toStationPathLink = FindPath.pathLinkGenerator(toStationPathNode, linkList, arrayLinkList);
            double toStationTravelDistance = Calculate.pathTotalDistance(toStationPathLink);
            double sumTravelDistance = toDropOffTravelDistance + toStationTravelDistance;
            order.setPickUpToDropOffToStationDistance(sumTravelDistance);
            totalDistance = taxiToPickUpDistance + toDropOffTravelDistance;
            order.setTotalTravelDistance(totalDistance);
            
            updateOrderAndMatch(order, match, taxi, customer, taxiToPickUpPathNode, taxiToPickUpPathLink, customerToPickUpPathNode, customerToPickUpPathLink, toDropOffPathNode, toDropOffPathLink);
            handleOrderStatus(order, match, taxi, customer, taxiToPickUpPathNode, taxiToPickUpPathLink, customerToPickUpPathNode, customerToPickUpPathLink, toDropOffPathNode, toDropOffPathLink, estimatedTaxiAtPickUpNodeTimePoint, estimatedCustomerAtPickUpNodeTimePoint);
        }
    }

    private static void updateOrderAndMatch(Order order, Match match, Taxi taxi, Customer customer, ArrayList<Node> taxiToPickUpPathNode, ArrayList<Link> taxiToPickUpPathLink, ArrayList<Node> customerToPickUpPathNode, ArrayList<Link> customerToPickUpPathLink, ArrayList<Node> toDropOffPathNode, ArrayList<Link> toDropOffPathLink) {
        order.setCurrentMatch(match);
        order.setMatchTaxi(taxi);
        order.setToDropOffPathNode(toDropOffPathNode);
        order.setToDropOffPathLink(toDropOffPathLink);
        order.getOrderTimePoint().put(2, currentTime); //實際完成2事件時間
        order.getEstimatedOrderTimePoint().put(2, currentTime);
        match.setTaxiToPickUpPathNode(taxiToPickUpPathNode);
        match.setTaxiToPickUpPathLink(taxiToPickUpPathLink);
        customer.setCurrentMatch(match);
        taxi.setCurrentMatch(match);
        taxi.setCurrentOrder(order);
    }

    private static void handleOrderStatus(Order order, Match match, Taxi taxi, Customer customer, ArrayList<Node> taxiToPickUpPathNode, ArrayList<Link> taxiToPickUpPathLink, ArrayList<Node> customerToPickUpPathNode, ArrayList<Link> customerToPickUpPathLink, ArrayList<Node> toDropOffPathNode, ArrayList<Link> toDropOffPathLink, double estimatedTaxiAtPickUpNodeTimePoint, double estimatedCustomerAtPickUpNodeTimePoint) {
        int orderStatus;
        boolean isCustomerAtPickUp = isAtPickUp(customer, order);
        boolean isTaxiAtPickUp = isAtPickUp(taxi, order);

        if (isCustomerAtPickUp && isTaxiAtPickUp) {
            orderStatus = 5;
            order.setOrderStatus(orderStatus); //設定訂單狀態
            updateOrderTimePoints(order, 5, currentTime);
            updateCustomerAndTaxiForDropOff(customer, taxi, toDropOffPathNode, toDropOffPathLink);
            createCustomerBoardingEvent(customer, taxi, match, order);
        } else if (isCustomerAtPickUp && !isTaxiAtPickUp) {
            orderStatus = 3;
            order.setOrderStatus(orderStatus);
            updateOrderTimePoints(order, 3, currentTime);
            System.out.println("上車點N"+order.getPickUpNodeID()+" 車子所在位置N"+ order.getMatchedTaxi().getCurrentNodeID());
            updateTaxiForPickUp(taxi, taxiToPickUpPathNode, taxiToPickUpPathLink);
            createTaxiAtPickUpEvent(customer, taxi, match, order, estimatedTaxiAtPickUpNodeTimePoint);
        } else if (!isCustomerAtPickUp && isTaxiAtPickUp) {
            orderStatus = 4;
            order.setOrderStatus(orderStatus);
            updateOrderTimePoints(order, 4, currentTime);
            updateCustomerForPickUp(customer, customerToPickUpPathNode, customerToPickUpPathLink);
            createCustomerAtPickUpEvent(customer, taxi, match, order, estimatedCustomerAtPickUpNodeTimePoint);
        } else if(!isCustomerAtPickUp && !isTaxiAtPickUp) {
            orderStatus = 2;
            order.setOrderStatus(orderStatus);
            updateCustomerAndTaxiForPickUp(customer, taxi, customerToPickUpPathNode, customerToPickUpPathLink, taxiToPickUpPathNode, taxiToPickUpPathLink);
            createPickUpEvents(customer, taxi, match, order, estimatedCustomerAtPickUpNodeTimePoint, estimatedTaxiAtPickUpNodeTimePoint);
        }
    }

    private static boolean isAtPickUp(Customer customer, Order order) {
		int currentNodeID = customer.getCurrentNodeID();
		int pickUpNodeID = order.getPickUpNodeID();
		if(currentNodeID == pickUpNodeID) {
			return true;
		}else {
			return false;
		}
	}

	private static boolean isAtPickUp(Taxi taxi, Order order) {
		int currentNodeID = taxi.getCurrentNodeID();
		int pickUpNodeID = order.getPickUpNodeID();
		if(currentNodeID == pickUpNodeID) {
			return true;
		}else {
			return false;
		}
	}

	private static void updateOrderTimePoints(Order order, int orderStatus, double currentTime) {
	    order.getOrderTimePoint().put(orderStatus, currentTime);
	    order.getEstimatedOrderTimePoint().put(orderStatus, currentTime);
	    if(orderStatus == 5) {
		    order.getOrderTimePoint().put(3, currentTime);
		    order.getOrderTimePoint().put(4, currentTime);
		    order.getEstimatedOrderTimePoint().put(3, currentTime);
		    order.getEstimatedOrderTimePoint().put(4, currentTime);			    
		}
	}

	private static void updateCustomerAndTaxiForDropOff(Customer customer, Taxi taxi, ArrayList<Node> toDropOffPathNode, ArrayList<Link> toDropOffPathLink) {
	    updateCustomerPath(customer, toDropOffPathNode, toDropOffPathLink);
	    updateTaxiPath(taxi, toDropOffPathNode, toDropOffPathLink);
	}

	private static void updateTaxiForPickUp(Taxi taxi, ArrayList<Node> taxiToPickUpPathNode, ArrayList<Link> taxiToPickUpPathLink) {

		ArrayList<Node> checkToPickUpPathNode = FindPath.shortestPath(taxi.getCurrentNodeID(), taxi.getCurrentOrder().getPickUpNodeID(), nodeList, linkList, arrayLinkList);
		ArrayList<Link> checkToPickUpPathLink = FindPath.pathLinkGenerator(checkToPickUpPathNode, linkList, arrayLinkList);
		taxi.setCurrentPathNode(checkToPickUpPathNode);
		taxi.setCurrentPathLink(checkToPickUpPathLink);
		
		while(taxiToPickUpPathLink.isEmpty()) {
			checkToPickUpPathNode = FindPath.shortestPath(taxi.getCurrentNodeID(), taxi.getCurrentOrder().getPickUpNodeID(), nodeList, linkList, arrayLinkList);
			checkToPickUpPathLink = FindPath.pathLinkGenerator(checkToPickUpPathNode, linkList, arrayLinkList);
			taxi.setCurrentPathNode(checkToPickUpPathNode);
			taxi.setCurrentPathLink(checkToPickUpPathLink);
		}
		
		Link link = taxiToPickUpPathLink.get(0);
	    taxi.setTaxiSpeed(link.getCurrentSpeed());
	    taxi.setMoving(true);
	    updateTaxiPath(taxi, taxiToPickUpPathNode, taxiToPickUpPathLink);
	}

	private static void updateCustomerForPickUp(Customer customer, ArrayList<Node> customerToPickUpPathNode, ArrayList<Link> customerToPickUpPathLink) {
	    customer.setMoving(true);
	    updateCustomerPath(customer, customerToPickUpPathNode, customerToPickUpPathLink);
	}

	private static void updateCustomerAndTaxiForPickUp(Customer customer, Taxi taxi, ArrayList<Node> customerToPickUpPathNode, ArrayList<Link> customerToPickUpPathLink, ArrayList<Node> taxiToPickUpPathNode, ArrayList<Link> taxiToPickUpPathLink) {
	    updateCustomerForPickUp(customer, customerToPickUpPathNode, customerToPickUpPathLink);
	    updateTaxiForPickUp(taxi, taxiToPickUpPathNode, taxiToPickUpPathLink);
	}

	private static void updateCustomerPath(Customer customer, ArrayList<Node> pathNodes, ArrayList<Link> pathLinks) {
	    customer.setCurrentPathNode(pathNodes);
	    customer.setCurrentPathLink(pathLinks);
	    if (!pathLinks.isEmpty()) {
	        Link link = pathLinks.get(0);
	        double linkDistance = link.getDistance();
	        double linkWalkTime = (linkDistance / customer.getWalkSpeed()) * 3600;
	        customer.setCurrentLink(link);
	        customer.setCurrentLinkID(link.getLinkID());
	        customer.setIndexOfLink(pathLinks.indexOf(link));
	        customer.setRemainLinkTime(linkWalkTime);
	        customer.setRemainLinkDistance(linkDistance);
	        customer.setNextNodeID(link.getEndNode().getNodeID());
	        customer.setNextX(link.getEndNode().getNodeX());
	        customer.setNextY(link.getEndNode().getNodeY());
	    }
	}

	private static void updateTaxiPath(Taxi taxi, ArrayList<Node> pathNodes, ArrayList<Link> pathLinks) {
	    taxi.setCurrentPathNode(pathNodes);
	    taxi.setCurrentPathLink(pathLinks);
	    if (!pathLinks.isEmpty()) {
	        Link link = pathLinks.get(0);
	        taxi.setLinkID(link.getLinkID());
	        taxi.setCurrentLink(link);
	        taxi.setIndexOfLink(pathLinks.indexOf(link));
	        taxi.setRemainLinkTime(link.getTravelTime());
	        taxi.setRemainLinkDistance(link.getDistance());
	        taxi.setNextNodeID(link.getEndNode().getNodeID());
	        taxi.setNextX(link.getEndNode().getNodeX());
	        taxi.setNextY(link.getEndNode().getNodeY());
	    }
	}

	private static void createCustomerBoardingEvent(Customer customer, Taxi taxi, Match match, Order order) {
	    Event customerBoardingEvent = new Event(++eventNums, 5, currentTime);
	    order.getEstimatedOrderTimePoint().put(5, currentTime);
	    order.setCurrentEvent(customerBoardingEvent);
	    customerBoardingEvent.setRelateCustomer(customer);
	    customerBoardingEvent.setRelateTaxi(taxi);
	    customerBoardingEvent.setRelateMatch(match);
	    customerBoardingEvent.setRelateOrder(order);
	    eventList.add(customerBoardingEvent);
	}

	private static void createTaxiAtPickUpEvent(Customer customer, Taxi taxi, Match match, Order order, double timePoint) {
	    Event taxiAtPickUpEvent = new Event(++eventNums, 4, timePoint);
	    order.getEstimatedOrderTimePoint().put(4, currentTime);
	    order.setCurrentEvent(taxiAtPickUpEvent);
	    order.setTaxiToPickUpEvent(taxiAtPickUpEvent);
	    taxiAtPickUpEvent.setRelateCustomer(customer);
	    taxiAtPickUpEvent.setRelateMatch(match);
	    taxiAtPickUpEvent.setRelateOrder(order);
	    taxiAtPickUpEvent.setRelateTaxi(taxi);
	    eventList.add(taxiAtPickUpEvent);
	}

	private static void createCustomerAtPickUpEvent(Customer customer, Taxi taxi, Match match, Order order, double timePoint) {
	    Event customerAtPickUpEvent = new Event(++eventNums, 3, timePoint);
	    order.getEstimatedOrderTimePoint().put(3, currentTime);
	    order.setCurrentEvent(customerAtPickUpEvent);
	    customerAtPickUpEvent.setRelateCustomer(customer);
	    customerAtPickUpEvent.setRelateMatch(match);
	    customerAtPickUpEvent.setRelateOrder(order);
	    customerAtPickUpEvent.setRelateTaxi(taxi);
	    eventList.add(customerAtPickUpEvent);
	}

	private static void createPickUpEvents(Customer customer, Taxi taxi, Match match, Order order, double estimatedCustomerAtPickUpNodeTimePoint, double estimatedTaxiAtPickUpNodeTimePoint) {
	    createCustomerAtPickUpEvent(customer, taxi, match, order, estimatedCustomerAtPickUpNodeTimePoint);
	    createTaxiAtPickUpEvent(customer, taxi, match, order, estimatedTaxiAtPickUpNodeTimePoint);
	}

	private static void customerArrivedAtPickUp(Event event) { //3
        Order order = event.getRelateOrder();
        Match match = order.getCurrentMatch();
        Customer customer = order.getCreatorCustomer();
        Taxi taxi = order.getMatchTaxi();
    	
    	//確認實際位置是不是到了
    	if(isAtPickUpNode(order, customer)) {
    	
	    	System.out.println("------ 客戶 到達 上車點 orderStatus = 3 ------");
	        System.out.println("現在時間:" + currentTime);           
	        System.out.println(String.format("O%d,C%d,到達上車點N%d,等待T%d", 
	        		order.getOrderID(),
	        		customer.getCustomerID(),
	        		order.getPickUpNodeID(),
	        		taxi.getTaxiID()));
	        
	        //更新order
	        if(order.getOrderStatus() == 2) {
	            order.setOrderStatus(3);
	            order.getOrderTimePoint().put(3, currentTime);
	            
	            //更新customer, reset customer 狀態
	            customer.setMoving(false);
	            customer.setCurrentNodeID(order.getPickUpNodeID());
	            customer.setCustomerX(order.getPickUpX());
	            customer.setCustomerY(order.getPickUpY());
	            customer.getCurrentPathNode().clear();
	            customer.getCurrentPathLink().clear();
	            customer.setCurrentLink(null);
	            customer.setCurrentLinkID(-1);
	            customer.setIndexOfLink(-1);
	            customer.setRemainLinkTime(-1);
	            customer.setNextNodeID(-1);
	            customer.setNextX(-1);
	            customer.setNextY(-1);
	            
	            System.out.println("------ 客戶等待計程車 ------");
	            System.out.println();
	        }
	        
	        if(order.getOrderStatus() == 4) {
	            order.setOrderStatus(5);
	            order.getOrderTimePoint().put(3, currentTime);
	            
	            customer.setMoving(false);
	            customer.setCurrentNodeID(order.getPickUpNodeID());
	            customer.setCustomerX(order.getPickUpX());
	            customer.setCustomerY(order.getPickUpY());
	            customer.getCurrentPathNode().clear();
	            customer.getCurrentPathLink().clear();
	            customer.setCurrentLink(null);
	            customer.setCurrentLinkID(-1);
	            customer.setIndexOfLink(-1);
	            customer.setRemainLinkTime(-1);
	            customer.setNextNodeID(-1);
	            customer.setNextX(-1);
	            customer.setNextY(-1);
	            
	            //增加event
	            Event customerBoarding = new Event(++eventNums, 5, currentTime);
	            customerBoarding.setRelateCustomer(customer);
	            customerBoarding.setRelateMatch(match);
	            customerBoarding.setRelateOrder(order);
	            customerBoarding.setRelateTaxi(taxi);
	            eventList.add(customerBoarding);
	            
	            System.out.println("------ 都到達上車點 ------");
	            System.out.println();
	          
	        }
    	} 
    }
    
    private static boolean isAtPickUpNode(Order order, Customer customer) {
    	Integer pickUpNodeID = order.getPickUpNodeID();
    	Integer currentNodeID = customer.getCurrentNodeID();
    	if (pickUpNodeID.equals(currentNodeID)) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    private static boolean isAtPickUpNode(Order order, Taxi taxi) {
    	System.out.println("pN" +order.getPickUpNodeID()+ " cN"+taxi.getCurrentNodeID());
    	
    	Integer pickUpNodeID = order.getPickUpNodeID();
    	Integer currentNodeID = taxi.getCurrentNodeID();
    	if (pickUpNodeID.equals(currentNodeID)) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    private static boolean isAtDropOffNode(Order order, Taxi taxi) {
    	Integer dropOffNodeID = order.getDropOffNodeID();
    	Integer currentNodeID = taxi.getCurrentNodeID();
    	if (dropOffNodeID.equals(currentNodeID)) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public static boolean isAtChargingStationNode(Taxi taxi) {
    	Integer chargingStationNodeID = taxi.getTargetChargingStationNodeID();
    	Integer currentNodeID = taxi.getCurrentNodeID();
    	
    	if (chargingStationNodeID.equals(currentNodeID)) {
        	System.out.println("T" + taxi.getTaxiID() + "在N" + taxi.getCurrentNodeID() + ", station在N" + taxi.getTargetChargingStationNodeID());
    		System.out.println("在充電站");
        	return true;
    	}else {
        	System.out.println("T" + taxi.getTaxiID() + "在N" + taxi.getCurrentNodeID() + ", station在N" + taxi.getTargetChargingStationNodeID());
    		return false;
    	}
    }
    
    public static boolean isAtNextNode(Taxi taxi) {
    	
    	Integer nextNodeID = taxi.getOldTaxiFinalNodeID();
    	Integer currentNodeID = taxi.getCurrentNodeID();
    	
    	System.out.println("NextN" + nextNodeID + " CurrentN" + taxi.getCurrentNodeID());
    	
    	if (nextNodeID.equals(currentNodeID)) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public static void taxiArrivedAtPickup(Event event) { //4
         System.out.println("------ 計程車  到達 上車點 orderStatus = 4 ------");
         System.out.println("現在時間:" + currentTime);
    	 
         Order order = event.getRelateOrder();
         System.out.println("status:" + order.getOrderStatus());
         Match match = order.getCurrentMatch();
         Customer customer = order.getCreatorCustomer();
         Taxi taxi = order.getMatchTaxi();
         
         //更新order
         if(order.getOrderStatus() == 2) {
             order.setOrderStatus(4);
             order.getOrderTimePoint().put(4, currentTime);
             
             //更新taxi, reset customer 狀態
             taxi.setMoving(false);
             taxi.setCurrentNodeID(order.getPickUpNodeID());
             taxi.setTaxiX(order.getPickUpX());
             taxi.setTaxiY(order.getPickUpY());
             taxi.getCurrentPathNode().clear();
             taxi.getCurrentPathLink().clear();
             taxi.setCurrentLink(null);
             taxi.setLinkID(-1);
             taxi.setIndexOfLink(-1);
             taxi.setRemainLinkTime(-1);
             taxi.setRemainLinkDistance(-1);
             taxi.setNextNodeID(-1);
             taxi.setNextX(-1);
             taxi.setNextY(-1);
             
             System.out.println("------ 計程車等待客戶 ------");
             System.out.println();
         }
         
         if(order.getOrderStatus() == 3) {
             order.setOrderStatus(5);
             order.getOrderTimePoint().put(4, currentTime);
             
             taxi.setMoving(false);
             System.out.println("pickUpNodeID:"+ order.getPickUpNodeID());
             taxi.setCurrentNodeID(order.getPickUpNodeID());
             taxi.setTaxiX(order.getPickUpX());
             taxi.setTaxiY(order.getPickUpY());
             taxi.getCurrentPathNode().clear();
             taxi.getCurrentPathLink().clear();
             taxi.setCurrentLink(null);
             taxi.setLinkID(-1);
             taxi.setIndexOfLink(-1);
             taxi.setRemainLinkTime(-1);
             taxi.setRemainLinkDistance(-1);
             taxi.setNextNodeID(-1);
             taxi.setNextX(-1);
             taxi.setNextY(-1);
             
             //增加event
             Event customerBoarding = new Event(++eventNums, 5, currentTime);
             customerBoarding.setRelateCustomer(customer);
             customerBoarding.setRelateMatch(match);
             customerBoarding.setRelateOrder(order);
             customerBoarding.setRelateTaxi(taxi);
             eventList.add(customerBoarding);
             
             System.out.println(String.format("O%d,T%d到達上車點N%d",
            		 order.getOrderID(),
            		 taxi.getTaxiID(),
            		 order.getPickUpNodeID()));
             
             System.out.println("------ 都到達上車點 ------");
             System.out.println();
           
         }
    }
    
    private static void customerBoarding(Event event) { //5
        System.out.println("------ 客戶 上車中 orderStatus = 5 ------");
        System.out.println("現在時間:" + currentTime);
        Random random = new Random();
        Order order = event.getRelateOrder();
        Match match = event.getRelateMatch();
        System.out.println("order:" + order.getOrderID());
        Customer customer = order.getCreatorCustomer();
        Taxi taxi = order.getMatchTaxi();
        System.out.println("C" + customer.getCustomerID() + "上車T" + taxi.getTaxiID() + "中");
        
        //更新 order
        order.setOrderStatus(5);
        order.getOrderTimePoint().put(5, currentTime);
        //更新 taxi
        taxi.setCurrentPathNode(order.getToDropOffPathNode());
        taxi.setCurrentPathLink(order.getToDropOffPathLink());
        System.out.println("設定toDropOffPath");
        
        if (order.getToDropOffPathNode().size() == 1) {
        	for(Node node : order.getToDropOffPathNode()) {
        		System.out.println("N" + node.getNodeID());
        	}
        	
        	ArrayList<Node> checkPath = new ArrayList<>();
        	ArrayList<Link> checkPathLink = new ArrayList<>();
        	checkPath = FindPath.shortestPath(order.getPickUpNodeID(), order.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
        	checkPathLink = FindPath.pathLinkGenerator(checkPath, linkList, arrayLinkList);
        	
        	//重新設定
        	order.setToDropOffPathNode(checkPath);
        	order.setToDropOffPathLink(checkPathLink);
        	taxi.setCurrentPathNode(checkPath);
        	taxi.setCurrentPathLink(checkPathLink);
        	
        	for(Link link : taxi.getCurrentPathLink()) {
        		System.out.print("L" + link.getLinkID() + ",");
        	}
    		System.out.println();

        	
        	System.out.println("上車點N:" + order.getPickUpNodeID() + ", 下車點N:" + order.getDropOffNodeID());
        }
        Link link = order.getToDropOffPathLink().get(0);
        taxi.setLinkID(link.getLinkID());
        taxi.setCurrentLink(link);
        taxi.setIndexOfLink(0);
        taxi.setRemainLinkTime(link.getTravelTime());
        taxi.setRemainLinkDistance(link.getDistance());
        taxi.setNextNodeID(link.getEndNode().getNodeID());
        taxi.setNextX(link.getEndNode().getNodeX());
        taxi.setNextY(link.getEndNode().getNodeY());

        //更新 customer
        customer.setCurrentPathNode(order.getToDropOffPathNode());
        customer.setCurrentPathLink(order.getToDropOffPathLink());
        customer.setCurrentLinkID(link.getLinkID());
        customer.setCurrentLink(link);
        customer.setIndexOfLink(0);
        customer.setRemainLinkTime(link.getTravelTime());
        customer.setNextNodeID(link.getEndNode().getNodeID());
        customer.setNextX(link.getEndNode().getNodeX());
        customer.setNextY(link.getEndNode().getNodeY());
        
        double boardingTime = 10 + (random.nextDouble() * 140); //10秒~150秒 隨機上車時間
        double departureTimePoint = currentTime + boardingTime;
        System.out.println("C" + order.getCreatorCustomer().getCustomerID() + "預計上車時間:" + boardingTime);
        Event departureEvent = new Event(++eventNums, 6, departureTimePoint);
        departureEvent.setRelateCustomer(customer);
        departureEvent.setRelateMatch(match);
        departureEvent.setRelateOrder(order);
        departureEvent.setRelateTaxi(taxi);
        
        eventList.add(departureEvent);    
        
        System.out.println("------ 客戶 上車中  ------");
        System.out.println();
    }
    
    private static void departureForDestination(Event event) { //6
        System.out.println("------ 計程車出發前往目的地  orderStatus = 6 ------");
        System.out.println("現在時間:" + currentTime);        
        Order order = event.getRelateOrder();
        Match match = order.getCurrentMatch();
        Customer customer = order.getCreatorCustomer();
        Taxi taxi = order.getMatchTaxi();
        
        //更新 order
        order.setOrderStatus(6);
        order.getOrderTimePoint().put(6, currentTime);
        
        //更新 customer
        customer.setMoving(true);
        
        //更新 taxi
        taxi.setMoving(true);
        taxi.setTaxiSpeed(taxi.getCurrentLink().getCurrentSpeed());
        
        //checkPath
        if(taxi.getCurrentPathLink() == null) {
        	taxi.setCurrentPathLink(order.getToDropOffPathLink());
        }
        
        //計算到達計程車時間
        double drivingToDropOffTime = Calculate.drivingTimeByShortestTravelTimePath(order.getPickUpNodeID(), order.getDropOffNodeID(), nodeList, linkList, arrayLinkList);
        order.getEstimatedOrderTimePoint().put(7, currentTime + drivingToDropOffTime);
        System.out.println("T" + taxi.getTaxiID() + "預計行車時間:" + drivingToDropOffTime);
        
        //更新event
        double arrivalDestinationTimePoint = order.getEstimatedOrderTimePoint().get(7);
        Event arrivalDestination = new Event(++eventNums, 7, arrivalDestinationTimePoint);
        arrivalDestination.setRelateCustomer(customer);
        arrivalDestination.setRelateMatch(match);
        arrivalDestination.setRelateOrder(order);
        arrivalDestination.setRelateTaxi(taxi);
        eventList.add(arrivalDestination);
        
        System.out.println("------ 計程車 服務中  ------");
        System.out.println();
        
    }
    
    private static void arrivalDestination(Event event) {//7
        System.out.println("------到達目的地  客戶 下車中 orderStatus = 7 ------");
        System.out.println("現在時間:" + currentTime);
        
        Order order = event.getRelateOrder();
        Match match = order.getCurrentMatch();
        Customer customer = order.getCreatorCustomer();
        Taxi taxi = order.getMatchTaxi();
        Random random = new Random();
        
        System.out.println("T" + taxi.getTaxiID() + "到達目的地N" + order.getDropOffNodeID());
        
        //更新order
        order.setOrderStatus(7);
        order.getOrderTimePoint().put(7, currentTime);
        
        //更新 taxi
        taxi.setTaxiSpeed(-1);
        taxi.setMoving(false);
        taxi.getCurrentPathNode().clear();
        taxi.getCurrentPathLink().clear();
        taxi.setLinkID(-1);
        taxi.setCurrentLink(null);
        taxi.setIndexOfLink(-1);
        taxi.setRemainLinkTime(-1);
        taxi.setRemainLinkDistance(-1);
        taxi.setNextNodeID(-1);
        taxi.setNextX(-1);
        taxi.setNextY(-1);

        //更新 customer
        customer.setMoving(false);
        customer.getCurrentPathNode().clear();
        customer.getCurrentPathLink().clear();
        customer.setCurrentLinkID(-1);
        customer.setCurrentLink(null);
        customer.setIndexOfLink(-1);
        customer.setRemainLinkTime(-1);
        customer.setNextNodeID(-1);
        customer.setNextX(-1);
        customer.setNextY(-1);
        
        double gettingOffTime = 10 + (random.nextDouble() * 140); //10秒~150秒 隨機下車時間
        System.out.println("C" + customer.getCustomerID() + "預計下車時間" + gettingOffTime);
        double serviceCompleteTimePoint = currentTime + gettingOffTime;
        System.out.println("C" + customer.getCustomerID() + "預計下車完成at:" + serviceCompleteTimePoint);
        Event serviceCompleteEvent = new Event(++eventNums, 8, serviceCompleteTimePoint);
        serviceCompleteEvent.setRelateCustomer(customer);
        serviceCompleteEvent.setRelateMatch(match);
        serviceCompleteEvent.setRelateOrder(order);
        serviceCompleteEvent.setRelateTaxi(taxi);
        eventList.add(serviceCompleteEvent);
        
        System.out.println("------ 客戶 下車中  ------");
        System.out.println();
        
    }

    public static void serviceCompletion(Event event) {//8
        System.out.println("------服務完成  orderStatus = 8 ------");
        System.out.println("現在時間:" + currentTime);
        Order order = event.getRelateOrder();
        Match match = order.getCurrentMatch();
        Customer customer = order.getCreatorCustomer();
        Taxi taxi = order.getMatchTaxi();
        
        System.out.println(String.format("O%d,T%d服務完C%d,且下車完成", 
        		order.getOrderID(),
        		taxi.getTaxiID(),
        		customer.getCustomerID()));
        
        //更新 order
        order.setOrderStatus(8);
        order.getOrderTimePoint().put(8, currentTime);
        orderList.remove(order);
        pastOrderList.add(order);
        
        //更新match
        matchList.remove(match);
        
        //更新 customer
        customer.setReservation(false);
        customer.setReservedPickUpTimePoint(-1);
        customer.setPickUpNodeID(-1);
        customer.setPickUpX(-1);
        customer.setPickUpY(-1);
        customer.setDropOffNodeID(-1);
        customer.setDropOffX(-1);
        customer.setDropOffY(-1);
        customer.setCurrentOrder(null);
        customer.setCurrentMatch(null);
        customer.getPastOrder().add(order);
        matchCustomerList.remove(customer);
        finishedCustomerList.add(customer);
        if(customerList.contains(customer)) customerList.remove(customer);
        
        
         
        if(needsBetteryCharged(taxi)){ //去充電
        	matchTaxiList.remove(taxi);
			chargingTaxiList.add(taxi);
            System.out.println("T" + taxi.getTaxiID() + "需要充電");
			goToChargingStation(taxi);

        }else { //不用充電
        	if(DRRM) {
        		if(!reAsign(taxi, orderList)) {
                    taxiList.add(taxi);
                    matchTaxiList.remove(taxi);
                    System.out.println("T" + taxi.getTaxiID() + "沒有更換指派, 可重新被匹配");
        		}
        	}else {
                taxiList.add(taxi);
                matchTaxiList.remove(taxi);
                System.out.println("T" + taxi.getTaxiID() + "沒有更換指派, 可重新被匹配");
        	}
        }
        serviceCompleteNums++;
        
        System.out.println("--- Service completion processed ---");
        System.out.println();
    }
    
    public static void taxiArrivedAtChargingStation(Event event) {
	    System.out.println("------taxi 到達 charging station ------");
	    System.out.println("現在時間:" + currentTime);
	    
	    Taxi taxi = event.getRelateTaxi();
	    System.out.println("T" + taxi.getTaxiID());
	    ChargingStation station = event.getRelateStation();
	    System.out.println(taxi.getTargetChargingStationNodeID() + ",CH" + station.getChargerID());
	
	         
	    //更新 taxi
	    taxi.setTaxiSpeed(-1);
	    taxi.setMoving(false);
	    taxi.getCurrentPathNode().clear();
	    taxi.getCurrentPathLink().clear();
	    taxi.setLinkID(-1);
	    taxi.setCurrentLink(null);
	    taxi.setIndexOfLink(-1);
	    taxi.setRemainLinkTime(-1);
	    taxi.setRemainLinkDistance(-1);
	    taxi.setNextNodeID(-1);
	    taxi.setNextX(-1);
	    taxi.setNextY(-1);
	    
	    //更新 chargingStation
	    station.getChargingTaxiList().add(taxi);
	    if(station.getChargingTaxiList().size() == station.getMaxChargeNums()) {
	    	station.setFull(true);
	    }
	    
	    //計算充滿時間, 平均52min充電完, 1分鐘充11單位 
	    double needsChargeBetteryLevel = MAX_BETTERY - taxi.getBatteryLevel();
	    double chargingTime = needsChargeBetteryLevel / 11;
	    System.out.println("T" + taxi.getTaxiID() + "預計充電時間" + chargingTime);
	    double chargingCompleteTimePoint = currentTime + chargingTime;
	    System.out.println("T" + taxi.getTaxiID() + "預計充電完成時間點" + chargingCompleteTimePoint);
	    Event chargingCompleteEvent = new Event(++eventNums, 10, chargingCompleteTimePoint);
	    chargingCompleteEvent.setRelateTaxi(taxi);
	    chargingCompleteEvent.setRelateStation(station);
	    eventList.add(chargingCompleteEvent);
	    
	    System.out.println("------ taxi 充電中  ------");
	    System.out.println();
	}

	private static void chargingCompletion(Event event) {
	    System.out.println("------taxi'charging is complete ------");
	    System.out.println("現在時間:" + currentTime);
	    
	    Taxi taxi = event.getRelateTaxi();
	    ChargingStation station = event.getRelateStation();
	    System.out.println("T" + taxi.getTaxiID() +"於CH:" + station.getChargerID() +  "充完電");
	
	    //更新 taxi
	    taxi.setBatteryLevel(MAX_BETTERY);
	    taxi.setTargetChargingStationNodeID(-1);
	    taxi.setTargetChargingStationID(-1);
	    taxi.setIndexOfStation(-1);
	    taxiList.add(taxi);
	    chargingTaxiList.remove(taxi);
        System.out.println("T" + taxi.getTaxiID() + "可重新被匹配");

	    
	    //更新station
	    station.getChargingTaxiList().remove(taxi);
	    station.setFull(false);
	}

	public static boolean needsBetteryCharged(Taxi taxi) {
		if(taxi.getBatteryLevel() < 20) { //20km
			return true;
		}else {
			return false;
		}
	}

	public static void goToChargingStation(Taxi taxi) {
		//找充電站
		int stationNodeID = findNearestStation(taxi);
		System.out.println("找到充電站ID:" + taxi.getTargetChargingStationID());
		
		if(nodeList.get(taxi.getCurrentNodeID() - 1).isHaveChargingStation()) { //目前位置就能充電
			
			System.out.println("目前位置就能充電");
			Event atChargingStation = new Event(++eventNums, 9, currentTime);
			atChargingStation.setRelateTaxi(taxi);
			atChargingStation.setRelateStation(chargingStationList.get(taxi.getIndexOfStation()));
			eventList.add(atChargingStation);
		}else {
	
			ArrayList<Node> pathNode = FindPath.shortestPath(taxi.getCurrentNodeID(), taxi.getTargetChargingStationNodeID(), nodeList, linkList, arrayLinkList);
			ArrayList<Link> pathLink = FindPath.pathLinkGenerator(pathNode, linkList, arrayLinkList);
			
			logCurrentPath(pathLink); //check
			
			//計算到達時間
			double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(taxi.getCurrentNodeID(), taxi.getTargetChargingStationNodeID(), nodeList, linkList, arrayLinkList);
			double atChargingStationTimePoint = currentTime + drivingTime;
			
			//更新計程車
			
			if(pathLink.isEmpty()) { //check錯誤
				ArrayList<Node> checkPathNode = FindPath.shortestPath(taxi.getCurrentNodeID(), stationNodeID, pathNode, pathLink, arrayLinkList);
				ArrayList<Link> checkPathLink = FindPath.pathLinkGenerator(checkPathNode, linkList, arrayLinkList);
				pathNode = checkPathNode;
				pathLink = checkPathLink;
			}
			
			taxi.setTaxiSpeed(pathLink.get(0).getCurrentSpeed());
			taxi.setMoving(true);
			taxi.setCurrentPathNode(pathNode);
			taxi.setCurrentPathLink(pathLink);
			taxi.setLinkID(pathLink.get(0).getLinkID());
			taxi.setCurrentLink(pathLink.get(0));
			taxi.setIndexOfLink(0);
			taxi.setRemainLinkDistance(pathLink.get(0).getDistance());
			taxi.setRemainLinkTime(pathLink.get(0).getTravelTime());
			taxi.setNextNodeID(taxi.getCurrentLink().getEndNode().getNodeID());
			taxi.setNextX(taxi.getCurrentLink().getEndNode().getNodeX());
			taxi.setNextY(taxi.getCurrentLink().getEndNode().getNodeY());
						
			//建立到達充電站事件
			Event atChargingStation = new Event(++eventNums, 9, atChargingStationTimePoint);
			atChargingStation.setRelateTaxi(taxi);
			atChargingStation.setRelateStation(chargingStationList.get(taxi.getIndexOfStation()));
			eventList.add(atChargingStation);
		}
	}

	public static int findNearestStation(Taxi taxi) {
		 double minDrivingTime = Double.MAX_VALUE;
		 
		 int chargingStationNodeID = -1;
		 int chargingStationID = -1;
		 int indexOfStation = -1;
		 ChargingStation station;
		 
		 System.out.println("T"+taxi.getTaxiID()+"在N"+taxi.getCurrentNodeID()+"開始找充電站");
		 
		 if(nodeList.get(taxi.getCurrentNodeID() - 1).isHaveChargingStation()) { //目前點就有
			 
			 if(chargingStationList.size() == (nodeList.get(taxi.getCurrentNodeID() - 1).getChargingStationID() - 1)) {
				 station = chargingStationList.get(chargingStationList.size() - 1);
			 }else {
				 station = chargingStationList.get( nodeList.get(taxi.getCurrentNodeID() - 1).getChargingStationID() - 1);
			 }
			 
			 chargingStationNodeID = station.getLocateNodeID();
			 chargingStationID = station.getChargerID();		
			 indexOfStation = chargingStationList.indexOf(station);
			 System.out.println("目前的點就有充電站CH" + station.getChargerID() + " N" + station.getLocateNodeID());
		 }else { //找最短
			 for(ChargingStation s : chargingStationList) {
				 double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(taxi.getCurrentNodeID(), s.getLocateNodeID(), nodeList, linkList, arrayLinkList);
				 if (drivingTime < minDrivingTime) {
					 minDrivingTime = drivingTime;
					 chargingStationNodeID = s.getLocateNodeID();
					 chargingStationID = s.getChargerID();		
					 indexOfStation = chargingStationList.indexOf(s);
				 }
			 }
			 
			 System.out.println("找到最近充電站CH" + chargingStationList.get(indexOfStation).getChargerID() + " N" + chargingStationList.get(indexOfStation).getLocateNodeID());

		 }
		 		 
		 //更新taxi
		 taxi.setTargetChargingStationNodeID(chargingStationNodeID);
		 taxi.setTargetChargingStationID(chargingStationID);
		 taxi.setIndexOfStation(indexOfStation);
		 		 
		 if(chargingStationNodeID != -1) {
			 System.out.println(String.format("stationNodeID:%d stationID:%d indexOfList:%d", chargingStationNodeID, chargingStationID, indexOfStation));
		 }else {
			 System.out.println("沒找到station");
		 }
		 
		 return chargingStationNodeID;
	 }
	
	public static int findNearestStationForDropOffNode(Taxi taxi, int dropOffNodeID) {
		 double minDrivingTime = Double.MAX_VALUE;
		 
		 int chargingStationNodeID = -1;
		 int chargingStationID = -1;
		 int indexOfStation = -1;
		 ChargingStation station;
		 
		 System.out.println("DropOffN"+ dropOffNodeID +"在N"+taxi.getCurrentNodeID()+"開始找充電站");
		 
		 if(nodeList.get(dropOffNodeID - 1).isHaveChargingStation()) { //目前點就有
			 System.out.println("N" + nodeList.get(dropOffNodeID - 1).getNodeID());
			 System.out.println("chID" + nodeList.get(dropOffNodeID - 1).getChargingStationID());
			 if(chargingStationList.size() == (nodeList.get(dropOffNodeID - 1).getChargingStationID() - 1)) {
				 station = chargingStationList.get(chargingStationList.size() - 1);
			 }else {
				 station = chargingStationList.get( nodeList.get(dropOffNodeID - 1).getChargingStationID() - 1);
			 }
			 chargingStationNodeID = nodeList.get(dropOffNodeID - 1).getChargingStationID();
			 chargingStationID = station.getChargerID();		
			 indexOfStation = chargingStationList.indexOf(station);
			 System.out.println("目前的點就有充電站CH" + station.getChargerID() + " N" + station.getLocateNodeID());
		 }else { //找最短
			 for(ChargingStation s : chargingStationList) {
				 double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(dropOffNodeID, s.getLocateNodeID(), nodeList, linkList, arrayLinkList);
				 if (drivingTime < minDrivingTime) {
					 minDrivingTime = drivingTime;
					 chargingStationNodeID = s.getLocateNodeID();
					 chargingStationID = s.getChargerID();		
					 indexOfStation = chargingStationList.indexOf(s);
				 }
			 }
			 
			 System.out.println("找到最近充電站CH" + chargingStationList.get(indexOfStation).getChargerID() + " N" + chargingStationList.get(indexOfStation).getLocateNodeID());

		 }
		 		 
		 //更新taxi
		 taxi.setTargetChargingStationNodeID(chargingStationNodeID);
		 taxi.setTargetChargingStationID(chargingStationID);
		 taxi.setIndexOfStation(indexOfStation);
		 		 
		 if(chargingStationNodeID != -1) {
			 System.out.println(String.format("stationNodeID:%d stationID:%d indexOfList:%d", chargingStationNodeID, chargingStationID, indexOfStation));
		 }else {
			 System.out.println("沒找到station");
		 }
		 
		 return chargingStationNodeID;
	 }
	
	public static int findNearestStationForOldTaxi(Taxi taxi) {
		 double minDrivingTime = Double.MAX_VALUE;
		 
		 int finalNodeID = taxi.getNextNodeID();
		 
		 int chargingStationNodeID = -1;
		 int chargingStationID = -1;
		 int indexOfStation = -1;
		 
		 System.out.println("T"+taxi.getTaxiID()+"即將在N"+finalNodeID+"找充電站");
		 
		 if(nodeList.get(finalNodeID - 1).isHaveChargingStation()) { //目前點就有
			 ChargingStation station = chargingStationList.get(nodeList.get(finalNodeID - 1).getChargingStationID() - 1);
			 
			 if(chargingStationList.size() == (nodeList.get(finalNodeID - 1).getChargingStationID() - 1)) {
				 station = chargingStationList.get(chargingStationList.size() - 1);
			 }else {
				 station = chargingStationList.get( nodeList.get(finalNodeID - 1).getChargingStationID() - 1);
			 }
			 
			 chargingStationNodeID = station.getLocateNodeID();
			 chargingStationID = station.getChargerID();		
			 indexOfStation = chargingStationList.indexOf(station);
			 System.out.println("final的點就有充電站CH" + station.getChargerID() + " N" + station.getLocateNodeID());
		 }else { //找最短
			 for(ChargingStation station : chargingStationList) {
				 double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(finalNodeID, station.getLocateNodeID(), nodeList, linkList, arrayLinkList);
				 if (drivingTime < minDrivingTime) {
					 minDrivingTime = drivingTime;
					 chargingStationNodeID = station.getLocateNodeID();
					 chargingStationID = station.getChargerID();		
					 indexOfStation = chargingStationList.indexOf(station);
				 }
			 }
			 
			 System.out.println("找到最近充電站CH" + chargingStationList.get(indexOfStation).getChargerID() + " N" + chargingStationList.get(indexOfStation).getLocateNodeID());

		 }
		 		 
		 //更新taxi
		 taxi.setTargetChargingStationNodeID(chargingStationNodeID);
		 taxi.setTargetChargingStationID(chargingStationID);
		 taxi.setIndexOfStation(indexOfStation);
		 		 
		 if(chargingStationNodeID != -1) {
			 System.out.println(String.format("stationNodeID:%d stationID:%d indexOfList:%d", chargingStationNodeID, chargingStationID, indexOfStation));
		 }else {
			 System.out.println("沒找到station");
		 }
		 
		 return chargingStationNodeID;
	 }
	
	
	
	private static Order createOrderForNewCustomer(Customer customer) {
        Order order = new Order(++orderNums, customer, currentTime);
        order.setTolerableWaitingTime(customer.getTolerableWaitingTime());
        order.setReservation(false);
        orderList.add(order);
        customer.setCurrentOrder(order); //設定order
        return order;
    }
    
    private static Order createOrderForNewReservedCustomer(Customer customer) {
        Order order = new Order(++orderNums, customer, currentTime);
        order.setTolerableWaitingTime(customer.getTolerableWaitingTime());
        order.setReservation(true);
        orderList.add(order);
        customer.setCurrentOrder(order); //設定order
        return order;
    }

    
    private static void updateRoadCondition(ArrayList<Link> linkList, double currentTime) { //更新道路速度
    	int trafficSpeedDown = trafficFlowAnalysis(currentTime); //判斷時間分類車流量
    	for(Link link : linkList) {
    		double currentSpeed = link.getCurrentSpeed(); //原本speed
    		double targetSpeed = updateSpeed(trafficSpeedDown, currentSpeed); //目標speed
    		double avgSpeed = (currentSpeed + targetSpeed) / 2;
    		link.setTargetSpeed(targetSpeed);
    		link.setAvgSpeed(avgSpeed);  
    		
    		//印出路況
//    		System.out.println(String.format("L%d 原路況:%.1f, 目標路況:%.1f, 平均速度:%.1f", 
//    				link.getLinkID(),
//    				currentSpeed,
//    				targetSpeed,
//    				avgSpeed));
    	}
    }
    
    private static void setRoadNextCondition(ArrayList<Link> linkList) {
    	for(Link link : linkList) {
    		double speed = link.getTargetSpeed();
    		link.setCurrentSpeed(speed);
    		link.setAvgSpeed(speed);
    	}
    }
    
    
    private static double updateSpeed(double trafficSpeedDown, double linkSpeed) {
        ExponentialDistribution defaultSpeedAffect = new ExponentialDistribution(10);
        double speedAffect = randomSign(defaultSpeedAffect.sample());
        double defaultSpeed = 60 + speedAffect;      
        
        ExponentialDistribution distribution = new ExponentialDistribution(trafficSpeedDown);
        double speedDown = distribution.sample();
        double adjustedSpeed = Math.max(0, defaultSpeed - speedDown); // 確保速度不會小於0
        adjustedSpeed = Math.min(adjustedSpeed, 80); // 確保速度不會超過80 km/hr
        return adjustedSpeed;
    }
    
    private static double randomSign(double speedAffect) {
    	Random random = new Random();
        double gaussian = random.nextGaussian(); //常態分布
        boolean isPositive = gaussian >= 0;
        return isPositive ? speedAffect : -speedAffect;
    }
    
    private static int trafficFlowAnalysis(double currentTime) {
    	int trafficSpeedDown = 0;
    	if(currentTime > 25200 && currentTime <= 32400) { //7-9 
    		trafficSpeedDown = 10;
    	}else if(currentTime > 43200 && currentTime <= 46800) {//12-13
    		trafficSpeedDown = 5;
    	}else if(currentTime > 61200 && currentTime <= 68400) {//17-19
    		trafficSpeedDown = 15; 
    	}else { // 其他時間
    		trafficSpeedDown = 2; 
    	}
    	
    	return trafficSpeedDown;
    }
    
    private static void updateCustomerByTimePeriod(double currentTime) {
    	double probabilityOf7To9 = 0.28;
    	double probabilityOf12To13 = 0.2;
    	double probabilityOf17To19 = 0.32;
    	double probabilityOfRestTime = 0.2;

    	if(currentTime > 25200 && currentTime <= 32400) { //7-9 
    		double tiemPeriodCustomerNums = CUSTOMER_PER_DAY * probabilityOf7To9;
    		double timePeriodReservedNums = RESERVED_CUSTOMER_PER_DAY * probabilityOf7To9;
    		LAMBDA = tiemPeriodCustomerNums / 2 / 60 / 60;
    		RESERVED_LAMBDA = timePeriodReservedNums / 2 / 60 / 60;
    	}else if(currentTime > 43200 && currentTime <= 46800) {//12-13
    		double tiemPeriodCustomerNums = CUSTOMER_PER_DAY * probabilityOf12To13;
    		double timePeriodReservedNums = RESERVED_CUSTOMER_PER_DAY * probabilityOf12To13;
    		LAMBDA = tiemPeriodCustomerNums / 2 / 60 / 60;
    		RESERVED_LAMBDA = timePeriodReservedNums / 2 / 60 / 60;
    	}else if(currentTime > 61200 && currentTime <= 68400) {//17-19
    		double tiemPeriodCustomerNums = CUSTOMER_PER_DAY * probabilityOf17To19;
    		double timePeriodReservedNums = RESERVED_CUSTOMER_PER_DAY * probabilityOf17To19;
    		LAMBDA = tiemPeriodCustomerNums / 2 / 60 / 60;
    		RESERVED_LAMBDA = timePeriodReservedNums / 2 / 60 / 60;
    	}else { // 其他時間
    		double tiemPeriodCustomerNums = CUSTOMER_PER_DAY * probabilityOfRestTime;
    		double timePeriodReservedNums = RESERVED_CUSTOMER_PER_DAY * probabilityOfRestTime;
    		LAMBDA = tiemPeriodCustomerNums / 18 / 60 / 60;
    		RESERVED_LAMBDA = timePeriodReservedNums / 18 / 60 / 60;
    	}
    	
    	System.out.println("更新客戶密集度");
    	
    	return;
    }
    
    private static void setAreaNodeList(ArrayList<Node> nodeList, ArrayList<Node> ubanNodeList, ArrayList<Node> suburbsNodeList, ArrayList<Node> restNodeList) {
    	
		//區域變數
    	double maxUrbanX = 10;
    	double minUrbanX = 7;
    	double maxUrbanY = 10;
    	double minUrbanY = 7;
    	double maxSuburbsX = 5;
    	double minSuburbsX = 0;
    	double maxSuburbsY = 5;
    	double minSuburbsY = 0;
    	
        // 根據選擇的區域選擇節點
        for (Node node : nodeList) {
            if ((node.getNodeX() >= minUrbanX && node.getNodeX() <= maxUrbanX) && 
            		(node.getNodeY() >= minUrbanY && node.getNodeY() <= maxUrbanY)) {
            	ubanNodeList.add(node);
            } else if ( (node.getNodeX() >= minSuburbsX && node.getNodeX() <= maxSuburbsX) && 
            		(node.getNodeY() >= minSuburbsY && node.getNodeY() <= maxSuburbsY)) {
                suburbsNodeList.add(node);
            } else {
            	restNodeList.add(node);
            }
        }
        
        // 印出
        System.out.println("UbanNodeList");
        for(Node n :ubanNodeList) {
        	System.out.print("N" + n.getNodeID() + ", ");
        }
        System.out.println();
        
        System.out.println("suburbsNodeList");
        for(Node n :suburbsNodeList) {
        	System.out.print("N" + n.getNodeID() + ", ");
        }
        System.out.println();
        
        System.out.println("restAreaNodeList");
        for(Node n :restAreaNodeList) {
        	System.out.print("N" + n.getNodeID() + ", ");
        }
        System.out.println();
    }
    
    public static boolean reAsign(Taxi taxi, ArrayList<Order> orderList) {
    	boolean isReRideMatch = false;
    	//試試看重新指派
    	for(Order order : orderList) {
    		if(order.isReAsigned()) continue; //有換過就過
    		if((order.getOrderStatus() == 2 && order.getMatchedTaxi().isMoving()) || 
    				(order.getOrderStatus() == 3 && order.getMatchedTaxi().isMoving())) {
    			if(isAtLastLink(order.getMatchedTaxi())) continue; //如果在last就跳過
    				if(isSwapSuccessFul(taxi, order)) { //交換看看
    					System.out.println("T" + taxi.getTaxiID() + "交換T" + order.getMatchedTaxi().getTaxiID());
    					setSwap(taxi, order);
    					isReRideMatch = true;
    					break;
    				}else {
    					continue;
    				}		
    		}else {
    			continue;
    		}
    	}
    	return isReRideMatch;
    }
    
    public static void setSwap(Taxi newTaxi, Order order) {
        Customer customer = order.getCreatorCustomer();
        Taxi oldTaxi = order.getMatchedTaxi();
        Match oldMatch = order.getCurrentMatch();
        int pickUpNodeID = order.getPickUpNodeID();
        Match newMatch = new Match(++matchNums, customer, newTaxi);
        int targetChargingStationNodeID = oldTaxi.getTargetChargingStationNodeID();
        int targetChargingStationID = oldTaxi.getTargetChargingStationID();
        int indexOfStation = oldTaxi.getIndexOfStation();
        Event oldEvent = order.getTaxiToPickUpEvent();
        
        int oldTaxiNextNodeID = oldTaxi.getCurrentLink().getEndNode().getNodeID();
        ArrayList<Node> remainPathNode = FindPath.shortestPath(oldTaxiNextNodeID, order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
        ArrayList<Link> remainPathLink = FindPath.pathLinkGenerator(remainPathNode, linkList, arrayLinkList);
        double remainDistance = Calculate.pathTotalDistance(remainPathLink);
        

        // 計算 newTaxi 到上車點的時間和路徑
        double newTaxiDrivingTime = -1;
        int newTaxiCurrentNodeID = newTaxi.getCurrentNodeID();
        if(newTaxiCurrentNodeID == pickUpNodeID) {
        	newTaxiDrivingTime = 0;
        }else {
            newTaxiDrivingTime = Calculate.drivingTimeByShortestTravelTimePath(newTaxiCurrentNodeID, order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
        }
        double newTaxiAtPickUpTimePoint = currentTime + newTaxiDrivingTime;
        ArrayList<Node> newTaxiToPickUpPathNode = FindPath.shortestPath(newTaxiCurrentNodeID, order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
        ArrayList<Link> newTaxiToPickUpPathLink = FindPath.pathLinkGenerator(newTaxiToPickUpPathNode, linkList, arrayLinkList);
        double newTaxiToPickUpDistance = Calculate.pathTotalDistance(newTaxiToPickUpPathLink);
        
        double newTotalDistance = order.getTotalTravelDistance();
        newTotalDistance = newTotalDistance - remainDistance + newTaxiToPickUpDistance;
        
        
        //更新 match
        matchList.remove(oldMatch);
        matchList.add(newMatch);
        
        // 設定 order
        order.setReAsigned(true);
        order.setMatchedTaxi(newTaxi);
        order.setCurrentMatch(newMatch);
        order.setTaxiToPickUpTimePoint(newTaxiAtPickUpTimePoint);
        order.setMatchedTaxi(newTaxi);
        order.setCurrentMatch(newMatch);
        order.setTotalTravelDistance(newTotalDistance);
        
        // 設定 customer
        customer.setCurrentMatch(newMatch);
              
        // 設定新 taxi
        newTaxi.setCurrentMatch(newMatch);
        newTaxi.setCurrentOrder(order);
        newTaxi.setTargetChargingStationNodeID(targetChargingStationNodeID);
        newTaxi.setTargetChargingStationID(targetChargingStationID);
        newTaxi.setIndexOfStation(indexOfStation);
        matchTaxiList.add(newTaxi);
        System.out.println("matchTaxiList增加 T" + newTaxi.getTaxiID());
        
        if (newTaxiCurrentNodeID != order.getPickUpNodeID()) { //newTaxi要移動到上車點
            order.setTaxiToPickUpPathNode(newTaxiToPickUpPathNode);
            order.setTaxiToPickUpPathLink(newTaxiToPickUpPathLink);
            order.setTaxiToPickUpTimePoint(newTaxiAtPickUpTimePoint);
            
            newTaxi.setMoving(true);
            newTaxi.setCurrentPathNode(newTaxiToPickUpPathNode);
            newTaxi.setCurrentPathLink(newTaxiToPickUpPathLink);
            
            //確保機制
            newTaxiToPickUpPathNode = FindPath.shortestPath(newTaxiCurrentNodeID, order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
            newTaxiToPickUpPathLink = FindPath.pathLinkGenerator(newTaxiToPickUpPathNode, linkList, arrayLinkList);

            
            System.out.println("N"+newTaxiCurrentNodeID + ", N" + order.getPickUpNodeID());
            
            while(newTaxiToPickUpPathLink.isEmpty()) {
            	newTaxiToPickUpPathNode = FindPath.shortestPath(newTaxiCurrentNodeID, order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
                newTaxiToPickUpPathLink = FindPath.pathLinkGenerator(newTaxiToPickUpPathNode, linkList, arrayLinkList);     
            }
            
            updateMovingTaxiForLink(newTaxi, newTaxiToPickUpPathLink.get(0));
        } else { //已經在上車點
            order.getTaxiToPickUpPathNode().clear();
            order.getTaxiToPickUpPathLink().clear();
            order.setTaxiToPickUpTimePoint(currentTime);
            
            resetTaxiPosition(newTaxi);
        }
        
        //更新newEvent
    	Event newTaxiToPickUpEvent = new Event(++eventNums, 4, newTaxiAtPickUpTimePoint);
    	newTaxiToPickUpEvent.setRelateCustomer(customer);
    	newTaxiToPickUpEvent.setRelateMatch(newMatch);
    	newTaxiToPickUpEvent.setRelateOrder(order);
    	newTaxiToPickUpEvent.setRelateTaxi(newTaxi);
        order.setTaxiToPickUpEvent(newTaxiToPickUpEvent);
        newTaxi.setCurrentEvent(newTaxiToPickUpEvent);
        eventList.add(newTaxiToPickUpEvent);
        System.out.println("舊E" + oldEvent.getEventID());
        System.out.println("新E" + newTaxiToPickUpEvent.getEventID());
        
        //更新oldTaxi
        oldTaxi.setTargetChargingStationNodeID(-1);
        oldTaxi.setTargetChargingStationID(-1);
        oldTaxi.setIndexOfStation(-1);
        findNearestStationForOldTaxi(oldTaxi);
        oldTaxi.setCurrentMatch(null);
        oldTaxi.setCurrentOrder(null);
        oldTaxi.setIndexOfLink(0);
        oldTaxi.getCurrentPathNode().clear();
        oldTaxi.getCurrentPathNode().add(oldTaxi.getCurrentLink().getStartNode());
        oldTaxi.getCurrentPathNode().add(oldTaxi.getCurrentLink().getEndNode());
        oldTaxi.getCurrentPathLink().clear();
        oldTaxi.getCurrentPathLink().add(oldTaxi.getCurrentLink());
        oldTaxi.setOldTaxiFinalNodeID(oldTaxi.getNextNodeID());
        System.out.println("oldT" + oldTaxi.getTaxiID() + " 目前L" + oldTaxi.getCurrentLink().getLinkID());
        System.out.println("remainDistance:" + oldTaxi.getRemainLinkDistance());
        logCurrentPath(oldTaxi.getCurrentPathLink());

        movingToNextTaxiList.add(oldTaxi);

        // 移除舊事件並創建新事件
        eventList.remove(oldEvent);

        // 設定 oldTaxi 繼續前往下一節點
        Event oldTaxiAtNextNodeEvent = new Event(++eventNums, 42, currentTime + oldTaxi.getRemainLinkTime());
        oldTaxiAtNextNodeEvent.setRelateTaxi(oldTaxi);
        oldTaxi.setCurrentEvent(oldTaxiAtNextNodeEvent);
        eventList.add(oldTaxiAtNextNodeEvent);
        
        System.out.println("old T" + oldTaxi.getTaxiID() + "new T" + newTaxi.getTaxiID() + "交換");
    }

    private static void updateMovingTaxiForLink(Taxi taxi, Link Link) {
        taxi.setTaxiSpeed(Link.getCurrentSpeed());
        taxi.setLinkID(Link.getLinkID());
        taxi.setCurrentLink(Link);
        taxi.setIndexOfLink(0);
        taxi.setRemainLinkDistance(Link.getDistance());
        taxi.setRemainLinkTime(Link.getDistance() / taxi.getTaxiSpeed() * 3600);
        taxi.setNextNodeID(Link.getEndNode().getNodeID());
        taxi.setNextX(Link.getEndNode().getNodeX());
        taxi.setNextY(Link.getEndNode().getNodeY());
    }

    private static void resetTaxiPosition(Taxi taxi) {
        taxi.setMoving(false);
        taxi.setTaxiSpeed(-1);
        taxi.getCurrentPathNode().clear();
        taxi.getCurrentPathLink().clear();
        taxi.setLinkID(-1);
        taxi.setCurrentLink(null);
        taxi.setIndexOfLink(-1);
        taxi.setRemainLinkDistance(-1);
        taxi.setRemainLinkTime(-1);
        taxi.setNextNodeID(-1);
        taxi.setNextX(-1);
        taxi.setNextY(-1); 
    }

    public static void oldTaxiAtNextNode(Event event) { //42
        System.out.println("------ old計程車  到達 nextNode orderStatus = 42 ------");
        System.out.println("現在時間:" + currentTime);
        
        if (event == null) {
            System.out.println("Error: Event is null");
            return;
        }
        
        Taxi taxi = event.getRelateTaxi();
        if (taxi == null) {
            System.out.println("Error: Matched taxi is null");
            return;
        }
        
        // 更新 taxi
        taxi.setMoving(false);
        taxi.setCurrentNodeID(taxi.getOldTaxiFinalNodeID());
        taxi.setTaxiX(nodeList.get(taxi.getCurrentNodeID() - 1).getNodeX());
        taxi.setTaxiY(nodeList.get(taxi.getCurrentNodeID() - 1).getNodeY());
        taxi.getCurrentPathNode().clear();
        taxi.getCurrentPathLink().clear();
        taxi.setLinkID(-1);
        taxi.setCurrentLink(null);
        taxi.setIndexOfLink(-1);
        taxi.setRemainLinkTime(-1);
        taxi.setRemainLinkDistance(-1);
        taxi.setNextNodeID(-1);
        taxi.setNextX(-1);
        taxi.setNextY(-1);
        taxi.setTargetChargingStationNodeID(-1);
        taxi.setTargetChargingStationID(-1);
        taxi.setIndexOfStation(-1);
        movingToNextTaxiList.remove(taxi);
        
        if (needsBetteryCharged(taxi)) {
            matchTaxiList.remove(taxi);
            chargingTaxiList.add(taxi);
            System.out.println("T" + taxi.getTaxiID() + "需要充電");
            goToChargingStation(taxi);
        } else {
            taxiList.add(taxi);
            matchTaxiList.remove(taxi);
            System.out.println("T" + taxi.getTaxiID() + "可重新被匹配");
        }

        System.out.println("------SWAP TAXI到達 next node------");
        System.out.println();
    }
    
    public static boolean isSwapSuccessFul(Taxi taxi, Order order) {
    	boolean checkSwap = false;
    	double newTaxiToPickUpDistance;
    	
    	
    	//計算swap時間, 新taxi到達上車點時間點`
    	double swapTimePoint = currentTime;
    	double newTaxidrivingTime = Calculate.drivingTimeByShortestTravelTimePath(taxi.getCurrentNodeID(), order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
    	double newTaxiAtPickUpTimePoint = swapTimePoint + newTaxidrivingTime;
    	
    	//計算里程確認電量, taxi to pickup, pickup to dropoff, dropoff to station
    	double totalTravelDistance = order.getPickUpToDropOffToStationDistance(); //匹配完成後訂單一開始就儲存好
    	if(taxi.getCurrentNodeID() == order.getPickUpNodeID()) { //如果newTaxi已在上車點
    		newTaxiToPickUpDistance = 0;
    	}else { //要移動到上車點
        	ArrayList<Node> newTaxiToPickUpPathNode = FindPath.shortestPath(taxi.getCurrentNodeID(), order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
        	ArrayList<Link> newTaxiToPickUpPathLink = FindPath.pathLinkGenerator(newTaxiToPickUpPathNode, linkList, arrayLinkList);
        	newTaxiToPickUpDistance = Calculate.pathTotalDistance(newTaxiToPickUpPathLink);
    	}
    	//計算總里程
    	totalTravelDistance += newTaxiToPickUpDistance;
      	
    	if(newTaxiAtPickUpTimePoint < order.getTaxiToPickUpTimePoint() && taxi.getBatteryLevel() > totalTravelDistance) {
    		System.out.println("更改指派後到達上車點時間:" + newTaxiAtPickUpTimePoint + ", 原本時間點" + order.getTaxiToPickUpTimePoint());
    		checkSwap = true;
    	}
    	return checkSwap;
    	
    	
    }
    
    private static void updateToPickUpTimePoint(Order order, Taxi taxi) {
    	double remainLinkTime = taxi.getRemainLinkDistance() / taxi.getCurrentSpeed();
    	double nextNodeTimePoint = currentTime + remainLinkTime;
    	int nextTaxiNodeID = taxi.getNextNodeID();
    	double drivingTime = Calculate.drivingTimeByShortestTravelTimePath(nextTaxiNodeID, order.getPickUpNodeID(), nodeList, linkList, arrayLinkList);
    	double newTaxiAtPickUpTimePoint = nextNodeTimePoint + drivingTime;
    	order.setTaxiToPickUpTimePoint(newTaxiAtPickUpTimePoint);
    	System.out.println("更新到上車點時間點" + newTaxiAtPickUpTimePoint);
    }
    
    public static void outPutToExcel(ArrayList<Order> pastOrderList, String filePath, String sheetName, double executionTime) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);

        // 創建新Sheet並命名
        String newSheetName = getUniqueSheetName(workbook, sheetName);
        Sheet sheet = workbook.createSheet(newSheetName);
        createHeaderRow(sheet);       

        //填入數據
        int rowNum = 1;
        double sumDiff = 0;
        double worstDiff = -Double.MAX_VALUE;
        double sumTravelDistance = 0;
        
        for (Order order : pastOrderList) {
            Map<Integer, Double> orderTimePoint = order.getOrderTimePoint();
            Customer customer = order.getCreatorCustomer();      
            
            double waitingTime;
            double diff;
            
            if(!order.isReservation()) {
                double walkingTime = orderTimePoint.get(3) - orderTimePoint.get(2);
                waitingTime = (orderTimePoint.get(5) - orderTimePoint.get(1)) - walkingTime;
                diff = waitingTime - customer.getTolerableWaitingTime();
            }else {
                waitingTime = orderTimePoint.get(5) - order.getReservedPickUpTimePoint();
                diff = waitingTime - customer.getTolerableWaitingTime();
            }
            
            sumDiff += diff;
            if(diff > worstDiff) worstDiff = diff;
            sumTravelDistance += order.getTotalTravelDistance();

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(order.getOrderID());
            row.createCell(1).setCellValue(customer.getCustomerID());
            row.createCell(2).setCellValue(order.getTolerableWaitingTime());
            row.createCell(3).setCellValue(order.isReservation());
            row.createCell(4).setCellValue(order.getReservedPickUpTimePoint());
            row.createCell(5).setCellValue(order.getRequestNodeID()); //叫車
            row.createCell(6).setCellValue(order.getPickUpNodeID()); //上車點       
            row.createCell(7).setCellValue(order.getDropOffNodeID()); //下車點   
            row.createCell(8).setCellValue(getSafeValue(orderTimePoint, 1)); //叫車點時間
            row.createCell(9).setCellValue(getSafeValue(orderTimePoint, 2)); //車輛匹配完成時間          
            row.createCell(10).setCellValue(getSafeValue(orderTimePoint, 3)); //客戶到達上車點時間
            row.createCell(11).setCellValue(getSafeValue(orderTimePoint, 4)); //車到達上車點時間
            row.createCell(12).setCellValue(getSafeValue(orderTimePoint, 5)); //都到達上車點時間, 客戶開始上車
            row.createCell(13).setCellValue(getSafeValue(orderTimePoint, 6)); //發車時間點
            row.createCell(14).setCellValue(getSafeValue(orderTimePoint, 7)); //到達目的地時間點, 客戶開始下車
            row.createCell(15).setCellValue(getSafeValue(orderTimePoint, 8)); // 服務完成時間點
            row.createCell(16).setCellValue(waitingTime); //等車時間 (s)
            row.createCell(17).setCellValue(waitingTime/60); //等車時間 (min)
            row.createCell(18).setCellValue(diff); // diff差值 (s)
            row.createCell(19).setCellValue(diff/60); // diff差值 (min)
            row.createCell(20).setCellValue(customer.getRequestTimes()); //叫車次數
            row.createCell(21).setCellValue(order.getTotalTravelDistance()); //總距離
            row.createCell(22).setCellValue(order.isReAsigned()); //有重新指派嗎
        }
        
        Row row1 = sheet.getRow(1);
        row1.createCell(23).setCellValue(customerNums); //客戶總數
        row1.createCell(24).setCellValue(customerList.size()); //等待配對數
        row1.createCell(25).setCellValue(taxiList.size()); //剩下可服務計程車數
        row1.createCell(26).setCellValue(simulationTimes); //第x次模擬
        row1.createCell(27).setCellValue(serviceCompleteNums); //完成服務數
        row1.createCell(28).setCellValue(orderNums); //總訂單數
        row1.createCell(29).setCellValue(executionTime); //程序執行時間
        row1.createCell(30).setCellValue((sumDiff/serviceCompleteNums)/60); //程序執行時間
        row1.createCell(31).setCellValue(worstDiff/60); //程序執行時間
        row1.createCell(32).setCellValue(sumTravelDistance/serviceCompleteNums); //程序執行時間

        
        //得到Summary sheet
        Sheet summarySheet = workbook.getSheet("Summary");
        if (summarySheet == null) {
            summarySheet = workbook.createSheet("Summary");
            createSummaryHeaderRow(summarySheet);
        }
        
        // 在總結Sheet上填入結果
        int summaryRowNum = summarySheet.getLastRowNum() + 1;
        Row summaryRow = summarySheet.createRow(summaryRowNum);
        summaryRow.createCell(0).setCellValue(newSheetName); // Sheet名
        summaryRow.createCell(1).setCellValue(customerNums); //客戶總數
        summaryRow.createCell(2).setCellValue(customerList.size()); //等待配對數
        summaryRow.createCell(3).setCellValue(taxiList.size()); //剩下可服務計程車數
        summaryRow.createCell(4).setCellValue(simulationTimes); //第x次模擬
        summaryRow.createCell(5).setCellValue(serviceCompleteNums); //完成服務數
        summaryRow.createCell(6).setCellValue(orderNums); //總訂單數
        summaryRow.createCell(7).setCellValue(executionTime); //程序執行時間
        summaryRow.createCell(8).setCellValue((sumDiff / serviceCompleteNums) / 60); //平均等待時間
        summaryRow.createCell(9).setCellValue(worstDiff / 60); //最差等待時間
        summaryRow.createCell(10).setCellValue(sumTravelDistance / serviceCompleteNums); //平均行駛距離
        

        fis.close();

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }

        workbook.close();
        System.out.println("excel寫入完成");
    }

    private static void createHeaderRow(Sheet sheet) {
        // Define headers
        String[] headers = {"orderID", "customerID", "tolerableWaitingTime", "isReserved", "reservedTimePoint",
        		"requestNodeID", "pickUpNodeID", "dropOffNodeID",
        		"1", "2", "3", "4", "5", "6", "7", "8",
        		"waitingTime(s)", "waitingTime(min)", "diff(s)", "diff(min)",
        		"RequestTimes", "travelDistance", "ReAsign",
        		"客戶總數", "等待配對數", "剩下計程車數", "第x次模擬", "完成服務數", "Order數", "程式執行時間", "avgDiff(min)", "worstDiff(min)", "avgTravelDistance" 
        		};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }
    
    private static void createSummaryHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Sheet名");
        headerRow.createCell(1).setCellValue("客戶總數");
        headerRow.createCell(2).setCellValue("等待配對數");
        headerRow.createCell(3).setCellValue("剩下可服務計程車數");
        headerRow.createCell(4).setCellValue("第x次模擬");
        headerRow.createCell(5).setCellValue("完成服務數");
        headerRow.createCell(6).setCellValue("總訂單數");
        headerRow.createCell(7).setCellValue("程序執行時間");
        headerRow.createCell(8).setCellValue("平均等待時間 (min)");
        headerRow.createCell(9).setCellValue("最差等待時間 (min)");
        headerRow.createCell(10).setCellValue("平均行駛距離");
    }

    private static String getUniqueSheetName(Workbook workbook, String baseSheetName) {
        String uniqueSheetName = baseSheetName;
        int suffix = 1;

        while (workbook.getSheet(uniqueSheetName) != null) {
            uniqueSheetName = baseSheetName + "_" + suffix;
            suffix++;
        }

        return uniqueSheetName;
    }
    
    private static double getSafeValue(Map<Integer, Double> map, int key) {
    	if (map.get(key) == null) {
    		return -1;
    	}else {
    		return map.get(key);
    	}
    }
    
    
}
