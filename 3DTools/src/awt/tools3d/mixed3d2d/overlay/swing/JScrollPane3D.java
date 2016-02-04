package awt.tools3d.mixed3d2d.overlay.swing;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class JScrollPane3D extends JScrollPane
{
	private Panel3D panel3D;

	private ArrayList<JComponent> internalJComponents;

	private JViewport jViewport;

	private boolean added = false;

	// can I just send all event through to scroll and see if it scrolls?
	public JScrollPane3D(Panel3D panel3D, Component view, int vsbPolicy, int hsbPolicy)
	{
		super(view, vsbPolicy, hsbPolicy);
		this.panel3D = panel3D;
		new Throwable("JSCROLLPANE3D does NOT work!!").printStackTrace();
	}

	public JScrollPane3D(Panel3D panel3D, Component view)
	{
		super(view);
		this.panel3D = panel3D;
		new Throwable("JSCROLLPANE3D does NOT work!!").printStackTrace();
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
			System.out.println("scroll add " + comp);
			if (comp instanceof JScrollBar)
			{
				internalJComponents.add((JScrollBar) comp);
			}
			else if (comp instanceof JViewport)
			{
				this.jViewport = (JViewport) comp;
			}
			else
			{
				System.out.println("what?");
			}

			//JViewport
			//2 JScrollPane$ScrollBar

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
	public void setVisible(boolean vis)
	{
		super.setVisible(vis);
		if (panel3D != null)
		{

			doLayout();

			if (vis && !added)
			{
				panel3D.addJComponent(this);

				for (JComponent jc : internalJComponents)
				{
					panel3D.addJComponent(jc);
				}

				if (jViewport != null)
				{
					panel3D.addJComponent(jViewport);
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
				if (jViewport != null)
				{
					panel3D.removeJComponent(jViewport);
				}
				added = false;
			}

		}

		repaint();
	}
}
