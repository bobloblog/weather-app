import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * This class should be called to start the Weather program.  It sets up the frame and checks for updates.
 * @author Locomotion15
 * 
 */


public class Setup
{
	private static final String NEWLINE = System.getProperty("line.separator"), fileSeparator = System.getProperty("file.separator");//cross-platform compatibility
	
	private static JFrame f;
	private static int version = 0;
	private static boolean soundMuted = false;
	private static String lastSearch = null;
	
	
	/**
	 * The main method will start the Weather program.  This program does not process any arguments.
	 * @param args None will be processed.
	 */
	public static void main(String[] args)
	{		
		new TestDriver();
		
		SwingUtilities.invokeLater(new Runnable()//Standard for Swing programs
	        {
	            public void run()
	            {
	                createAndShowGUI(); 
	            }
	        });
	}
	
	 private static void createAndShowGUI()
	 {
		new Thread(new Runnable(){public void run()//Creates a thread to decrease latency
		{
			try{update();}
			catch(Exception e){nonFatalError("Unable to fetch update information.\nErrLn: Setup 51\nDesc: General issue");}
		}}).start();
		 
		 f = new JFrame("Weather");
		 f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}//Java look-and-feel sucks
	     catch (Exception e) {}//Non-fatal with no ill-effects. No need to bother the user.
		 f.setSize(600, 335);//600x335
		 f.setResizable(false);
	     f.setLocationRelativeTo(null);//Centers it
	     f.setIconImage(Toolkit.getDefaultToolkit().getImage("resources\\images\\icon.png"));
	     f.setVisible(true);
	     
	     if(lastSearch == null)
	    	 new WindowManager(f, version);//See WindowManager
	     else
	    	 new WindowManager(f, version, soundMuted, lastSearch);//See WindowManager
	 }
	 
	 private static void update() throws Exception
	 {
	     try//Finds the version of the running program stored in the Resources/Data.txt file
		 {
	    	 BufferedReader in;
		     in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("resources\\Data.txt"))));
		     version = Integer.parseInt(in.readLine());
		     soundMuted = Boolean.parseBoolean(in.readLine());
		     lastSearch = in.readLine();
		     in.close();
		 }catch(Exception e){nonFatalError("Unable to read version information.\nErrLn: Setup 80\nDesc: Error reading \"Data.txt\"");}
			
		try//Compares the running version number to the update version number stored online
		{
			URL tempUpdateAddress = new URL("https://sites.google.com/site/locomotion15/update.txt");//Forever where the update instructions will be located
			BufferedReader in;
			
			in = new BufferedReader(
				new InputStreamReader(
					tempUpdateAddress.openStream()));
	              
			if(version >= Integer.parseInt(in.readLine()))//The first line is ALWAYS the version number. Closes here if up to date.
			{
				in.close();
				return;
			}
	        
			in.close();
		}
		catch(Exception e){nonFatalError("Unable to fetch version information.\nErrLn: Setup 99\nDesc: Error connecting to \"update.txt\"");}
		
	    if(JOptionPane.showConfirmDialog(f, "An update is available. Would you like to download and install it now?", "Update Available", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != 0)
	       	return;
	    
	    try{Runtime.getRuntime().exec(" java -jar " + "Update.jar " + version + ""); System.exit(0);}//Runs the Update program (See WeatherUpdate project).
	    catch(Exception e){nonFatalError("The update was unable to initiate.\nErrLn: Setup 105\nDesc: Error executing \"Update.jar\"");}
	    
	 }
	 
	 private static void nonFatalError(String errorMessage)
	 {
		 JOptionPane.showMessageDialog(f, errorMessage, "Non-fatal Error", JOptionPane.ERROR_MESSAGE);
	 }
	
	 private static void fatalError(String errorMessage)
	 {
		 JOptionPane.showMessageDialog(f, errorMessage + "\nThe program will now terminante.", "Fatal Error!", JOptionPane.ERROR_MESSAGE);
		 gracefulClose();
	 }
		
	 private static void gracefulClose()
	 {
		 System.out.println("Closing now...");
		
		 File file = new File("resources\\data.txt");
			
		 try//Writes the info file
		 {
			 PrintWriter writer = new PrintWriter(new FileWriter(file, false));
				
			 writer.write(version + NEWLINE);
			 writer.write(soundMuted + NEWLINE);
			 writer.write(lastSearch);
				
			 writer.close();
		 }catch(Exception e){}
			
		 f.dispose();
		 System.exit(0); //calling the method is a must
	 }
	 
}
