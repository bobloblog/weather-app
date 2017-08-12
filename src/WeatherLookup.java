import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * WeatherLookup uses Yahoo! Weather to look up the local weather, given the WOEID.
 * @author Locomotion15
 *
 */
public class WeatherLookup
{
	
	private String temperature = "N/A", humidity = "N/A", windDirection = "N/A", windSpeed = "N/A", visibility = "N/A", pressure = "N/A", trend = ""; //trend being pressure trend (0 steady, 1 rising, 2 falling)
    private String city = "N/A", state = "N/A", country = "N/A", currentConditions = "N/A", sunrise = "N/A", sunset = "N/A";
    private String lat = "", lng = "";
    private int weatherCode = -1;
	
    
	public WeatherLookup(String woeid, WeatherObject weatherObject) throws Exception
    {
    	BufferedReader in;
        String inputLine;
        
    	System.out.println("Connecting to Yahoo! Weather...");
        URL weather = new URL("http://weather.yahooapis.com/");
        URL weatherFull = new URL(weather, "forecastrss?w=" + woeid);
        System.out.println(weatherFull);
       
        try
        {
            in = new BufferedReader(
            new InputStreamReader(
            weatherFull.openStream()));
            System.out.println("Connection Successful.");
        }
        catch(Exception e)
        {
            throw new Exception();
        }
        
        while ((inputLine = in.readLine()) != null)
        {
        	if(inputLine.indexOf("<yweather:") != -1)//Limits the number of checks required per line
        	{
        		if(inputLine.indexOf("<yweather:location") != -1)
        		{
        			if(inputLine.indexOf("city=") != -1)
        				city = parseStringAfter(inputLine, "city="); 
        			if(inputLine.indexOf("region=") != -1)
        				state = parseStringAfter(inputLine, "region="); 
        			if(inputLine.indexOf("country=") != -1)
        				country = parseStringAfter(inputLine, "country=");
        		}
        		else if(inputLine.indexOf("<yweather:wind") != -1)
        		{
        			if(inputLine.indexOf("wind chill=") != -1)
        				temperature = parseStringAfter(inputLine, "wind chill="); 
        			if(inputLine.indexOf("direction=") != -1)
        				windDirection = parseStringAfter(inputLine, "direction=");
        			if(inputLine.indexOf("speed=") != -1)
        				windSpeed = parseStringAfter(inputLine, "speed=");
        		}
        		else if(inputLine.indexOf("<yweather:atmosphere") != -1)
        		{
        			if(inputLine.indexOf("humidity=") != -1)
        				humidity = parseStringAfter(inputLine, "humidity=");
        			if(inputLine.indexOf("visibility=") != -1)
        				visibility = parseStringAfter(inputLine, "visibility=");
        			if(inputLine.indexOf("pressure=") != -1)
        				pressure = parseStringAfter(inputLine, "pressure=");
        			if(inputLine.indexOf("rising=") != -1)//the trend of pressure
        				trend = parseStringAfter(inputLine, "rising=");
        		}
        		else if(inputLine.indexOf("<yweather:astronomy") != -1)
        		{
        			if(inputLine.indexOf("sunrise=") != -1)
        				sunrise = parseStringAfter(inputLine, "sunrise=");
        			if(inputLine.indexOf("sunset=") != -1)
        				sunset = parseStringAfter(inputLine, "sunset=");
        		}
        		else if(inputLine.indexOf("<yweather:condition") != -1)
        		{
        			if(inputLine.indexOf("text=") != -1)
        				currentConditions = parseStringAfter(inputLine, "text=");
        			if(inputLine.indexOf("code=") != -1 && weatherCode == -1)
        				weatherCode = new Integer(parseIntAfter(inputLine, "code="));
        			break;
        		}
        	}
        	else if(inputLine.indexOf("<geo:") != -1)
        	{
        		if(inputLine.indexOf("<geo:lat>") != -1)
        			lat = inputLine.substring(inputLine.indexOf(">") + 1, inputLine.lastIndexOf("<"));
        		else if(inputLine.indexOf("<geo:long>") != -1)
        			lng = inputLine.substring(inputLine.indexOf(">") + 1, inputLine.lastIndexOf("<"));
        	}
        } 
        in.close();
        
        System.out.println("Weather Fetch Successful.");
        
        if(city.compareTo("N/A") == 0)
        {
            System.out.println("Invalid Location. Functions terminated.");
            return;
        }
        
        buildWeatherObject(weatherObject);
    }
	
	/**
     * Based on the formatting of the Yahoo! XML, this file parses an integer contained between quotation marks (ie "\"1\"") after a given String (ie "theLoneliestNumber=")
     * @param line The line that contains the Strings of the search item and the integer (ie "<info theLoneliestNumber="1"/>)
     * @param search  The item to search for just before the integer (ie "theLoneliestNumber=")
     * @return The integer (ie 1)
     */
    private String parseIntAfter(String line, String search)
    {
        Integer value = -1;
        int slot = line.indexOf("\"", line.indexOf(search));
        String temp = line.substring(slot + 1, line.indexOf("\"", slot + 1));
        try{value = Integer.parseInt(temp);}
        catch(Exception e)
        {
            return "N/A";
        }
        return value.toString();
    }
    
    /**
     * Based on the formatting of the Yahoo! XML, this file parses a string contained between quotation marks (ie "\"one\"") after a given String (ie "theLoneliestNumber=")
     * @param line The line that contains the Strings of the search item and the string (ie "<info theLoneliestNumber="one"/>)
     * @param search  The item to search for just before the integer (ie "theLoneliestNumber=")
     * @return The string (ie "one")
     */
    private String parseStringAfter(String line, String search)
    {
    	int slot = line.indexOf("\"", line.indexOf(search));
        String temp = line.substring(slot + 1, line.indexOf("\"", slot + 1));
        return temp;
    }
    
    private void buildWeatherObject(WeatherObject weatherObject)
    {
    	weatherObject.setTemperature(temperature);
    	weatherObject.setHumidity(humidity);
    	weatherObject.setWindDirection(windDirection);
    	weatherObject.setWindSpeed(windSpeed);
    	weatherObject.setVisibility(visibility);
    	weatherObject.setPressure(pressure);
    	weatherObject.setTrend(trend);
    	weatherObject.setCity(city);
    	weatherObject.setState(state);
    	weatherObject.setCountry(country);
    	weatherObject.setCurrentConditions(currentConditions);
    	weatherObject.setSunrise(sunrise);
    	weatherObject.setSunset(sunset);
    	weatherObject.setLat(lat);
    	weatherObject.setLng(lng);
    	weatherObject.setWeatherCode(weatherCode);
    }
}
