import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

class Client{
	
	public static void main(String args[]){
	
		UserInterface.designInterface();
	}		
}
									//Creating Home User Interface
class UserInterface{
	
	static JFrame frame=new JFrame("Car Showroom");
	static Font font=new Font("Courier",Font.BOLD,18);
	static JTextField clientName ;
	public static void designInterface(){
		frame.setLayout(null);
		frame.setBounds(100,100,500,600);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		JLabel label=new JLabel("Welcome to Car Showroom");
			
		JButton addCar=new JButton("Add New Car");
		JButton sellCar=new JButton("Sell A Car");
		JButton searchCar=new JButton("Search Car By Make");
		JButton totalValue=new JButton("Total Value Of Sales");
		JButton carsForSale=new JButton("Cars For Sale");	
		clientName = new JTextField("ClientName");
		label.setBounds(120,30,300,35);
		addCar.setBounds(165,100,170,35);
		clientName.setBounds(10, 10, 130, 20);
		sellCar.setBounds(165,160,170,35);
		searchCar.setBounds(165,220,170,35);
		totalValue.setBounds(165,280,170,35);
		carsForSale.setBounds(165,340,170,35);
		
		addCar.addActionListener(new Listener());
		sellCar.addActionListener(new Listener());
		searchCar.addActionListener(new Listener());
		totalValue.addActionListener(new Listener());
		carsForSale.addActionListener(new Listener());

		label.setFont(font);
		frame.add(label);
		frame.add(addCar);
		frame.add(sellCar);
		frame.add(searchCar);
		frame.add(totalValue);
		frame.add(carsForSale);
		frame.add(clientName);
		frame.setVisible(true);	
	}
}
									//Event handlers for Home buttons
class Listener implements ActionListener{
	
	public void actionPerformed(ActionEvent ae){
	
		JButton b=(JButton)ae.getSource();
		String name=b.getText();
		
		if(name.equals("Add New Car")){
			UserInterface.frame.setVisible(false);
			AddNewCar.designAddNewCarInterface();	
		}
		else if(name.equals("Sell A Car")){
			UserInterface.frame.setVisible(false);
			SellCar.designSellCarInterface();
		}
		else if(name.equals("Search Car By Make")){
			
			String make=JOptionPane.showInputDialog(null,"Enter Make of car to search:");
			Communication connect=new Communication();
			connect.sendToServer("Search Car By Make",make);
				
		}
		else if(name.equals("Total Value Of Sales")){
			
			Communication connect=new Communication();
			connect.sendToServer("Total Value Of Sales");
				
		}
		else if(name.equals("Cars For Sale")){
	
			Communication connect=new Communication();
			connect.sendToServer();		
		}	
	}
}
										//Creating Add new Car Frame
class AddNewCar  implements ActionListener{
	
	static JFrame frame=new JFrame("Add Car");
	static JTextField makeField=new JTextField();
	static JTextField regField=new JTextField();
	static JTextField priceField=new JTextField();
	static JTextField mileageField=new JTextField();
				
	
	public static void designAddNewCarInterface(){
		
		frame.setLayout(null);
		frame.setBounds(100,100,500,600);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		JLabel label=new JLabel("Add New Car to System");
		JButton add=new JButton("Add Car");
		JButton back=new JButton("Back");
		
		JLabel makeLabel=new JLabel("Enter Make:");
		JLabel regLabel=new JLabel("Enter Reg. No:");
		JLabel priceLabel=new JLabel("Enter Price:");
		JLabel mileageLabel=new JLabel("Enter Mileage:");
		
		label.setBounds(150,30,200,35);
		makeLabel.setBounds(50,100,100,30);
		makeField.setBounds(180,100,250,30);
		regLabel.setBounds(50,150,100,30);
		regField.setBounds(180,150,250,30);
		priceLabel.setBounds(50,200,100,30);
		priceField.setBounds(180,200,250,30);
		mileageLabel.setBounds(50,250,100,30);
		mileageField.setBounds(180,250,250,30);
		back.setBounds(180,330,100,30);
		add.setBounds(330,330,100,30);
		
		add.addActionListener(new AddNewCar());
		back.addActionListener(new AddNewCar());
		
		frame.add(makeLabel);
		frame.add(makeField);
		frame.add(regLabel);
		frame.add(regField);
		frame.add(priceLabel);
		frame.add(priceField);
		frame.add(mileageLabel);
		frame.add(mileageField);
		frame.add(add);
		frame.add(back);	
		frame.setVisible(true);	
	}
	public void actionPerformed(ActionEvent ae){
		
		JButton b=(JButton)ae.getSource();
		if(b.getText().equals("Add Car")){
									//Send This data to the server
			String make=makeField.getText();
			String reg=regField.getText();
			double price=0.0d, mileage=0.0d;
			Boolean invalidInput=false;
			try{						//Ensuring that price and mileage are numbers
				invalidInput=false;
				price=Double.parseDouble(priceField.getText());
				mileage=Double.parseDouble(mileageField.getText());
				
			}
			catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(null,"Price and mileage must be numbers.");
				priceField.setText("");
				mileageField.setText("");
				invalidInput=true;
			}
			if(invalidInput==false){
				Communication connect=new Communication();
				connect.sendToServer(b.getText(),make,reg,price,mileage);
				
			}
				
		}
		else{								//When Back button is clicked
			makeField.setText("");
			regField.setText("");
			priceField.setText("");
			mileageField.setText("");
			AddNewCar.frame.setVisible(false);
			UserInterface.frame.setVisible(true);	
		}			
	}
}
class SellCar implements ActionListener{
	
