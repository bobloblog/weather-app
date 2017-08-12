import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * This class loads and plays the sounds used in the program. WindowManager must pass codes based on the time of day or type of weather to call certain images.
 * The timeOfDay codes are 0 = day, 1 = night, 2 = morning, 3 = evening
 * The weather codes are fetched from Yahoo! weather. The description of each code can be found here: http://developer.yahoo.com/weather/
 * The sound file structure is as follows:
 *  All sounds must be .wav files (unfortunately)
 *  Each sound must have a one (1) second "start" file (This is used to allow the sound to fade in) ex: "rainstart.wav"
 *  Each sound must have a loopable file that is no more than twenty (20) seconds ex: "rain.wav"
 *  Any sounds that do NOT require a "start" file BUT are to be played on a loop should use the SoundManager.playSoundLoopAll(String fileName) method
 *  Any sounds that are not to be played on a loop AND that do not require a "start" file should use the SoundManager.playSound(String fileName) method
 * @author Locomotion15
 *
 */

public class SoundManager extends Thread
{
	private Clip sound;
	
	/**
	 * The default constructor prepares SoundManager to be used, but does not prepare any sound files to be played.
	 */
	public SoundManager()
	{
		
	}
	
	/**
	 * This constructor takes the weather code and time of day as parameters; it prepares SoundManager to be used then loads and plays the sounds that match the codes
	 * @param code The code for current weather conditions
	 * @param timeOfDay The code for the time of day
	 */
	public SoundManager(int code, int timeOfDay)
	{
		findSound(code, timeOfDay);
	}
	
	/**
	 * This methods finds and plays a sound (and stops the previous sound if necessary) based on the codes given
	 * @param code The code for the current weather conditions
	 * @param timeOfDay The code for the time of day
	 */
	public void findSound(int code, int timeOfDay)
	{
		switch(code)
		{
			case 0: playSoundLoop("thunderstorm"); break;
			case 1: playSoundLoop("heavyrain"); break;
			case 2: playSoundLoop("heavyrain"); break;
			case 3: playSoundLoop("thunderstorm"); break;
			case 4: playSoundLoop("thunderstorm"); break;
			case 5: playSoundLoop("lightrain"); break;
			case 6: playSoundLoop("lightrain"); break;
			case 7: playSoundLoop("lightrain"); break;
			case 8: playSoundLoop("lightrain"); break;
			case 9: playSoundLoop("lightrain"); break;
			case 10: playSoundLoop("lightrain"); break;
			case 11: playSoundLoop("heavyrain"); break;
			case 12: playSoundLoop("heavyrain"); break;
			case 13: playSoundLoop("snow"); break;
			case 14: playSoundLoop("snow"); break;
			case 15: playSoundLoop("wind"); break;
			case 16: playSoundLoop("snow"); break;
			case 17: playSoundLoop("hail"); break;
			case 18: playSoundLoop("lightrain"); break;
			case 19: playStandardSound(timeOfDay); break;
			case 20: playSound("crows"); break;
			case 21: playStandardSound(timeOfDay); break;
			case 22: playStandardSound(timeOfDay); break;
			case 23: playSoundLoop("wind"); break;
			case 24: playSoundLoop("wind"); break;
			case 25: playStandardSound(timeOfDay); break;
			case 26: playStandardSound(timeOfDay); break;
			case 27: playStandardSound(timeOfDay); break;
			case 28: playStandardSound(timeOfDay); break;
			case 29: playStandardSound(timeOfDay); break;
			case 30: playStandardSound(timeOfDay); break;
			case 31: playStandardSound(timeOfDay); break;
			case 32: playStandardSound(timeOfDay); break;
			case 33: playStandardSound(timeOfDay); break;
			case 34: playStandardSound(timeOfDay); break;
			case 35: playSoundLoop("hail"); break;
			case 36: playStandardSound(timeOfDay); break;
			case 37: playSoundLoop("thunderstorm"); break;
			case 38: playSoundLoop("thunderstorm"); break;
			case 39: playSoundLoop("thunderstorm"); break;
			case 40: playSoundLoop("heavyrain"); break;
			case 41: playSoundLoop("snow"); break;
			case 42: playSoundLoop("snow"); break;
			case 43: playSoundLoop("snow"); break;
			case 44: playStandardSound(timeOfDay); break;
			case 45: playSoundLoop("thundershower"); break;
			case 46: playSoundLoop("snow"); break;
			case 47: playSoundLoop("thundershower"); break;
		}
	}
	
