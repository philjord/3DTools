package tools3d.resolution;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.prefs.Preferences;

import javax.media.j3d.J3dUtil;
import javax.swing.JOptionPane;

public class ScreenResolution
{
	/**
	 * 
	 * NOTE!! JRE7 can crash out on calls to config the canvas3D
	 * From Readme:
	 * Jre 7 some version cause a crash bug, wiht an error log like:
	 * DefaultRenderingErrorListener.errorOccurred:
	 * CONTEXT_CREATION_ERROR: Renderer: Error creating Canvas3D graphics context or bad pixelformat 
	 * Or
	 *  javax.media.j3d.IllegalRenderingStateException: Java 3D ERROR : OpenGL 1.2 or better is required (GL_VERSION=1.1)
	 * Run check java to discover installed version, uninstalling java 7 or forcibly using jre6 are teh only solutions
	 * Programmatic solution is...
	 * add the canvas3d on screen early, just before this method call the method
	 * cameraPanel.startRendering();
	 * 
	 * Ask the user for a resolution setting and returns it (or exits if user cancels)
	 * 		GraphicsSettings gs = ScreenResolution.organiseResolution(this);
	 * you should then call 
	 * 		Canvas3D.getView().setSceneAntialiasingEnable(gs.isAARequired());
	 * 
	 *  we assume the canvas3d is the ONLY child of this frame (so resolution=canvas3d dims)
	 * @param prefs where to gather data from, use null to force reselect
	 * @param frame
	 * @return
	 */

	public static GraphicsSettings organiseResolution(Preferences prefs, Frame frame, boolean initMinRes, boolean exitOnCancel,
			boolean forceSelect)
	{
		System.out.println("organising resolution...");

		//chekc to make sure 3dtools joglpipeline in use
		J3dUtil.checkJarLoadVersion();//TODO: test this!

		// warn about Java 7
		if (System.getProperty("java.version").indexOf("1.7.") != -1)
		{
			System.out.println("Warning! Java 7 can cause crashes in java3d, uninstall it is the only answer");
		}

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
			//TODO: I need to hand use the jinternalframe version of this guy,but also render it in the panel3D
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
				frame.removeNotify();
				frame.setUndecorated(true);
				frame.addNotify();
				gd.setFullScreenWindow(frame);
				if (gd.getFullScreenWindow() == null)
					System.out.println("Did not get fullscreen exclusive mode");
				else
					System.out.println("Got fullscreen exclusive mode");

				if (gd.isDisplayChangeSupported())
				{
					gd.setDisplayMode(desiredMode);
				}

				frame.requestFocus();
			}
			else
			{
				Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
				if (desiredMode.getWidth() > size.getWidth() || desiredMode.getHeight() > size.getHeight())
				{
					JOptionPane.showMessageDialog(null, "Resizing window to match desktop settings " + size, "Window Too Large",
							JOptionPane.ERROR_MESSAGE);
					frame.setSize(size);
				}
				else
				{
					frame.pack();// so insets return something
					frame.setSize(desiredMode.getWidth() + frame.getInsets().left + frame.getInsets().right, desiredMode.getHeight()
							+ frame.getInsets().top + frame.getInsets().bottom);
					frame.setLocation(new Point(((int) (size.getWidth() - frame.getWidth()) >> 1), ((int) (size.getHeight() - frame
							.getHeight()) >> 1)));
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
	public static GraphicsSettings organiseResolution(Preferences prefs, Component comp, boolean initMinRes, boolean exitOnCancel,
			boolean forceSelect)
	{
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
			JOptionPane.showMessageDialog(null, "Resizing window to match desktop settings " + size, "Window Too Large",
					JOptionPane.ERROR_MESSAGE);
			comp.setSize(size);
		}
		else
		{
			comp.setSize(desiredMode.getWidth(), desiredMode.getHeight());
		}

		return gs;
	}
}
