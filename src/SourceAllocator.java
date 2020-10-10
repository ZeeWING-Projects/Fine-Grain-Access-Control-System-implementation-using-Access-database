import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.locks.*;

@SuppressWarnings("unchecked")
public class SourceAllocator<E> {

	
	private LinkedList<E> data[];
	private StampedLock locks[];
	private ResultSet rs;
	
	
	
	public SourceAllocator(int n)
	{
		
		
		if(n > 1000)
		{
		   data = (LinkedList<E>[])(new LinkedList[n/10]);
		   locks = new StampedLock[n/10];
		}
		else
		{
		   data = (LinkedList<E>[])(new LinkedList[100]);
		   locks = new StampedLock[100];
		}
		for(int j = 0; j < data.length;j++)
			{
				data[j] = new LinkedList<E>();
				locks[j] = new StampedLock();
			}	
	}
	
	
	private int GetLockIndex(E x){
		
		int k = x.hashCode();
		int h = Math.abs(k % locks.length);
		
		return(h);		
	}
	
	public void addCar(E x,DataOutputStream writer,String data[],Connection con,Statement st)
	{
		
	  if(x != null)
	  {
	   
		Long key =  locks[GetLockIndex(x)].writeLock();
		System.out.println("Lock Aquired for Adding car by Client: "+data[5]+" Has hash code "+GetLockIndex(x));
		try
			      {
			    	System.out.println("Caller: (SL)"+data[5]);
					rs=st.executeQuery("SELECT * FROM Cars WHERE Make='"+data[1]+"'");
					String make="";
					while(rs.next()){
						make=rs.getString(1);
					}
					if(!make.equals(data[1])){
						
					Thread.sleep(5000);	
					st.executeUpdate("INSERT INTO Cars(Make,RegNo,Price,Mileage,ForSale)VALUES('"+data[1]+"','"+data[2]+"','"+data[3]+"','"+data[4]+"',1)");	
					writer.writeUTF("Car Added to server sucessfully.. by : "+data[5]);
					}
					else{
						writer.writeUTF("Car with this make already exists: ");
					}
			  	
			      }
			    catch (Exception e){System.out.println("Error in Add car method where caller is : "+data[5]);}
			    finally
			    {
			    	System.out.println("Lock Unlocked for Adding car by Client: "+data[5]+" Has hash code "+GetLockIndex(x));
			    	try {locks[GetLockIndex(x)].unlock(key);} catch (Exception e2) {}
			    }		
	  }
	}
	
	public void Test(E x,String data[])
	{
		
	  if(x != null)
	  {
	   
		Long key =  locks[GetLockIndex(x)].writeLock();
		System.out.println("Lock Aquired by Client: "+data[5]+" Has hash code "+GetLockIndex(x));
		try
			      {
					System.out.println("Adding car processing for client "+data[5]+" Started");
			    	Thread.sleep(5000);
			    	System.out.println("Adding car processing for client "+data[5]+" Ended");
			    	
			      }
			    catch (Exception e){System.out.println("Error in Add car method where caller is : "+data[5]);}
			    finally
			    {
			    	System.out.println("Lock Unlocked  by Client: "+data[5]+" Has hash code "+GetLockIndex(x));
			    	try {locks[GetLockIndex(x)].unlock(key);} catch (Exception e2) {}
			    }		
	  }
	}
	
	
	public void SellCar(E x,DataOutputStream writer,String data[],Connection con,Statement st)
	{
		
	  if(x != null)
	  {
		Long key =  locks[GetLockIndex(x)].writeLock();
		System.out.println("Lock Aquired by Client for selling car: "+data[3]+" Has hash code "+GetLockIndex(x));  
		try
			      {
			    	rs=st.executeQuery("SELECT * FROM Cars WHERE Make='"+data[1]+"' AND RegNo='"+data[2]+"'");
					if (!rs.isBeforeFirst() ) {    
	  					 System.out.println("Invalid credentials or car is already sold...Sorry"); 
						writer.writeUTF("Invalid credentials or car is already sold...Sorry");
					} 
					else{
						Thread.sleep(5000);
						st.executeUpdate("Update Cars set ForSale=0 WHERE Make='"+data[1]+"' AND RegNo='"+data[2]+"'");
						writer.writeUTF("Car sold sucessfully");
					}		
			    	
			      }
			    catch (Exception e){System.out.println("Error in Sell car method where caller is : "+data[3]);}
			    finally
			    {
			    	System.out.println("Lock Unlocked  by Client: for selling car"+data[3]+" Has hash code "+GetLockIndex(x));
			    	try {locks[GetLockIndex(x)].unlock(key);} catch (Exception e2) {}
			    }		
	  }
	  
	}
	
