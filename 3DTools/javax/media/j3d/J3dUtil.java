package javax.media.j3d;

public class J3dUtil
{
	public static void postProcessFrameBuffer(float distortionOffset)
	{
		((JoglPipeline) Pipeline.getPipeline()).postProcessFrameBuffer(distortionOffset);
	}

}
