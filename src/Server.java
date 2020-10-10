import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Server{
	public static void main(String args[]){
	
		Channel object=new Channel();
		object.getMessage();					
	}
}
			
class Channel{							//This class will entertain all requests of Clients
	
	static ServerSocket serverSocket;
	static String clientRequest="";
	static SourceAllocator<String> getSource;
	static Connection con;
	static ResultSet rs;
	static Statement st;
	static OutputStream messageToClient;
	static DataOutputStream writer;
	public Channel(){	
		try{
			serverSocket=new ServerSocket(5600);
			getSource = new SourceAllocator<String>(100);
			
		}catch(Exception ex){
			System.out.println("Port Issue.....Restart Server"+ex.toString());
		}
	}
	
	public static void getMessage(){
	    try{
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		con=DriverManager.getConnection("jdbc:ucanaccess://CarStore.accdb");
		st=con.createStatement();
			
			Socket socket;
			DataInputStream in ;
		
			
			ExecutorService Cthreadpool = Executors.newCachedThreadPool();
			
		while(true){
			//Keep listening the requests	
				socket=serverSocket.accept();
				in = new DataInputStream(socket.getInputStream());
				messageToClient=socket.getOutputStream();
				writer=new DataOutputStream(messageToClient);
			
				clientRequest=in.readUTF();
				String data[]=clientRequest.split(":");
				
			if(data[0].equals("Add Car"))
			{

				System.out.println("Client Named : "+data[5]+" Called Add Car method");
				Cthreadpool.execute(new AddCarProcessThread(data));
			}
			
			else if(data[0].equals("Sell Car")){
				System.out.println("Client Named : "+data[3]+" Called Sell Car method");
				Cthreadpool.execute(new SellCarProcessThread(data));
								
			}
			else if(data[0].equals("Search Car By Make")){
			
				System.out.println("Client Named : "+data[2]+" Called Search By Make  method");
				Cthreadpool.execute(new SearchByMakeProcessThread(data));
				
			}
			else if(data[0].equals("Total Value Of Sales"))
			{
				System.out.println("Client Named : "+data[1]+" Called total sales value  method");	
				Cthreadpool.execute(new TotalSalesValueProcessThread(data));
			}
			
			else if(data[0].equals("Cars For Sale"))
			{
				System.out.println("Client Named : "+data[1]+" Called Cars for sale  method");	
				Cthreadpool.execute(new CarsForSaleProcessThread(data));	
			}			
			}
		
		}catch(Exception ex){
		}	
	}		
}

class AddCarProcessThread extends Channel implements Runnable
{
	static String data[];
	public AddCarProcessThread(String data[]) {
		this.data=data;
	}
	@Override
	public void run() {
		getSource.addCar(data[5], writer, data, con, st);
	}
}
class SellCarProcessThread extends Channel implements Runnable
{
	static String data[];
	public SellCarProcessThread(String data[]) {
		this.data=data;
	}
	@Override
	public void run() {
		
		getSource.SellCar(data[3], writer, data, con, st);
	}
}

class SearchByMakeProcessThread extends Channel implements Runnable
{
	static String data[];
	public SearchByMakeProcessThread(String data[]) 
	{
		this.data=data;
	}
	@Override
	public void run() 
	{	
		getSource.SearchByMake(data[2], writer, data, con, st);	
	}
}
class TotalSalesValueProcessThread extends Channel implements Runnable
{
	static String data[];
	public TotalSalesValueProcessThread(String data[]) 
	{
		this.data=data;
	}
	@Override
	public void run() 
	{
		
		getSource.TotalValueOfSales(data[1], writer, data, con, st);	
	}
}
class CarsForSaleProcessThread extends Channel implements Runnable
{
	static String data[];
	public CarsForSaleProcessThread(String data[]) 
	{
		this.data=data;
	}
	@Override
	public void run() 
	{
		getSource.CarsForSell(data[1], writer, data, con, st);	
	}
}

