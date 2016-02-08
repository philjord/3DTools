package awt.tools3d.resolution;

import javax.swing.JOptionPane;

import java2.awt.DisplayMode;

public class GraphicsSettings
{
	private DisplayMode originalDisplayMode = null;

	private DisplayMode desiredDisplayMode = null;

	private boolean runFullscreen = false;

	private boolean aaRequired = false;

	private boolean oculusView = false;

	private int anisotropicFilterDegree = 0;

	private boolean cancelled = false;

	private boolean valid = true;

	public GraphicsSettings()
	{

	}

	/**
	 * Report whether or not the fullscreen option was checked.
	 * @return a boolean, true if the app should run in fullscreen
	 */
	public boolean isRunFullscreen()
	{
		return runFullscreen;
	}

	public void setRunFullscreen(boolean runFullscreen)
	{
		//quick check for -Dsun.java2d.noddraw=true being set
		String nodd = System.getProperty("sun.java2d.noddraw");
		if (runFullscreen && (nodd == null || !nodd.equals("true")))
		{
			JOptionPane.showMessageDialog(null, "Full screen without JVM VM argument -Dsun.java2d.noddraw=true is bad.");
		}

		String os = System.getProperty("os.name");

		if (os.indexOf("Mac") != -1)
		{
			JOptionPane.showMessageDialog(null, "Try Control+Command+F to exit full screen");
		}

		this.runFullscreen = runFullscreen;
	}

	public void setOriginalDisplayMode(DisplayMode originalDisplayMode)
	{
		this.originalDisplayMode = originalDisplayMode;
	}

	/**
	 * Retrieve the original display mode the desktop was in before the application started
	 * @return a DisplayMode object
	 */
	public DisplayMode getOriginalDisplayMode()
	{
		return originalDisplayMode;
	}

	/**
	 * Can be null in case of cancel
	 * @return
	 */
	public DisplayMode getDesiredDisplayMode()
	{
		return desiredDisplayMode;
	}

	public void setDesiredDisplayMode(DisplayMode desiredDisplayMode)
	{
		this.desiredDisplayMode = desiredDisplayMode;
	}

	public boolean isAaRequired()
	{
		return aaRequired;
	}

	public void setAaRequired(boolean aaRequired)
	{
		this.aaRequired = aaRequired;
	}

	public boolean isOculusView()
	{
		return oculusView;
	}

	public void setOculusView(boolean oculusView)
	{
		this.oculusView = oculusView;
	}

	public int getAnisotropicFilterDegree()
	{
		return (int) Math.pow(2, anisotropicFilterDegree - 1);
	}

	/**
	 * Set as a power figure n , but returned as the 2^n 
	 * @param anisotropicFilterDegree
	 */
	public void setAnisotropicFilterDegree(int anisotropicFilterDegree)
	{
		this.anisotropicFilterDegree = anisotropicFilterDegree;
	}

	public boolean isCancelled()
	{
		return cancelled;
	}

	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}

	public String toPrefString()
	{
		String prefStr = "";
		if (originalDisplayMode != null)
		{
			prefStr += originalDisplayMode.getWidth() + ":" + //
					originalDisplayMode.getHeight() + ":" + //
					originalDisplayMode.getBitDepth() + ":" + //
					originalDisplayMode.getRefreshRate() + ":";
		}
		else
		{
			prefStr += "640:480:16:60:";
		}

		if (desiredDisplayMode != null)
		{
			prefStr += desiredDisplayMode.getWidth() + ":" + //
					desiredDisplayMode.getHeight() + ":" + //
					desiredDisplayMode.getBitDepth() + ":" + //
					desiredDisplayMode.getRefreshRate() + ":";
		}
		else
		{
			prefStr += "640:480:16:60:";
		}

		prefStr += runFullscreen + ":" + //
				aaRequired + ":" + //
				oculusView + ":" + //
				anisotropicFilterDegree + ":" + //
				cancelled;

		return prefStr;
	}

	/**
	 * isValid set false if the prefs String doesn't load properly
	 * @param prefStr
	 
	 */
	public void fromPrefString(String prefStr)
	{
		try
		{
			String[] prefs = prefStr.split(":");
			originalDisplayMode = new DisplayMode(Integer.parseInt(prefs[0]), Integer.parseInt(prefs[1]), Integer.parseInt(prefs[2]),
					Integer.parseInt(prefs[3]));
			desiredDisplayMode = new DisplayMode(Integer.parseInt(prefs[4]), Integer.parseInt(prefs[5]), Integer.parseInt(prefs[6]),
					Integer.parseInt(prefs[7]));
			runFullscreen = Boolean.parseBoolean(prefs[8]);
			aaRequired = Boolean.parseBoolean(prefs[9]);
			oculusView = Boolean.parseBoolean(prefs[10]);
			anisotropicFilterDegree = Integer.parseInt(prefs[11]);
			cancelled = Boolean.parseBoolean(prefs[12]);
			valid = true;

		}
		catch (Exception e)
		{
			//oh well maybe next time...
			valid = false;
		}

	}

	public boolean isValid()
	{
		return valid;
	}
}
