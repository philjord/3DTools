package tools3d.navigation.rules.deprecated;

/**
 * @author Administrator
 *
 */
public class GravityEvent
{
	public static final int LANDED_EVENT = 0;

	public static final int TAKE_OFF_EVENT = 1;

	private int type = 0;

	// -ve is velocity downward
	private double yVelocity = 0;

	private float terrainDist;

	public GravityEvent()
	{

	}

	public void set(int type, double yVelocity, float terrainDist)
	{
		this.type = type;
		this.yVelocity = yVelocity;
		this.terrainDist = terrainDist;
	}

	/**
	 * @return
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * @return
	 */
	public double getYVelocity()
	{
		return yVelocity;
	}

	/**
	 * @param i
	 */
	public void setType(int i)
	{
		type = i;
	}

	/**
	 * @param d
	 */
	public void setYVelocity(double d)
	{
		yVelocity = d;
	}

	public float getTerrainDist()
	{
		return terrainDist;
	}

	public void setTerrainDist(float terrainDist)
	{
		this.terrainDist = terrainDist;
	}

}
