package tools3d.hud;

import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;

import tools3d.swingoverlay.Panel3D;

public class Canvas3D2D extends Canvas3D
{
	private ArrayList<HUDElement> elements = new ArrayList<HUDElement>();

	private ArrayList<Panel3D> panel3ds = new ArrayList<Panel3D>();

	public Canvas3D2D(GraphicsConfiguration arg0)
	{
		super(arg0);
	}

	public Canvas3D2D(GraphicsConfiguration arg0, boolean arg1)
	{
		super(arg0, arg1);
	}

	public void addElement(HUDElement element)
	{
		if (element != null && !elements.contains(element))
		{
			synchronized (elements)
			{
				elements.add(element);
			}
		}
	}

	public void removeElement(HUDElement element)
	{
		synchronized (elements)
		{
			elements.remove(element);
		}
	}

	public void addPanel3D(Panel3D panel3D)
	{
		if (panel3D != null && !panel3ds.contains(panel3D))
		{
			synchronized (panel3ds)
			{
				panel3ds.add(panel3D);
			}
		}
	}

	public void removePanel3D(Panel3D panel3D)
	{
		synchronized (panel3ds)
		{
			panel3ds.remove(panel3D);
		}
	}

	//private AffineTransform idAT = new AffineTransform();

	public void postRender()
	{
		J3DGraphics2D g = getGraphics2D();

		//AffineTransform saveAT = g.getTransform();

		//TODO:! odd image scrolling effect when looking at animated textures
		//doesn't help
		//g.setTransform(idAT);
		synchronized (elements)
		{
			for (HUDElement e : elements)
			{
				if (e != null && e.isEnabled())
				{
					g.drawImage(e.getBufferedImage(), e.getAbsoluteX(), e.getAbsoluteY(), null);
				}
			}
		}

		synchronized (panel3ds)
		{
			for (Panel3D p : panel3ds)
			{
				if (p != null && p.isEnabled())
				{
					g.drawImage(p.getBufferedImage(), p.getX(), p.getY(), null);
				}
			}
		}

		// if removeNotify has been called we might get an exception, the JDesktop internal frames used to cause
		// this problem but a special internalframe which stubs off the setfocus calls fixes problem
		g.flush(true);

		//g.setTransform(saveAT);

	}
}
