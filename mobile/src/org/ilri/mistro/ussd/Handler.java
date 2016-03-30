package org.ilri.mistro.ussd;

import java.util.ArrayList;
import java.util.HashMap;

import hms.sdp.ussd.MchoiceUssdException;
import hms.sdp.ussd.client.MchoiceUssdSender;

public class Handler
{
	protected HashMap<String, Object> userData;
	public static String FOOTPRINT_KEY="footprint";
	protected MchoiceUssdSender ussdSender;
	protected String address;
	protected String conversationId;
	public Handler(MchoiceUssdSender ussdSender, String address, String conversationId, HashMap<String, Object> userData) 
	{
		this.userData=userData;
		this.address=address;
		this.ussdSender=ussdSender;
		this.conversationId=conversationId;
	}
	
	public void setUserData(HashMap<String, Object> userData)
	{
		this.userData=userData;
	}
	
	protected void showMenu(String menuText, int menuId)
	{
		try 
		{
			ussdSender.sendMessage(menuText, address, conversationId, false);
			ArrayList<Integer> userFootprint=(ArrayList<Integer>)userData.get(FOOTPRINT_KEY);
			userFootprint.add(menuId);
		} 
		catch (MchoiceUssdException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void closeMenu(String parentMenuText)
	{
		try
		{
			ussdSender.sendMessage(parentMenuText, address, conversationId, false);
			ArrayList<Integer> userFootprint=(ArrayList<Integer>)userData.get(FOOTPRINT_KEY);
			userFootprint.remove(userFootprint.size()-1);//pop last
		} 
		catch (MchoiceUssdException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected String getFootprintText()
	{
		ArrayList<Integer> userFootprint=(ArrayList<Integer>)userData.get(FOOTPRINT_KEY);
		String footprintText="";
		for(int i=0;i<userFootprint.size();i++)
		{
			if(i==0)
			{
				footprintText=String.valueOf(userFootprint.get(i));
			}
			else
			{
				footprintText=footprintText+","+String.valueOf(userFootprint.get(i));
			}
		}
		return footprintText;
	}
	
	protected void clearFootprint()
	{
		ArrayList<Integer> userFootprint=(ArrayList<Integer>)userData.get(FOOTPRINT_KEY);
		userFootprint.clear();
		userFootprint.add(0);
	}
}
