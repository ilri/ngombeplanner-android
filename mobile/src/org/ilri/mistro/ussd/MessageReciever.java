package org.ilri.mistro.ussd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import hms.sdp.ussd.MchoiceUssdException;
import hms.sdp.ussd.MchoiceUssdMessage;
import hms.sdp.ussd.MchoiceUssdTerminateMessage;
import hms.sdp.ussd.client.MchoiceUssdReceiver;
import hms.sdp.ussd.client.MchoiceUssdSender;


public class MessageReciever extends MchoiceUssdReceiver
{
	private final String mainMenuText="MISTRO\n 1:Register farmer\n 2:Register cow\n 98:Exit";
	private final String sessionTerminationText="Session terminated";
	private ConcurrentMap<String, HashMap<String, Object>> session=new ConcurrentHashMap<String, HashMap<String, Object>>();
	
	@Override
	public void onMessage(MchoiceUssdMessage arg0)
	{
		String message = arg0.getMessage();
        String address = arg0.getAddress();
        String conversationId = arg0.getConversationId();
        String correlationId = arg0.getCorrelationId();
		try 
		{
			MchoiceUssdSender ussdSender=new MchoiceUssdSender("http://127.0.0.1:8000/ussd/", "appid", "password");
			if(session.containsKey(address))//not the first time for user
			{
				System.out.println("Message recieved: "+message);
				HashMap<String, Object> userData=session.get(address);
				ArrayList<Integer> userFootprint=(ArrayList<Integer>)userData.get(Handler.FOOTPRINT_KEY);
				if(userFootprint.size()==0)//from the main menu
				{
					System.out.println("Footprint empty, user prbably at main menu");
					if(message.equals(String.valueOf(FarmerHandler.screen1Id)))
					{
						System.out.println("Redirecting message to FarmerHandler");
						FarmerHandler farmerHandler=(FarmerHandler)userData.get(FarmerHandler.KEY);
						farmerHandler.setUserData(userData);
						farmerHandler.showInitMenu();
					}
					else if(message.equals(String.valueOf(CowHandler.screen1Id)))//user wants to register cow
					{
						System.out.println("Redirecting message to CowHandler");
						CowHandler cowHandler=(CowHandler)userData.get(CowHandler.KEY);
						cowHandler.showInitMessage();
					}
					else
					{
						terminateSession(ussdSender, true, address, conversationId);
					}
				}
				else
				{
					if(userFootprint.get(0)==FarmerHandler.screen1Id)
					{
						System.out.println("Redirecting user to FarmerHandler");
						FarmerHandler farmerHandler=(FarmerHandler)userData.get(FarmerHandler.KEY);
						farmerHandler.handleMessage(message);
					}
					else if(userFootprint.get(0)==CowHandler.screen1Id)//farmer wants to register other cows
					{
						System.out.println("Redirecting message to CowHandler");
						CowHandler cowHandler=(CowHandler)userData.get(CowHandler.KEY);
						cowHandler.handleMessage(message);
					}
					else if(userFootprint.get(0)==0)
					{
						userFootprint.clear();
						if(Integer.valueOf(message)==1)
						{
							ussdSender.sendMessage(mainMenuText, address, conversationId, false);
						}
						else
						{
							terminateSession(ussdSender, true, address, conversationId);
						}
							
					}
				}
			}
			else//first time for this user
			{
				System.out.println("First time user, creating session");
				HashMap<String, Object> hashMap=new HashMap<String, Object>();
				hashMap.put(Handler.FOOTPRINT_KEY, new ArrayList<Integer>());
				hashMap.put(FarmerHandler.KEY, new FarmerHandler(ussdSender,address, conversationId,null));
				hashMap.put(CowHandler.KEY, new CowHandler(ussdSender, address, conversationId, null));
				hashMap.put(Database.KEY, new Database("ilri_mistro", "root", "jason"));
				session.put(address, hashMap);
				ussdSender.sendMessage(mainMenuText, address, conversationId, false);
			}
		} 
		catch (MchoiceUssdException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void terminateSession(MchoiceUssdSender ussdSender, boolean showTerminationText, String address, String conversationId)
	{
		try 
		{
			if(showTerminationText)
			{
				ussdSender.sendMessage(sessionTerminationText, address, conversationId, true);
			}
			Database userDatabase=(Database)session.get(address).get(Database.KEY);
			if(userDatabase!=null)
			{
				userDatabase.close();
			}
			session.remove(address);
		} 
		catch (MchoiceUssdException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onSessionTerminate(MchoiceUssdTerminateMessage arg0) 
	{
		//TODO:close the database
		session.remove(arg0.getAddress());
	}
	

}