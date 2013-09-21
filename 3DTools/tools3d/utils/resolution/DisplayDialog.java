/*
 * DisplayDialog.java
 *
 * Created on February 21, 2007, 12:33 PM
 */

package tools3d.utils.resolution;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * DisplayDialog is a JDialog object that presents the user with a selection of
 * possible display modes.  The user is able to select the screen resolution,
 * refresh rate, color depth, and whether the app should run in fullscreen or
 * windowed mode.
 * 
 * NOTE if you select any resolution mode other than current you will reset all attached montiors
 * which will muck up multi monitor window layouts
 * @author Kevin J. Duling (kevin@duling.us)
 */
public final class DisplayDialog extends JDialog implements ActionListener, ItemListener
{
	private final JButton cancel = new JButton("Cancel");

	private final JButton okay = new JButton("Ok");

	private final JCheckBox fullscreenCheckbox = new JCheckBox("Fullscreen");

	private final GraphicsDevice graphicsDevice;

	private final DisplayMode originalDisplayMode;

	private DisplayMode desiredDisplayMode = null;

	private final Map<String, DisplayMode> availableDisplayModes = new HashMap<String, DisplayMode>(100);

	private final JComboBox modesDropDown = new JComboBox();

	private boolean runFullscreen = false;

	private static final EmptyBorder border5 = new EmptyBorder(5, 5, 5, 5);

	private static final int DONT_CARE = -1;

	/**
	 * Creates a new instance of DisplayDialog.
	 * @param parent The parent compnent for this Swing object
	 */
	public DisplayDialog(final JFrame parent)
	{
		super(parent, true);
		final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
		if (!graphicsDevice.isFullScreenSupported())
			fullscreenCheckbox.setEnabled(false);
		originalDisplayMode = graphicsDevice.getDisplayMode();
		desiredDisplayMode = findDesiredDisplayMode(640, 480, 60, 32); // set this to something safe for a default value

		okay.setMnemonic(KeyEvent.VK_O);
		okay.addActionListener(this);

		cancel.setMnemonic('C');
		cancel.addActionListener(this);

		fullscreenCheckbox.addItemListener(this);

		final JPanel mainPanel = new JPanel(new BorderLayout());

		final GridLayout centerPanelLayout = new GridLayout(1, 3);
		final JPanel centerPanel = new JPanel(centerPanelLayout);
		mainPanel.add("Center", centerPanel);
		centerPanel.add(buildResolutionPanel());

		final GridLayout southPanelLayout = new GridLayout(1, 3);
		southPanelLayout.setHgap(5);
		final JPanel southPanel = new JPanel(new GridBagLayout());
		southPanel.add(fullscreenCheckbox);
		southPanel.add(okay);
		southPanel.add(cancel);
		mainPanel.add("South", southPanel);

		add(mainPanel);
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) (size.getWidth() - getWidth()) >> 1, (int) (size.getHeight() - getHeight()) >> 1);
		setAlwaysOnTop(true);

	}

	/**
	 * Called by the Swing framework when an action is peformed
	 * @param event the object that caused the event
	 */
	public void actionPerformed(final ActionEvent event)
	{
		if (event.getSource() == okay)
			handleOkay();
		if (event.getSource() == cancel)
			handleCancel();
	}

	/**
	 * Utility method to build the Swing panel the controls will sit on
	 * @return a JPanel object
	 */
	private JPanel buildResolutionPanel()
	{
		final DisplayMode[] modes = graphicsDevice.getDisplayModes();
		final JPanel resolutionPanel = new JPanel(new GridBagLayout());
		resolutionPanel.setBorder(new CompoundBorder(new TitledBorder(null, "Resolution", TitledBorder.LEFT, TitledBorder.TOP), border5));

		for (DisplayMode mode : modes)
		{
			if (mode.getBitDepth() > 8 && mode.getWidth() > 600 && mode.getHeight() > 400)
			{
				String strMode = mode.getWidth() + "x" + mode.getHeight() + " " + mode.getRefreshRate() + "Hz " + mode.getBitDepth()
						+ " bpp";
				availableDisplayModes.put(strMode, mode);
				modesDropDown.addItem(strMode);
				// select it if it's the current
				if (mode.equals(originalDisplayMode))
				{
					desiredDisplayMode = mode;
					modesDropDown.setSelectedItem(strMode);
				}
			}
		}
		modesDropDown.setSize(modesDropDown.getPreferredSize().width, 200);
		resolutionPanel.add(modesDropDown);

		return resolutionPanel;
	}

	/**
	 * Retrieve the display mode desired by the user
	 * @return a DisplayMode object
	 */
	public DisplayMode getDesiredDisplayMode()
	{
		return desiredDisplayMode;
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
	 * Do any work needed when the user presses the Okay button
	 */
	private void handleOkay()
	{
		desiredDisplayMode = availableDisplayModes.get(modesDropDown.getSelectedItem());
		dispose();
	}

	/**
	 * Do any work needed when the user presses the Cancel button
	 */
	private void handleCancel()
	{
		runFullscreen = false;
		dispose();
	}

	/**
	 * This is a utility function to try to locate a display mode based on the provided parameters.
	 * The constant DONT_CARE can be used to indicate there is no preference for that value.  When
	 * it is used, the first mode that matches the other parameters will be chosen.
	 * @param width desired screen width
	 * @param height desired screen height
	 * @param refresh desired refresh rate
	 * @param depth desired color depth
	 * @return a DisplayMode object that matches the parameters or 'null' if one could not be found
	 */
	private DisplayMode findDesiredDisplayMode(final int width, final int height, final int refresh, final int depth)
	{
		final DisplayMode[] modes = graphicsDevice.getDisplayModes();
		for (DisplayMode mode : modes)
		{
			boolean bFound = true;
			if (width != DONT_CARE && mode.getWidth() != width)
				bFound = false;
			if (height != DONT_CARE && mode.getHeight() != height)
				bFound = false;
			if (refresh != DONT_CARE && mode.getRefreshRate() != refresh)
				bFound = false;
			if (depth != DONT_CARE && mode.getBitDepth() != depth)
				bFound = false;
			if (bFound)
				return mode;
		}
		return null;
	}

	/**
	 * Report whether or not the fullscreen option was checked.
	 * @return a boolean, true if the app should run in fullscreen
	 */
	public boolean fullscreen()
	{
		//quick check for -Dsun.java2d.noddraw=true being set
		String nodd = System.getProperty("sun.java2d.noddraw");
		if (runFullscreen && (nodd == null || !nodd.equals("true")))
		{
			JOptionPane.showMessageDialog(null, "Full screen without JVM VM argument -Dsun.java2d.noddraw=true is bad.");
		}

		return runFullscreen;
	}

	/**
	 * Callback when a Swing object changes state.
	 * @param e the Swing item that caused the event
	 */
	public void itemStateChanged(final ItemEvent e)
	{
		if (e.getSource() == fullscreenCheckbox)
			runFullscreen = !runFullscreen;
	}
}