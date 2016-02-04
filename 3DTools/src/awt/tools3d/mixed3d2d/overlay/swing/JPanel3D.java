package awt.tools3d.mixed3d2d.overlay.swing;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class JPanel3D extends JPanel
{
	private Panel3D panel3D;

	private boolean added = false;

	/**
	 * Borders set on a JPanel3D will not be displayed, but will muck up insets something chronic
	 * @param panel3D
	 */
	public JPanel3D(Panel3D panel3D)
	{
		super(false);// not double buffered
		this.panel3D = panel3D;
		setLayout(null);//default to no layout manager
	}

	@Override
	public void repaint()
	{
		super.repaint();
		if (panel3D != null)
			panel3D.redraw(true);
	}

	@Override
	public void setVisible(boolean vis)
	{
		super.setVisible(vis);
		doLayout();

		if (panel3D != null)
		{
			if (vis && !added)
			{
				for (Component comp : this.getComponents())
				{
					if (comp instanceof JPanel3D)
					{
						comp.setVisible(vis);
					}
					else if (comp instanceof JComponent)
					{
						JComponent jc = (JComponent) comp;
						panel3D.addJComponent(jc);
						
					}

				}
				added = true;
			}
			else if (!vis && added)
			{

				for (Component comp : this.getComponents())
				{
					if (comp instanceof JPanel3D)
					{
						comp.setVisible(vis);
					}
					else if (comp instanceof JComponent)
					{
						JComponent jc = (JComponent) comp;
						panel3D.removeJComponent(jc);
					}

				}
				added = false;
			}

		}

		repaint();
	}

}
