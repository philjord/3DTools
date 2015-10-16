package tools3d.utils.scenegraph;

import javax.vecmath.Color3f;

public interface Fadable
{
	/**percent is from 0 to 1.0 and will be clamped if over/under
	* set 0 to use normal transparency for shape
	*/
	public void fade(float percent);

	/** 
	 * handy place to put this
	 * use null to indicate disable outline
	 * 
	 * @param c
	 */
	public void setOutline(Color3f c);
}
