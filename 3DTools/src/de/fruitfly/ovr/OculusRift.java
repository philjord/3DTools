package de.fruitfly.ovr;

public class OculusRift implements IOculusRift
{

	private boolean initialized = false;

	private HMDInfo hmdInfo = new HMDInfo();

	public OculusRift()
	{

	}

	public boolean init()
	{
		if (!initialized)
		{
			if (initSubsystem())
			{
				initialized = true;
				hmdInfo.DisplayDeviceName = _getDisplayDeviceName();
				hmdInfo.ProductName = _getProductName();
				hmdInfo.Manufacturer = _getManufacturer();
				hmdInfo.Version = _getVersion();
				hmdInfo.DesktopX = _getDesktopX();
				hmdInfo.DesktopY = _getDesktopY();
				hmdInfo.DisplayId = _getDisplayId();
				hmdInfo.HResolution = _getHResolution();
				hmdInfo.VResolution = _getVResolution();
				hmdInfo.HScreenSize = _getHScreenSize();
				hmdInfo.VScreenSize = _getVScreenSize();
				hmdInfo.VScreenCenter = _getVScreenCenter();
				hmdInfo.EyeToScreenDistance = _getEyeToScreenDistance();
				hmdInfo.LensSeparationDistance = _getLensSeparationDistance();
				hmdInfo.InterpupillaryDistance = _getInterpupillaryDistance();
				hmdInfo.DistortionK[0] = _getDistortionK0();
				hmdInfo.DistortionK[1] = _getDistortionK1();
				hmdInfo.DistortionK[2] = _getDistortionK2();
				hmdInfo.DistortionK[3] = _getDistortionK3();
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
		pollSubsystem();
	}

	public void poll(float deltaTime)
	{
		pollSubsystemDT(deltaTime);
	}

	public HMDInfo getHMDInfo()
	{
		return hmdInfo;
	}

	public SensorInfo getSensorInfo()
	{
		return null;
	}

	public float getYaw()
	{
		return _getYaw();
	}

	public float getPitch()
	{
		return _getPitch();
	}

	public float getRoll()
	{
		return _getRoll();
	}

	public void reset()
	{
		_reset();
	}

	public void destroy()
	{
		destroySubsystem();
	}

	private native boolean initSubsystem();

	private native void pollSubsystem();

	private native void pollSubsystemDT(float deltaTime);

	private native void _reset();

	private native void destroySubsystem();

	private native String _getDisplayDeviceName();

	private native String _getProductName();

	private native String _getManufacturer();

	private native int _getVersion();

	private native int _getDesktopX();

	private native int _getDesktopY();

	private native long _getDisplayId();

	private native int _getHResolution();

	private native int _getVResolution();

	private native float _getHScreenSize();

	private native float _getVScreenSize();

	private native float _getVScreenCenter();

	private native float _getEyeToScreenDistance();

	private native float _getLensSeparationDistance();

	private native float _getInterpupillaryDistance();

	private native float _getDistortionK0();

	private native float _getDistortionK1();

	private native float _getDistortionK2();

	private native float _getDistortionK3();

	private native float _getYaw();

	private native float _getPitch();

	private native float _getRoll();

	static
	{
		System.loadLibrary("JRiftLibrary");
	}

	public static void main(String[] args)
	{
		OculusRift or = new OculusRift();
		or.init();

		HMDInfo hmdInfo = or.getHMDInfo();
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
