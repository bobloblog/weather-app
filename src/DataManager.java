import java.sql.Time;
import java.util.ArrayList;

/**
 * This class acts as an aggregate gateway to access all of the information used by the program.  
 * 
 * @author Locomotion15
 *
 */

public class DataManager
{
	public String county = "N/A", fips = "N/A", woeid = "N/A";
	public Time time;
	public ArrayList<AlertObject> alerts;
	public WeatherObject weatherObject;
	
	public DataManager()
	{
		weatherObject = new WeatherObject();
	}
	
	public DataManager(String query)
	{
		weatherObject = new WeatherObject();
		
		lookUpWOEID(query);
		lookUpWeather(weatherObject.getWOEID());
		lookUpTime(weatherObject.getLat(), weatherObject.getLng());
		lookUpFIPS(weatherObject.getState(), weatherObject.getCounty());
		lookUpAlerts(weatherObject.getState(), weatherObject.getFIPS());
	}
	
	/**
	 * This method translates a user-input location into a WOEID that can be used to locate weather information online. 
	 * This method also obtains important county information.
	 * @param query A location for which to retrieve the WOEID
	 */
	private void lookUpWOEID(String query)
	{
		WOEIDLookup woeidLU = new WOEIDLookup(query);
		weatherObject.setWOEID(woeidLU.getWOEID());
		weatherObject.setCounty(woeidLU.getCounty());
	}
	
	private void lookUpWeather(String WOEID)
	{
		try{//////////////////////////////////////////////////////////////////////////
		new WeatherLookup(WOEID, weatherObject);}
		catch(Exception e){System.out.println(e);}
	}
	
	/**
	 * This method uses a live online database to lookup the local time, given the latitude and longitude of a location.
	 * @param latitude A String representing the latitude
	 * @param longitude A String representing the longitude
	 */
	private void lookUpTime(String latitude, String longitude)
	{
		TimeLookup timeLU = new TimeLookup(latitude, longitude);
		weatherObject.setLocalTime(timeLU.getTime().toString());
	}
	
	/**
	 * This method uses a National Weather Service database to look up a geolocation, or FIPS code based on a given state and county.
	 * FIPS codes are used to identify which counties are affected by an alert.
	 * @param stateAbr The two letter abbreviation of the state
	 * @param countyName The name of the county
	 */
	private void lookUpFIPS(String stateAbr, String countyName)
	{
		FIPSLookup fipsLU = new FIPSLookup(stateAbr, countyName);
		weatherObject.setFIPS(fipsLU.getFIPS());
	}
	
	/**
	 * This method fetches alert information from the Nation Weather Service's XML database given the state abbreviation and county FIPS code.
	 * @param stateAbr The two-letter state abbreviation
	 * @param fips The five or six digit FIPS code
	 */
	private void lookUpAlerts(String stateAbr, String fips)
	{
		AlertLookup alertLU = new AlertLookup(stateAbr, fips);
		weatherObject.setAlerts(alertLU.getAlerts());
	}
}