	/**
	 * This method creates a synchronized thread in which the sound file can be played. In this particular method, the "start" file for each sound is played for 3/4 of a 
	 * second before the full file is played on a loop.
	 * @param fileName The file name of the sound excluding the extension (all must be .WAV)
	 */
	public synchronized void playSoundLoop(final String fileName)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try{sound.stop();}catch(Exception e){}//Stops the previous sound
		
				try
				{
					Clip temp = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("resources\\sounds\\" + fileName + "start.WAV"));
					//Ever wonder why I never have the file extension passed in the argument? It allows me to easily append the name of the file
					temp.open(inputStream);
					temp.start();
					
					sound = AudioSystem.getClip();
					inputStream = AudioSystem.getAudioInputStream(new File("resources\\sounds\\" + fileName + ".WAV"));
					sound.open(inputStream);
					
					sleep(750);//Allows time for the one second "start" file to play, while also avoiding any gap in sound
					sound.loop(Clip.LOOP_CONTINUOUSLY); 
					temp.stop();
					temp.close();
					
				}
				catch (Exception e)//I need to work on error handling throughout
				{
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
	
	/**
	 * This method creates a synchronized thread in which the sound file can be played. In this particular method, the "start" file is ignored and the full file is played
	 * on a continuous loop.
	 * @param fileName The file name of the sound excluding the extension (all must be .WAV)
	 */
	public synchronized void playSoundLoopAll(final String fileName)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try{sound.stop();}catch(Exception e){}
		
				try
				{
					sleep(1000);
					sound = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("resources\\sounds\\" + fileName + ".WAV"));
					sound.open(inputStream);
					sound.loop(Clip.LOOP_CONTINUOUSLY);
				}
				catch (Exception e)
				{
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
	
	/**
	 * This method creates a synchronized thread in which the sound file can be played. In this particular method, the "start" file is ignored and the full file is played
	 * only once.
	 * @param fileName The file name of the sound excluding the extension (all must be .WAV)
	 */
	public synchronized void playSound(final String fileName)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try{sound.stop();}catch(Exception e){}
		
				try
				{
					sleep(1000);
					sound = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("resources\\sounds\\" + fileName + ".WAV"));
					sound.open(inputStream);
					sound.start();
				}
				catch (Exception e)
				{
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}

	/**
	 * This method plays the currently loaded sound on a loop. This method is not fully operational and should not yet be used.
	 * Further, this method does not accommodate the differences between looped and unlooped sounds.
	 */
	public void startSound()
	{
		try{sound.loop(Clip.LOOP_CONTINUOUSLY);}
		catch(Exception e){}
	}
	
	/**
	 * This method stops the currently loaded sound.
	 */
	public void stopSound()
	{
		sound.stop();
	}
	
	/**
	 * This method switches the currently loading sound from playing to stopped or vice-versa. This method is not fully operational and should not yet be used.
	 * Further, this method does not accommodate the differences between looped and unlooped sounds.
	 */
	public void soundOnOff()
	{
		if(sound.isRunning())
			sound.stop();
		else
			sound.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public boolean soundIsRunning()
	{
		if(sound == null)
			return false;
		else if(sound.isRunning())
			return true;
		else
			return false;
	}
	
	/**
	 * The standard sound for most "fair" weather scenarios is "birds" for morning and day, and "crickets" for evening and night.
	 * @param timeOfDay The code representing the current time of day.
	 */
	private void playStandardSound(int timeOfDay)
	{
		if(timeOfDay == 0)
			playSoundLoop("birds");
		else if(timeOfDay == 1)
			playSoundLoop("crickets");
		else if(timeOfDay == 2)
			playSoundLoop("birds");
		else if(timeOfDay == 3)
			playSoundLoop("crickets");
	}

}
