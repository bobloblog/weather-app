import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class loads and paints the images used in the program. WindowManager must pass codes based on the time of day or type of weather to call certain images.
 * The timeOfDay codes are 0 = day, 1 = night, 2 = morning, 3 = evening
 * The weather codes are fetched from Yahoo! weather. The description of each code can be found here: http://developer.yahoo.com/weather/
 * The paintComponent(Graphics g) of this class MUST be called in the paintComponent(Graphics g) of the WindowManager class in order for the images to be displayed at the call of repaint()
 * @author Locomotion15
 *
 */

public class ImageManager
{
	private BufferedImage textHighlight, finalImage;
	private String finalImageName = "";
	
	/**
	 * This default constructor loads the day image with no weather image. This is what is displayed on startup
	 */
	public ImageManager()
	{

		try
		{
			textHighlight = ImageIO.read(new File("resources\\images\\textHighlight.PNG"));
		}
		catch (IOException e){}
		
		setTimeImage("day");
		setWeatherImage("");
		setFinalImage();
	}
	
	/**
	 * This method translates given codes into images and loads them into RAM and prepares them to be displayed at the next call of repaint() in WindowManager
	 * @param code This code represents the current conditions. Descriptions of the codes can be found at http://developer.yahoo.com/weather/
	 * @param timeOfDay This code, 0, 1, 2, or 3; represents day, night, morning, or evening respectively.
	 */
	public void findImages(int code, int timeOfDay)
	{
		
		switch(timeOfDay)
		{
			case 0: setTimeImage("day"); break;
			case 1: setTimeImage("night"); break;
			case 2: setTimeImage("twilight"); break;
			case 3: setTimeImage("twilight"); break;
			default: setTimeImage("day"); break;
		}
		switch(code)
		{
			case 0: setWeatherImage("thunderstorm");
			case 1: setWeatherImage("thunderstorm"); break;
			case 2: setWeatherImage("thunderstorm"); break;
			case 3: setWeatherImage("thunderstorm"); break;
			case 4: setWeatherImage("thunderstorm"); break;
			case 5: setWeatherImage("winterymix"); break;
			case 6: setWeatherImage("winterymix"); break;
			case 7: setWeatherImage("winterymix"); break;
			case 8: setWeatherImage("winterymix"); break;
			case 9: setWeatherImage("showers"); break;
			case 10: setWeatherImage("winterymix"); break;
			case 11: setWeatherImage("showers"); break;
			case 12: setWeatherImage("showers"); break;
			case 13: setWeatherImage("lightsnow"); break;
			case 14: setWeatherImage("lightsnow"); break;
			case 15: setWeatherImage("heavysnow"); break;
			case 16: setWeatherImage("heavysnow"); break;
			case 17: setWeatherImage("showers"); break;
			case 18: setWeatherImage("winterymix"); break;
			case 19: setWeatherImage("foggy"); break;//haze
			case 20: setWeatherImage("foggy"); break;
			case 21: setWeatherImage("foggy"); break;//haze
			case 22: setWeatherImage("foggy"); break;//haze
			case 23: setWeatherImage(""); break;//wind
			case 24: setWeatherImage("wind"); break;
			case 25: setWeatherImage("blank"); break;
			case 26: setWeatherImage("cloudy"); break;
			case 27: setWeatherImage("cloudy"); break;
			case 28: setWeatherImage("cloudy"); break;
			case 29: setWeatherImage("partlycloudy"); break;
			case 30: setWeatherImage("partlycloudy"); break;
			case 31: setWeatherImage(""); break;
			case 32: setWeatherImage(""); break;
			case 33: setWeatherImage(""); break;
			case 34: setWeatherImage(""); break;
			case 35: setWeatherImage("showers"); break;
			case 36: setWeatherImage(""); break;
			case 37: setWeatherImage("thunderstorm"); break;
			case 38: setWeatherImage("thunderstorm"); break;
			case 39: setWeatherImage("thunderstorm"); break;
			case 40: setWeatherImage("showers"); break;
			case 41: setWeatherImage("heavysnow"); break;
			case 42: setWeatherImage("lightsnow"); break;
			case 43: setWeatherImage("heavysnow"); break;
			case 44: setWeatherImage("partlycloudy"); break;
			case 45: setWeatherImage("thunderstorm"); break;
			case 46: setWeatherImage("lightsnow"); break;
			case 47: setWeatherImage("thunderstorm"); break;
			default: setWeatherImage(""); break;
		}
		System.out.println(finalImageName);
		setFinalImage();
	}
	
	/**
	 * This method buffers the background image, or the image that represents the time of day.
	 * @param imageName The file name of the image, NOT including the extension. All images must be .PNG
	 */
	private void setTimeImage(String imageName)
	{
		finalImageName += imageName;
		/*
		timeImage = null;
		try
		{
			timeImage = ImageIO.read(new File("resources\\images\\" + imageName + ".PNG"));
		}
		catch (IOException e){}
		*/
	}
	
	/**
	 * This method buffers the foreground image, or the image that represents the current weather
	 * @param imageName The file name of the image, NOT including the extension. All images must be .PNG
	 */
	private void setWeatherImage(String imageName)
	{
		finalImageName += imageName;
		/*
		weatherImage = null;
		try
		{
			weatherImage = ImageIO.read(new File("resources\\images\\" + imageName + ".PNG"));
		}
		catch (IOException e){}
		*/
	}
	
	private void setFinalImage()
	{
		finalImage = null;
		try
		{
			finalImage = ImageIO.read(new File("resources\\images\\" + finalImageName + ".PNG"));
		}
		catch (IOException e){}
		
		finalImageName = "";
	}
	
	
	/**
	 * This method must be called in WindowManager.paintComponent(Graphics g) in order to ensure the images are painted at the call of WindowManager.repaint()
	 * @param g The graphics component on which the images will be painted
	 */
	public void paintComponent(Graphics g)
	{
		g.drawImage(finalImage, 0, 0, null);
		//g.drawImage(textHighlight, 0, 0, null);
	}
}
