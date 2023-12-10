package WayneEnterprise;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class Order{
	private static int orderIdCounter=1;
	private int orderId;
	private int cargoWeight;
	private String destination;
	private long orderTime;
	
	public Order(int cargoWeight, String destination)
	{
		this.orderId = orderIdcounter++;
		this.cargoWeight = cargoWeight;
		this.destination = destination;
		this.orderTime= System.currentTimeMillis();
		
	}
	public int getCargoWeight() {
		return cargoWeight;
	}
	public String getDestination() {
		return destination;
	}
	public long getOrderTime() {
		return orderTime;
	}
}
class Ship{
	private int cargoCapacity=0;
	private int tripsMade=0;
	
	public boolean loadCargo(Order order)
	{
		if(cargoCapacity + order.getCargoWeight()<=300)
		{
			cargoCapacity+=order.getCargoWeight();
			return true;
		}
		return false;
			
	}
	public void unloadCargo() {
		cargoCapacity=0;
		tripsMade++;
	}
	public boolean isMaintenanceRequired() {
		return tripsMade%5==0;
		}
}

class WayneEnterprise {
	private static final int OrderCost =1000;
	private static final int CancelledOrderCost = 250;
	private static final int TargetRevenue = 1000000;
	private static final int MaxConsecutiveCancellation = 3;
	
	private int revenue=0;
	private int ordersDelivered=0;
	private int ordersCancelled=0;
	private int consecutiveCancellation=0;
	
	public BlockingQueue<Order> orderQueue= new LinkedBlockingQueue();
	public BlockingQueue<Ship> availableShips = new LinkedBlockingQueue();
	
	public void placeOrder(Order order)
	{
		try {
			orderQueue.put(order);
		}catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	public void shipOrder(Ship ship) {
		try {
			Order order = orderQueue.take();
			if(ship.loadCargo(order)) {
				revenue+=OrderCost;
				ordersDelivered++;
				consecutiveCancellation=0;
				
			}else {
				revenue-=CancelledOrderCost;
				ordersCancelled++;
				consecutiveCancellation++;
				
			}
			if(consecutiveCancellation>=MaxConsecutiveCancellation)
			{
				
			}
			if(ship.isMaintenanceRequired())
			{
				Thread.sleep(60000);
			}
			ship.unloadCargo();
			availableShips.put(ship);
		}catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	public int getRevenue() {
		return revenue;
	}
	public boolean isSimulationComplete() {
		return revenue>=TargetRevenue;
	}

	

}
public class ShippingSimulation{
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WayneEnterprise wayneEnterprise = new WayneEnterprise();
		for(int i=0;i<7;i++)
		{
			Ship ship = new Ship();
			try {
				wayneEnterprise.availableShips.put(ship);
			}catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		for(int i=0;i<7;i++)
		{
			startCustomerThread(wayneEnterprise);
		}
		for(int i=0;i<5;i++)
		{
			startShippingThread(wayneEnterprise);
		}

	}
	private static void startCustomerThread(WayneEnterprise wayneEnterprise)
	{
		new Thread(()->{
			Random random = new Random();
			while(!wayneEnterprise.isSimulationComplete()) {
				int cargoWeight = random.nextInt(41)+10;
				String destination =(random.nextBoolean()?"Gotham":"Atlanta");
				Order order = new Order(cargoWeight,destination);
				wayneEnterprise.placeOrder(order);
				try {
					Thread.sleep(100);
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	private static void startShippingThread(WayneEnterprise wayneEnterprise)
	{
		new Thread(()->{
			while(!wayneEnterprise.isSimulationComplete()) {
				try {
					Ship ship = wayneEnterprise.availableShips.take();
					Ship ship = wayneEnterprise.shipOrder(ship);
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
