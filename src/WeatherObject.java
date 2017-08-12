import java.util.ArrayList;


public class WeatherObject
{
	
	private String temperature = "N/A", humidity = "N/A", windDirection = "N/A", windSpeed = "N/A", visibility = "N/A", pressure = "N/A", trend = ""; //trend being pressure trend (0 steady, 1 rising, 2 falling)
    private String city = "N/A", county = "N/A", state = "N/A", country = "N/A", currentConditions = "N/A", sunrise = "N/A", sunset = "N/A";
    private String lat = "", lng = "", woeid = "N/A", fips="N/A", localTime = "N/A";
    private int weatherCode = -1;
    private ArrayList<AlertObject> alerts;
	
	public WeatherObject()
	{
		
	}
	
	public void setWOEID(String nWOEID)
	{
		woeid = nWOEID;
	}
	
	public void setTemperature(String nTemperature)
	{
		temperature = nTemperature;
	}
	
	public void setHumidity(String nHumidity)
	{
		humidity = nHumidity;
	}
	
	public void setWindDirection(String nWindDirection)
	{
		windDirection = nWindDirection;
	}
	
	public void setWindSpeed(String nWindSpeed)
	{
		windSpeed = nWindSpeed;
	}
	
	public void setVisibility(String nVisibility)
	{
		visibility = nVisibility;
	}
	
	public void setPressure(String nPressure)
	{
		pressure = nPressure;
	}
	
	public void setTrend(String nTrend)
	{
		trend = nTrend;
	}
	
	public void setCity(String nCity)	
	{
		city = nCity;
	}
	
	public void setCounty(String nCounty)
	{
		county = nCounty;
	}
	
	public void setState(String nState)
	{
		state = nState;
	}
	
	public void setCountry(String nCountry)
	{
		country = nCountry;
	}
	
	public void setCurrentConditions(String nCurrentConditions)
	{
		currentConditions = nCurrentConditions;
	}
	
	public void setSunrise(String nSunrise)
	{
		sunrise = nSunrise;
	}
	
	public void setSunset(String nSunset)
	{
		sunset = nSunset;
	}
	
	public void setLocalTime(String nLocalTime)
	{
		localTime = nLocalTime;
	}
	
	public void setLat(String nLat)
	{
		lat = nLat;
	}
	
	public void setLng(String nLng)
	{
		lng = nLng;
	}
	
	public void setFIPS(String nFIPS)
	{
		fips = nFIPS;
	}
	
	public void setWeatherCode(int nWeatherCode)
	{
		weatherCode = nWeatherCode;
	}
	
	public void setAlerts(ArrayList<AlertObject> nAlerts)
	{
		alerts = nAlerts;
	}
	
	//SWITCH BETWEEN SET AND GET. PLACED FOR READABILITY.	
	
	public String getWOEID()
	{
		return woeid;
	}
	
	public String getTemperature()
	{
		return temperature;
	}
	
	public String getHumidity()
	{
		return humidity;
	}
	
	public String getWindDirection()
	{
		return windDirection;
	}
	
	public String getWindSpeed()
	{
		return windSpeed;
	}
	
	public String getVisibility()
	{
		return visibility;
	}
	
	public String getPressure()
	{
		return pressure;
	}
	
	public String getTrend()
	{
		return trend;
	}
	
	public String getCity()	
	{
		return city;
	}
	
	public String getCounty()
	{
		return county;
	}
	
	public String getState()
	{
		return state;
	}
	
	public String getCountry()
	{
		return country;
	}
	
	public String getCurrentConditions()
	{
		return currentConditions;
	}
	
	public String getSunrise()
	{
		return sunrise;
	}
	
	public String getSunset()
	{
		return sunset;
	}
	
	public String getLocalTime()
	{
		return localTime;
	}
	
	public String getLat()
	{
		return lat;
	}
	
	public String getLng()
	{
		return lng;
	}
	
	public String getFIPS()
	{
		return fips;
	}
	
	public int getWeatherCode()
	{
		return weatherCode;
	}
	
	public ArrayList<AlertObject> getAlerts()
	{
		return alerts;
	}
	
	 public String toString()
	    {
		 	String temp = "";
	    	temp += "Current weather for " + city + (((state.equals("")))?", ":", " + state.toUpperCase() + " - ") + country + " WOEID: " + woeid + "\n\n";
	        temp += "Current Conditions: " + currentConditions + "\n";
	        temp += "Temperature: " + temperature + (temperature.equals("N/A")?"\n":"°F\n");
	        temp += "Humidity: " + humidity + (humidity.equals("N/A")?"\n":"%\n");
	        
	        try{temp += "Wind: " + findDirection(Integer.parseInt(windDirection)) + " at " + windSpeed + (windSpeed.equals("N/A")?"\n":"mph\n");}
	        catch(Exception e){temp += "Wind: " + "N/A" + " at " + windSpeed + (windSpeed.equals("N/A")?"\n":"mph\n");}
	        
	        if(visibility.equals(""))//Corrects possible error with Yahoo! API
	        	visibility = "N/A";
	        temp += "Visibility: " + visibility + (visibility.equals("N/A")?"\n":" miles\n");
	        
	        if(trend.compareTo("0") == 0)//converts the pressure data into the arrow that correctly represents the trend
	        	trend = "→";
	        else if(trend.compareTo("1") == 0)
	        	trend = "↑";
	        else if(trend.compareTo("2") == 0)
	        	trend = "↓";
	        else
	        	trend = "";
	        temp += "Pressure: " + pressure + (pressure.equals("N/A")?"\n":(" in " + trend + "\n"));        	
	        temp += "Sunrise: " + sunrise + "  Sunset: " + sunset;
	        
	        return temp;
	    }
	    
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
}
