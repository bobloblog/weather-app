import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;

/**
 * NOTE: AS OF 6/18/2013 THIS CLASS IS BEING PHASED OUT. IT WILL BE REPLACED BY SEVERAL CLASSES, THE HIGHEST LEVEL OF WHICH IS DataManager. THIS IS AN ATTEMPT TO REORGANIZE THE CORE OF THE PROGRAM
 * TO BE MORE EFFECTIVE AND EASIER TO UPGRADE AND MAINTAIN. IT ALSO ADDRESSES SOME NAMING CONVENTION CHANGES TO MAKE THE PROGRAM MORE READABLE TO OTHERS.
 * 
 * This class is the messiest of them all and really needs to be rewritten. Eventually. Anyway...
 * This class is the core of the weather program.  It looks up the location, translates it into a WOEID, uses the WOEID to look up the weather, looks up the current time
 * at the location, and sets up all of the information into an easily readable string.
 * 
 * Possible changes: create a DataManager class which handles the classes LocationLookup, WeatherLookup, TimeLookup, and AlertLookup
 * This will organize the code much better and, if done correctly, could potentially allow WeatherLookup, TimeLookup, and AlertLookup to be run in separate threads, increasing performance
 * @author Locomotion15
 *
 */

public class Weather
{
	private String info = "";
	private AlertLookup alerts;
	
	private String temperature = "N/A", humidity = "N/A", windDirection = "N/A", windSpeed = "N/A", visibility = "N/A", pressure = "N/A", trend = ""; //trend being pressure trend (0 steady, 1 rising, 2 falling)
    private String city = "N/A", county = "N/A", state = "N/A", country = "N/A", currentConditions = "N/A", sunrise = "N/A", sunset = "N/A";
    private String lat = "", lng = "", woeid = "N/A", localTime = "N/A";
    private int weatherCode = -1;
	
    /**
     * The constructor for Weather takes only a String representation of the location as a parameter
     * @param location A string representation of the location to be looked up
     */
    public Weather(String location)
    {
        try{main(location);}
        catch(Exception e){return;}//Crappy error detection
    }
    
    /**
     * The main method acts as a driver for this class, controlling which information is fetched when
     * @param input A string representation of the location to be looked up
     * @throws Exception Because
     */
    public void main(String input) throws Exception
    {
    	String file = input;//idk. Like I said, this needs to be rewritten.
        alerts = new AlertLookup();        
        
       woeid = "";
        
        try{
        	woeid = woeidLookup(file);
        }catch(Exception e){info = "That is not a valid location.";}
        
        try{
        weatherLookup(woeid);
        }catch(Exception e){info = "Unable to connect to Yahoo! weather.";}
    }
    
    /**
     * This method looks up the current time at the location being searched
     * @throws Exception
     */
    public void timeLookup() throws Exception
    {
    	BufferedReader in;
        String inputLine;
        
        System.out.println("Looking up time info...");
        URI time = new URI("http", "//ws.geonames.org/timezone?lat=" + lat + "&lng=" + lng + "&username=Locomotion15", "");
        URL timeFull = time.toURL();
        
        System.out.println(timeFull);
        
        try
        {
            in = new BufferedReader(
            new InputStreamReader(
            timeFull.openStream()));
            System.out.println("Connection Succesfull");
        }
        catch(Exception e)
        {
        	System.out.println("Houston, we have a problem.");
            throw new Exception();
        }
        
        while ((inputLine = in.readLine()) != null)
            if(inputLine.indexOf("<time>") != -1)
            {
            	localTime = inputLine.substring(inputLine.lastIndexOf("<") - 5, inputLine.lastIndexOf("<"));
            	break;
            }
    }

    /**
     * This method fetches the weather from Yahoo! weather based on the WOEID that was looked up earlier
     * @param woeid A string representation of the WOEID
     * @throws Exception
     */
    public void weatherLookup(String woeid) throws Exception
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
        
        if(country.compareTo("United States") == 0)//There is no point in checking for alerts if you're not in America
        {
        	FIPSLookup fipsLookup = new FIPSLookup(state, county);
        	alerts = new AlertLookup(state, fipsLookup.getFIPS());
        	System.out.println("Local alerts: " + alerts.getAlerts().size());//----------------------------------------------------------------------------------------------
        }
        else
        	alerts = new AlertLookup();
        
