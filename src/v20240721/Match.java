package v20240721;

import java.util.ArrayList;

public class Match { 
	//�t��ID
	private int matchID;
	
	//�t��զX
	private Customer customer;
    private Taxi taxi;
    private Order order;
    
    //���u��T
    private ArrayList<Node> taxiToPickUpPathNode = new ArrayList<>(); //taxi��W���I
	private ArrayList<Link> taxiToPickUpPathLink = new ArrayList<>();
	
    public Match(int matchID, Customer customer, Taxi taxi) {
        this.matchID = matchID;
    	this.customer = customer;
        this.taxi = taxi;
    }

	public int getMatchID() {
		return matchID;
	}


	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Taxi getTaxi() {
		return taxi;
	}

	public void setTaxi(Taxi taxi) {
		this.taxi = taxi;
	}

	
	public ArrayList<Node> getTaxiToPickUpPathNode() {
		return taxiToPickUpPathNode;
	}

	public void setTaxiToPickUpPathNode(ArrayList<Node> taxiToPickUpPathNode) {
		this.taxiToPickUpPathNode = taxiToPickUpPathNode;
	}

	public ArrayList<Link> getTaxiToPickUpPathLink() {
		return taxiToPickUpPathLink;
	}

	public void setTaxiToPickUpPathLink(ArrayList<Link> taxiToPickUpPathLink) {
		this.taxiToPickUpPathLink = taxiToPickUpPathLink;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	
}
