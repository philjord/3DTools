package tools3d.utils.scenegraph;

public interface Fadable
{
	/**percent is from 0 to 1.0 and will be clamped if over/under
	* set 0 to use normal transparency for shape
	*/
	public void fade(float percent);
}