	static JFrame frame=new JFrame("Add Car");
	static JTextField makeField=new JTextField();
	static JTextField regField=new JTextField();
					
	public static void designSellCarInterface(){
		
		frame.setLayout(null);
		frame.setBounds(100,100,500,600);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		JLabel label=new JLabel("Sell Your cars here");
		JButton sell=new JButton("Sell Car");
		JButton back=new JButton("Back");
		
		JLabel makeLabel=new JLabel("Enter Make:");
		JLabel regLabel=new JLabel("Enter Reg. No:");
		label.setBounds(150,30,200,35);
		makeLabel.setBounds(50,100,100,30);
		makeField.setBounds(180,100,250,30);
		regLabel.setBounds(50,150,100,30);
		regField.setBounds(180,150,250,30);
		back.setBounds(180,330,100,30);
		sell.setBounds(330,330,100,30);
		

		sell.addActionListener(new SellCar());
		back.addActionListener(new SellCar());
		
		frame.add(makeLabel);
		frame.add(makeField);
		frame.add(regLabel);
		frame.add(regField);
		frame.add(sell);
		frame.add(back);	
		frame.setVisible(true);	
	}
	public void actionPerformed(ActionEvent ae){
		JButton b=(JButton)ae.getSource();
		if(b.getText().equals("Sell Car")){
										//Send This data to the server
			
			String make=makeField.getText();
			String reg=regField.getText();
			
			Communication connect=new Communication();
			connect.sendToServer(b.getText(),make,reg);
				
		}
		else{
			makeField.setText("");
			regField.setText("");
			SellCar.frame.setVisible(false);
			UserInterface.frame.setVisible(true);	
		}		
	}
}
								//Building communication channel bw server/Client
class Communication extends UserInterface{
		
	static Socket clientSocket;	
	static OutputStream messageToServer;
	static DataOutputStream sendMessage;
	static DataInputStream in ;
	static JFrame frame=new JFrame("Cars For Sale");

	public Communication(){
		try{
			clientSocket=new Socket("localhost",5600);
			messageToServer=clientSocket.getOutputStream();	
			sendMessage = new DataOutputStream(messageToServer);
			in = new DataInputStream(clientSocket.getInputStream());
			
		}catch(Exception ex){
			//JOptionPane.showMessageDialog(null,"Start Server and Check again");
		}
	}	
	
	public void sendToServer(String btnName,String make,String reg,double price,double mileage){
		
		try{	
			sendMessage.writeUTF(btnName+":"+make+":"+reg+":"+price+":"+mileage+":"+clientName.getText().toString());	
			JOptionPane.showMessageDialog(null,""+in.readUTF());

		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Start Server and Check again");
		}
	}

	public void sendToServer(String btnName,String make,String reg){
		
		try{
		
			sendMessage.writeUTF(btnName+":"+make+":"+reg+":"+clientName.getText().toString());	
			JOptionPane.showMessageDialog(null,""+in.readUTF());
			
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Start Server and Check again");
		}
	}

	public void sendToServer(String btnName,String make){
		
		try{
			sendMessage.writeUTF(btnName+":"+make+":"+clientName.getText().toString());
			
			JOptionPane.showMessageDialog(null,""+in.readUTF());
			
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Start Server and Check again");
		}
	}
										
	public void sendToServer(String btnName){	
		try{
			sendMessage.writeUTF(btnName+":"+clientName.getText().toString());	
			JOptionPane.showMessageDialog(null,""+in.readUTF());
			
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Start Server and Check again");
		}
	}
	
	public void sendToServer(){
		
		try{
	
			sendMessage.writeUTF("Cars For Sale"+":"+clientName.getText().toString());	
	
			String response = in.readUTF().toString();
			
			if(response.equals("Sorry .. Please wait for a while . right now database is updating"))
			{
				JOptionPane.showMessageDialog(null,"Sorry "+clientName.getText()+" Please wait for a while . right now database is updating");
			}
			
			else
			{	
			String records[]=response.split("#");
			String rows[][]=new String[records.length][4];
			
			String column[]={"Make","RegNo","Price","Mileage"};
			int i=0, j=0;
			for(i=0;i<records.length;i++){
				
				String get[]=records[i].split(":");
				rows[i][j]=get[j];
				j++;
				rows[i][j]=get[j];
				j++;
				rows[i][j]=get[j];
				j++;
				rows[i][j]=get[j];
				System.out.println(rows[i][j]);
				
				j=0;
			}			
			
			frame.setBounds(100,100,500,600);
			frame.setResizable(false);
			JTable jt=new JTable(rows,column);
			jt.setDefaultEditor(Object.class, null);
			jt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jt.setRowHeight(30);        
    			JScrollPane sp=new JScrollPane(jt);
        	
			frame.add(sp); 
			frame.setVisible(true);	
		}
			
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Start Server and Check again");
		}
	}
}