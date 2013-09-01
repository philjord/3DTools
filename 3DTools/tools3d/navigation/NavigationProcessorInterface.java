package tools3d.navigation;

public interface NavigationProcessorInterface
{
	public boolean isActive();

	public void setActive(boolean b);

	public void changeRotation(double scaledDeltaY, double scaledDeltaX);

	public void setRotationPerSec(float f, float g);

	public void setZChange(float fastForwardRate);

	public void setXChange(float f);

	public void setYChange(float verticalRate);

	public void process(long frameDuration);
}
