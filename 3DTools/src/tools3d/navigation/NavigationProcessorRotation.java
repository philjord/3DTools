package tools3d.navigation;

import java.util.Vector;

import org.jogamp.vecmath.Quat4f;
import org.jogamp.vecmath.Vector3f;

import tools3d.navigation.rules.NavigationRotationRule;
import tools3d.utils.Utils3D;
import tools3d.utils.YawPitch;

/**
 * 
 * Note another object must call process on a regular basis for the processor to run this is normally
 * NavigationTemporalBehaviour, but could be a simple thread
 * This processor canonly be used for rotational constraints, all transslational rules
 * should use the JBullet processor 
 * 
 * @author Administrator
 * 
 */

//TODO: properly split NavigationProcessorInterface (bullet and this), so the 2 are used in tandem nicely
public class NavigationProcessorRotation implements NavigationProcessorInterface
{
	/** The source and destination for transform changes */
	private AvatarLocation avatarLocation;

	// NOTE: rotattion per sec over rides absolute rotation
	/** The amount to move the view in mouse coords up/down per second */
	private float rotationYPerSec = 0;

	/** The amount to move the view in mouse coords left/right per second */
	private float rotationXPerSec = 0;

	/** The absolute rotation up/down */
	private float rotationY = 0;

	/** The absolute rotation left/right */
	private float rotationX = 0;

	// NOTE not a listener list, as this rules are not simply listening
	private Vector<NavigationRotationRule> navigationRotationRules = new Vector<NavigationRotationRule>();

	private boolean active = false;

	public NavigationProcessorRotation(AvatarLocation avatarLocation)
	{
		this.avatarLocation = avatarLocation;
	}

	public void addNavigationRotationRule(NavigationRotationRule navigationRotationRule)
	{
		if (!navigationRotationRules.contains(navigationRotationRule))
		{
			navigationRotationRules.add(navigationRotationRule);
		}
	}

	public void removeNavigationRotationRule(NavigationRotationRule navigationRotationRule)
	{
		navigationRotationRules.remove(navigationRotationRule);
	}

	@Override
	public void setRotationPerSec(float newRotationX, float newRotationY)
	{
		this.rotationXPerSec = newRotationX;
		this.rotationYPerSec = newRotationY;
		this.rotationX = Float.MIN_VALUE;
		this.rotationY = Float.MIN_VALUE;
	}

	@Override
	public void changeRotation(double addRotationX, double addRotationY)
	{
		this.rotationX += addRotationX;
		this.rotationY += addRotationY;
	}

	// deburners

	/** starting trans */
	private Vector3f avatarTranslation = new Vector3f();

	/** starting rotation*/
	private Quat4f avatarRot = new Quat4f();

	/** A working value for the current frame's rotation of the eye */
	private Quat4f desiredRot = new Quat4f();

	/** a temp   */
	private YawPitch tempYawPitch = new YawPitch();

	@Override
	public void process(long timeElapsedSinceLastProcess)
	{
		if (active)
		{
			// if it's been more than a second we need to discard this frame of changes as it's unlikely to be a nice result
			if (timeElapsedSinceLastProcess < 1000)
			{
				// get the rotation out
				Utils3D.safeGetQuat(avatarLocation.getTransform(), avatarRot);

				// get the translation out ...
				avatarLocation.getTransform().get(avatarTranslation);

				// work out how much change should happen by how long since we last updated, in seconds
				float motionDelay = timeElapsedSinceLastProcess / 1000f;

				double rotY = 0;
				double rotX = 0;

				// is there anything to do?
				if (rotationYPerSec != 0 || rotationXPerSec != 0)
				{
					rotY = rotationYPerSec * motionDelay;
					rotX = rotationXPerSec * motionDelay;
				}
				else if (rotationY != Double.MIN_VALUE || rotationX != Double.MIN_VALUE)
				{
					rotY = rotationY;
					rotX = rotationX;

					// now empty the rotation holders, as the required amount of rot has been done
					rotationY = 0;
					rotationX = 0;
				}

				if (rotY != 0 || rotX != 0)
				{
					// *********ROTATION HANDLING ****************
					tempYawPitch.set(avatarRot);
					tempYawPitch.setYaw(tempYawPitch.getYaw() + rotY);
					tempYawPitch.setPitch(tempYawPitch.getPitch() + rotX);

					tempYawPitch.get(desiredRot);
				}

				// *************APPLY ROTATION RULES*************

				// apply the various rules, NOTE these are in order, the output from one is the input to the next
				for (NavigationRotationRule nrr : navigationRotationRules)
				{
					if (nrr.isActive())
					{
						desiredRot = nrr.applyRule(desiredRot, avatarRot);
					}
				}

				// set the location back at the avatar location
				avatarLocation.set(desiredRot, avatarTranslation);

			}
		}
	}

	@Override
	public boolean isActive()
	{
		return active;
	}

	@Override
	public void setActive(boolean active)
	{
		this.active = active;
	}

	@Override
	public void setZChange(float fastForwardRate)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void setXChange(float f)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void setYChange(float verticalRate)
	{
		throw new UnsupportedOperationException();
	}

}