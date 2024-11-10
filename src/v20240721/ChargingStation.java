package v20240721;

import java.util.ArrayList;

public class ChargingStation {
	//超充站充滿電約 45min ~ 1hr
	//一個充電站大約服務 2km^2
	// 10*10 面積 平均分布 約可以擺32個
	// 平均充電樁 4~10個
	
	private int chargerID;
	
	//目前位置
	private int locateNodeID;
	private double chargerX;
	private double chargerY;
	
	private int maxChargeNums = 10;
	private ArrayList<Taxi> chargingTaxiList = new ArrayList<>();
	private boolean isFull = false;
	
	public ChargingStation(int chargerID, int locateNodeID, double chargerX, double chargerY) {
		this.chargerID = chargerID;
		this.locateNodeID = locateNodeID;
		this.chargerX = chargerX;
		this.chargerY = chargerY;
	}

	public ArrayList<Taxi> getChargingTaxiList() {
		return chargingTaxiList;
	}

	public void setChargingTaxiList(ArrayList<Taxi> chargingTaxiList) {
		this.chargingTaxiList = chargingTaxiList;
	}

	public boolean isFull() {
		return isFull;
	}

	public void setFull(boolean isFull) {
		this.isFull = isFull;
	}

	public int getChargerID() {
		return chargerID;
	}

	public int getLocateNodeID() {
		return locateNodeID;
	}

	public double getChargerX() {
		return chargerX;
	}

	public double getChargerY() {
		return chargerY;
	}

	public int getMaxChargeNums() {
		return maxChargeNums;
	}
	
	
}
