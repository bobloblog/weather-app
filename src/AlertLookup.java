import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * This class fetches alert information from the Nation Weather Service's XML database given the state abbreviation and county FIPS code.
 * @author Locomotion15
 *@date 22.11.2013
 *
 */

public class AlertLookup
{
	 private String state = "", fips = "";
	    private boolean full = true;//I can't remember or figure out what this does, but if you change it it doesn't work so...
	    private ArrayList<URL> alertPages;//The URLs connecting to all of the XML pages of the alerts
	    private ArrayList<AlertObject> alerts;
	    private int stateAlerts = -1;//This is simply a tally of the total state alerts, used for troubleshooting
	    
	    /**
	     * Fetches alert information given the state abbreviation and county FIPS code
	     * @param nState The two-letter state abbreviation
	     * @param nfips The five or six digit FIPS code, or geolocation for the county
	     */
	    public AlertLookup(String nState, String nfips)
	    {
	        alertPages = new ArrayList<URL>();
	        alerts = new ArrayList<AlertObject>();
	        state = nState;
	        fips = nfips;
	        try{main();}
	        catch(Exception e){return;}
	    }
	    
	    /**
	     * I really don't know what the point of this is but whatever.
	     * Moral of the story? Comment your code as you do it.
	     */
	    public AlertLookup()
	    {
	        full = false;
	        alerts = new ArrayList<AlertObject>();
	    }
	    
	    /**
	     * This method acts as the driver for the AlertFiner class
	     * @throws Exception If anything goes wrong. I need to update this.
	     */
	    public void main() throws Exception
	    {
	        System.out.println("Fetching alerts from the National Weather Service...");
	        
	        URL alertsURL = new URL("http://alerts.weather.gov/cap/");
	        URL alertsFull = new URL(alertsURL, state.toLowerCase() + ".php?x=1");
	        
	        System.out.println(alertsFull);
	        
	        try{getLinks(alertsFull);}
	        catch(Exception e){System.out.println("Unable to connect"); return;}
	        
	        for(int i = 0; i < alertPages.size(); i++)
	        {
	            try{findAlertInformation(alertPages.get(i));}
	            catch(Exception e){}
	        }
	        
	        System.out.println("State alerts: " + stateAlerts);
	        System.out.println("Local alerts: " + alerts.size());
	        
	    }
	    
	    /**
	     * This method finds all the links to each individual XML weather alert
	     * @param link The link to the state's page for weather alerts
	     * @throws Exception
	     */
	    public void getLinks(URL link) throws Exception
	    {
	        BufferedReader in;
	        String inputLine;
	        
	        try
	        {
	            in = new BufferedReader(
	            new InputStreamReader(
	            link.openStream()));
	            System.out.println("Connection Successful.");
	        }
	        catch(Exception e)
	        {
	            System.out.println("Could not connect to alerts.");
	            return;
	        }
	        
	        boolean firstInstance = true;
	        while ((inputLine = in.readLine()) != null)
	        {
	            if(inputLine.indexOf("<link href=") != -1)
	            {
	                int slot = inputLine.indexOf("\"", inputLine.indexOf("<link href="));
	                String temp = "";
	                try{temp = inputLine.substring(slot + 1, inputLine.indexOf("\"", slot + 1));}
	                catch(Exception e){}
	                if(!firstInstance)//The first link actually links to something that isn't an alert so, this ensures it is skipped
	                    alertPages.add(new URL(temp));//I have no idea what this does, but alerts don't work if it's gone
	                firstInstance = false;
	                stateAlerts++;
	            }
	            if(inputLine.indexOf("<value>0") != -1 && !inputLine.contains(fips) && alertPages.size() > 0)//This checks to see if the alert is for the specified FIPS
	            {
	            	System.out.println("Alert URL Removed: " + alertPages.get(alertPages.size() - 1));
	            	alertPages.remove(alertPages.size() - 1);//removes the link if it does not concern the specified FIPS
	            }
	        }
	    }
	    
