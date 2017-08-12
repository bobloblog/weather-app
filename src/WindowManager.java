import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * This class manages the GUI.  Because of the nature of the GUI, this class also acts as the driver of the program.
 * @author Locomotion15
 *
 */

@SuppressWarnings("serial")
public class WindowManager extends JPanel implements ActionListener//JPanel: means that THIS class is a panel. Easy for Swing. ActionListener: Allows this object to listen for events
{
	private final String NEWLINE = System.getProperty("line.separator"), FILE_SEPARATOR = System.getProperty("file.separator");//cross-platform compatibility
	
	private JFrame f;
	private JTextField search;
	private JTextArea information, blankSide, blankBottom;//Where all the info is displayed; a filler
	private JButton button, mute;
	private JComboBox alerts;
	private JProgressBar progress;
	private ArrayList<AlertObject> alertArray;//This array holds all of the current Alert objects for the given location.
	private SoundManager sound;//See SoundManager
	private ImageManager images;//See ImageManager
	private Weather weather;//See Weather. This is global so that weather info can be passed around without having to reload it.
	private int timeOfDay = 0; //0 = day, 1 = night, 2 = morning, 3 = evening
	private int ver = 0; //version
	private final long THIRTY_MINUTES = 1800000;
	private boolean soundMuted = false;
	private String mostRecentSearch = "";
	
