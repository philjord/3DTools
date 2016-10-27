package tools3d.navigation.rules;

import org.jogamp.vecmath.Quat4f;

import tools3d.utils.YawPitch;

public class NavigationRotationRuleYawPitchConstraint implements NavigationRotationRule
{
	private double minYaw;

	private double maxYaw;

	private double minPitch;

	private double maxPitch;

	private boolean active = false;

	/**
	 * Note
	 * 
	 * Yaw goes from 0 facing forward along -Z rotating to the left to up to nearly 3.14 at the point of facing along +Z
	 * then almost -3.14 this mean "max" refers to how far leftwards and min is how far right wards.
	 * 
	 * Pitch is 0 at level up to PI/2 straight up and -PI/2 straight down continuing
	 * 
	 * @param minYaw
	 * @param maxYaw
	 * @param minPitch
	 * @param maxPitch
	 */
	public NavigationRotationRuleYawPitchConstraint(double minYaw, double maxYaw, double minPitch, double maxPitch)
	{
		setContraints(minYaw, maxYaw, minPitch, maxPitch);
	}

	public void setContraints(double minYaw, double maxYaw, double minPitch, double maxPitch)
	{
		if (minYaw > maxYaw || minPitch > maxPitch || minYaw < -Math.PI || maxYaw > Math.PI || minPitch < -Math.PI / 2
				|| maxPitch > Math.PI / 2)
		{
			new Exception("min/max bad " + minYaw + " " + maxYaw + " " + minPitch + " " + maxPitch).printStackTrace();
			return;
		}

		this.minYaw = minYaw;
		this.maxYaw = maxYaw;
		this.minPitch = minPitch;
		this.maxPitch = maxPitch;
	}

	// deburner
	private YawPitch newYawPitch = new YawPitch();

	private Quat4f newRotation = new Quat4f();

	public Quat4f applyRule(Quat4f desiredRotation, Quat4f currentRotation)
	{
		// get the yaw and pitch out, note yawPitch is clamped to the correct range, so -1 will be 359 degrees
		newYawPitch.set(desiredRotation);

		// if outside the range set to the closest value
		double yaw = newYawPitch.getYaw();
		// we need this for testing by the +PI/-PI swap over point (due south)
		double yawAsPositive = yaw < 0 ? yaw + (Math.PI * 2) : yaw;
		double yawAsNegative = yaw > 0 ? yaw - (Math.PI * 2) : yaw;

		// is yaw outside it's bounds?
		if (yaw < minYaw || yaw > maxYaw)
		{
			System.out.println("outside bounds! " + yaw);

			// if it is we need to find what it's closest to and clamp to that.
			double diffToMax = yawAsPositive - maxYaw;
			double diffToMin = yawAsNegative - minYaw;

			if (Math.abs(diffToMax) < Math.abs(diffToMin))
			{
				newYawPitch.setYaw(maxYaw);
			}
			else
			{
				newYawPitch.setYaw(minYaw);
			}
		}

		// pitch goes from +PI/2 (up) down to -PI/2 with no wrapping
		double pitch = newYawPitch.getPitch();
		if (pitch < minPitch)
		{
			System.out.println("pitch outside bounds! " + pitch);
			newYawPitch.setPitch(minPitch);
		}
		else if (pitch > maxPitch)
		{
			System.out.println("pitch outside bounds! " + pitch);
			newYawPitch.setPitch(maxPitch);
		}

		newYawPitch.get(newRotation);

		return newRotation;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}
}
