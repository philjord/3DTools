package tools3d.ovr;


public class HmdInfo
{

	public float HScreenSize = 0.14976f;

	public float VScreenSize = 0.09356f;

	public float VScreenCenter = 0.0468f;

	public float EyeToScreenDistance = 0.041f;

	public float LensSeparationDistance = 0.0635f;

	public float InterpupillaryDistance = 0.064f;

	@Override
	public String toString()
	{
		return "HMDInfo [  " + ", HScreenSize=" + HScreenSize + ", VScreenSize=" + VScreenSize //
				+ ", VScreenCenter=" + VScreenCenter + ", EyeToScreenDistance=" + EyeToScreenDistance //
				+ ", LensSeparationDistance=" + LensSeparationDistance + ", InterpupillaryDistance=" + InterpupillaryDistance;

	}
}