	public WindowManager(JFrame frame, int version, boolean soundOff, String lastSearch)
	{
		this(frame, version);
		soundMuted = soundOff;
		if(soundMuted)
			mute.setText("Unmute");
		search.setText(lastSearch);
		updateWeather();
	}
	/**
	 * The only available constructor for WindowManager, this creates all of the GUI elements, adds them to the screen, and prepares their actionListeners.
	 * No further action is required after calling this to prepare the program for use.
	 * @param frame A frame is required for WindowManager to operate. This frame must be set to visible.
	 */
	public WindowManager(JFrame frame, int version)
	{
		super(new FlowLayout());//Layouts are scary, so I stuck with an easy one.
		
		sound = new SoundManager();
		images = new ImageManager();
		
		frame.add(this);
		f = frame;   
		
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				gracefulClose();
			}
		});
		
		ver = version;
		
		search = new JTextField(35);//56 was old measurement. Changed b/c font color on background
		search.addActionListener(this);
		search.setActionCommand("search");//Fires when enter is pressed. Same action as the button performs. Nifty.
		search.setOpaque(true);	
		search.addMouseListener(new MouseAdapter(){//This adds a mouse listener to clear the search bar if it is clicked.
			@Override
			public void mouseClicked(MouseEvent e){//The actual mouse event
				search.selectAll();
				//search.setText("");//Sets the text to nothing
			}
		});
		add(search);
		
		button = new JButton("Search");
		button.addActionListener(this);
        button.setActionCommand("search");
        button.setOpaque(false);//Makes the border transparent
		add(button);
		
		mute = new JButton("Mute");
		mute.addActionListener(this);
        mute.setActionCommand("mute");
        mute.setOpaque(false);//Makes the border transparent
		add(mute);
		
		blankSide = new JTextArea(3, 15);//This is a filler, for appearance's sake. Because flow layout doesn't allow this to be accomplished any other way.
		blankSide.setEditable(false);
        blankSide.setOpaque(false);
        add(blankSide);
		
		information = new JTextArea(10, 45);
		information.setWrapStyleWord(true);//Wraps words
		information.setLineWrap(true);//Wraps, but by letter, not word. See above. Idk.
        information.setEditable(false);
        information.setOpaque(false);
        information.setForeground(Color.BLACK);//Also known as font color
        information.setText("Please type a location in the box above and press \"Search.\"" +
        		" You can view more information about warnings by selecting them from the drop-down menu below this text box." +
        		" Weather information is provided by Yahoo! Weather and the National Weather Service.");
        //Font font = new Font("Arial", Font.BOLD, 12);
        //information.setFont(font);
        
        JScrollPane temp = new JScrollPane(information);//Creates a scroll pane for alerts
        temp.setOpaque(false);
        temp.getViewport().setOpaque(false);//makes scroll items transparent
        temp.setBorder(null);//Gets rid of the ugly border
		add(temp);
		
		blankBottom = new JTextArea(1, 70);//This is a filler, for appearance's sake. Because flow layout doesn't allow this to be accomplished any other way.
		blankBottom.setEditable(false);
        blankBottom.setOpaque(false);
        add(blankBottom);
		
		progress = new JProgressBar();//The progress bar that displays when loading
		progress.setIndeterminate(true);//This means that progress is not monitored, it simply has that continuous loading thingy
		progress.setOpaque(false);
		progress.setVisible(false);
		add(progress);
		
		Object[] tempMess = {"No active alerts"};//The drop-down menu for alerts.
		alerts = new JComboBox(tempMess);
		alerts.addActionListener(this);
		alerts.setActionCommand("alerts");
		alerts.setEnabled(false);
		alerts.setOpaque(false);
		alerts.setVisible(false);
		add(alerts);
		
		repaint();//Make sure it's beautiful!
	}
	
	/**
	 * This private method sets the global timeOfDay int to represent whether it is morning, day, evening, or night
	 * @param current The current time in milliseconds after midnight
	 * @param rise The time of sunrise in milliseconds after midnight
	 * @param set The time fo sunset in milliseconds after midnight
	 */
	private void timeOfDay(long current, long rise, long set)
	{
		if(current > rise + THIRTY_MINUTES && current < set - THIRTY_MINUTES)//30 minutes or more after sunrise AND 30 minutes or more before sunset. Day.
		{
			information.setForeground(Color.BLACK);
			timeOfDay = 0;
		}
		else if(current < rise - THIRTY_MINUTES || current > set + THIRTY_MINUTES)//30 minutes or more before sunrise OR 30 minutes or more after sunset. Night.
		{
			information.setForeground(Color.LIGHT_GRAY);
			timeOfDay = 1;
		}
		else if(current > rise - THIRTY_MINUTES && current < rise + THIRTY_MINUTES)//Within 30 minutes on either side of sunrise. Morning.
		{
			information.setForeground(Color.LIGHT_GRAY);
			timeOfDay = 2;
		}
		else//Within 30 minutes on either side of sunset. Evening.
		{
			information.setForeground(Color.BLACK);
			timeOfDay = 3;
		}		
	}
	
	/**
	 * This method triggers all of the weather fetching, and then displays it on the screen
	 */
	public void updateWeather()
	{							
		weather = new Weather(search.getText());//See Weather		
		
		timeOfDay(weather.getLocalTime().getTime(), weather.getSunrise().getTime(), weather.getSunset().getTime());
		information.setText(weather.getWeatherString());
		information.setCaretPosition(0);//Scrolls to the top
		
		images.findImages(weather.getWeatherCode(), timeOfDay);//MUST follow the call of timeOfDay method
		if(!soundMuted)
			sound.findSound(weather.getWeatherCode(), timeOfDay);//MUST follow the call of timeOfDay method
		
		remove(alerts);//Hides the current alerts
		
		alertArray = weather.getAlerts();
		if(alertArray.size() > 0)//Sets up alerts drop-down menu and displays
		{
			Object[] alertMess = new Object[alertArray.size() + 1];
			
			for(int i = 0; i < alertMess.length - 1; i++)
				alertMess[i] = alertArray.get(i).getBriefHeadline();
			
			alertMess[alertMess.length - 1] = "Basic Weather Info";
			
			alerts = new JComboBox(alertMess);
			alerts.setEnabled(true);
			alerts.setVisible(true);
		}
		else//Hides if no alerts
		{
			Object[] alertMess = {"No active alerts"};
			alerts = new JComboBox(alertMess);	
			alerts.setEnabled(false);
			alerts.setVisible(false);
		}
		
		alerts.addActionListener(this);
		alerts.setActionCommand("alerts");
		add(alerts);
		
		f.setTitle("Weather for " + weather.getCity() +
				(!(weather.getState().equals(""))?", " + weather.getState():" ") + 			//Displays state if available
				((alertArray.size() > 0)?" -- " + alertArray.size() + " active alert" +		//Displays the number of alerts if any
						((alertArray.size() != 1)?"s":"")									//Nested ternary operator adds "s" to "alerts" if necessary
				:""));																		//Leaves alerts blank if none
	}
	
	
	private void displayWeatherInfo()
	{
		information.setText(weather.getWeatherString());
		information.setCaretPosition(0);//Scrolls to the top
		f.setTitle("Weather for " + weather.getCity() +
				(!(weather.getState().equals(""))?", " + weather.getState():" ") + 			//Displays state if available
				((alertArray.size() > 0)?" -- " + alertArray.size() + " active alert" +		//Displays the number of alerts if any
						((alertArray.size() != 1)?"s":"")									//Nested ternary operator adds "s" to "alerts" if necessary
				:""));																		//Leaves alerts blank if none
	}
	/**
	 * This private method displays information on the alert selected by the user.
	 * @param index The array index of the alert.
	 */
	private void displayAlertInfo(int index)
	{
		 if(index < alertArray.size())
		 {
			 information.setText(alertArray.get(index).allToString());
			 information.setCaretPosition(0);//Scrolls to the top
			 
			 f.setTitle("Weather for " + weather.getCity() + (!(weather.getState().equals(""))?", " + weather.getState():" ") + " -- " + alertArray.get(index).getEvent());//Ternary operators are fun
		 }
	}
	
	/**
	 * This method is called when any action is fired
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().compareTo("search") == 0)//Search bar or search button
		{
			progress.setVisible(true);//Displays the progress bar for all to see
			System.out.println("============\nQuery: " + search.getText());

			new SwingWorker<Void, Void>()//This allows Swing components to update while weather info is being fetched (*ehem* JProgressBar)
			{
				protected Void doInBackground()
				{
					updateWeather();
					mostRecentSearch = search.getText();
					return null;//required for type Void
				}

				protected void done()
				{
					progress.setVisible(false);
					repaint();
				}
			}.execute();	
		}
		else if(e.getActionCommand().compareTo("mute") == 0)//The mute button was pressed
		{
			if(sound.soundIsRunning())
			{
				sound.stopSound();
				soundMuted = true;
				mute.setText("Unmute");
			}
			else
			{
				sound.findSound(weather.getWeatherCode(), timeOfDay);
				soundMuted = false;
				mute.setText("Mute");
			}
		}
		else if(e.getActionCommand().compareTo("alerts") == 0)//An alert was selected
		{
			if(alerts.getSelectedIndex() == alerts.getItemCount() - 1)
				displayWeatherInfo();
			else
				displayAlertInfo(alerts.getSelectedIndex());
		}
	}
	
	@Override
    public void paintComponent(Graphics g)//Standard for swing
	{
		images.paintComponent(g);//See ImageManager
	}
	
	private void nonFatalError(String errorMessage)
	{
		JOptionPane.showMessageDialog(this, errorMessage, "Error!", JOptionPane.ERROR_MESSAGE);
	}
	
	private void fatalError(String errorMessage)
	{
		JOptionPane.showMessageDialog(this, errorMessage, "Fatal Error!", JOptionPane.ERROR_MESSAGE);
		gracefulClose();
	}
	
	private void gracefulClose()
	{
		System.out.println("Closing now...");
		
		File file = new File("resources\\data.txt");
		
		try//Writes the info file
		{
			PrintWriter writer = new PrintWriter(new FileWriter(file, false));
			
			writer.write(ver + NEWLINE);
			writer.write(soundMuted + NEWLINE);
			writer.write(mostRecentSearch);
			
			writer.close();
		}catch(Exception e){System.out.println("Close failed" + e);}
		
		f.dispose();
		System.exit(0); //calling the method is a must
	}
}
