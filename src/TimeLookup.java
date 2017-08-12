import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.sql.Time;

/**
 * TimeLookup uses a live online database to lookup the local time, given the latitude and longitude of a location.
 * @author Locomotion15
 *
 */
public class TimeLookup
{
	
	private String time = "N/A";
	
	/**
	 * To retrieve the time, both the latitude and longitude of the location are needed.
	 * @param latitude A String representing the latitude
	 * @param longitude A String representing the longitude
	 */
	public TimeLookup(String latitude, String longitude)
	{
		try{
			BufferedReader in;
			String inputLine;
        
			URI timeURI = new URI("http", "//ws.geonames.org/timezone?lat=" + latitude + "&lng=" + longitude + "&username=Locomotion15", "");
			URL timeURL = timeURI.toURL();
        
			try
			{
				in = new BufferedReader(
						new InputStreamReader(
								timeURL.openStream()));
			}
			catch(Exception e)
			{
				System.out.println("Houston, we have a problem.");
				throw new Exception();
			}
        
			while ((inputLine = in.readLine()) != null)
				if(inputLine.indexOf("<time>") != -1)
				{
					time = inputLine.substring(inputLine.lastIndexOf("<") - 5, inputLine.lastIndexOf("<"));
					break;
				}
		}catch(Exception e){return;}
	}
	
	/**
	 * This method returns the current time as a Time object
	 * @return The current time
	 */
	@SuppressWarnings("deprecation")
	public Time getTime()
	{
		System.out.println(time);
		try{return new Time(Integer.parseInt(time.substring(0, time.indexOf(":"))), Integer.parseInt(time.substring(time.indexOf(":") + 1)), 0);}
    	catch(Exception e){System.out.println("Whoops!!"); return new Time(0);}
	}
}
