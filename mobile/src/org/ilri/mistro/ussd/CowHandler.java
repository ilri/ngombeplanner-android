package org.ilri.mistro.ussd;

import hms.sdp.ussd.client.MchoiceUssdSender;

import java.util.HashMap;

public class CowHandler extends Handler
{
	private Cow currentCow;
	private int numberOfCows;
	private int currentCowNo;
	public static final String KEY="cowHandler";
	public static final int screen1Id=2;
	private String[] screens=new String[]{"REGISTRATION\n\n How many cows do you want to register?",
			"REGISTRATION\n\n What is the name of the ",
			"REGISTRATION\n\n All cows have been registered. Input 1 to go back to the main menu"};
	public CowHandler(MchoiceUssdSender ussdSender, String address, String conversationId, HashMap<String, Object> userData) 
	{
		super(ussdSender,address,conversationId,userData);
		currentCow=new Cow();
		currentCowNo=0;
	}
	
	public void setUnregisteredCows(int unregisteredCows)
	{
		this.numberOfCows=unregisteredCows;
	}
	
	public void showInitMessage()
	{
		System.out.println("Showing initial CowHandler screen");
		showMenu(screens[0], screen1Id);
	}
	
	public void handleMessage(String message)
	{
		System.out.println("CowHandler handling message");
		String footprintText=getFootprintText();
		System.out.println(footprintText);
		if(footprintText.equals(String.valueOf(FarmerHandler.screen1Id)+","+String.valueOf(1)+","+String.valueOf(2)+","+String.valueOf(CowHandler.screen1Id)) || footprintText.equals(String.valueOf(screen1Id)))
		{
			numberOfCows=Integer.valueOf(message);
			System.out.println("Message probably response with number of cows = "+message);
			currentCowNo=1;
			showMenu(screens[1]+getTH(currentCowNo)+" cow", 1);
		}
		else// if(footprintText.contains(String.valueOf(FarmerHandler.screen1Id)+","+String.valueOf(1)+","+String.valueOf(2)+","+String.valueOf(CowHandler.screen1Id)+String.valueOf(1)))
		{
			System.out.println("Footprint contains "+footprintText);
			if(currentCowNo<=numberOfCows)
			{
				System.out.println("Message probably contains name of cow "+currentCowNo);
				currentCow.setName(message);
				FarmerHandler farmerHandler=(FarmerHandler)userData.get(FarmerHandler.KEY);
				farmerHandler.addCow(currentCow);
				currentCow=new Cow();
				currentCowNo++;
				if(currentCowNo>numberOfCows)//just registered the last cow
				{
					showMenu(screens[2], 2);
					clearFootprint();
				}
				else
				{
					showMenu(screens[1]+getTH(currentCowNo)+" cow", 1);
				}
			}
		}
	}
	
	private String getTH(int i)
	{
		String s=String.valueOf(i);
		if(s.endsWith("1"))
		{
			s=s+"st";
		}
		else if(s.endsWith("2"))
		{
			s=s+"nd";
		}
		else if(s.endsWith("3"))
		{
			s=s+"rd";
		}
		else if(s.endsWith("4")||s.endsWith("5")||s.endsWith("6")||s.endsWith("7")||s.endsWith("8")||s.endsWith("9")||s.endsWith("0"))
		{
			s=s+"th";
		}
		return s;
	}

}