	public void SearchByMake(E x,DataOutputStream writer,String data[],Connection con,Statement st)
	{
	//Checking whether any thread is writing or not . If no thread is writing the lock all indexes of hashmap from writing 
		boolean flag = true;
		long[] stamps = new long[locks.length];
		int counter=0;
		for(int i=0;i<locks.length;i++)
		{
			if(locks[i].isWriteLocked())
				{
				flag=false;	break;
				}
			else
			{
				counter++;
				stamps[i]=locks[i].readLock();
			}
		}
	
	if(flag)
	{
		System.out.println("Lock Aquired for Search by make  by Client: "+data[2]+" Has hash code "+GetLockIndex(x));
		try {
		//searching process goes from here
		rs=st.executeQuery("SELECT * FROM Cars WHERE Make='"+data[1]+"'");
		if (!rs.isBeforeFirst() ) {    
			writer.writeUTF("No Car with "+data[1]+" Exists on server");
			System.out.println("Lock Unlocked for Search by make by Client: "+data[2]+" Has hash code "+GetLockIndex(x));
			  for(int i=0;i<counter;i++) locks[i].unlock(stamps[i]);

		}
		else{
		while(rs.next()){
		writer.writeUTF("Search Result for car with make: "+data[1]+"\nRegNo: "+rs.getString(2)+"\nPrice: "+rs.getString(3)+"\nMileage: "+rs.getString(4)+"\nForSale: "+rs.getString(5));

		
		}
		
		System.out.println("Lock Unlocked for Search by make by Client: "+data[2]+" Has hash code "+GetLockIndex(x));
		  for(int i=0;i<counter;i++) locks[i].unlock(stamps[i]);
		  
		}
		
		}catch (Exception e) 
		{
			System.out.println("Error in Search by make id  method where caller is : "+data[2]);
		}
		
	}
	else 
	{	  //releasing all write locks.
		try {
			writer.writeUTF("Sorry "+data[2]+" Database is currenly updating so wait for a while");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Lock Unlocked for Search by make by Client: "+data[2]+" Has hash code "+GetLockIndex(x));
		  for(int i=0;i<counter;i++) locks[i].unlock(stamps[i]);
		  		 	
	}
	
	}
	
	public void TotalValueOfSales(E x,DataOutputStream writer,String data[],Connection con,Statement st)
	{
	//Checking whether any thread is writing or not . If no thread is writing the lock all indexes of hashmap from writing 
		boolean flag = true;
		long[] stamps = new long[locks.length];
		int counter=0;
		for(int i=0;i<locks.length;i++)
		{
			if(locks[i].isWriteLocked())
				{
				flag=false;	break;
				}
			else
			{
				counter++;
				stamps[i]=locks[i].readLock();
			}
		}
	
	if(flag)
	{
		System.out.println("Lock Aquired  for totalSales by Client:   "+data[1]+" Has hash code "+GetLockIndex(x));
		
		try {
			
			rs=st.executeQuery("SELECT * FROM Cars");
			double totalPrice=0.0d;
			while(rs.next()){
			
				String price=rs.getString(3);
				totalPrice+=Double.parseDouble(price);
			}
			writer.writeUTF("Total Value of All cars:\n"+totalPrice);
			System.out.println("Lock Unlocked  for totalSales by Client:  "+data[1]+" Has hash code "+GetLockIndex(x));
			for(int i=0;i<counter;i++) locks[i].unlock(stamps[i]);
			  		 	
			
		} catch (Exception e) {
				System.out.println("Error in method named Total values of sales called by client : "+data[1]);
		}
		
	}
	else 
	{	  //releasing all write locks.
		try {
			writer.writeUTF("Sorry  : "+data[1]+" Please wait for a while because right now data base is updating ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Lock Unlocked  for totalSales by Client:  "+data[1]+" Has hash code "+GetLockIndex(x));
		for(int i=0;i<counter;i++) locks[i].unlock(stamps[i]);
		  		 	
	}
	
	
	}
	
	public void CarsForSell(E x,DataOutputStream writer,String data[],Connection con,Statement st)

	{
	//Checking whether any thread is writing or not . If no thread is writing the lock all indexes of hashmap from writing 
		boolean flag = true;
		long[] stamps = new long[locks.length];
		int counter=0;
		for(int i=0;i<locks.length;i++)
		{
			if(locks[i].isWriteLocked())
				{
				flag=false;	break;
				}
			else
			{
				counter++;
				stamps[i]=locks[i].readLock();
			}
		}
		String sendData="";
	if(flag)
	{
		System.out.println("Lock Aquired Cars for sale  by Client: "+data[1]+" Has hash code "+GetLockIndex(x));
		
		try {
			
			Thread.sleep(10000);
			
			rs=st.executeQuery("SELECT * FROM Cars WHERE ForSale=1");
			if (!rs.isBeforeFirst() ) {    
				writer.writeUTF("All cars have been sold out.");
			}
			
			while(rs.next()){
				
				sendData+=rs.getString(1)+":";	
				sendData+=rs.getString(2)+":";
				sendData+=rs.getString(3)+":";
				sendData+=rs.getString(4);
				sendData+="#";					
			}
			
			writer.writeUTF(sendData);
			System.out.println("Lock Unlocked Cars for sales by Client: "+data[1]+" Has hash code "+GetLockIndex(x));
			  for(int i=0;i<counter;i++) locks[i].unlock(stamps[i]);
			
		} catch (Exception e) {
			System.out.println("Error in Cars for sell  method where caller is : "+data[1]);
		}
		
	}
	else 
	{	  //releasing all write locks.
		try {
			writer.writeUTF("Sorry .. Please wait for a while . right now database is updating");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Lock Unlocked Cars for sales by Client: "+data[1]+" Has hash code "+GetLockIndex(x));
		  for(int i=0;i<counter;i++) locks[i].unlock(stamps[i]);
		  		 	
	}
	
	
	}
	
}