        if(city.compareTo("N/A") == 0)
        {
            info = "That is not a valid location.";
            System.out.println("Invalid Location. Functions terminated.");
            return;
        }
        
        
        try{timeLookup();}
        catch(Exception e){System.out.println("Time failed. " + e);}
        setupInfoString();
    }
    
    /**
     * This method sets up a beautiful string that represents all of the weather information that was gathered. This string can be placed directly onto the GUI
     */
    public void setupInfoString()
    {
    	info += "Current weather for " + city + (((state.equals("")))?", ":", " + state.toUpperCase() + " - ") + country + " WOEID: " + woeid + "\n\n";
        info += "Current Conditions: " + currentConditions + "\n";
        info += "Temperature: " + temperature + (temperature.equals("N/A")?"\n":"°F\n");
        info += "Humidity: " + humidity + (humidity.equals("N/A")?"\n":"%\n");
        
        try{info += "Wind: " + findDirection(Integer.parseInt(windDirection)) + " at " + windSpeed + (windSpeed.equals("N/A")?"\n":"mph\n");}
        catch(Exception e){info += "Wind: " + "N/A" + " at " + windSpeed + (windSpeed.equals("N/A")?"\n":"mph\n");}
        
        if(visibility.equals(""))//Corrects possible error with Yahoo! API
        	visibility = "N/A";
        info += "Visibility: " + visibility + (visibility.equals("N/A")?"\n":" miles\n");
        
        if(trend.compareTo("0") == 0)//converts the pressure data into the arrow that correctly represents the trend
        	trend = "→";
        else if(trend.compareTo("1") == 0)
        	trend = "↑";
        else if(trend.compareTo("2") == 0)
        	trend = "↓";
        else
        	trend = "";
        info += "Pressure: " + pressure + (pressure.equals("N/A")?"\n":(" in " + trend + "\n"));        	
        info += "Sunrise: " + sunrise + "  Sunset: " + sunset;  
    }
    
    /**
     * This method translates the user-inputed location into a WOEID that can be used to locate weather information online.
     * @param file The name of the location
     * @return A String representation of the WOEID
     * @throws Exception
     */
    public String woeidLookup(String file) throws Exception
    {
    	System.out.println("Looking up WOEID...");//Uses an online search engine to look up the WOEID that is used to identify weather stations
        URI woeidURI = new URI("http", "woeid.rosselliot.co.nz", "/lookup/" + file + "/", "");
        URL woeidFull = woeidURI.toURL();
        
        System.out.println(woeidFull);
        
        BufferedReader in;
        String inputLine, woeid = "N/A";
        
    	try
        {
            in = new BufferedReader(
            new InputStreamReader(
            woeidFull.openStream()));
            System.out.println("Connection Successful.");
        }
        catch(Exception e)
        {
        	throw new Exception();
        }
        
        county = "N/A";
        
        while ((inputLine = in.readLine()) != null)
        {
            if(inputLine.indexOf("data-district_county=") != -1)//reads for the county name which is used later to narrow down alerts
            {
                int slot = inputLine.indexOf("\"", inputLine.indexOf("data-district_county="));
                String temp = inputLine.substring(slot + 1, inputLine.indexOf("\"", slot + 1));
                county = temp;
            }
            if(inputLine.indexOf("data-woeid=") != -1)//reads for the woeid
            {
                woeid = parseIntAfter(inputLine, "data-woeid=");
            }
        }
        in.close();
        
        System.out.println("WOEID: " + woeid);
        
        return woeid;
    }
    
    /**
     * This method translates a degree of direction into a String representation of a direction (ie 90 degrees is E)
     * @param degree An integer value representing the degree from North
     * @return
     */
    private String findDirection(int degree)
    {
        if(degree > 338 || degree <= 22)
            return "N";
        if(degree >= 23 && degree <= 68)
            return "NE";
        if(degree >= 69 && degree <= 113)
            return "E";
        if(degree >= 114 && degree <= 158)
            return "SE";
        if(degree >= 159 && degree <= 203)
            return "S";
        if(degree >= 204 && degree <= 248)
            return "SW";
        if(degree >= 249 && degree <= 293)
            return "W";
        if(degree >= 294 && degree <= 338)
            return "NW";
        return "N/A";
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
    
    /**
     * This method returns a beautiful String representation of all of the weather information gathered. This can be added directly to the GUI
     * @return A beautiful String
     */
    public String getWeatherString()
    {
    	return info;
    }
    
    /**
     * This method returns all of the alerts that were gathered in the weather fetching process
     * @return An ArrayList of Alert objects
     */
    public ArrayList<AlertObject> getAlerts()
    {
    	return  alerts.getAlerts();
    }
    
    /**
     * This method returns a String representation of the temperature excluding units (°F)
     * @return The temperature, in Farenheiht, excluding units
     */
    public String getTemp()
    {
    	return temperature;
    }
    
    /**
     * This method returns a String representation of the humidity excluding units (%)
     * @return The humidity, by percent saturation, excluding units
     */
    public String getHumidity()
    {
    	return humidity;
    }
    
    /**
     * This method returns a String representation of the wind direction as a numerical value (see Weather.findDirection(int degree))
     * @return The numerical value for the wind direction, in degrees, excluding units
     */
    public String getWindDirection()
    {
    	return windDirection;
    }
    
    /**
     * This method returns a String representation of the wind speed excluding units (mph)
     * @return The wind speed, in mph, excluding units
     */
    public String getWindSpeed()
    {
    	return windSpeed;
    }
    
    /**
     * This method returns a String representation of the visibility excluding units (mi)
     * @return The visibility, in miles, excluding units
     */
    public String getVisibility()
    {
    	return visibility;
    }
    
    /**
     * This method returns a String representation of the atmospheric pressure excluding units (in)
     * @return The atmospheric pressure, in inches, excluding units
     */
    public String getPressure()
    {
    	return pressure;
    }
    
    /**
     * This method returns the city name
     * @return The city name
     */
    public String getCity()
    {
    	return city;
    }
    
    /**
     * This method returns the county name
     * @return The county name, or "" if none
     */
    public String getCounty()
    {
    	return county;
    }
    
    /**
     * This method returns the state/province two-letter abbreviation
     * @return The state/province two-letter abbreviation
     */
    public String getState()
    {
    	return state;
    }
    
    /**
     * This method returns the name of the county
     * @return The name of the country
     */
    public String getCountry()
    {
    	return country;
    }
    
    /**
     * This method returns a String representation of the current conditions (ie "Cloudy")
     * @return The current conditions
     */
    public String getCurrentConditions()
    {
    	return currentConditions;
    }
    
    /**
     * This method returns an integer representation of the current conditions. More info can be found at http://developer.yahoo.com/weather/
     * @return The weather code
     */
    public int getWeatherCode()
    {
    	return weatherCode;
    }
    
    /**
     * This method returns the current local time as a Time object
     * @return The current local time
     */
    @SuppressWarnings("deprecation")
	public Time getLocalTime()
    {
    	System.out.println("Local time: " + localTime);
    	try{return new Time(Integer.parseInt(localTime.substring(0, localTime.indexOf(":"))), Integer.parseInt(localTime.substring(localTime.indexOf(":") + 1)), 0);}
    	catch(Exception e){System.out.println("Whoops!"); return new Time(0);}
    }
    
    /**
     * This method returns the local sunrise time as a Time object
     * @return The local sunrise time
     */
    @SuppressWarnings("deprecation")
	public Time getSunrise()
    {
    	try{return new Time(Integer.parseInt(sunrise.substring(0, sunrise.indexOf(":"))), Integer.parseInt(sunrise.substring(sunrise.indexOf(":") + 1, sunrise.indexOf(" "))), 0);}
    	catch(Exception e){return new Time(0);}
    }
    
    /**
     * This method returns the local sunset time as a Time object
     * @return The local sunset time
     */
    @SuppressWarnings("deprecation")
	public Time getSunset()
    {
    	try{return new Time(Integer.parseInt(sunset.substring(0, sunset.indexOf(":"))) + 12, Integer.parseInt(sunset.substring(sunset.indexOf(":") + 1, sunset.indexOf(" "))), 0);}
    	catch(Exception e){return new Time(0);}
    }
}