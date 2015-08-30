package javax.media.j3d;


public class J3dUtil
{
	public static void postProcessFrameBufferbad(float distortionOffset, Canvas3D cv)
	{
		//((JoglPipeline) Pipeline.getPipeline()).postProcessFrameBuffer(distortionOffset, cv);
	}

	public static void checkJarLoadVersion()
	{
		try
		{
			boolean b = JoglPipeline.TOOLS3D_MARKER;
		}
		catch (Error e)
		{
			System.out.println("Bad load order jogl java3d is loaded ahead of 3Dtools.jar!");
		}
	}
}
