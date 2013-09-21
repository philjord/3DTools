package tools3d.utils.resolution;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JOptionPane;

public class ScreenResolution
{
	/**
	 * Ask the user for a resolution setting and returns it (or exits if user cancels)
	 * 		DisplayDialog dlg = ScreenResolution.organiseResolution(this);
	 * you should then call 
	 * 		Canvas3D.getView().setSceneAntialiasingEnable(dlg.isAARequired());
	 * @param frame
	 * @return
	 */
	public static DisplayDialog organiseResolution(Frame frame)
	{
		DisplayDialog dlg = new DisplayDialog(null);//DON'T use frame due to frame.setUndecorated(true);
		dlg.setVisible(true);
		DisplayMode desiredMode = dlg.getDesiredDisplayMode();
		if (desiredMode == null)
			System.exit(0);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		if (dlg.fullscreen())
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
				frame.setSize(desiredMode.getWidth(), desiredMode.getHeight());
				frame.setLocation(new Point((int) (size.getWidth() - frame.getWidth()) >> 1,
						(int) (size.getHeight() - frame.getHeight()) >> 1));
			}
			frame.setVisible(true);
		}
		return dlg;
	}
}
