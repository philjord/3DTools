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
	 * add the canvas3d on screen early, just before this method call
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
		System.out.println("organising Resolution...");

		GraphicsSettings gs = null;
		if (prefs != null && !forceSelect)
		{
			String prefStr = prefs.get("GraphicsSettings", "");
			if (prefStr != null && prefStr.length() > 0)
			{
				gs = new GraphicsSettings();
				gs.fromPrefString(prefStr);
			}
		}

		if (gs == null || gs.isCancelled())
		{
			gs = new GraphicsSettings();
			DisplayDialog dlg = new DisplayDialog(null, initMinRes, true);
			//DisplayDialog dlg = new DisplayDialog(frame, initMinRes, true);			
			dlg.setVisible(true);
			gs = dlg.getGraphicsSettings();
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

		GraphicsSettings gs = null;
		if (prefs != null && !forceSelect)
		{
			String prefStr = prefs.get("GraphicsSettings", "");
			if (prefStr != null && prefStr.length() > 0)
			{
				gs = new GraphicsSettings();
				gs.fromPrefString(prefStr);
			}
		}

		if (gs == null || gs.isCancelled())
		{
			gs = new GraphicsSettings();
			DisplayDialog dlg = new DisplayDialog(null, initMinRes, false);
			dlg.setVisible(true);
			gs = dlg.getGraphicsSettings();
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
