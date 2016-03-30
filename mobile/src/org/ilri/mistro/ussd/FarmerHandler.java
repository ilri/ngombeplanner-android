package org.ilri.mistro.ussd;

import java.util.HashMap;
import hms.sdp.ussd.client.MchoiceUssdSender;

public class FarmerHandler extends Handler
{
	public static final String KEY="farmerHandler";
	public static final int screen1Id=1;
	private final String[] screens=new String[]
	        {"REGISTRATION\n\n What is your name?",
			"REGISTRATION\n\n Which County are you in?",
			"REGISTRATION\n\n Which District are you in?"};
	private final Farmer farmer;
	private int unregisteredCows;
	public FarmerHandler(MchoiceUssdSender ussdSender, String address, String conversationId, HashMap<String, Object> userData)
	{
		super(ussdSender,address,conversationId,userData);
		this.farmer=new Farmer(address);//TODO: decrypt address
	}
	
	public void showInitMenu()//show initial menu in this class
	{
		System.out.println("Showing init Farmer message");
		showMenu(screens[0], screen1Id);
	}
	
	public void handleMessage(String message)
	{
		String footprintText=getFootprintText();
		if(footprintText.equals(String.valueOf(screen1Id)))//message from first screen
		{
			farmer.setName(message);
			showMenu(screens[1], 1);
		}
		else if(footprintText.equals(String.valueOf(screen1Id)+","+String.valueOf(1)))//reply from second screen
		{
			farmer.setCounty(message);
			showMenu(screens[2], 2);
		}
		else if(footprintText.equals(String.valueOf(screen1Id)+","+String.valueOf(1)+","+String.valueOf(2)))//reply from third screen
		{
			farmer.setDistrict(message);
			//showMenu(screens[3], 3);//replace this with cowhandler init message
			CowHandler cowHandler=(CowHandler)userData.get(CowHandler.KEY);
			cowHandler.setUserData(userData);
			cowHandler.showInitMessage();
			farmer.addToDatabase();
		}
		else if(footprintText.contains(String.valueOf(screen1Id)+","+String.valueOf(1)+","+String.valueOf(2)+","+String.valueOf(CowHandler.screen1Id)))//reply from fourth screen
		{
			System.out.println("Redirecting user to CowHandler");
			CowHandler cowHandler=(CowHandler)userData.get(CowHandler.KEY);
			cowHandler.handleMessage(message);
			
		}
	}

	public void addCow(Cow newCow)
	{
		farmer.addCow(newCow);
	}

	@Override
	public void setUserData(HashMap<String, Object> userData)
	{
		super.setUserData(userData);
		//add database to farmer object
		Database database=(Database)userData.get(Database.KEY);
		farmer.setDatabase(database);
	}
	
}
