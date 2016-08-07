package tools3d.audio;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.PointSound;
import javax.media.j3d.Sound;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import tools3d.audio.converter.ConverterBB;

/**NOTE! this string is a URL this is how you specify a relative url.
*MediaContainer sample = new MediaContainer("file:media/sounds/factCREAK.au");
*
*Play an mp3 like this JLayerPlayer.playMP3("resources/sounds/Known as Dune.mp3");
*/

public class SimpleSounds
{

	// Muhahaha! should only use what it uses and no more, dear god what have I done?
	private static ByteBuffer bb = ByteBuffer.allocateDirect(1000000);

	/**Not to be used for background sounds! use the system supplied direct 
	* mp3 play capacity, this guy is for spatialized sounds only!
	* Possibly the first wav file fails??JOAL error code: 40961
	*/
	public static BranchGroup createPointSoundMp3(InputStream is)
	{

		ConverterBB conv = new ConverterBB();

		int detail = Converter.PrintWriterProgressListener.VERBOSE_DETAIL;
		ConverterBB.ProgressListener listener = new ConverterBB.PrintWriterProgressListener(new PrintWriter(System.out, true), detail);

		try
		{
			bb.clear();
			conv.convert(is, bb, listener, null);
		}
		catch (JavaLayerException ex)
		{
			System.err.println("Convertion failure: " + ex);
		}

		bb.rewind();
		byte[] buffer = new byte[bb.remaining()];
		bb.get(buffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		MediaContainer mc = new MediaContainer(bais);
		return createPointSound(mc);
	}

	public static BranchGroup createPointSound(MediaContainer mc)
	{
		PointSound ps = new PointSound();
		ps.setSoundData(mc);
		ps.setPosition(new Point3f(0, 0, 0));
		float staticAttenuation = 10;
		float maxGain = staticAttenuation / 100f;
		ps.setInitialGain(0.2f);
		int minimumAttenuationDistance = 1;
		int maximumAttenuationDistance = 10;
		ps.setDistanceGain(new float[] { 0, minimumAttenuationDistance, maximumAttenuationDistance }, new float[] { maxGain, maxGain, 0 });
		ps.setEnable(true);
		ps.setPause(false);

		ps.setSchedulingBounds(new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY));
		ps.setLoop(-1);
		ps.setContinuousEnable(true);

		BranchGroup bg = new BranchGroup();
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.addChild(ps);

		return bg;
	}

	public static BranchGroup createPointSound(String soundURL)
	{
		PointSound sound = new PointSound();

		BoundingSphere soundBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		sound.setSchedulingBounds(soundBounds);

		MediaContainer sample = new MediaContainer("file:" + soundURL);
		sample.setCapability(MediaContainer.ALLOW_URL_WRITE);
		sample.setCapability(MediaContainer.ALLOW_URL_READ);

		sound.setDistanceGain(createLinearAttentuation(100f, 20));

		sound.setLoop(Sound.INFINITE_LOOPS);
		sound.setContinuousEnable(false);
		sound.setReleaseEnable(false);
		sound.setSoundData(sample);
		sound.setInitialGain(1.0f);
		Point3f sound2Pos = new Point3f(0.0f, 0.0f, 0.0f);
		sound.setPosition(sound2Pos);
		sound.setEnable(true);

		BranchGroup soundGroup = new BranchGroup();
		//	soundGroup.addChild(sound);
		return soundGroup;

	}

	/**
	 * Do not try to use mp3 files here, use the system installed mp3 file handler!
	 * @param soundURL
	 * @return
	 */
	public static BranchGroup createBackgroundSound(String soundURL)
	{

		BackgroundSound sound = new BackgroundSound();

		BoundingSphere soundBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY);
		sound.setSchedulingBounds(soundBounds);

		MediaContainer sample = new MediaContainer("file:" + soundURL);
		sample.setCapability(MediaContainer.ALLOW_URL_WRITE);
		sample.setCapability(MediaContainer.ALLOW_URL_READ);

		sound.setLoop(Sound.INFINITE_LOOPS);
		sound.setContinuousEnable(false);
		sound.setReleaseEnable(false);
		sound.setSoundData(sample);
		sound.setInitialGain(1.0f);
		sound.setEnable(true);

		BranchGroup soundGroup = new BranchGroup();
		//		soundGroup.addChild(sound);
		return soundGroup;

	}

	private static Point2f[] createLinearAttentuation(float maxDist, int numPoints)
	{
		Point2f[] returnArray = new Point2f[numPoints];

		for (float i = 0; i < numPoints; i++)
		{
			float dist = (maxDist / numPoints) * i;
			float att = 1 - (i / numPoints);
			Point2f currentSet = new Point2f(dist, att);
			returnArray[(int) i] = currentSet;
		}

		return returnArray;
	}

}