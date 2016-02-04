package awt.tools3d.mixed3d2d.overlay.swing.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import awt.tools3d.mixed3d2d.overlay.swing.Panel3D;

/**
 * @author Administrator
 *
 */
public class ExitDialogPane3D extends JPanel implements ActionListener
{
	private Panel3D panel3D;

	private JLabel label = new JLabel();

	private JButton yesButton = new JButton("Yes");

	private JButton noButton = new JButton("No");

	private boolean exitConfirmed = false;

	private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

	public ExitDialogPane3D(Panel3D _panel3D)
	{
		this.panel3D = _panel3D;
		setLayout(null);
		//TODO: stupid scaling from hud shape!!!
		float hW = 0.7950311f;
		float hH = 0.79448146f;
		setSize((int) (180 / hW), (int) (55 / hH));

		add(label);
		add(yesButton);
		add(noButton);

		label.setLocation((int) (5 / hW), (int) (5 / hH));
		label.setSize((int) (175 / hW), (int) (20 / hH));
		yesButton.setLocation((int) (20 / hW), (int) (30 / hH));
		yesButton.setSize((int) (60 / hW), (int) (20 / hH));
		noButton.setLocation((int) (100 / hW), (int) (30 / hH));
		noButton.setSize((int) (60 / hW), (int) (20 / hH));

		yesButton.addActionListener(this);
		noButton.addActionListener(this);
		label.setText("Are you sure you wish to exit?");
		panel3D.redraw(true);

		setVisible(false);

	}

	public void addActionListener(ActionListener actionListener)
	{
		if (!actionListeners.contains(actionListener))
			actionListeners.add(actionListener);
	}

	public void removeActionListener(ActionListener actionListener)
	{
		actionListeners.remove(actionListener);
	}

	public boolean isExitConfirmed()
	{
		return exitConfirmed;
	}

	public void setVisible(boolean vis)
	{
		super.setVisible(vis);
		if (vis)
		{
			panel3D.addJComponent(this);
			panel3D.addJComponent(yesButton);
			panel3D.addJComponent(noButton);
		}
		else
		{
			panel3D.removeJComponent(this);
			panel3D.removeJComponent(yesButton);
			panel3D.removeJComponent(noButton);
		}
		panel3D.redraw(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		String command = "doNothing";
		if (e.getSource() == yesButton)
		{
			exitConfirmed = true;
			command = "exit";
		}
		setVisible(false);

		ActionEvent ae = new ActionEvent(this, 1, command);
		for (ActionListener actionListener : actionListeners)
		{
			actionListener.actionPerformed(ae);
		}
	}
}
