package awt.tools3d.resolution;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Point;
import java.util.prefs.Preferences;

import javax.media.j3d.J3dUtil;
import javax.swing.JOptionPane;

import java2.awt.DisplayMode;
import java2.awt.GraphicsDevice;
import java2.awt.GraphicsEnvironment;

public class ScreenResolution
{
	/**
	 * Ok, so it turns out I killed Kenny, you basterd...
	 * 
	 * Ask the user for a resolution setting and returns it (or exits if user cancels)
	 * 		GraphicsSettings gs = ScreenResolution.organiseResolution(this);
	 * you should then check for a null and respond 
	 * you should the call something like 
	 * 		canvas3D.getView().setSceneAntialiasingEnable(gs.isAARequired());
	 *		DDSTextureLoader.setAnisotropicFilterDegree(gs.getAnisotropicFilterDegree());
	 *you will always need to call this as well 
	 *		frame.setVisible(true);
	 * 
	 *  we assume the canvas3d is the ONLY child of this frame (so resolution=canvas3d dims)
	 * 
	 * @param prefs where to gather data from, use null to force reselect
	 * @param frame the frame that will hold the canvas3d
	 * @param initMinRes a safety check for simply applying the minimum spec (test new OS/hardware)
	 * @param exitOnCancel will not return null, but simply system exit on a cancel
	 * @param forceSelect if false and prefs holds a last used graphics setting, that is used and no dialog is displayed 
	 * @return the setting selected, along with setting up the Frame
	 */
	public static GraphicsSettings organiseResolution(Preferences prefs, Frame frame, boolean initMinRes, boolean exitOnCancel, boolean forceSelect)
	{
		return organiseResolution(prefs, frame, initMinRes, exitOnCancel, forceSelect, false);
	}

	public static GraphicsSettings organiseResolution(Preferences prefs, Frame frame, boolean initMinRes, boolean exitOnCancel, boolean forceSelect, boolean undecorated)
	{
		System.out.println("organising resolution...");

		//check to make sure 3dtools joglpipeline in use
		J3dUtil.checkJarLoadVersion();

		GraphicsSettings prefsGS = null;
		if (prefs != null)
		{
			String prefStr = prefs.get("GraphicsSettings", "");
			if (prefStr != null && prefStr.length() > 0)
			{
				prefsGS = new GraphicsSettings();
				prefsGS.fromPrefString(prefStr);
			}
		}

		GraphicsSettings gs = null;

		if (forceSelect || prefsGS == null || !prefsGS.isValid())
		{

			gs = new GraphicsSettings();
			//TODO: I need to hand use the jinternalframe version of this guy, but also render it in the panel3D
			DisplayDialog dlg = DisplayDialog.createDisplayDialog(null, initMinRes, true, prefsGS);
			dlg.setVisible(true);
			gs = dlg.getGraphicsSettings();
		}
		else
		{
			gs = prefsGS;
		}

		if (gs == null || gs.isCancelled())
		{
			if (exitOnCancel)
			{
				System.out.println("Resolution select cancelled, exiting...");
				System.exit(0);
			}
			return null;
		}
		if (prefs != null)
		{
			prefs.put("GraphicsSettings", gs.toPrefString());
		}

		DisplayMode desiredMode = gs.getDesiredDisplayMode();
		if (desiredMode != null)
		{
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();

			if (gs.isRunFullscreen())
			{
				gd.setFullScreenWindow(null);
				//NOTE!!!! calling frame.removeNotify on a visible canvas3D will crash Java3d/Jogl with a bad driver
				if (!frame.isVisible())
				{
					frame.removeNotify();
					frame.setUndecorated(true);
					frame.addNotify();
				}
				gd.setFullScreenWindow(frame);
				if (gd.getFullScreenWindow() == null)
					System.out.println("Did not get fullscreen exclusive mode");
				else
					System.out.println("Got fullscreen exclusive mode");

				if (gd.isDisplayChangeSupported())
				{
					gd.setDisplayMode(desiredMode);
				}
				else
				{
					System.out.println("gd.isDisplayChangeSupported() == false");
				}

				frame.requestFocus();
			}
			else
			{
				//I need no one to be full screen
				gd.setFullScreenWindow(null);

				Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
				if (desiredMode.getWidth() > size.getWidth() || desiredMode.getHeight() > size.getHeight())
				{
					JOptionPane.showMessageDialog(frame, "Resizing window to match desktop settings " + size, "Window Too Large", JOptionPane.ERROR_MESSAGE);
					frame.setSize(size);
				}
				else
				{
					frame.pack();// so insets return something
					frame.setSize(desiredMode.getWidth() + frame.getInsets().left + frame.getInsets().right, desiredMode.getHeight() + frame.getInsets().top + frame.getInsets().bottom);
					frame.setLocation(new Point(((int) (size.getWidth() - frame.getWidth()) >> 1), ((int) (size.getHeight() - frame.getHeight()) >> 1)));

					//NOTE!!!! calling frame.removeNotify on a visible canvas3D will crash Java3d/Jogl with a bad driver
					if (undecorated && !frame.isVisible())
					{
						frame.removeNotify();
						frame.setUndecorated(true);
						frame.addNotify();
					}
				}
				frame.setVisible(true);
			}
		}
		return gs;
	}

	/**
	 * For use is setting up an embedded component
	 * @param prefs
	 * @param frame
	 * @param initMinRes
	 * @param exitOnCancel
	 * @param forceSelect
	 * @return
	 */
	public static GraphicsSettings organiseResolution(Preferences prefs, Component comp, boolean initMinRes, boolean exitOnCancel, boolean forceSelect)
	{

		System.out.println("organising resolution...");

		//check to make sure 3dtools joglpipeline in use
		J3dUtil.checkJarLoadVersion(); 

		GraphicsSettings prefsGS = null;
		if (prefs != null)
		{
			String prefStr = prefs.get("GraphicsSettings", "");
			if (prefStr != null && prefStr.length() > 0)
			{
				prefsGS = new GraphicsSettings();
				prefsGS.fromPrefString(prefStr);
			}
		}

		GraphicsSettings gs = null;

		if (forceSelect || prefsGS == null || !prefsGS.isValid())
		{
			gs = new GraphicsSettings();
			DisplayDialog dlg = DisplayDialog.createDisplayDialog(null, initMinRes, false, prefsGS);
			dlg.setVisible(true);
			gs = dlg.getGraphicsSettings();
		}
		else
		{
			gs = prefsGS;
		}

		if (gs == null || gs.isCancelled())
		{
			if (exitOnCancel)
			{
				System.out.println("Resolution select cancelled, exiting...");
				System.exit(0);
			}
			return null;
		}
		if (prefs != null)
		{
			prefs.put("GraphicsSettings", gs.toPrefString());
		}

		DisplayMode desiredMode = gs.getDesiredDisplayMode();

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		if (desiredMode.getWidth() > size.getWidth() || desiredMode.getHeight() > size.getHeight())
		{
			JOptionPane.showMessageDialog(comp, "Resizing window to match desktop settings " + size, "Window Too Large", JOptionPane.ERROR_MESSAGE);
			comp.setSize(size);
		}
		else
		{
			comp.setSize(desiredMode.getWidth(), desiredMode.getHeight());
		}

		return gs;
	}
}
