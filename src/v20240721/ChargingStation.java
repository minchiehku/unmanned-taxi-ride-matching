package v20240721;

import java.util.ArrayList;

public class ChargingStation {
	//�W�R���R���q�� 45min ~ 1hr
	//�@�ӥR�q���j���A�� 2km^2
	// 10*10 ���n �������� ���i�H�\32��
	// �����R�q�� 4~10��
	
	private int chargerID;
	
	//�ثe��m
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
