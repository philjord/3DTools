package awt.tools3d.resolution;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
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

import java2.awt.DisplayMode;
import java2.awt.GraphicsDevice;
import java2.awt.GraphicsEnvironment;

/**
 * DisplayDialog is a JDialog object that presents the user with a selection of
 * possible display modes.  The user is able to select the screen resolution,
 * refresh rate, color depth, and whether the app should run in fullscreen or
 * windowed mode.
 * 
 * NOTE if you select any resolution mode other than current you will reset all attached monitors
 * which will muck up multi-monitor window layouts
 */
 
public final class DisplayDialog extends JPanel implements ActionListener
{
	private final JButton cancel = new JButton("Cancel");

	private final JButton okay = new JButton("Ok");

	private final JButton props = new JButton("Props");

	private final JCheckBox fullscreenCheckbox = new JCheckBox("Fullscreen");

	private final JCheckBox aaCheckbox = new JCheckBox("AntiAlias");

	private final JCheckBox ovCheckbox = new JCheckBox("OculusView");

	private final GraphicsDevice graphicsDevice;

	private final Map<String, DisplayMode> availableDisplayModes = new HashMap<String, DisplayMode>();

	private ArrayList<DisplayMode> modesToOffer = new ArrayList<DisplayMode>();

	private Map<String, ArrayList<DisplayMode>> displayModesByRes = new HashMap<String, ArrayList<DisplayMode>>();

	private final JComboBox<String> modesDropDown = new JComboBox<String>();

	private final JComboBox<String> bitDepthDropDown = new JComboBox<String>();

	private final JComboBox<String> refreshDropDown = new JComboBox<String>();

	private JSlider anisotropicFilterDegree;

	private static EmptyBorder border5 = new EmptyBorder(5, 5, 5, 5);

	private static final int DONT_CARE = -1;

	private GraphicsSettings graphicsSettings = new GraphicsSettings();

	private Container wrapper;

	public static DisplayDialog createDisplayDialog(Frame frame, boolean initMinRes, boolean allowFullScreen, GraphicsSettings prefsGS)
	{
		JDialog jd = new JDialog(frame, true);
		DisplayDialog dd = new DisplayDialog(frame, jd, initMinRes, allowFullScreen, prefsGS);

		jd.pack();
		jd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		jd.setLocation((int) (size.getWidth() - jd.getWidth()) >> 1, (int) (size.getHeight() - jd.getHeight()) >> 1);
		jd.setAlwaysOnTop(true);
		return dd;
	}
	
	public static DisplayDialog createDisplayInternalFrame(Frame frame, boolean initMinRes, boolean allowFullScreen, GraphicsSettings prefsGS)
	{
		JInternalFrame jd = new JInternalFrame(null, true);
		DisplayDialog dd = new DisplayDialog(frame, jd, initMinRes, allowFullScreen, prefsGS);

		jd.pack();
		jd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		jd.setLocation((int) (size.getWidth() - jd.getWidth()) >> 1, (int) (size.getHeight() - jd.getHeight()) >> 1);
		//jd.setAlwaysOnTop(true);
		return dd;
	}

	/**
	 * Creates a new instance of DisplayDialog.
	 * @param frame The parent component for this Swing object
	 * @param wrapper MUST be either JDialog or JInternalFrame
	 * @param initMinRes 
	 * @param allowFullScreen 
	 * @param prefsGS 
	 */
	public DisplayDialog(Frame frame, Container wrapper, boolean initMinRes, boolean allowFullScreen, GraphicsSettings prefsGS)
	{

		//super(frame, true);
		this.wrapper = wrapper;

		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
		if (!graphicsDevice.isFullScreenSupported())
			fullscreenCheckbox.setEnabled(false);

		if (prefsGS != null)
		{
			graphicsSettings.setOriginalDisplayMode(prefsGS.getOriginalDisplayMode());
			graphicsSettings.setDesiredDisplayMode(prefsGS.getDesiredDisplayMode());
			graphicsSettings.setAnisotropicFilterDegree(prefsGS.getAnisotropicFilterDegree());
			graphicsSettings.setRunFullscreen(prefsGS.isRunFullscreen());
			graphicsSettings.setAaRequired(prefsGS.isAaRequired());
			graphicsSettings.setOculusView(prefsGS.isOculusView());
		}
		else
		{
			graphicsSettings.setOriginalDisplayMode(graphicsDevice.getDisplayMode());
			graphicsSettings.setDesiredDisplayMode(graphicsDevice.getDisplayMode());
		}

		okay.setMnemonic(KeyEvent.VK_O);
		okay.addActionListener(this);

		if (wrapper instanceof JDialog)
		{
			((JDialog) wrapper).getRootPane().setDefaultButton(okay);
		}
		else if (wrapper instanceof JInternalFrame)
		{
			((JInternalFrame) wrapper).getRootPane().setDefaultButton(okay);
		}

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

		if (allowFullScreen)
			fullscreenCheckbox.setSelected(graphicsSettings.isRunFullscreen());
		aaCheckbox.setSelected(graphicsSettings.isAaRequired());
		ovCheckbox.setSelected(graphicsSettings.isOculusView());

		mainPanel.add("South", southPanel);

		add(mainPanel);

		wrapper.add(this);

	}

