package tools3d.resolution;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
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
@SuppressWarnings("rawtypes")
public final class DisplayDialog extends JDialog implements ActionListener
{
	private final JButton cancel = new JButton("Cancel");

	private final JButton okay = new JButton("Ok");

	private final JButton props = new JButton("Props");

	private final JCheckBox fullscreenCheckbox = new JCheckBox("Fullscreen");

	private final JCheckBox aaCheckbox = new JCheckBox("AntiAlias");

	private final JCheckBox ovCheckbox = new JCheckBox("OculusView");

	private final GraphicsDevice graphicsDevice;

	private final Map<String, DisplayMode> availableDisplayModes = new HashMap<String, DisplayMode>(100);

	private final JComboBox modesDropDown = new JComboBox();

	private JSlider anisotropicFilterDegree;

	private static EmptyBorder border5 = new EmptyBorder(5, 5, 5, 5);

	private static final int DONT_CARE = -1;

	private GraphicsSettings graphicsSettings = new GraphicsSettings();

	/**
	 * Creates a new instance of DisplayDialog.
	 * @param frame The parent compnent for this Swing object
	 * @param initMinRes 
	 * @param allowFullScreen 
	 */
	public DisplayDialog(Frame frame, boolean initMinRes, boolean allowFullScreen)
	{
		super(frame, true);
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
		if (!graphicsDevice.isFullScreenSupported())
			fullscreenCheckbox.setEnabled(false);
		graphicsSettings.setOriginalDisplayMode(graphicsDevice.getDisplayMode());

		okay.setMnemonic(KeyEvent.VK_O);
		okay.addActionListener(this);

		cancel.setMnemonic(KeyEvent.VK_C);
		cancel.addActionListener(this);

		cancel.setMnemonic(KeyEvent.VK_P);
		props.addActionListener(this);

		JPanel mainPanel = new JPanel(new BorderLayout());

		GridLayout centerPanelLayout = new GridLayout(2, 1);
		JPanel centerPanel = new JPanel(centerPanelLayout);
		mainPanel.add("Center", centerPanel);
		centerPanel.add(buildResolutionPanel(initMinRes));
		centerPanel.add(buildAnisoSlider());

		JPanel southPanel = new JPanel(new GridLayout(2, 1));
		JPanel southPanelChecks = new JPanel(new GridBagLayout());
		JPanel southPanelButts = new JPanel(new GridBagLayout());
		southPanel.add(southPanelChecks);
		southPanel.add(southPanelButts);
		if (allowFullScreen)
			southPanelChecks.add(fullscreenCheckbox);
		southPanelChecks.add(aaCheckbox);
		southPanelChecks.add(ovCheckbox);
		southPanelButts.add(okay);
		southPanelButts.add(cancel);
		southPanelButts.add(props);

		mainPanel.add("South", southPanel);

		add(mainPanel);
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) (size.getWidth() - getWidth()) >> 1, (int) (size.getHeight() - getHeight()) >> 1);
		setAlwaysOnTop(true);

	}

	private JPanel buildAnisoSlider()
	{
		//TODO: check if card supports 16 or less
		JPanel anisoPanel = new JPanel(new GridBagLayout());
		anisoPanel
				.setBorder(new CompoundBorder(new TitledBorder(null, "Anisotropic Filter", TitledBorder.LEFT, TitledBorder.TOP), border5));

		anisotropicFilterDegree = new JSlider(0, 5, 0);
		Hashtable labels = anisotropicFilterDegree.createStandardLabels(1, 0);
		Enumeration e = labels.keys();

		while (e.hasMoreElements())
		{
			Integer i = (Integer) e.nextElement();
			JLabel label = (JLabel) labels.get(i);
			label.setText(i == 0 ? "Off" : ("" + (int) Math.pow(2, i - 1)));
		}
		anisotropicFilterDegree.setLabelTable(labels);
		anisotropicFilterDegree.setPaintLabels(true);
		anisotropicFilterDegree.setSnapToTicks(true);
		anisoPanel.add(anisotropicFilterDegree);
		return anisoPanel;
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
		if (event.getSource() == props)
			handleProps();

	}

	/**
	 * Utility method to build the Swing panel the controls will sit on
	 * @param initMinRes pre select the first (lowest setting) otherwise default to current
	 * @return a JPanel object
	 */
	private JPanel buildResolutionPanel(boolean initMinRes)
	{
		final DisplayMode[] modes = graphicsDevice.getDisplayModes();
		final JPanel resolutionPanel = new JPanel(new GridBagLayout());
		resolutionPanel.setBorder(new CompoundBorder(new TitledBorder(null, "Resolution", TitledBorder.LEFT, TitledBorder.TOP), border5));

		DisplayMode lowestMode = null;

		for (DisplayMode mode : modes)
		{
			if (mode.getBitDepth() > 8 && mode.getWidth() > 600 && mode.getHeight() > 400 //
					&& mode.getRefreshRate() == 60)// might be too restrictive LCD only
			{
				String strMode = mode.getWidth() + "x" + mode.getHeight() + " " + mode.getRefreshRate() + "Hz " + mode.getBitDepth()
						+ " bpp";
				// only if it's not there but...
				if (availableDisplayModes.get(strMode) == null)
				{
					availableDisplayModes.put(strMode, mode);
					modesDropDown.addItem(strMode);
					// select it if it's the current
					if (initMinRes)
					{
						if (lowestMode == null || isLower(lowestMode, mode))
						{
							lowestMode = mode;
						}
					}
					else
					{
						if (mode.equals(graphicsSettings.getOriginalDisplayMode()))
						{
							modesDropDown.setSelectedItem(strMode);
						}
					}
				}
			}
		}
		if (initMinRes && lowestMode != null)
		{
			String strMode = lowestMode.getWidth() + "x" + lowestMode.getHeight() + " " + lowestMode.getRefreshRate() + "Hz "
					+ lowestMode.getBitDepth() + " bpp";
			modesDropDown.setSelectedItem(strMode);
		}
		modesDropDown.setSize(modesDropDown.getPreferredSize().width, 200);
		resolutionPanel.add(modesDropDown);

		return resolutionPanel;
	}

	private boolean isLower(DisplayMode newMode, DisplayMode oldMode)
	{

		return newMode.getHeight() < oldMode.getHeight() && newMode.getWidth() < oldMode.getWidth()
				&& newMode.getBitDepth() < oldMode.getBitDepth();
	}

	/**
	 * Retrieve the display mode desired by the user
	 * @return a DisplayMode object
	 */
	public GraphicsSettings getGraphicsSettings()
	{
		return graphicsSettings;
	}

	/**
	 * Do any work needed when the user presses the Okay button
	 */
	private void handleOkay()
	{
		graphicsSettings.setCancelled(false);
		graphicsSettings.setDesiredDisplayMode(availableDisplayModes.get(modesDropDown.getSelectedItem()));
		graphicsSettings.setRunFullscreen(fullscreenCheckbox.isSelected());
		graphicsSettings.setAaRequired(aaCheckbox.isSelected());
		graphicsSettings.setOculusView(ovCheckbox.isSelected());
		graphicsSettings.setAnisotropicFilterDegree(anisotropicFilterDegree.getValue());

		dispose();
	}

	/**
	 * Do any work needed when the user presses the Cancel button
	 */
	private void handleCancel()
	{
		graphicsSettings.setCancelled(true);
		dispose();
	}

	private void handleProps()
	{
		JTextArea myTextArea = new JTextArea();
		JScrollPane jScrollPane1 = new JScrollPane(myTextArea);

		jScrollPane1.setPreferredSize(new Dimension(400, 500));
		myTextArea.setColumns(20);
		myTextArea.setEditable(false);
		myTextArea.setRows(5);

		QueryProperties.printJ3DProps(myTextArea);

		JOptionPane.showMessageDialog(this, jScrollPane1);
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
	public DisplayMode findDesiredDisplayMode(int width, int height, int refresh, int depth)
	{
		DisplayMode[] modes = graphicsDevice.getDisplayModes();
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

}