
/**
 * This object is used for data storage.  It simply stores all information contained in a weather alert.
 * @author Locomotion15
 * @date 22.11.2013
 *
 */
public class AlertObject
{
    private String  head = "N/A", even = "N/A", urg = "N/A", sev = "N/A", cert = "N/A", desc = "N/A", inst = "N/A";  

    public AlertObject(String headline, String event, String urgency, String severity, String certainty, String description, String instructions)
    {
        head = headline;
        even = event;
        urg = urgency;
        sev = severity;
        cert = certainty;
        desc = description;
        inst = instructions;
    }

    /**
     * The headline is a one-line, short interpretation of the weather alert as written by the Nation Weather Service
     * @return One-line interpretation of the alert
     */
    public String getHeadline()
    {
        return head;
    }
    
    /**
     * The brief headline parses the headline, excluding information about who issued the alert but maintaining the beginning and ending time of the alert
     * @return Alert name and timeframe
     */
    public String getBriefHeadline()
    {
    	//return head.substring(0, head.indexOf(" issued")) + ": " + head.substring(head.indexOf("issued ") + 7, head.indexOf(" until")) + " - " + head.substring(head.indexOf("until ") + 6, head.indexOf(" by"));
    	//The above statement parses the string even further, but leaves much room for error. The following statement is more reliable.
    	
    	return head.substring(0, head.indexOf(" by"));
    }
    
    /**
     * Most alerts contain an event type (ie Severe Weather, Amber Alert, etc). For the purposes of this program, all fetched alerts will be some sort of Severe Weather
     * @return The alert's event type
     */
    public String getEvent()
    {
        return even;
    }
    
    /**
     * Most alerts will contain information about the urgency of the event (ie Urgency, Very Urgent, etc)
     * @return The urgency of the alert
     */
    public String getUrgency()
    {
        return urg;
    }
    
    /**
     * Most alerts will classify their level of intensity (ie Severe, Extreme, Catastrophic, etc)
     * @return The level of severity for the alert
     */
    public String getSeverity()
    {
        return sev;
    }
    
    /**
     * Most alerts will contain information about the certainty of the event (ie Certain, Observed, Occuring, Possible, Forecasted, etc)
     * @return The certainty of the event specified in the alert
     */
    public String getCertainty()
    {
        return cert;
    }
    
    /**
     * All alerts have a long description depicting specific details about the event
     * @return The long description of the alert
     */
    public String getDescription()
    {
        return desc;
    }
    
    /**
     * Most alerts will contain a set of specific instructions or actions that should be taken by an individual to protect life and property
     * @return The instructions contained in the alert
     */
    public String getInstructions()
    {
        return inst;
    }
    
    /**
     * Prints all of the information contained in the alert. Use allToString() instead
     */
    public void printAll()
    {
        System.out.println(this);
    }
    
    /**
     * Creates a string that represents all of the information contained in the alert in a clear and readable way
     * @return A string representation of all of the information contained in the alert
     */
    public String allToString()
    {
    	String temp = "";
    	temp += head + "\n";
        temp += "Event: " + even  + "   Urgency: " + urg + "   Severity: " + sev + "   Certainty: " + cert + "\n";
        temp += desc + "\n\n";
        temp += inst + "\n";
        
        return temp;
    }
    
}