	private JPanel buildAnisoSlider()
	{
		//TODO: check if card supports 16 or less
		JPanel anisoPanel = new JPanel(new GridBagLayout());
		anisoPanel
				.setBorder(new CompoundBorder(new TitledBorder(null, "Anisotropic Filter", TitledBorder.LEFT, TitledBorder.TOP), border5));

		anisotropicFilterDegree = new JSlider(0, 5, 0);
		Hashtable<?, ?> labels = anisotropicFilterDegree.createStandardLabels(1, 0);
		Enumeration<?> e = labels.keys();

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

		anisotropicFilterDegree.setValue(graphicsSettings.getAnisotropicFilterDegree());

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
		//TODO: I really should have a size, bitdepth and refresh pickers, maybe a drop down then 2 sliders
		final DisplayMode[] modes = graphicsDevice.getDisplayModes();
		final JPanel resolutionPanel = new JPanel(new GridBagLayout());
		resolutionPanel.setBorder(new CompoundBorder(new TitledBorder(null, "Resolution", TitledBorder.LEFT, TitledBorder.TOP), border5));

		ModeComparator modeComparator = new ModeComparator();
		//find lowest first
		DisplayMode lowestMode = null;
		for (DisplayMode mode : modes)
		{
			if (lowestMode == null || modeComparator.compare(mode, lowestMode) == -1)
			{
				lowestMode = mode;
			}
			//add it if it's good enough to bother (or 0 which means no info)
			if ((mode.getBitDepth() > 8 && mode.getWidth() >= 800 && mode.getHeight() >= 600 //
					&& ((mode.getRefreshRate() >= 50 && mode.getRefreshRate() % 5 == 0)) || mode.getRefreshRate() == 0))
			{
				modesToOffer.add(mode);
			}
		}

		//if no options add the super lowest mode of all
		if (modesToOffer.size() == 0)
		{
			modesToOffer.add(lowestMode);
		}

		Collections.sort(modesToOffer, modeComparator);

		for (DisplayMode mode : modesToOffer)
		{
			String resStrMode = mode.getWidth() + "x" + mode.getHeight();
			String fullStrMode = resStrMode + " " + mode.getRefreshRate() + "Hz " + mode.getBitDepth() + " bpp";

			// only if it's not there but...
			if (availableDisplayModes.get(fullStrMode) == null)
			{
				availableDisplayModes.put(fullStrMode, mode);

				if (!displayModesByRes.containsKey(resStrMode))
				{
					displayModesByRes.put(resStrMode, new ArrayList<DisplayMode>());
					modesDropDown.addItem(resStrMode);
				}

				if (modesDropDown.getSelectedItem() == null)
				{
					modesDropDown.setSelectedItem(resStrMode);
				}

				// select it if it's the current, and we want the lowest
				if (!initMinRes && mode.equals(graphicsSettings.getDesiredDisplayMode()))
				{
					modesDropDown.setSelectedItem(resStrMode);
				}

				displayModesByRes.get(resStrMode).add(mode);
			}

		}

		resetBitDepthAndRefresh();

		modesDropDown.setSize(modesDropDown.getPreferredSize().width, 200);
		resolutionPanel.add(modesDropDown);
		bitDepthDropDown.setSize(bitDepthDropDown.getPreferredSize().width, 200);
		resolutionPanel.add(bitDepthDropDown);
		refreshDropDown.setSize(refreshDropDown.getPreferredSize().width, 200);
		resolutionPanel.add(refreshDropDown);

		modesDropDown.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				resetBitDepthAndRefresh();
			}
		});
		return resolutionPanel;
	}

	private void resetBitDepthAndRefresh()
	{
		bitDepthDropDown.removeAllItems();
		refreshDropDown.removeAllItems();

		if (modesDropDown.getSelectedItem() != null)
		{
			ArrayList<DisplayMode> modes = displayModesByRes.get(modesDropDown.getSelectedItem());

			ArrayList<String> depthStrs = new ArrayList<String>();
			for (DisplayMode mode : modes)
			{
				String depthStr = mode.getBitDepth() + " bpp";
				if (!depthStrs.contains(depthStr))
				{
					depthStrs.add(depthStr);
					bitDepthDropDown.addItem(depthStr);
				}
			}

			//select the best
			if (bitDepthDropDown.getItemCount() > 0)
				bitDepthDropDown.setSelectedIndex(bitDepthDropDown.getItemCount() - 1);

			ArrayList<String> refreshStrs = new ArrayList<String>();
			for (DisplayMode mode : modes)
			{
				String refreshStr = mode.getRefreshRate() + "Hz";
				if (!refreshStrs.contains(refreshStr))
				{
					refreshStrs.add(refreshStr);
					refreshDropDown.addItem(refreshStr);
				}
			}
			if (refreshDropDown.getItemCount() > 0)
				refreshDropDown.setSelectedIndex(refreshDropDown.getItemCount() - 1);

			//TODO: one day work this out and disable the option
			//output if there is a missing depth/refresh combo
			if (bitDepthDropDown.getItemCount() * refreshDropDown.getItemCount() != modes.size())
			{
				System.out.println("Possible missing resolution combination!");
				System.out.println("res count " + modes.size() + ", bitdepth count " + bitDepthDropDown.getItemCount()
						+ ", refresh rate count " + refreshDropDown.getItemCount());
			}
		}
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
		if (modesDropDown.getSelectedItem() != null && refreshDropDown.getSelectedItem() != null
				&& bitDepthDropDown.getSelectedItem() != null)
		{
			String selectedString = modesDropDown.getSelectedItem() + " " + refreshDropDown.getSelectedItem() + " "
					+ bitDepthDropDown.getSelectedItem();
			DisplayMode selectedMode = availableDisplayModes.get(selectedString);

			graphicsSettings.setCancelled(false);
			graphicsSettings.setDesiredDisplayMode(selectedMode);
			graphicsSettings.setRunFullscreen(fullscreenCheckbox.isSelected());
			graphicsSettings.setAaRequired(aaCheckbox.isSelected());
			graphicsSettings.setOculusView(ovCheckbox.isSelected());
			graphicsSettings.setAnisotropicFilterDegree(anisotropicFilterDegree.getValue());

			dispose();
		}
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

	private void dispose()
	{
		if (wrapper instanceof JDialog)
		{
			((JDialog) wrapper).dispose();
		}
		else if (wrapper instanceof JInternalFrame)
		{
			((JInternalFrame) wrapper).dispose();
		}

	}

	public void setVisible(boolean aFlag)
	{
		if (wrapper instanceof JDialog)
		{
			((JDialog) wrapper).setVisible(aFlag);
		}
		else if (wrapper instanceof JInternalFrame)
		{
			((JInternalFrame) wrapper).setVisible(aFlag);
		}
	}

	private class ModeComparator implements Comparator<DisplayMode>
	{
		@Override
		public int compare(DisplayMode newMode, DisplayMode oldMode)
		{
			if (newMode.getBitDepth() < oldMode.getBitDepth())
				return -1;
			else if (newMode.getBitDepth() > oldMode.getBitDepth())
				return 1;
			else
			{
				if (newMode.getWidth() < oldMode.getWidth())
					return -1;
				else if (newMode.getWidth() > oldMode.getWidth())
					return 1;
				else
				{
					if (newMode.getHeight() < oldMode.getHeight())
						return -1;
					else if (newMode.getHeight() > oldMode.getHeight())
						return 1;
					else
					{
						if (newMode.getRefreshRate() < oldMode.getRefreshRate())
							return -1;
						else if (newMode.getRefreshRate() > oldMode.getRefreshRate())
							return 1;
						else
						{
							return 0;
						}
					}
				}
			}
		}
	};
}