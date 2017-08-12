import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * This class translates a user-input location into a WOEID that can be used to locate weather information online.  This class also obtains important county information.
 * @author Locomotion15
 *
 */
public class WOEIDLookup
{

	private String woeid = "N/A", county = "";
	
	/**
	 * To retrieve the WOEID, a query for a location is required.
	 * @param query A location for which to retrieve the WOEID
	 */
	public WOEIDLookup(String query)
	{
		try{
			System.out.println("Looking up WOEID...");//Uses an online search engine to look up the WOEID that is used to identify weather stations
			URI woeidURI = new URI("http", "woeid.rosselliot.co.nz", "/lookup/" + query + "/", "");
			URL woeidFull = woeidURI.toURL();
        
			System.out.println(woeidFull);
			
			BufferedReader in;
			String inputLine;
        
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
        
			while ((inputLine = in.readLine()) != null)
			{
				if(inputLine.indexOf("data-district_county=") != -1)//reads for the county name which is used later to narrow down alerts
				{
					int slot = inputLine.indexOf("\"", inputLine.indexOf("data-district_county="));
					String temp = inputLine.substring(slot + 1, inputLine.indexOf("\"", slot + 1));
					county = temp;
				}
				if(inputLine.indexOf("data-woeid=") != -1)//reads for the WOEID
				{
					woeid = parseIntAfter(inputLine, "data-woeid=");
				}
			}
			in.close();
			System.out.println("WOEID: " + woeid);
		}catch(Exception e){return;}
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
	 * This method returns a string representation of the WOEID
	 * @return The WOEID
	 */
	public String getWOEID()
	{
		return woeid;
	}
	
	/**
	* Based on the formatting of XML, this file parses an integer contained between quotation marks (eg "\"1\"") after a given String (eg "theLoneliestNumber=")
	* @param line The line that contains the Strings of the search item and the integer (eg "<info theLoneliestNumber="1"/>)
	* @param search  The item to search for just before the integer (eg "theLoneliestNumber=")
	* @return The integer (eg 1)
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
}
