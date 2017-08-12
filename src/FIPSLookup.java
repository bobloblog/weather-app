import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FIPSLookup uses a National Weather Service database to look up a geolocation, or FIPS code based on a given state and county.
 * FIPS codes are used to identify which counties are affected by an alert.
 * @author Locomotion15
 *
 */
public class FIPSLookup
{
    public String stateAbr, countyName, fipsCode;
    
    /**
     * To retrieve a FIPS code, both the state abbreviation and the county name are required
     * @param state The two letter abbreviation of the state
     * @param county The name of the county
     */
    public FIPSLookup(String state, String county)
    {
        stateAbr= state;
        countyName = county;
        fipsCode = "N/A";
        try{main();}catch(Exception e){System.out.println("failed " + e);return;}
    }

    /**
     * This main method looks up the FIPS code. ***Eventually, I need to re-think the error checking in here.
     * @throws Exception
     */
    public void main() throws Exception
    {
        System.out.println("Looking up FIPS for " + countyName + ", " + stateAbr + "...");

        //URL link = new URL("http", "www.nws.noaa.gov", "/nwr/CntyCov/");//Creates the beautifully dynamic URL
      //String file = "nwr" + stateAbr.toUpperCase() + ".htm";
        
        URL link = new URL("http", "www.nws.noaa.gov", "/nwr/coverage/");//Creates the beautifully dynamic URL
        String file = "ccov.php?State=" + stateAbr.toUpperCase();
        link = new URL(link, file);
  
        System.out.println(link);
        
        BufferedReader in;
        try{
            in = new BufferedReader(
            new InputStreamReader(
            link.openStream()));
            System.out.println("Connection Successful.");
        }
        catch(Exception e){System.out.println("Could not find geolocation"); return;}

        String inputLine;

        while ((inputLine = in.readLine()) != null)
        {
            if(inputLine.contains("</td>") && inputLine.contains(countyName))
            {
                //inputLine = in.readLine();
                //int slot = inputLine.indexOf("</");//Because it goes <>12345</>
                //String temp = inputLine.substring(slot - 6, slot);//Because the FIPS is 5 digits, and "<" takes another character spot
                //fipsCode = "" + (Integer.parseInt(temp));
            	
            	Pattern fipsPattern = Pattern.compile("\\d\\d\\d\\d\\d\\d");//Pattern: 6 digits
            	
            	Matcher m = fipsPattern.matcher(inputLine);
            	
            	while(m.find())
            		fipsCode = m.group();
            	
                break;
            }
            
        }

        in.close();
        
        System.out.println("FIPS: " + fipsCode);
    }
    
    /**
     * This method returns a String representation of the FIPS code
     * @return The county code
     */
    public String getFIPS()
    {
        return fipsCode;
    }
}