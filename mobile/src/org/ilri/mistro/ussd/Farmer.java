package org.ilri.mistro.ussd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class Farmer 
{
	private String name;
	private String county;
	private String district;
	private List<Cow> cows;
	private Database database;
	private String mobileNumber;
	private int farmerDatabaseId;
	
	public Farmer(String mobileNumber) 
	{
		farmerDatabaseId=-1;
		cows=new ArrayList<Cow>();
		this.database=null;
		this.mobileNumber=mobileNumber;
	}
	public void setName(String name)
	{
		this.name=name;
	}
	public void setDatabase(Database database)
	{
		this.database=database;
		if(database==null)
		{
			System.out.println("Database in farmer reinitialized but appears to be null");
		}
	}
	public void setCounty(String county)
	{
		this.county=county;
	}
	public void setDistrict(String district)
	{
		this.district=district;
	}
	public void addCow(Cow cow)
	{
		cows.add(cow);
		//TODO: insert cow to database
		if(farmerDatabaseId!=-1)
		{
			System.out.println("Adding cow to database");
			database.initInsertStatement("INSERT INTO `ilri_mistro`.`cow` (`farmer_id`,`name`) VALUES(?,?)",true);
			database.addColumnValue(farmerDatabaseId);
			database.addColumnValue(cow.getName());
			
			ResultSet resultSet=database.executeInsert();
			try 
			{
				if(resultSet!=null && resultSet.next())
				{
					cow.setDatabaseId(resultSet.getInt(1));
					System.out.println("Cow Id gotten = "+resultSet.getInt(1));
				}
			} 
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Unable to add cow to database. Farmer id has not been set");
		}
		
	}
	
	public void addToDatabase()
	{
		
		if(database!=null)
		{
			System.out.println("Adding farmer to database");
			database.initInsertStatement("INSERT INTO `ilri_mistro`.`farmer` (`name`,`mobile_no`,`location_county`,`location_district`,`date_added`) VALUES(?,?,?,?,?)",true);
			database.addColumnValue(name);
			database.addColumnValue(mobileNumber);
			database.addColumnValue(county);
			database.addColumnValue(district);
			Date date=new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
			database.addColumnValue(sdf.format(date));
			
			ResultSet resultSet = database.executeInsert();
			try 
			{
				if(resultSet!=null && resultSet.next())
				{
					farmerDatabaseId=resultSet.getInt(1);
					System.out.println("Farmer Id gotten = "+farmerDatabaseId);
				}
			} 
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//TODO: get farmers database ID
		}
		else
		{
			System.out.println("Unable to add farmer to database. Database object appears to be null");
		}
	}
}