	    /**
	     * This method scrapes the alert data off of the alert's XML
	     * @param link The link to an XML page for an alert
	     * @throws Exception If anything goes wrong. Update required
	     */
	    public void findAlertInformation(URL link) throws Exception
	    {
	        BufferedReader in;
	        String inputLine, headline = "N/A", event = "N/A", urgency = "N/A", severity = "N/A", certainty = "N/A", description = "N/A", instructions = "N/A";
	        //For more information on headline, event, urgency, severity, certainty, description, and instructions, please see the AlertObject class
	        
	        try
	        {
	            in = new BufferedReader(
	            new InputStreamReader(
	            link.openStream()));
	        }
	        catch(Exception e)
	        {
	            System.out.println("Could not connect to alerts.");
	            return;
	        }
	        
	        boolean inDescription = false, inInstructions = false;
	        //These booleans are used to properly parse items that are contained on more that one input line
	        while ((inputLine = in.readLine()) != null)
	        {
	            if(inputLine.indexOf("<headline>") != -1)
	            {
	                int slot = inputLine.indexOf(">");//Because it goes <headline>PBS Weatherman Predicts Learning With 90 Percent Chance of Wonderstorms</headline>
	                headline = inputLine.substring(slot + 1, inputLine.indexOf("</", slot - 2));//Courtesy to The Onion for that headline
	            }
	            else if(inputLine.indexOf("<event>") != -1)
	            {
	                int slot = inputLine.indexOf(">");
	                event = inputLine.substring(slot + 1, inputLine.indexOf("</", slot - 2));
	            }
	            else if(inputLine.indexOf("<urgency>") != -1)
	            {
	                int slot = inputLine.indexOf(">");
	                urgency = inputLine.substring(slot + 1, inputLine.indexOf("</", slot - 2));
	            }
	            else if(inputLine.indexOf("<severity>") != -1)
	            {
	                int slot = inputLine.indexOf(">");
	                severity = inputLine.substring(slot + 1, inputLine.indexOf("</", slot - 2));
	            }
	            else if(inputLine.indexOf("<certainty>") != -1)
	            {
	                int slot = inputLine.indexOf(">");
	                certainty = inputLine.substring(slot + 1, inputLine.indexOf("</", slot - 2));
	            }
	            else if(inputLine.indexOf("<description>") != -1)
	            {
	                int slot = inputLine.indexOf(">");
	                description = inputLine.substring(slot + 1);
	                inDescription = true;
	            }
	            else if(inDescription)
	            {
	                if(inputLine.indexOf("</description>") != -1)
	                {
	                    description = description + " " + inputLine.substring(0, inputLine.indexOf("</"));
	                    inDescription = false;
	                }
	                else
	                    description = description + " " + inputLine;
	            }
	            else if(inputLine.indexOf("<instruction>") != -1)
	            {
	                int slot = inputLine.indexOf(">");
	                try{instructions = inputLine.substring(slot + 1, inputLine.indexOf("</"));}
	                catch(Exception e)
	                {
	                    instructions = inputLine.substring(slot + 1);
	                    inInstructions = true;
	                }
	            }
	            else if(inInstructions)
	            {
	                if(inputLine.indexOf("</instruction>") != -1)
	                {
	                    instructions = instructions + " " + inputLine.substring(0, inputLine.indexOf("</"));
	                    inInstructions = false;
	                    break;
	                }
	                else
	                    instructions = instructions + " " + inputLine;
	            }
	        }
	        
	        in.close();

	       	alerts.add(new AlertObject(headline, event, urgency, severity, certainty, description, instructions));
	    }
	    
	    
	    /**
	     * This returns an ArrayList of Alert objects representing all of the alerts that apply to the county or state
	     * @return An ArrayList of Alert objects representing all of the alerts that apply to the county or state
	     */
	    public ArrayList<AlertObject> getAlerts()
	    {
	        return alerts;
	    }
	    
	    /**
	     * Who knows?
	     * @return IDK
	     */
	    public boolean full()
	    {
	        return full;
	    }
	}