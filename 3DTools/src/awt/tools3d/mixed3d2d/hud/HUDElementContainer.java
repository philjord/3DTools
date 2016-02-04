package awt.tools3d.mixed3d2d.hud;


public class HUDElementContainer
{
	private boolean enabled = true;

	private int x = 0;

	private int y = 0;

	private HUDElementContainer parent;

	public HUDElementContainer()
	{

	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void add(HUDElementContainer child)
	{
		child.setParent(this);
	}

	public void setParent(HUDElementContainer parent)
	{
		this.parent = parent;
	}

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getAbsoluteX()
	{
		if (parent != null)
		{
			return parent.getAbsoluteX() + x;
		}
		else
		{
			return x;
		}
	}

	public int getAbsoluteY()
	{
		if (parent != null)
		{
			return parent.getAbsoluteY() + y;
		}
		else
		{
			return y;
		}
	}

	 

}
