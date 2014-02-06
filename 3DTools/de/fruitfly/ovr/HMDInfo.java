package de.fruitfly.ovr;

import java.util.Arrays;

public class HMDInfo
{
	/*Here are all the defaults from the HMDInfo class.

	HResolution = 1280

	VResolution = 800

	HScreenSize = 0.14976

	VScreenSize = 0.09356

	VScreenCenter = 0.0468

	EyeToScreenDistance = 0.041

	LensSeparationDistance = 0.0635

	InterpupillaryDistance = 0.064

	DistortionK[0] = 1.0

	DistortionK[1] = 0.22

	DistortionK[2] = 0.24

	DistortionK[3] = 0.0

	ChromAbCorrection[0] = 0.996

	ChromAbCorrection[1] = -0.004

	ChromAbCorrection[2] = 1.014

	ChromAbCorrection[3] = 0.0*/

	public int HResolution = 1280;

	public int VResolution = 800;

	public float HScreenSize = 0.14976f;

	public float VScreenSize = 0.09356f;

	public float VScreenCenter = 0.0468f;

	public float EyeToScreenDistance = 0.041f;

	public float LensSeparationDistance = 0.0635f;

	public float InterpupillaryDistance = 0.064f;

	public float[] DistortionK = new float[]
	{ 1.0f, 0.22f, 0.24f, 0.0f };

	public int DesktopX = -1;

	public int DesktopY = -1;

	public String DisplayDeviceName = "Unknown";

	public long DisplayId = -1;

	public int Version = -1;

	public String Manufacturer = "Unknown";

	public String ProductName = "Unknown";

	@Override
	public String toString()
	{
		return "HMDInfo [ HResolution=" + HResolution + ", VResolution=" + VResolution //
				+ ", HScreenSize=" + HScreenSize + ", VScreenSize=" + VScreenSize //
				+ ", VScreenCenter=" + VScreenCenter + ", EyeToScreenDistance=" + EyeToScreenDistance //
				+ ", LensSeparationDistance=" + LensSeparationDistance + ", InterpupillaryDistance=" + InterpupillaryDistance //
				+ ", DistortionK=" + Arrays.toString(DistortionK)//
				+ ", DesktopX=" + DesktopX + ", DesktopY=" + DesktopY//
				+ ", DisplayDeviceName=" + DisplayDeviceName + ", DisplayId=" + DisplayId + "]";
	}
}
