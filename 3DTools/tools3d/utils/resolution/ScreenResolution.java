package tools3d.utils.resolution;

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

	public static GraphicsSettings organiseResolution(Preferences prefs, Frame frame, boolean initMinRes, boolean exitOnCancel)
	{
		GraphicsSettings gs = null;
		if (prefs != null)
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
			DisplayDialog dlg = new DisplayDialog(null, initMinRes);//DON'T use incoming frame due to frame.setUndecorated(true);
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

		prefs.put("GraphicsSettings", gs.toPrefString());

		DisplayMode desiredMode = gs.getDesiredDisplayMode();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		if (gs.isRunFullscreen())
		{
			frame.setUndecorated(true);
			frame.setResizable(false);
			gd.setFullScreenWindow(frame);
			if (gd.getFullScreenWindow() == null)
				System.out.println("Did not get fullscreen exclusive mode");
			else
				System.out.println("Got fullscreen exclusive mode");

			if (gd.isDisplayChangeSupported())
			{
				gd.setDisplayMode(desiredMode);
			}

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
				frame.setSize(desiredMode.getWidth() + frame.getInsets().left + frame.getInsets().right,
						desiredMode.getHeight() + frame.getInsets().top + frame.getInsets().bottom);
				frame.setLocation(new Point(((int) (size.getWidth() - frame.getWidth()) >> 1),
						((int) (size.getHeight() - frame.getHeight()) >> 1)));
			}
			frame.setVisible(true);
		}
		return gs;
	}
}
