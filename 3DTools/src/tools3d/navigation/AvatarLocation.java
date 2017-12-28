package tools3d.navigation;

import org.jogamp.java3d.Transform3D;
import org.jogamp.vecmath.Quat4f;
import org.jogamp.vecmath.Vector3f;

import tools.WeakListenerList;
import tools3d.utils.scenegraph.LocationUpdateListener;

public class AvatarLocation
{
	private Quat4f rotation = new Quat4f();

	private Vector3f translation = new Vector3f();

	private WeakListenerList<LocationUpdateListener> avatarLocationListeners = new WeakListenerList<LocationUpdateListener>();

	public AvatarLocation()
	{
	}

	/**NOTE!!!!!!!!!! Weak, you MUST retain a reference!
	 * 
	 * @param avatarLocationListener
	 */
	public void addAvatarLocationListener(LocationUpdateListener avatarLocationListener)
	{
		synchronized (avatarLocationListeners)
		{
			avatarLocationListeners.add(avatarLocationListener);
		}
	}

	public void removeAvatarLocationListener(LocationUpdateListener avatarLocationListener)
	{
		synchronized (avatarLocationListeners)
		{
			avatarLocationListeners.remove(avatarLocationListener);
		}
	}

	public Transform3D getTransform()
	{
		return new Transform3D(rotation, translation, 1f);
	}

	public Vector3f get(Vector3f trans)
	{
		trans.set(translation);
		return trans;
	}

	public Quat4f get(Quat4f rot)
	{
		rot.set(rotation);
		return rot;
	}

	public void get(Quat4f rot, Vector3f trans)
	{
		get(rot);
		get(trans);
	}

	public void setTranslation(Vector3f trans)
	{
		set(rotation, trans);
	}

	public void setRotation(Quat4f rot)
	{
		set(rot, translation);
	}

	public void set(Quat4f rot, Vector3f trans)
	{
		if (!Float.isNaN(trans.x) && !Float.isNaN(rot.x))
		{
			//epsilon equals is a "as similar as this" system
			if (!rot.epsilonEquals(rotation, 0.0001f) || !trans.epsilonEquals(translation, 0.005f))
			{
				synchronized (avatarLocationListeners)
				{
					rotation.set(rot);
					translation.set(trans);

					for (int i = 0; i < avatarLocationListeners.size(); i++)
					{
						if (avatarLocationListeners.get(i) != null)
							avatarLocationListeners.get(i).locationUpdated(rot, trans);
					}
				}
			}
		}
		else
		{
			new Throwable("bad trans or rot set in " + this).printStackTrace();
		}
	}

}
