package tools3d.swingoverlay;

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class JInternalFrame3D extends JInternalFrame
{
	private static int TITLE_BAR_HEIGHT = 28;

	private Panel3D panel3D;

	private ArrayList<JComponent> addedJComponents = new ArrayList<JComponent>();

	private ArrayList<JComponent> internalJComponents;

	private BasicInternalFrameTitlePane biftp;

	private boolean added = false;

	public JInternalFrame3D(Panel3D _panel3D, String title)
	{
		super(title, false, false);
		this.panel3D = _panel3D;
	}

	public JInternalFrame3D(Panel3D _panel3D, String title, int width, int height, boolean closable)
	{
		super(title, false, closable);
		this.panel3D = _panel3D;
		setSize(width, height);
	}

	public JInternalFrame3D(Panel3D _panel3D, String title, String imageFile, int width, int height, boolean closable)
	{
		super(title, false, closable);
		this.panel3D = _panel3D;
		setSize(width, height);
		Image image = Toolkit.getDefaultToolkit().getImage(imageFile);
		image = image.getScaledInstance(-1, TITLE_BAR_HEIGHT, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(image, "");
		setFrameIcon(icon);
	}

	public Point getComponentPosition(JComponent c)
	{
		Point p = new Point(0, 0);

		// no offset for internal parts
		if (internalJComponents.contains(c))
		{
			p.x = 0;
			p.y = 0;
		}
		else
		{
			p.x = getInsets().left;
			p.y = getContentPaneHeightOffset();
		}
		return p;
	}

	private int getContentPaneHeightOffset()
	{
		int ho = 0;
		if (biftp != null)
		{
			ho += TITLE_BAR_HEIGHT; //but not really the biftp.getHeight(), odd?
		}

		if (getJMenuBar() != null)
		{
			ho += getJMenuBar().getHeight();
		}

		return ho;
	}

	@Override
	public void removeAll()
	{
		for (JComponent jc : addedJComponents)
		{
			super.remove(jc);
		}

		addedJComponents.clear();
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index)
	{
		//due to members being instantiated after super constructor
		if (internalJComponents == null)
		{
			internalJComponents = new ArrayList<JComponent>();
		}
		if (comp instanceof JComponent)
		{
			if (comp instanceof JRootPane)
			{
				internalJComponents.add((JRootPane) comp);
			}
			else if (comp instanceof BasicInternalFrameTitlePane)
			{
				biftp = (BasicInternalFrameTitlePane) comp;
				internalJComponents.add(biftp);
				//find and add close button too
				for (Component titleComp : biftp.getComponents())
				{
					if (titleComp instanceof JButton)
					{
						JButton jb = (JButton) titleComp;
						jb.setEnabled(true);//because tiny laf ignores my closable param above
						internalJComponents.add(jb);
					}
				}
			}
			else
			{
				addedJComponents.add((JComponent) comp);
			}
		}
		super.addImpl(comp, constraints, index);
	}

	@Override
	public void repaint()
	{
		super.repaint();
		if (panel3D != null)
			panel3D.redraw(true);
	}

	@Override
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		if (biftp != null)
		{
			biftp.setSize(width - (this.getInsets().left + this.getInsets().right), TITLE_BAR_HEIGHT);
			biftp.doLayout();
		}
		getContentPane().setSize(width - (this.getInsets().left + this.getInsets().right),
				height - (getContentPaneHeightOffset() + this.getInsets().top + this.getInsets().bottom));
		getContentPane().doLayout();
	}

	@Override
	public void setVisible(boolean vis)
	{
		super.setVisible(vis);
		if (panel3D != null)
		{
			getContentPane().doLayout();
			biftp.doLayout();
			doLayout();

			if (vis && !added)
			{
				panel3D.addJComponent(this);

				for (JComponent jc : internalJComponents)
				{
					panel3D.addJComponent(jc);
				}

				for (JComponent jc : addedJComponents)
				{
					panel3D.addJComponent(jc);
				}
				added = true;
			}
			else if (added)
			{
				panel3D.removeJComponent(this);
				for (JComponent jc : internalJComponents)
				{
					panel3D.removeJComponent(jc);
				}
				for (JComponent jc : addedJComponents)
				{
					panel3D.removeJComponent(jc);
				}
				added = false;
			}

		}

		repaint();
	}

}
