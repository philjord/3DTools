package tools3d.ovr;

public class OculusRift03
{

	private boolean initialized = false;

	private HmdInfo hmdInfo = new HmdInfo();

	private HmdDesc hmdDesc;

	public OculusRift03()
	{

	}

	public boolean init()
	{
		if (!initialized)
		{
			if (_initSubsystem())
			{
				initialized = true;

				hmdDesc = (HmdDesc) _getHmdDesc();

				/*hmdInfo.HScreenSize = _getHScreenSize();
				hmdInfo.VScreenSize = _getVScreenSize();
				hmdInfo.VScreenCenter = _getVScreenCenter();
				hmdInfo.EyeToScreenDistance = _getEyeToScreenDistance();
				hmdInfo.LensSeparationDistance = _getLensSeparationDistance();
				hmdInfo.InterpupillaryDistance = _getInterpupillaryDistance();*/

			}
			else
			{
				System.out.println("OculusRift failed to init.");
			}
		}

		return initialized;
	}

	public boolean isInitialized()
	{
		return initialized;
	}

	public void poll()
	{
		if (initialized)
		{
			_pollSubsystem();
		}
		else
		{
			throw new OculusException("Oculus Not initialized!");
		}
	}

	public void poll(float deltaTime)
	{
		if (initialized)
		{
			_pollSubsystemDT(deltaTime);
		}
		else
		{
			throw new OculusException("Oculus Not initialized!");
		}
	}

	public HmdInfo getHMDInfo()
	{
		if (initialized)
		{
			return hmdInfo;
		}
		else
		{
			throw new OculusException("Oculus Not initialized!");
		}
	}

	public HmdDesc getHmdDesc()
	{
		if (initialized)
		{
			return hmdDesc;
		}
		else
		{
			throw new OculusException("Oculus Not initialized!");
		}
	}

	public void destroy()
	{
		if (initialized)
		{
			_destroySubsystem();
		}
		else
		{
			throw new OculusException("Oculus Not initialized!");
		}
	}

	public float getYaw()
	{
		if (initialized)
		{
			return _getYaw();
		}
		else
		{
			throw new OculusException("Oculus Not initialized!");
		}
	}

	public float getPitch()
	{
		if (initialized)
		{
			return _getPitch();
		}
		else
		{
			throw new OculusException("Oculus Not initialized!");
		}
	}

	public float getRoll()
	{
		if (initialized)
		{
			return _getRoll();
		}
		else
		{
			throw new OculusException("Oculus Not initialized!");
		}
	}

	public void reset()
	{
		if (initialized)
		{
			_reset();
		}
		else
		{
			throw new OculusException("Oculus Not initialized!");
		}
	}

	private native boolean _initSubsystem();

	private native void _pollSubsystem();

	private native void _pollSubsystemDT(float deltaTime);

	private native void _reset();

	private native void _destroySubsystem();

	private native float _getYaw();

	private native float _getPitch();

	private native float _getRoll();

	private native Object _getHmdDesc();

	/*private native float _getHScreenSize();

	private native float _getVScreenSize();

	private native float _getVScreenCenter();

	private native float _getEyeToScreenDistance();

	private native float _getLensSeparationDistance();

	private native float _getInterpupillaryDistance();*/

	static
	{
		System.loadLibrary("JRiftLibrary03");
	}

	public static void main(String[] args)
	{
		OculusRift03 or = new OculusRift03();
		or.init();

		HmdInfo hmdInfo = or.getHMDInfo();
		System.out.println(hmdInfo);

		while (or.isInitialized())
		{
			or.poll();

			System.out.println("Yaw: " + or.getYaw() + " Pitch: " + or.getPitch() + " Roll: " + or.getRoll());

			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		or.destroy();
	}
}
