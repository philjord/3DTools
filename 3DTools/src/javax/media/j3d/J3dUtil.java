package javax.media.j3d;

public class J3dUtil
{
	public static void postProcessFrameBuffer(float distortionOffset, Canvas3D cv)
	{
		((JoglPipeline) Pipeline.getPipeline()).postProcessFrameBuffer(distortionOffset, cv);
	}

	

}
